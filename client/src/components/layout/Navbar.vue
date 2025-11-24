<script setup>
import { User, PlaneTakeoff, LogOut, Handbag } from 'lucide-vue-next';
</script>

<template>
    <div class="navbar bg-[#B6AE9F] dark:bg-[#393E46] shadow-sm">
        <div class="flex-1">
            <a class="btn btn-ghost font-bold text-[#393E46] dark:text-[#DFD0B8] text-xl">
                <Handbag />
                Travel App</a>
        </div>
        <div class="flex gap-2">
            <div class="dropdown dropdown-end">
                <div tabIndex={0} role="button" class="btn btn-ghost btn-circle avatar">
                    <div class="w-10 rounded-full">
                        <img alt="Tailwind CSS Navbar component"
                            src="https://img.daisyui.com/images/stock/photo-1534528741775-53994a69daeb.webp" />
                    </div>
                </div>
                <ul tabIndex="-1"
                    class="menu menu-sm dropdown-content bg-white dark:bg-[#393E46] rounded-box z-1 mt-3 w-52 p-2 shadow text-gray-600 dark:text-[#DFD0B8] font-medium">
                    <li>
                        <a class="text-lg">
                            <User class="h-5 w-5" />
                            Profile
                        </a>
                    </li>
                    <li><a class="text-lg">
                            <PlaneTakeoff class="h-5 w-5" />
                            My Trips
                        </a></li>
                    <li><a class="text-lg text-red-400">
                            <LogOut class="h-5 w-5" />
                            Logout
                        </a></li>
                </ul>
            </div>
        </div>
    </div>
</template>
