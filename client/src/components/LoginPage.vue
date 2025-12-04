<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { useAuthStore } from '../stores/auth';

const router = useRouter();
const auth = useAuthStore();

// --- Form state ---
const email = ref('');
const password = ref('');
const errorMessage = ref('');
const isLoading = ref(false);

// --- Handle Login ---
const login = async () => {
    errorMessage.value = '';

    if (!email.value || !password.value) {
        errorMessage.value = 'Email and password are required.';
        return;
    }

    isLoading.value = true;
    try {
        const response = await axios.post(`${import.meta.env.VITE_API_BASE}/auth/login`, {
            email: email.value,
            password: password.value
        });

        // 👉 ใช้ Pinia แทน localStorage ตรง ๆ
        auth.setTokens(
            response.data.accessToken,
            response.data.refreshToken
        );

        // 👉 รอ fetchUser เสร็จ เพื่อให้ navbar ปีนทันที
        await auth.fetchUser();

        // Redirect to homepage or My Destinations
        router.push('/');
    } catch (err: any) {
        console.error(err);
        errorMessage.value = err.response?.data?.message || 'Invalid email or password.';
    } finally {
        isLoading.value = false;
    }
};

// --- Handle form submit ---
const handleSubmit = (e: Event) => {
    e.preventDefault();
    login();
};
</script>

<template>
    <div class="w-full min-h-screen flex flex-col justify-center items-center bg-[#EFECE3] dark:bg-[#222831] px-4">
        <form @submit="handleSubmit"
            class="w-full max-w-sm bg-base-200 border border-base-300 rounded-xl p-6 shadow-md">
            <h1 class="text-2xl text-center font-medium mb-4 text-[#4A70A9] dark:text-[#DEDED1]">Login</h1>

            <div class="mb-4">
                <label class="block text-gray-700 dark:text-[#DEDED1] mb-1">Email</label>
                <input type="email" v-model="email" class="input input-bordered w-full" placeholder="Email" required />
            </div>

            <div class="mb-4">
                <label class="block text-gray-700 dark:text-[#DEDED1] mb-1">Password</label>
                <input type="password" v-model="password" class="input input-bordered w-full" placeholder="Password"
                    required />
            </div>

            <p v-if="errorMessage" class="text-red-500 mb-2 text-sm">{{ errorMessage }}</p>

            <button type="submit" :disabled="isLoading" class="btn btn-neutral w-full mb-2">
                <span v-if="isLoading" class="loading loading-spinner loading-sm mr-2"></span>
                Login
            </button>

            <button type="reset" @click="() => { email = ''; password = ''; errorMessage = ''; }"
                class="btn btn-ghost w-full">Reset</button>

            <p class="mt-4 text-center text-sm">
                Don't have an account?
                <router-link to="/register" class="text-blue-500">Register</router-link>
            </p>
        </form>
    </div>
</template>
