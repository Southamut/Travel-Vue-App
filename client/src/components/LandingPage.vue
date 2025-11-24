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

// --- Methods and Handlers ---

//Handle input change
const handleChange = (e: any) => {
    keywords.value = e.target.value;
}

//Get data
const getData = async () => {
    try {
        // 1. เปลี่ยนชื่อพารามิเตอร์จาก keywords เป็น query
        // 2. เพิ่ม pagination parameters (page=0, size=10)
        const response = await axios.get(
            `${API_BASE}/trips?page=0&size=10&query=${keywords.value}`
        );

        // 3. ปรับการเข้าถึงข้อมูล: 
        toDisplay.value = response.data.content;

        console.log("Data loaded:", response.data);
    } catch (error) {
        console.error("Error fetching trip data:", error);
    }
}

//tag selection handler
const handleTagClick = (tag) => {
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

            <!-- trip cards -->
            <TripCards class="mt-16" :toDisplay="toDisplay" @tag-clicked="handleTagClick" />
        </div>
    </div>
</template>