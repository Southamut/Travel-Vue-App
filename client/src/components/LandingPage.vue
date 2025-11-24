<script setup lang="ts">
import Navbar from './layout/Navbar.vue';
import TripCards from './layout/TripCards.vue';

import { ref, watch, onMounted } from 'vue';
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
    keywords.value = e.target.value;
}

//Get data and pagination
const currentPage = ref(0);     // หมายเลขหน้าปัจจุบัน (เริ่มที่ 0)
const totalPages = ref(1);      // จำนวนหน้ารวมทั้งหมด
const itemsPerPage = ref(10);   // จำนวนรายการต่อหน้า (size)

// ฟังก์ชันสำหรับเปลี่ยนหน้า
const goToPage = (pageNumber: number) => {
    // ป้องกันการเปลี่ยนหน้าเกินขอบเขต
    if (pageNumber >= 0 && pageNumber < totalPages.value) {
        currentPage.value = pageNumber;
        getData(); // เรียก API เพื่อดึงข้อมูลหน้าใหม่
    }
};

const getData = async () => {

    //set loading state
    isLoading.value = true;

    try {
        const response = await axios.get(
            // ส่ง page และ size เข้าไป
            `${API_BASE}/trips?page=${currentPage.value}&size=${itemsPerPage.value}&query=${keywords.value}`
        );

        // อัปเดต State Pagination จาก Response
        toDisplay.value = response.data.content;
        totalPages.value = response.data.totalPages; // ต้องตรงกับชื่อใน TripPageResponse
        currentPage.value = response.data.number;    // หมายเลขหน้าที่ Spring Boot ส่งกลับมา

    } catch (error) {
        console.error("Error fetching trip data:", error);
    } finally {
        //unset loading state
        isLoading.value = false;
    }
}

//tag selection handler
const handleTagClick = (tag:string) => {
    // รีเซ็ตหน้าเป็น 0 เมื่อมีการคลิก Tag เพื่อเริ่มค้นหาใหม่
    currentPage.value = 0;
    // ป้องกัน tag ซ้ำ
    if (!selectedTags.value.includes(tag)) {
        // เพิ่ม tag ใหม่
        selectedTags.value.push(tag);

        // นำ tag ทั้งหมดมารวมกันแล้วใส่ในช่องค้นหา (เหมือนกับการพิมพ์)
        const newSearchText = selectedTags.value.join(" ");
        // เพิ่มช่องว่างท้ายสุดเพื่อให้ผู้ใช้พิมพ์ต่อได้ง่าย
        keywords.value = newSearchText + " ";
    }
};

// --- Lifecycle Hooks and Watchers ---

// Watcher: เมื่อ keywords เปลี่ยนแปลง ให้เรียก getData() เพื่อค้นหาใหม่
watch(keywords, () => {
    getData();
});

// เมื่อ Component ถูกสร้างเสร็จ ให้โหลดข้อมูลครั้งแรก
onMounted(() => {
    getData();
});


</script>

<template>
    <Navbar />
    <!-- window box -->
    <div class="bg-[#EFECE3] dark:bg-[#222831] w-full font-prompt">
        <div class="max-w-7xl mx-auto">
            <!-- title -->
            <div class="flex flex-col items-center justify-center pt-15">
                <h1 class="text-5xl font-medium text-[#4A70A9] dark:text-[#DEDED1]">เที่ยวไหนดี</h1>
            </div>

            <!-- search box  -->
            <div class="flex flex-col items-center justify-center mt-10 mb-5">
                <label htmlFor="search"
                    class="text-sm lg:text-md xl:text-xl font-medium text-gray-500 dark:text-[#DFD0B8] w-9/12 text-left">หาที่เที่ยวแล้วไปกัน</label>
                <input type="text" placeholder="หาที่เที่ยวแล้วไปกัน..."
                    class="w-9/12 p-2 text-center text-sm xl:text-xl border-b border-gray-300 dark:text-[#DFD0B8]"
                    @input="handleChange" :value="keywords" />
            </div>

            <!-- show loading -->
            <div v-if="isLoading" class="flex justify-center items-center mt-20 min-h-[30vh]">
                <span class="loading loading-dots loading-xl text-[#4A70A9]"></span>
            </div>
            <div v-else>
                <!-- trip cards -->
                <TripCards class="mt-16" :toDisplay="toDisplay" @tag-clicked="handleTagClick" />
                <div class="pb-10">
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
        </div>
    </div>
</template>