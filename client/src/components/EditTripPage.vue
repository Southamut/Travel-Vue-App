<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import axios from 'axios';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import { ArrowLeft } from 'lucide-vue-next';
import { useToastStore } from '../stores/toast'

const API_BASE = import.meta.env.VITE_API_BASE || '';
const router = useRouter();
const route = useRoute();
const auth = useAuthStore();
const toast = useToastStore()

const tripId = Number(route.params.id);

// --- form state ---
const title = ref('');
const description = ref('');
const tags = ref<string[]>([]);
const newTag = ref('');

const latitude = ref(13.7563);
const longitude = ref(100.5018);
const province = ref(''); // readonly auto-filled

type Item = { id: string; type: 'old' | 'new'; url?: string; file?: File };
const items = ref<Item[]>([]); // combined old (url) + new (file preview)

// original snapshot for reset
let originalSnapshot: any = null;

// errors / loading
const errors = ref({ title: '', description: '', tags: '', province: '', location: '' });
const isSubmitting = ref(false);

// helpers
const generateId = () => `${Date.now()}_${Math.random().toString(36).slice(2, 8)}`;

// tags
const addTag = () => {
    if (!newTag.value.trim()) return;
    tags.value.push(newTag.value.trim());
    newTag.value = '';
};
const removeTag = (i: number) => tags.value.splice(i, 1);

// images: select new files
function onSelectImages(e: Event) {
    const inp = e.target as HTMLInputElement;
    if (!inp.files) return;
    const selected = Array.from(inp.files);

    if (items.value.length + selected.length > 4) {
        toast.error('You can only have up to 4 images.');
        return;
    }

    for (const f of selected) {
        const url = URL.createObjectURL(f);
        items.value.push({ id: generateId(), type: 'new', url, file: f });
    }
    inp.value = '';
}

// remove item (old or new)
function removeItem(index: number) {
    const it = items.value[index];
    if (it?.type === 'new' && it.url) URL.revokeObjectURL(it.url);
    items.value.splice(index, 1);
}

// drag reorder
let dragIndex: number | null = null;
const dragStart = (i: number) => (dragIndex = i);
const dropOn = (i: number) => {
    if (dragIndex === null) return;
    const spliced = items.value.splice(dragIndex, 1);
    const it = spliced[0];
    if (!it) return;
    items.value.splice(i, 0, it);
    dragIndex = null;
};

// --- map + reverse geocode ---
let map: L.Map | null = null;
let marker: L.Marker | null = null;

const searchQuery = ref('');
const searchResults = ref<any[]>([]);
const showDropdown = ref(false);

const initMap = () => {
    map = L.map('map').setView([latitude.value, longitude.value], 12);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors',
    }).addTo(map);

    marker = L.marker([latitude.value, longitude.value], { draggable: true }).addTo(map);

    marker.on('dragend', async () => {
        const pos = marker!.getLatLng();
        latitude.value = pos.lat;
        longitude.value = pos.lng;
        await reverseGeocodeAndSetProvince(pos.lat, pos.lng);
    });

    map.on('click', async (e: L.LeafletMouseEvent) => {
        marker!.setLatLng(e.latlng);
        latitude.value = e.latlng.lat;
        longitude.value = e.latlng.lng;
        await reverseGeocodeAndSetProvince(e.latlng.lat, e.latlng.lng);
    });
};

async function reverseGeocodeAndSetProvince(lat: number, lon: number) {
    try {
        const res = await axios.get('https://nominatim.openstreetmap.org/reverse', {
            params: { format: 'json', lat, lon, zoom: 10, addressdetails: 1 },
        });
        const addr = res.data?.address || {};
        province.value = addr.state || addr.region || addr.county || addr.city || addr.town || '';
    } catch (err) {
        console.error('Reverse geocode failed', err);
        province.value = '';
    }
}

// search nominatim
watch(searchQuery, async (q) => {
    if (!q) {
        searchResults.value = [];
        showDropdown.value = false;
        return;
    }
    try {
        const res = await axios.get('https://nominatim.openstreetmap.org/search', {
            params: { q, format: 'json', addressdetails: 1, limit: 6 },
        });
        searchResults.value = res.data || [];
        showDropdown.value = searchResults.value.length > 0;
    } catch (err) {
        searchResults.value = [];
        showDropdown.value = false;
    }
});

async function selectLocation(loc: any) {
    const lat = parseFloat(loc.lat);
    const lon = parseFloat(loc.lon);
    latitude.value = lat;
    longitude.value = lon;
    map!.setView([lat, lon], 14);
    marker!.setLatLng([lat, lon]);
    searchQuery.value = loc.display_name;
    showDropdown.value = false;
    await reverseGeocodeAndSetProvince(lat, lon);
}

// --- load trip ---
const loadTrip = async () => {
    try {
        const res = await axios.get(`${API_BASE}/trips/${tripId}`);
        const t = res.data;
        title.value = t.title;
        description.value = t.description;
        tags.value = [...t.tags];
        latitude.value = t.latitude || 13.7563;
        longitude.value = t.longitude || 100.5018;
        province.value = t.province || '';
        // populate items with old photos
        items.value = (t.photos || []).map((u: string) => ({ id: generateId(), type: 'old', url: u }));
        // snapshot for reset
        originalSnapshot = {
            title: t.title,
            description: t.description,
            tags: [...t.tags],
            latitude: latitude.value,
            longitude: longitude.value,
            province: province.value,
            items: items.value.map((x: Item) => ({ ...x })), // shallow copy
        };
    } catch (err) {
        console.error(err);
        toast.error('Failed to load trip');
        router.back();
    }
};

const resetForm = () => {
    if (!originalSnapshot) return;
    title.value = originalSnapshot.title;
    description.value = originalSnapshot.description;
    tags.value = [...originalSnapshot.tags];
    latitude.value = originalSnapshot.latitude;
    longitude.value = originalSnapshot.longitude;
    province.value = originalSnapshot.province;
    // revoke any object URLs created since original
    for (const it of items.value) {
        if (it.type === 'new' && it.url) URL.revokeObjectURL(it.url);
    }
    items.value = originalSnapshot.items.map((x: any) => ({ ...x }));
    map!.setView([latitude.value, longitude.value], 12);
    marker!.setLatLng([latitude.value, longitude.value]);
};

const cancelEdit = () => router.push('/my-trips');

// --- submit update ---
const submitEdit = async () => {
    errors.value = { title: '', description: '', tags: '', province: '', location: '' };
    let hasError = false;
    if (!title.value.trim()) { errors.value.title = 'Title is required'; hasError = true; }
    if (!description.value.trim()) { errors.value.description = 'Description is required'; hasError = true; }
    if (!tags.value.length) { errors.value.tags = 'At least one tag is required'; hasError = true; }
    if (!latitude.value || !longitude.value) { errors.value.location = 'Location is required'; hasError = true; }
    if (hasError) return;

    if (!auth.token) { toast.error('Please login'); return; }

    isSubmitting.value = true;
    try {
        // upload new files in order of items
        const uploadedUrls: string[] = [];
        // maintain mapping for new-file items: we'll fill as we upload
        for (const it of items.value) {
            if (it.type === 'new' && it.file) {
                const form = new FormData();
                form.append('file', it.file);
                const res = await axios.post(`${API_BASE}/files/upload`, form, {
                    headers: {
                        Authorization: `Bearer ${auth.token}`,
                        'Content-Type': 'multipart/form-data',
                    },
                });
                uploadedUrls.push(res.data.url);
            } else if (it.type === 'old' && it.url) {
                uploadedUrls.push(it.url);
            }
        }

        // Prepare payload with photos in the same order as items
        const payload = {
            title: title.value,
            description: description.value,
            photos: uploadedUrls,
            tags: tags.value,
            latitude: latitude.value,
            longitude: longitude.value,
            province: province.value,
        };

        await axios.put(`${API_BASE}/trips/${tripId}`, payload, {
            headers: { Authorization: `Bearer ${auth.token}` },
        });

        router.push('/my-trips');
    } catch (err) {
        console.error(err);
        toast.error('Failed to update trip');
    } finally {
        isSubmitting.value = false;
    }
};

// init
onMounted(async () => {
    await loadTrip();
    initMap();
});
</script>

<template>
    <div class="bg-[#EFECE3] dark:bg-[#222831] w-full min-h-screen font-prompt pt-12 pb-24">
        <div class="max-w-3xl mx-auto px-6">
            <!-- Back Button -->
            <button @click="router.back()"
                class="btn rounded-full mb-6">
                <ArrowLeft class="w-5 h-5 mr-1" /> Back
            </button>
            <h1 class="text-4xl sm:text-5xl text-center font-medium text-[#4A70A9] dark:text-[#DEDED1] mb-10">Edit Trip</h1>

            <div class="card bg-[#DEDED1] dark:bg-base-100 p-10 rounded-2xl">
                <!-- Title -->
                <label class="block text-lg text-[#4A70A9] font-medium mb-2">Title</label>
                <input v-model="title" class="input input-bordered w-full bg-[#EFECE3] dark:bg-[#222831]" />
                <p v-if="errors.title" class="text-red-500 text-sm mt-1">{{ errors.title }}</p>

                <!-- Description -->
                <label class="block text-lg text-[#4A70A9] font-medium mt-6 mb-2">Description</label>
                <textarea v-model="description" rows="5"
                    class="textarea textarea-bordered w-full bg-[#EFECE3] dark:bg-[#222831]"></textarea>
                <p v-if="errors.description" class="text-red-500 text-sm mt-1">{{ errors.description }}</p>

                <!-- Tags -->
                <label class="block text-lg text-[#4A70A9] font-medium mt-6 mb-2">Tags</label>
                <div class="flex gap-2 mb-3 flex-wrap">
                    <span v-for="(t, i) in tags" :key="i" class="badge badge-outline">{{ t }} <button
                            @click="removeTag(i)" class="ml-1">x</button></span>
                </div>
                <div class="flex gap-2">
                    <input v-model="newTag" placeholder="Add tag"
                        class="input input-bordered w-full bg-[#EFECE3] dark:bg-[#222831]" />
                    <button @click="addTag" class="btn bg-[#4A70A9] text-white">Add</button>
                </div>

                <!-- Photos -->
                <label class="block text-lg text-[#4A70A9] font-medium mt-8 mb-2">Photos (Max 4)</label>
                <input type="file" accept="image/*" multiple class="file-input file-input-ghost"
                    @change="onSelectImages" />
                <div class="grid grid-cols-2 gap-3 mt-4">
                    <div v-for="(it, idx) in items" :key="it.id" draggable="true" @dragstart.prevent="dragStart(idx)"
                        @dragover.prevent @drop.prevent="dropOn(idx)"
                        class="relative rounded-lg overflow-hidden shadow-md">
                        <img :src="it.url" class="w-full h-40 object-cover" />
                        <span class="absolute top-2 left-2 bg-black/60 text-white text-xs px-2 py-1 rounded">{{ idx ===
                            0 ? 'Main Photo' : 'Photo ' + idx }}</span>
                        <button @click="removeItem(idx)"
                            class="absolute top-2 right-2 bg-red-500 text-white rounded-full px-2 py-1">✕</button>
                    </div>
                </div>

                <!-- Location & Province -->
                <label class="block text-lg text-[#4A70A9] font-medium mt-10 mb-2">Location</label>
                <input v-model="searchQuery" placeholder="Search location..."
                    class="input input-bordered w-full mb-2" />
                <ul v-if="showDropdown"
                    class="absolute z-50 w-full bg-[#EFECE3] dark:bg-[#393E46] rounded shadow max-h-60 overflow-auto">
                    <li v-for="(r, i) in searchResults" :key="i" @click="selectLocation(r)"
                        class="px-3 py-2 hover:bg-base-100 cursor-pointer">{{ r.display_name }}</li>
                </ul>
                <div id="map" class="w-full h-80 rounded-lg border-2 border-gray-300 mt-2"></div>

                <p class="text-sm mt-2">Lat: {{ latitude.toFixed(6) }} | Lng: {{ longitude.toFixed(6) }}</p>

                <label class="block text-lg text-[#4A70A9] font-medium mt-6 mb-2">Province (auto)</label>
                <input v-model="province" readonly class="input input-bordered w-full bg-[#EFECE3] dark:bg-[#222831]" />

                <!-- Buttons -->
                <div class="flex flex-col gap-4 mt-10">
                    <button @click="submitEdit" :disabled="isSubmitting"
                        class="btn bg-[#4A70A9] text-[#DEDED1] w-full rounded-full">
                        <span v-if="isSubmitting" class="loading loading-spinner loading-sm mr-2"></span> Save Changes
                    </button>
                    <button @click="resetForm" class="btn btn-outline w-full rounded-full">Reset</button>
                    <button @click="cancelEdit" class="btn bg-gray-400 text-white w-full rounded-full">Cancel</button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.font-prompt {
    font-family: 'Prompt', sans-serif;
}
</style>
