<script setup lang="ts">
import { User, PlaneTakeoff, LogOut, Handbag } from 'lucide-vue-next';
import { useRouter } from 'vue-router';
import { onMounted } from 'vue';
import { useAuthStore } from '../../stores/auth';

const router = useRouter();
const auth = useAuthStore();

// Initialize auth on page load
onMounted(() => {
    auth.fetchUser();
});
</script>

<template>
    <div class="navbar bg-[#DEDED1] dark:bg-[#393E46] shadow-sm sticky top-0 z-50">
        <div class="flex-1">
            <a @click="router.push('/')" class="btn btn-ghost font-bold text-[#393E46] dark:text-[#DFD0B8] text-xl">
                <Handbag />
                Travel App
            </a>
        </div>

        <!-- Logged In -->
        <div v-if="auth.isAuth" class="flex gap-2">
            <div class="dropdown dropdown-end">
                <div tabindex="0" role="button" class="btn btn-ghost btn-circle avatar">
                    <div class="w-10 rounded-full bg-gray-300 flex items-center justify-center text-gray-600">
                        {{ auth.user?.display_name ? auth.user.display_name[0].toUpperCase() : 'U' }}
                    </div>
                </div>

                <ul tabindex="0"
                    class="menu menu-sm dropdown-content bg-[#EFECE3] dark:bg-[#393E46] rounded-box mt-3 w-52 p-2 shadow text-gray-600 dark:text-[#DFD0B8] font-medium">

                    <li>
                        <a class="text-lg">
                            <User class="h-5 w-5" />
                            Profile
                        </a>
                    </li>

                    <li>
                        <a class="text-lg" @click="router.push('/my-trips')">
                            <PlaneTakeoff class="h-5 w-5" />
                            My Trips
                        </a>
                    </li>

                    <li>
                        <a class="text-lg text-red-400" @click="auth.logout">
                            <LogOut class="h-5 w-5" />
                            Logout
                        </a>
                    </li>

                </ul>
            </div>
        </div>

        <!-- Not Logged In -->
        <div v-else class="flex gap-2">
            <button @click="router.push('/login')" class="btn btn-outline btn-sm">Login</button>
            <button @click="router.push('/register')" class="btn btn-primary btn-sm">Register</button>
        </div>
    </div>
</template>
