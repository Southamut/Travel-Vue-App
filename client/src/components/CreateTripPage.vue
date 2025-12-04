<script setup lang="ts">
import { ref, onMounted, watch } from "vue";
import axios from "axios";
import L from "leaflet";
import 'leaflet/dist/leaflet.css';
import { useAuthStore } from "../stores/auth";
import { useRouter } from "vue-router";

const API_BASE = import.meta.env.VITE_API_BASE || "";
const auth = useAuthStore();
const router = useRouter();

// ---------------------------------------------
// FORM STATE
// ---------------------------------------------
const title = ref("");
const description = ref("");
const province = ref("");
const tags = ref<string[]>([]);
const newTag = ref("");

const latitude = ref(13.7563);
const longitude = ref(100.5018);

const photos = ref<File[]>([]);
const previews = ref<string[]>([]);

// ---------------------------------------------
// ERROR STATE
// ---------------------------------------------
const errors = ref({
    title: "",
    description: "",
    province: "",
    tags: "",
    location: ""
});

// ---------------------------------------------
// TAG HANDLING
// ---------------------------------------------
const addTag = () => {
    if (!newTag.value.trim()) return;
    tags.value.push(newTag.value.trim());
    newTag.value = "";
};
const removeTag = (index: number) => tags.value.splice(index, 1);

// ---------------------------------------------
// IMAGE HANDLING
// ---------------------------------------------
const onSelectImages = (e: Event) => {
    const input = e.target as HTMLInputElement;
    if (!input.files) return;

    const selected = Array.from(input.files);
    if (photos.value.length + selected.length > 4) {
        errors.value.tags = "You can only upload up to 4 images."; // optional message
        return;
    }
    selected.forEach(file => {
        photos.value.push(file);
        previews.value.push(URL.createObjectURL(file));
    });
    input.value = "";
};
const removeImage = (index: number) => {
    photos.value.splice(index, 1);
    previews.value.splice(index, 1);
};

// Drag reorder
const dragIndex = ref<number | null>(null);
const dragStart = (index: number) => (dragIndex.value = index);
const dropOn = (index: number) => {
    if (dragIndex.value === null) return;
    const photo = photos.value.splice(dragIndex.value, 1)[0];
    const preview = previews.value.splice(dragIndex.value, 1)[0];
    photos.value.splice(index, 0, photo);
    previews.value.splice(index, 0, preview);
    dragIndex.value = null;
};

// ---------------------------------------------
// MAP + CUSTOM AUTOCOMPLETE SEARCH
// ---------------------------------------------
const map = ref<L.Map | null>(null);
const marker = ref<L.Marker | null>(null);
const searchQuery = ref("");
const searchResults = ref<any[]>([]);
const showDropdown = ref(false);

const initMap = () => {
    map.value = L.map("map").setView([latitude.value, longitude.value], 12);

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: "&copy; OpenStreetMap contributors",
    }).addTo(map.value);

    marker.value = L.marker([latitude.value, longitude.value], { draggable: true }).addTo(map.value);

    marker.value.on("dragend", () => {
        const pos = marker.value!.getLatLng();
        latitude.value = pos.lat;
        longitude.value = pos.lng;
    });

    map.value.on("click", (e: L.LeafletMouseEvent) => {
        marker.value!.setLatLng(e.latlng);
        latitude.value = e.latlng.lat;
        longitude.value = e.latlng.lng;
    });
};

// Watch searchQuery for autocomplete
watch(searchQuery, async (val) => {
    if (!val) {
        searchResults.value = [];
        showDropdown.value = false;
        return;
    }

    try {
        const res = await axios.get("https://nominatim.openstreetmap.org/search", {
            params: {
                q: val,
                format: "json",
                addressdetails: 1,
                limit: 5,
            },
        });
        searchResults.value = res.data;
        showDropdown.value = searchResults.value.length > 0;
    } catch (err) {
        console.error(err);
        searchResults.value = [];
        showDropdown.value = false;
    }
});

// Select a location from dropdown
const selectLocation = (loc: any) => {
    const lat = parseFloat(loc.lat);
    const lon = parseFloat(loc.lon);

    latitude.value = lat;
    longitude.value = lon;
    map.value!.setView([lat, lon], 14);
    marker.value!.setLatLng([lat, lon]);

    searchQuery.value = loc.display_name;
    showDropdown.value = false;
};

onMounted(() => {
    initMap();
});

// ---------------------------------------------
// SUBMIT TRIP
// ---------------------------------------------
const isSubmitting = ref(false);

const submitTrip = async () => {
    // reset errors
    errors.value = { title: "", description: "", province: "", tags: "", location: "" };

    let hasError = false;
    if (!title.value.trim()) {
        errors.value.title = "Title is required";
        hasError = true;
    }
    if (!description.value.trim()) {
        errors.value.description = "Description is required";
        hasError = true;
    }
    if (!province.value) {
        errors.value.province = "Province is required";
        hasError = true;
    }
    if (!tags.value.length) {
        errors.value.tags = "At least one tag is required";
        hasError = true;
    }
    if (!latitude.value || !longitude.value) {
        errors.value.location = "Location is required";
        hasError = true;
    }

    if (hasError) return;

    if (!auth.token) {
        errors.value.title = "You must be logged in";
        return;
    }

    isSubmitting.value = true;

    try {
        const uploadedUrls: string[] = [];
        for (let i = 0; i < photos.value.length; i++) {
            const formData = new FormData();
            formData.append("file", photos.value[i]);
            const res = await axios.post(`${API_BASE}/files/upload`, formData, {
                headers: { Authorization: `Bearer ${auth.token}`, "Content-Type": "multipart/form-data" },
            });
            uploadedUrls.push(res.data.url);
        }

        const payload = {
            title: title.value,
            description: description.value,
            photos: uploadedUrls,
            tags: tags.value,
            latitude: latitude.value,
            longitude: longitude.value,
            province: province.value,
        };

        await axios.post(`${API_BASE}/trips`, payload, {
            headers: { Authorization: `Bearer ${auth.token}` },
        });

        router.push("/my-trips");
    } catch (err) {
        console.error(err);
        errors.value.title = "Failed to create trip";
    } finally {
        isSubmitting.value = false;
    }
};
</script>

<template>
    <div class="bg-[#EFECE3] dark:bg-[#222831] w-full min-h-screen font-prompt pt-12 pb-24">
        <div class="max-w-3xl mx-auto px-6">
            <h1 class="text-5xl text-center font-medium text-[#4A70A9] dark:text-[#DEDED1] mb-10">
                Create Trip
            </h1>

            <div class="card bg-[#DEDED1] dark:bg-base-100 p-10 rounded-2xl">

                <!-- Title -->
                <label class="block text-lg text-[#4A70A9] dark:text-[#DEDED1] font-medium mb-2">Title</label>
                <input v-model="title" type="text" class="input input-bordered w-full bg-[#EFECE3] dark:bg-[#222831]" />
                <p v-if="errors.title" class="text-red-500 text-sm mt-1">{{ errors.title }}</p>

                <!-- Description -->
                <label
                    class="block text-lg text-[#4A70A9] dark:text-[#DEDED1] font-medium mt-6 mb-2">Description</label>
                <textarea v-model="description" rows="5"
                    class="textarea textarea-bordered bg-[#EFECE3] dark:bg-[#222831] w-full"></textarea>
                <p v-if="errors.description" class="text-red-500 text-sm mt-1">{{ errors.description }}</p>

                <!-- Province -->
                <label class="block text-lg text-[#4A70A9] dark:text-[#DEDED1] font-medium mt-6 mb-2">Province</label>
                <select v-model="province" class="select select-bordered bg-[#EFECE3] dark:bg-[#222831] w-full">
                    <option disabled value="">Select province</option>
                    <option>Bangkok</option>
                    <option>Chiang Mai</option>
                    <option>Chiang Rai</option>
                    <option>Phuket</option>
                    <option>Khon Kaen</option>
                    <option>Ayutthaya</option>
                </select>
                <p v-if="errors.province" class="text-red-500 text-sm mt-1">{{ errors.province }}</p>

                <!-- Tags -->
                <label class="block text-lg text-[#4A70A9] dark:text-[#DEDED1] font-medium mt-6 mb-2">Tags</label>
                <div class="flex gap-2 mb-3 flex-wrap">
                    <span v-for="(t, i) in tags" :key="i" class="badge badge-outline">
                        {{ t }}
                        <button @click="removeTag(i)" class="ml-1">x</button>
                    </span>
                </div>
                <div class="flex gap-2">
                    <input v-model="newTag" type="text" placeholder="Add tag"
                        class="input input-bordered bg-[#EFECE3] dark:bg-[#222831] w-full" />
                    <button class="btn bg-[#4A70A9] text-white" @click="addTag">Add</button>
                </div>
                <p v-if="errors.tags" class="text-red-500 text-sm mt-1">{{ errors.tags }}</p>

                <!-- Photos Upload -->
                <label class="block text-lg text-[#4A70A9] dark:text-[#DEDED1] font-medium mt-8 mb-2">Photos (Max
                    4)</label>
                <input type="file" accept="image/*" multiple class="file-input file-input-ghost"
                    @change="onSelectImages" />
                <div class="grid grid-cols-2 gap-3 mt-4">
                    <div v-for="(p, index) in previews" :key="index" draggable="true" @dragstart="dragStart(index)"
                        @dragover.prevent @drop="dropOn(index)" class="relative rounded-lg overflow-hidden shadow-md">
                        <img :src="p" class="w-full h-40 object-cover" />
                        <span class="absolute top-2 left-2 bg-black/60 text-white text-xs px-2 py-1 rounded">
                            {{ index === 0 ? "Main Photo" : "Photo " + index }}
                        </span>
                        <button @click="removeImage(index)"
                            class="absolute top-2 right-2 bg-red-500 text-white rounded-full px-2 py-1">
                            ✕
                        </button>
                    </div>
                </div>

                <!-- LOCATION PICKER -->
                <label class="block text-lg text-[#4A70A9] dark:text-[#DEDED1] font-medium mt-10 mb-2">Location</label>
                <div class="relative">
                    <input v-model="searchQuery" type="text" placeholder="Search location..."
                        class="input input-bordered w-full mb-2" @focus="showDropdown = searchResults.length > 0" />
                    <ul v-if="showDropdown"
                        class="absolute menu dropdown-content z-2000 w-full bg-[#EFECE3] dark:bg-[#393E46] text-gray-600 dark:text-[#DFD0B8] rounded shadow max-h-60 overflow-auto">
                        <li v-for="(res, index) in searchResults" :key="index" @click="selectLocation(res)"
                            class="px-3 py-2 hover:bg-base-100 cursor-pointer">
                            {{ res.display_name }}
                        </li>
                    </ul>
                </div>
                <div id="map" class="w-full h-80 rounded-lg border-2 border-gray-300 mt-2"></div>
                <p class="text-sm mt-2 text-gray-600 dark:text-gray-300">
                    Lat: {{ latitude.toFixed(6) }} | Lng: {{ longitude.toFixed(6) }}
                </p>
                <p v-if="errors.location" class="text-red-500 text-sm mt-1">{{ errors.location }}</p>

                <!-- Submit -->
                <button class="btn bg-[#4A70A9] text-[#DEDED1] w-full mt-10 rounded-full" :disabled="isSubmitting"
                    @click="submitTrip">
                    <span v-if="isSubmitting" class="loading loading-spinner loading-sm mr-2"></span>
                    Create Trip
                </button>

            </div>
        </div>
    </div>
</template>
