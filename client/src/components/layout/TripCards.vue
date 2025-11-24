<script setup>
import { Link } from 'lucide-vue-next';
</script>

<template>
    <div class="card sm:card-side bg-[#DEDED1] dark:bg-gray-900 mx-10">
        <figure class="sm:w-80 aspect-4/3 p-6">
            <img src="https://img.daisyui.com/images/stock/photo-1635805737707-575885ab0820.webp" alt="Movie"
                className="w-full h-full object-cover rounded-xl" />
        </figure>
        <div class="card-body gap-4">
            <h2 class="card-title text-md md:text-lg lg:text-xl xl:text-2xl font-bold text-gray-800 dark:text-[#DEDED1]">Example Trip</h2>
            <p class="text-xs md:text-sm lg:text-md font-medium text-gray-500 dark:text-[#DFD0B8]">Description Trips</p>
            <ul class="flex flex-row gap-2 mb-4">
                <li class="badge badge-outline">
                    tag
                </li>
                <li class="badge badge-outline">
                    tag
                </li>
            </ul>
            <div class="card-actions justify-between items-end">
                <div class="grid grid-cols-3 gap-2">
                    <div class="col-span-1 aspect-square w-12 sm:w-16 lg:w-20"><img
                            src="https://img.daisyui.com/images/stock/photo-1635805737707-575885ab0820.webp" alt="Movie"
                            className="w-full h-full object-cover rounded-xl" />
                    </div>
                    <div class="col-span-1 aspect-square w-12 sm:w-16 lg:w-20"><img
                            src="https://img.daisyui.com/images/stock/photo-1635805737707-575885ab0820.webp" alt="Movie"
                            className="w-full h-full object-cover rounded-xl" />
                    </div>
                    <div class="col-span-1 aspect-square w-12 sm:w-16 lg:w-20"><img
                            src="https://img.daisyui.com/images/stock/photo-1635805737707-575885ab0820.webp" alt="Movie"
                            className="w-full h-full object-cover rounded-xl" />
                    </div>
                </div>
                <button class="btn btn-ghost border-2 border-[#4A70A9] rounded-full aspect-square w-12 h-12 sm:w-16 sm:h-16">
                    <Link class="text-[#4A70A9]" />
                </button>
            </div>
        </div>
    </div>
</template>