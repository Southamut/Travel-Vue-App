<script setup>
import { Link } from 'lucide-vue-next';
import { defineProps, defineEmits } from 'vue';
import { ref } from 'vue';

// ----------------------------------------------------
// 1. Component ย่อย: LinkButton (สำหรับปุ่มคัดลอกลิงก์)
// ----------------------------------------------------


// Function for copy link
const showToast = ref(false);
const toastType = ref(''); // 'success' หรือ 'error'
const toastMessage = ref('');

const copyToClipboard = async (text) => {
    try {
        await navigator.clipboard.writeText(text);

        // แสดง Toast สำเร็จ
        toastType.value = 'success';
        toastMessage.value = 'Link copied to clipboard!';
        showToast.value = true;

    } catch (error) {
        // แสดง Toast ล้มเหลว
        toastType.value = 'error';
        toastMessage.value = 'Failed to copy link!';
        showToast.value = true;
    }

    // ตั้งเวลาซ่อน Toast
    setTimeout(() => {
        showToast.value = false;
    }, 3000); // 3 วินาที
};

// ----------------------------------------------------
// 3. Component หลัก: TripSuggestList
// ----------------------------------------------------

// Prop Declaration (แทน props ใน React)
defineProps({
    toDisplay: {
        type: Array,
        required: true
    }
});

// Event Declaration (แทน onTagClick ใน React)
const emit = defineEmits(['tag-clicked']);

// Handler สำหรับส่ง Tag ที่ถูกคลิกขึ้นไปให้ Component แม่
const handleTagClick = (tag) => {
    emit('tag-clicked', tag);
}
</script>

<template>
    <div v-for="item in toDisplay" :key="item.eid" class="mb-8">
        <div class="card sm:card-side bg-[#DEDED1] dark:bg-base-100 mx-10">
            <figure class="sm:w-80 aspect-4/3 p-6">
                <img :src="item.photos[0]" alt="trip-image" className="w-full h-full object-cover rounded-xl" />
            </figure>
            <div class="card-body gap-4">
                <h2 class="card-title text-md md:text-lg lg:text-xl xl:text-2xl font-bold text-gray-800 dark:text-[#DEDED1]"
                    :href="item.url">
                    {{ item.title }}</h2>
                <p class="text-xs md:text-sm lg:text-md font-medium text-gray-500 dark:text-[#DFD0B8]">
                    {{ item.description.length > 100 ? item.description.slice(0, 100) + "..." : item.description }}
                </p>
                <a :href="item.url" class="text-xs md:text-sm lg:text-md font-medium text-[#4A70A9] dark:text-[#DEDED1] underline">อ่านต่อ
                </a>
                <ul class="flex flex-row flex-wrap gap-2 mb-4">
                    <template v-for="(tag, index) in item.tags" :key="index">
                        <li class="badge badge-outline">
                            <button @click="handleTagClick(tag)"
                                class="text-xs md:text-sm lg:text-md font-medium text-gray-500 dark:text-[#DEDED1]">
                                {{ tag }} </button>
                        </li>
                    </template>
                </ul>
                <div class="card-actions justify-between items-end">
                    <div class="grid grid-cols-3 gap-2">
                        <div class="col-span-1 aspect-square w-12 sm:w-16 lg:w-20"><img :src="item.photos[1]"
                                alt="sample-image-1" className="w-full h-full object-cover rounded-xl" />
                        </div>
                        <div class="col-span-1 aspect-square w-12 sm:w-16 lg:w-20"><img :src="item.photos[2]"
                                alt="sample-image-2" className="w-full h-full object-cover rounded-xl" />
                        </div>
                        <div class="col-span-1 aspect-square w-12 sm:w-16 lg:w-20"><img :src="item.photos[3]"
                                alt="sample-image-3" className="w-full h-full object-cover rounded-xl" />
                        </div>
                    </div>
                    <button @click="copyToClipboard(item.url)"
                        class="btn btn-ghost border-2 border-[#4A70A9] dark:border-[#DFD0B8] rounded-full aspect-square w-12 h-12 sm:w-16 sm:h-16">
                        <Link class="text-[#4A70A9] dark:text-[#DFD0B8]" />
                    </button>

                    <div v-if="showToast" class="toast toast-end">
                        <div :class="['alert', toastType === 'success' ? 'alert-success' : 'alert-error']">
                            <span class="text-xl text-white">{{ toastMessage }}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>