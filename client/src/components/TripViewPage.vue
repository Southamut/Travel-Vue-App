<script setup lang="ts">
import { ImageOff, MapPin, ArrowLeft } from 'lucide-vue-next';
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_BASE || "";

// Router
const route = useRoute();
const router = useRouter();
const tripId = route.params.id;

// --- State Management ---
const trip = ref<any>(null);
const isLoading = ref(true);
const errorMessage = ref<string | null>(null);

// Reference for the Carousel Container
const carouselContainer = ref<HTMLDivElement | null>(null);

// Fetch trip detail
const getTripDetail = async () => {
    isLoading.value = true;
    try {
        const response = await axios.get(`${API_BASE}/trips/${tripId}`);
        trip.value = response.data;
    } catch (error) {
        console.error("Error fetching trip detail:", error);
        errorMessage.value = "Failed to load trip information.";
    } finally {
        isLoading.value = false;
    }
};

onMounted(() => {
    getTripDetail();
});

// Open Google Maps
const openGoogleMaps = () => {
    if (!trip.value?.latitude || !trip.value?.longitude) return;
    const url = `https://www.google.com/maps?q=${trip.value.latitude},${trip.value.longitude}`;
    window.open(url, '_blank');
};

// Function to handle horizontal scrolling 
const goToSlide = (targetIndex: number) => {
    const targetId = `slide${targetIndex}`;
    const targetElement = carouselContainer.value?.querySelector(`#${targetId}`);

    if (targetElement) {
        // Use scrollIntoView to scroll the element within its scrollable parent (the carousel)
        targetElement.scrollIntoView({
            behavior: 'smooth',
            inline: 'start', // Align the start edge of the item with the start edge of the container
            block: 'nearest' // Ensures no unnecessary vertical scroll is triggered
        });
    }
};
</script>

<template>
    <div class="bg-[#EFECE3] dark:bg-[#222831] min-h-screen font-prompt w-full">
        <div class="max-w-6xl mx-auto px-6 py-10">
            <!-- Back Button -->
            <button @click="router.back()"
                class="btn btn-ghost sm:text-xl lg:text-2xl text-[#4A70A9] dark:text-[#DEDED1] mb-6">
                <ArrowLeft class="w-5 h-5 mr-1" /> Back
            </button>

            <!-- Loading State -->
            <div v-if="isLoading" class="flex justify-center items-center min-h-[40vh]">
                <span class="loading loading-dots loading-lg text-[#4A70A9]"></span>
            </div>

            <!-- Error State -->
            <div v-else-if="errorMessage" class="flex justify-center items-center min-h-[40vh]">
                <p class="text-lg text-red-500">{{ errorMessage }}</p>
            </div>

            <!-- Trip Detail Content -->
            <div v-else>
                <h1 class="text-2xl md:text-4xl font-bold text-[#4A70A9] dark:text-[#DEDED1] mb-10 text-center">
                    {{ trip.title }}
                </h1>

                <!-- Image Gallery / Main Photo -->
                <div class="w-full flex justify-center mb-10">
                    <div
                        class="w-full aspect-video overflow-hidden rounded-2xl bg-gray-200 dark:bg-gray-700 flex items-center justify-center">
                        <template v-if="trip.photos && trip.photos.length > 0">
                            <div class="carousel w-full h-full snap-x snap-mandatory scroll-smooth"
                                ref="carouselContainer">
                                <div v-for="(photo, index) in trip.photos" :key="index" :id="`slide${index + 1}`"
                                    class="carousel-item w-full h-full relative snap-center">
                                    <img :src="photo" class="w-full h-full object-cover" />
                                    <div
                                        class="absolute left-5 right-5 top-1/2 flex -translate-y-1/2 transform justify-between">

                                        <div @click="goToSlide(index === 0 ? trip.photos.length : index)"
                                            class="btn btn-circle cursor-pointer z-10">
                                            ❮
                                        </div>

                                        <div @click="goToSlide(index + 2 > trip.photos.length ? 1 : index + 2)"
                                            class="btn btn-circle cursor-pointer z-10">
                                            ❯
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </template>
                        <template v-else>
                            <ImageOff class="w-20 h-20 text-gray-500 dark:text-gray-300" />
                        </template>
                    </div>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-10">
                    <!-- Left Side: Details -->
                    <div>
                        <h2 class="text-2xl font-semibold text-[#4A70A9] dark:text-[#DEDED1] mb-3">About This Place</h2>
                        <p class="text-gray-700 dark:text-[#DFD0B8] leading-relaxed whitespace-pre-line">
                            {{ trip.description || "No description available." }}
                        </p>

                        <!-- Province / Address -->
                        <div class="mt-6 flex items-center gap-2 text-gray-700 dark:text-[#DEDED1]">
                            <MapPin class="w-5 h-5" />
                            <span>{{ trip.province || "Unknown Location" }}</span>
                        </div>

                        <!-- Tags -->
                        <div v-if="trip.tags && trip.tags.length" class="mt-6 flex flex-wrap gap-2">
                            <span v-for="tag in trip.tags" :key="tag"
                                class="badge badge-outline text-gray-600 dark:text-[#DEDED1]">
                                {{ tag }}
                            </span>
                        </div>
                    </div>

                    <!-- Right Side: Google Map -->
                    <div>
                        <h2 class="text-2xl font-semibold text-[#4A70A9] dark:text-[#DEDED1] mb-3">Location</h2>

                        <template v-if="trip.latitude && trip.longitude">
                            <iframe
                                :src="`https://www.google.com/maps?q=${trip.latitude},${trip.longitude}&z=14&output=embed`"
                                class="w-full h-64 md:h-80 rounded-xl shadow-md border-2 border-gray-300 dark:border-gray-600">
                            </iframe>

                            <button @click="openGoogleMaps"
                                class="btn bg-[#4A70A9] text-[#DEDED1] mt-4 w-full rounded-full">
                                View on Google Maps
                            </button>
                        </template>
                        <template v-else>
                            <div
                                class="w-full h-64 bg-gray-300 dark:bg-gray-700 rounded-xl flex items-center justify-center">
                                <p class="text-gray-600 dark:text-[#DEDED1] font-medium text-center px-4">
                                    Map information not available for this destination.
                                </p>
                            </div>
                        </template>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.font-prompt {
    font-family: 'Prompt', sans-serif;
}
</style>
