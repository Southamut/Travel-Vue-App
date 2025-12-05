<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import axios from 'axios';
import MyTripCard from './layout/MyTripCard.vue';
import { X } from 'lucide-vue-next';

//auth
const auth = useAuthStore();
const router = useRouter();

const API_BASE = import.meta.env.VITE_API_BASE || "";

const keywords = ref('');
const selectedTags = ref<string[]>([]);
const toDisplay = ref([]);
const isLoading = ref(false);

const currentPage = ref(0);
const totalPages = ref(1);
const itemsPerPage = ref(9);

const handleChange = (e: any) => {
    currentPage.value = 0;
    keywords.value = e.target.value.trim();
    getData();
};

const goToPage = (page: number) => {
    if (page >= 0 && page < totalPages.value) {
        currentPage.value = page;
    }
};

const getData = async () => {
    isLoading.value = true;

    try {
        const res = await axios.get(`${API_BASE}/trips/mine`, {
            headers: { Authorization: `Bearer ${auth.token}` },
            params: {
                page: currentPage.value,
                size: itemsPerPage.value,
                query: keywords.value || null,
                tags: selectedTags.value.length ? selectedTags.value : null
            }
        });
        toDisplay.value = res.data.content;
        totalPages.value = res.data.totalPages;
    } catch (e) {
        console.error(e);
        toDisplay.value = [];
    } finally {
        isLoading.value = false;
    }
};

const handleTagClick = (tag: string) => {
    const idx = selectedTags.value.indexOf(tag);
    if (idx > -1) selectedTags.value.splice(idx, 1);
    else selectedTags.value.push(tag);

    currentPage.value = 0;
    getData();
};

const removeTag = (tag: string) => {
    const i = selectedTags.value.indexOf(tag);
    if (i > -1) selectedTags.value.splice(i, 1);
    currentPage.value = 0;
    getData();
};

watch([keywords, currentPage], () => {
    getData();
}, { immediate: true });
</script>


<template>
    <!-- window box -->
    <div class="bg-[#EFECE3] dark:bg-[#222831] w-full font-prompt">
        <div class="max-w-7xl mx-auto">
            <!-- title -->
            <div class="flex flex-col sm:flex-row items-center justify-center sm:justify-between pt-15 w-9/12 mx-auto">
                <h1 class="text-5xl font-medium text-[#4A70A9] dark:text-[#DEDED1]">My Trips</h1>
                <button @click="router.push('/my-trips/create')"
                    class="btn bg-[#4A70A9] text-white rounded-full px-6 py-6 mt-4 hover:bg-[#3a5f91] transition-colors">
                    Create Trip
                </button>
            </div>

            <!-- search box  -->
            <div class="flex flex-col items-center justify-center mt-10 mb-5">
                <label for="search"
                    class="text-sm lg:text-md xl:text-xl font-medium text-gray-500 dark:text-[#DFD0B8] w-9/12 text-left">ค้นหาทริปของคุณ</label>
                <input type="text" placeholder="ค้นหาทริปของคุณ..." id="search"
                    class="w-9/12 p-2 text-center text-sm xl:text-xl border-b border-gray-300 dark:text-[#DFD0B8]"
                    @input="handleChange" :value="keywords" />
                <!-- selected tags -->
                <div class="flex flex-wrap justify-center gap-2 mt-4">
                    <span v-for="tag in selectedTags" :key="tag"
                        class="badge badge-outline text-lg text-gray-500 dark:text-[#DEDED1] transition-transform duration-300 transform hover:scale-105">
                        {{ tag }}
                        <button @click="removeTag(tag)" class="text-gray-500 dark:text-[#DEDED1] font-medium">
                            <X class="h-4 w-4" />
                        </button>
                    </span>
                </div>
            </div>

            <!-- show loading -->
            <div v-if="isLoading" class="h-screen">
                <div class="flex justify-center items-center mt-20 min-h-[30vh]">
                    <span class="loading loading-dots loading-xl text-[#4A70A9]"></span>
                </div>
            </div>
            <div v-else>
                <!-- trip cards -->
                <div v-if="toDisplay.length > 0">
                    <MyTripCard class="mt-16" :toDisplay="toDisplay" @tag-clicked="handleTagClick" />
                    <div class="py-10">
                        <div class="flex justify-center">
                            <div class="join shadow-md">
                                <button v-for="page in totalPages" :key="page" class="join-item btn"
                                    :class="{ 'btn-primary': currentPage === page - 1 }" @click="goToPage(page - 1)">
                                    {{ page }}
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                <div v-else class="flex justify-center items-center mt-20 min-h-[30vh]">
                    <p class="text-xl text-gray-600 dark:text-[#DFD0B8]">
                        “No trips found. Try a different keyword.”
                    </p>
                </div>
            </div>
        </div>
    </div>
</template>