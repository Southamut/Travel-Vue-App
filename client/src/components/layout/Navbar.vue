<script setup lang="ts">
import { User, PlaneTakeoff, LogOut, Handbag } from 'lucide-vue-next';
import { useRouter } from 'vue-router';
import { ref, onMounted, onUnmounted } from 'vue';
import axios from 'axios';

const router = useRouter();

//Auth State
const isAuth = ref(false);
const user = ref<{ id: number, display_name: string | null, email: string } | null>(null);
const API_BASE = import.meta.env.VITE_API_BASE;

//Check token and fetch user info
const fetchUser = async () => {
    const token = localStorage.getItem('accessToken');
    if (!token) {
        isAuth.value = false;
        user.value = null;
        return;
    }

    try {
        const response = await axios.get(`${API_BASE}/auth/me`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });

        user.value = response.data; // { id, display_name, email }
        isAuth.value = true;
    } catch (err) {
        console.error('Failed to fetch user:', err);
        isAuth.value = false;
        user.value = null;
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
    }
};

//Handle logout
// --- Logout ---
const logout = async () => {
    const token = localStorage.getItem('accessToken');
    if (!token) return;

    try {
        await axios.post(`${API_BASE}/auth/logout`, {}, {
            headers: { Authorization: `Bearer ${token}` },
        });
    } catch (err) {
        console.warn('Logout API error, ignoring', err);
    } finally {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        isAuth.value = false;
        user.value = null;
        window.dispatchEvent(new Event("auth-changed"));
        router.push('/');
    }
};

//handle redirect
const goToHomepage = () => {
    router.push(`/`);
};
const goToLogin = () => router.push(`/login`);
const goToRegister = () => router.push(`/register`);

onMounted(() => {
    fetchUser();
    window.addEventListener("auth-changed", fetchUser);
});

onUnmounted(() => {
    window.removeEventListener("auth-changed", fetchUser);
});
</script>

<template>
    <div class="navbar bg-[#DEDED1] dark:bg-[#393E46] shadow-sm sticky top-0 z-50">
        <div class="flex-1">
            <a @click="goToHomepage" class="btn btn-ghost font-bold text-[#393E46] dark:text-[#DFD0B8] text-xl">
                <Handbag />
                Travel App
            </a>
        </div>
        <!-- Logged In -->
        <div v-if="isAuth" class="flex gap-2">
            <div class="dropdown dropdown-end">
                <div tabindex="0" role="button" class="btn btn-ghost btn-circle avatar">
                    <div class="w-10 rounded-full bg-gray-300 flex items-center justify-center text-gray-600">
                        {{ user?.display_name ? user.display_name[0].toUpperCase() : 'U' }}
                    </div>
                </div>
                <p tabindex="-1">{{ user?.display_name }}</p>
                <ul tabindex="-2"
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
                        <a class="text-lg text-red-400" @click="logout">
                            <LogOut class="h-5 w-5" />
                            Logout
                        </a>
                    </li>
                </ul>
            </div>
        </div>

        <!-- Not Logged In -->
        <div v-else class="flex gap-2">
            <button @click="goToLogin" class="btn btn-outline btn-sm">Login</button>
            <button @click="goToRegister" class="btn btn-primary btn-sm">Register</button>
        </div>
    </div>
</template>
