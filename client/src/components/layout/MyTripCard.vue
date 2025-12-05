<script setup lang="ts">
import { ImageOff, SquarePen, Trash } from 'lucide-vue-next';
import { defineProps } from 'vue';
import { useRouter } from 'vue-router';
import DeleteModal from '../modal/DeleteModal.vue';
import { ref } from 'vue';
import axios from 'axios';
import { useAuthStore } from '../../stores/auth';
import { useToastStore } from '../../stores/toast'

const router = useRouter();
const showDeleteModal = ref(false);
const deleteTarget = ref<any>(null);
const toast = useToastStore()

const auth = useAuthStore();
const API_BASE = import.meta.env.VITE_API_BASE || "";

// Prop Declaration (แทน props ใน React)
interface TripCardProps {
    toDisplay: Array<any>; // หรือ Array<TripResponse>
}

// กำหนด default value ให้กับ Prop
const props = withDefaults(defineProps<TripCardProps>(), {
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

//handle edit trip
const editTrip = (id: number) => {
    router.push(`/my-trips/edit/${id}`);
};

//handle delete trip
const confirmDeleteTrip = async (item: any) => {
    if (!item) return;
    try {
        // ปิด modal ก่อนหรือหลังก็ได้
        const token = auth.token;
        if (!token) {
            toast.error("No token found, please login.");
            return;
        }

        await axios.delete(`${API_BASE}/trips/${item.id}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });

        // ถ้าลบสำเร็จ ให้ปิด modal และลบ item จาก list ทันที
        closeModal();

        // update local state: remove item จาก toDisplay
        const index = props.toDisplay.findIndex((i) => i.id === item.id);
        if (index > -1) props.toDisplay.splice(index, 1);

        toast.success("Trip deleted successfully");
    } catch (err) {
        console.error("Failed to delete trip:", err);
        toast.error("Failed to delete trip");

    }
};

const openModal = (item: any) => {
    deleteTarget.value = item;
    showDeleteModal.value = true;
};

const closeModal = () => {
    deleteTarget.value = null;
    showDeleteModal.value = false;
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
                    <div class="card-actions justify-between">
                        <button @click.stop="editTrip(item.id)"
                            class="btn btn-ghost tooltip tooltip-bottom tooltip-primary border-2 border-[#4A70A9] dark:border-[#DFD0B8] rounded-full aspect-square w-12 h-12 sm:w-16 sm:h-16"
                            data-tip="Edit Trip">
                            <SquarePen class="text-[#4A70A9] dark:text-[#DFD0B8]" />
                        </button>
                        <button @click.stop="openModal(item)"
                            class="btn btn-ghost tooltip tooltip-bottom tooltip-error border-2 border-red-400 rounded-full aspect-square w-12 h-12 sm:w-16 sm:h-16"
                            data-tip="Delete Trip">
                            <Trash class="text-red-400" />
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- Modal -->
    <DeleteModal :show="showDeleteModal" :itemTitle="deleteTarget?.title" @confirm="confirmDeleteTrip(deleteTarget)"
        @cancel="closeModal" />
</template>