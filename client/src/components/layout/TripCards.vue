<script setup lang="ts">
import { Link, ImageOff } from 'lucide-vue-next';
import { defineProps, defineEmits } from 'vue';
import { useRouter } from 'vue-router';
import { useToastStore } from '../../stores/toast'

// ----------------------------------------------------
// 1. Component ย่อย: LinkButton (สำหรับปุ่มคัดลอกลิงก์)
// ----------------------------------------------------

// Function for copy link
const toast = useToastStore()
const router = useRouter();



const copyToClipboard = async (id: number) => {
    try {
        const text = `${window.location.origin}/trips/${id}`;
        await navigator.clipboard.writeText(text);

        // แสดง Toast สำเร็จ
        toast.success('Link copied to clipboard!')

    } catch (error) {
        // แสดง Toast ล้มเหลว
        toast.error('Failed to copy link!')
    }
};

// ----------------------------------------------------
// 3. Component หลัก: TripSuggestList
// ----------------------------------------------------

// Prop Declaration (แทน props ใน React)
interface TripCardProps {
    toDisplay: Array<any>; // หรือ Array<TripResponse>
}

// กำหนด default value ให้กับ Prop
withDefaults(defineProps<TripCardProps>(), {
    toDisplay: () => [], // ต้องใช้ Factory function สำหรับ Object/Array defaults
});


// Event Declaration (แทน onTagClick ใน React)
const emit = defineEmits(['tag-clicked']);

// Handler สำหรับส่ง Tag ที่ถูกคลิกขึ้นไปให้ Component แม่
const handleTagClick = (tag: String) => {
    emit('tag-clicked', tag);
}

//handle go to trip detail
const goToTripDetail = (id: number) => {
    router.push(`/trips/${id}`);
};

</script>

<template>
    <div class="trip-cards-container grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 px-6">
        <div v-for="item in toDisplay" :key="item.id">
            <div @click="goToTripDetail(item.id)" class="card h-full rounded-2xl! bg-[#DEDED1] dark:bg-base-100
            transition-transform transform hover:-translate-y-2 hover:scale-105
            hover:shadow-xl duration-300 cursor-pointer">
                <figure class="aspect-video p-2 overflow-hidden relative">
                    <template v-if="item.photos[0]">
                        <img :src="item.photos[0]" alt="trip-image"
                            class="w-full h-full rounded-xl object-cover transition-transform duration-300 transform hover:scale-105"
                            @error="item.photos[0] = null" />
                    </template>
                    <template v-else>
                        <ImageOff class="w-16 h-16 text-gray-400 dark:text-gray-300" />
                    </template>
                </figure>

                <div class="card-body gap-4">
                    <h2 class="card-title text-md md:text-lg lg:text-xl xl:text-2xl font-bold text-gray-800 dark:text-[#DEDED1]"
                        :href="item.url">
                        {{ item.title }}</h2>
                    <p class="text-xs md:text-sm lg:text-md font-medium text-gray-500 dark:text-[#DFD0B8]">
                        {{ item.description.length > 100 ? item.description.slice(0, 100) + "..." : item.description }}
                    </p>
                    <ul class="flex flex-row flex-wrap gap-2 mb-4">
                        <template v-for="(tag) in item.tags" :key="index">
                            <li class="badge badge-outline transition-transform duration-300 transform hover:scale-105">
                                <button @click.stop="handleTagClick(tag)"
                                    class="text-xs md:text-sm lg:text-md font-medium text-gray-500 dark:text-[#DEDED1] cursor-pointer">
                                    {{ tag }} </button>
                            </li>
                        </template>
                    </ul>
                    <div class="card-actions justify-between items-end">
                        <div class="grid grid-cols-3 gap-2">
                            <div
                                class="col-span-1 aspect-square w-12 sm:w-16 lg:w-20 flex items-center justify-center bg-gray-200 dark:bg-gray-700 rounded-xl overflow-hidden">
                                <template v-if="item.photos[1]">
                                    <img :src="item.photos[1]" alt="sample-image-1"
                                        class="w-full h-full object-cover rounded-xl" @error="item.photos[1] = null" />
                                </template>
                                <template v-else>
                                    <ImageOff class="w-8 h-8 text-gray-400 dark:text-gray-300" />
                                </template>
                            </div>
                            <div
                                class="col-span-1 aspect-square w-12 sm:w-16 lg:w-20 flex items-center justify-center bg-gray-200 dark:bg-gray-700 rounded-xl overflow-hidden">
                                <template v-if="item.photos[2]">
                                    <img :src="item.photos[2]" alt="sample-image-1"
                                        class="w-full h-full object-cover rounded-xl" @error="item.photos[2] = null" />
                                </template>
                                <template v-else>
                                    <ImageOff class="w-8 h-8 text-gray-400 dark:text-gray-300" />
                                </template>
                            </div>
                            <div
                                class="col-span-1 aspect-square w-12 sm:w-16 lg:w-20 flex items-center justify-center bg-gray-200 dark:bg-gray-700 rounded-xl overflow-hidden">
                                <template v-if="item.photos[3]">
                                    <img :src="item.photos[3]" alt="sample-image-1"
                                        class="w-full h-full object-cover rounded-xl" @error="item.photos[3] = null" />
                                </template>
                                <template v-else>
                                    <ImageOff class="w-8 h-8 text-gray-400 dark:text-gray-300" />
                                </template>
                            </div>
                        </div>
                        <button @click.stop="copyToClipboard(item.id)"
                            class="btn btn-ghost tooltip tooltip-bottom tooltip-primary border-2 border-[#4A70A9] dark:border-[#DFD0B8] rounded-full aspect-square w-12 h-12 sm:w-16 sm:h-16"
                            data-tip="Copy Link">
                            <Link class="text-[#4A70A9] dark:text-[#DFD0B8]" />
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>