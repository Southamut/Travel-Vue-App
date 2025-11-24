<script setup lang="ts">
import { ref } from 'vue';

//for search box
const keywords = ref('')

const handleChange = (e: any) => {
    keywords.value = e.target.value;
}

</script>

<template>
    <!-- window box -->
    <div className="bg-white w-full font-prompt">
        <!-- title -->
        <div className="flex flex-col items-center justify-center mt-15">
            <h1 className="text-5xl font-medium text-blue-400">เที่ยวไหนดี</h1>
        </div>

        <!-- search box  -->
        <div className="flex flex-col items-center justify-center mt-10 mb-5">
            <label htmlFor="search"
                className="text-sm xl:text-xl font-medium text-gray-500 w-9/12 text-left">หาที่เที่ยวแล้วไปกัน</label>
            <input type="text" placeholder="หาที่เที่ยวแล้วไปกัน..."
                className="w-9/12 p-2 text-center text-sm xl:text-xl border-b border-gray-300" @input="handleChange"
                :value="keywords" />
        </div>
    </div>
</template>