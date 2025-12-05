<script setup lang="ts">
import TripCards from './layout/TripCards.vue';
import { X } from 'lucide-vue-next';

import { ref, watch } from 'vue';
import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_BASE || "";

// --- State Management ---

//for search box
const keywords = ref('')

//For displaying
const toDisplay = ref([]);

// For Tag Search
const selectedTags = ref([]);

//For loading state
const isLoading = ref(false); // เริ่มต้นเป็น false

// --- Methods and Handlers ---

//Handle input change
const handleChange = (e: any) => {
    currentPage.value = 0;
    keywords.value = e.target.value.trim();
    getData();
};

//Get data and pagination
const currentPage = ref(0);     // หมายเลขหน้าปัจจุบัน (เริ่มที่ 0)
const totalPages = ref(1);      // จำนวนหน้ารวมทั้งหมด
const itemsPerPage = ref(9);   // จำนวนรายการต่อหน้า (size)

// ฟังก์ชันสำหรับเปลี่ยนหน้า
const goToPage = (pageNumber: number) => {
    // ป้องกันการเปลี่ยนหน้าเกินขอบเขต
    if (pageNumber >= 0 && pageNumber < totalPages.value) {
        currentPage.value = pageNumber;
    }
};

const getData = async () => {
    isLoading.value = true;
    const safePage = currentPage.value ?? 0;

    try {
        const response = await axios.get(`${API_BASE}/trips`, {
            params: {
                page: safePage,
                size: itemsPerPage.value,
                query: keywords.value || null,
                tags: selectedTags.value.length ? selectedTags.value : null,
            }
        });

        toDisplay.value = response.data.content;
        totalPages.value = response.data.totalPages;

    } catch (error) {
        console.error("Error fetching trip data:", error);
        toDisplay.value = [];
    } finally {
        isLoading.value = false;
    }
}


//tag selection handler
const handleTagClick = (tag: string) => {
    const index = selectedTags.value.indexOf(tag);
    if (index > -1) {
        // ถ้า Tag มีอยู่แล้ว ให้ลบออก
        selectedTags.value.splice(index, 1);
    } else {
        // ถ้า Tag ยังไม่มี ให้เพิ่มเข้าไป
        selectedTags.value.push(tag);
    }
    currentPage.value = 0;
    // getData จะ combine keywords + selectedTags เอง
    getData();
};

//remove tag from selectedTags
const removeTag = (tag: string) => {
    const index = selectedTags.value.indexOf(tag);
    if (index > -1) selectedTags.value.splice(index, 1);
    currentPage.value = 0;
    getData();
};


// --- Lifecycle Hooks and Watchers ---

// Watcher: เมื่อ keywords เปลี่ยนแปลง ให้เรียก getData() เพื่อค้นหาใหม่
watch([keywords, currentPage], () => {
    getData();
}, { immediate: true }); // เรียกครั้งแรก (initial load)


</script>

<template>
    <!-- window box -->
    <div class="bg-[#EFECE3] dark:bg-[#222831] w-full font-prompt">
        <div class="max-w-7xl mx-auto">
            <!-- title -->
            <div class="flex flex-col items-center justify-center pt-15">
                <h1 class="text-5xl font-medium text-[#4A70A9] dark:text-[#DEDED1]">เที่ยวไหนดี</h1>
            </div>

            <!-- search box  -->
            <div class="flex flex-col items-center justify-center mt-10 mb-5">
                <label for="search"
                    class="text-sm lg:text-md xl:text-xl font-medium text-gray-500 dark:text-[#DFD0B8] w-9/12 text-left">หาที่เที่ยวแล้วไปกัน</label>
                <input type="text" placeholder="หาที่เที่ยวแล้วไปกัน..." id="search"
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
                    <TripCards class="mt-16" :toDisplay="toDisplay" @tag-clicked="handleTagClick" />
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