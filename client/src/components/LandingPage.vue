<script setup lang="ts">
import Navbar from './layout/Navbar.vue';
import TripCards from './layout/TripCards.vue';

import { ref } from 'vue';

//for search box
const keywords = ref('')

const handleChange = (e: any) => {
    keywords.value = e.target.value;
}

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
            <TripCards class="mt-16"/>
        </div>
    </div>
</template>