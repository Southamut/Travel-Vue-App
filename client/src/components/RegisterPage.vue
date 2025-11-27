<script setup lang="ts">
import { ref } from "vue";
import axios from "axios";
import { useRouter } from 'vue-router';

const API_BASE = import.meta.env.VITE_API_BASE || "";

const email = ref("");
const password = ref("");
const confirmPassword = ref("");

const isLoading = ref(false);
const errorMessage = ref("");
const isRegistrationSuccess = ref(false);
const router = useRouter();

const handleSubmit = async (e: Event) => {
    e.preventDefault();
    errorMessage.value = "";

    // --- Local Validation ---
    if (password.value !== confirmPassword.value) {
        errorMessage.value = "Passwords do not match.";
        return;
    }

    // Strong password validation
    const strongPasswordRegex = /(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}/;
    if (!strongPasswordRegex.test(password.value)) {
        errorMessage.value =
            "Password must contain uppercase, lowercase, number, and at least 8 characters.";
        return;
    }

    // --- Send to backend ---
    try {
        isLoading.value = true;

        await axios.post(`${API_BASE}/auth/register`, {
            email: email.value,
            password: password.value,
        });

        // 1. Set success state to true (This handles the UI change)
        isRegistrationSuccess.value = true;

        // 2. Clear fields
        email.value = "";
        password.value = "";
        confirmPassword.value = "";

    } catch (err: any) {
        // ... (Error handling) ...
    } finally {
        isLoading.value = false;
    }
};

// Function for the "Go to Login" button (you'll need to implement this navigation)
const goToLogin = () => router.push(`/login`);
</script>

<template>
    <div class="w-full min-h-screen flex flex-col justify-center items-center bg-[#EFECE3] dark:bg-[#222831] px-4">
        <div v-if="isRegistrationSuccess"
            class="w-full max-w-sm bg-base-100 border border-base-300 rounded-xl p-6 shadow-xl text-center">

            <svg xmlns="http://www.w3.org/2000/svg" class="stroke-current shrink-0 h-12 w-12 text-success mx-auto mb-4"
                fill="none" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>

            <h1 class="text-2xl font-bold text-[#4A70A9] dark:text-[#DEDED1] mb-2">
                Registration Successful!
            </h1>
            <p class="text-gray-600 dark:text-[#DFD0B8] mb-6">
                Your account has been created. You can now log in.
            </p>

            <button @click="goToLogin" class="btn bg-[#4A70A9] hover:bg-[#3d5e8c] text-white w-full">
                Go to Login
            </button>
        </div>

        <form v-else @submit="handleSubmit"
            class="w-full max-w-sm bg-base-200 border border-base-300 rounded-xl p-6 shadow-md">
            <h1 class="text-2xl text-center font-medium mb-4 text-[#4A70A9] dark:text-[#DEDED1]">
                Register
            </h1>

            <!-- Email -->
            <div class="mb-4">
                <label class="block text-gray-700 dark:text-[#DEDED1] mb-1">Email</label>
                <input type="email" v-model="email" class="input input-bordered w-full" placeholder="Email" required />
            </div>

            <!-- Password -->
            <div class="mb-4">
                <label class="block text-gray-700 dark:text-[#DEDED1] mb-1">Password</label>
                <input type="password" v-model="password" class="input input-bordered w-full" placeholder="Password"
                    required />
            </div>

            <!-- Confirm Password -->
            <div class="mb-4">
                <label class="block text-gray-700 dark:text-[#DEDED1] mb-1">Confirm Password</label>
                <input type="password" v-model="confirmPassword" :class="[
                    'input input-bordered w-full',
                    confirmPassword &&
                        confirmPassword !== password
                        ? 'input-error'
                        : ''
                ]" placeholder="Confirm Password" required />

                <!-- Real-time validation -->
                <p v-if="confirmPassword && confirmPassword !== password" class="text-red-500 text-sm mt-1">
                    Passwords do not match
                </p>
            </div>

            <!-- Error Message -->
            <p v-if="errorMessage" class="text-red-500 mb-2 text-sm">
                {{ errorMessage }}
            </p>

            <!-- Submit -->
            <button type="submit" :disabled="isLoading" class="btn btn-neutral w-full mb-2">
                <span v-if="isLoading" class="loading loading-spinner loading-sm mr-2"></span>
                Register
            </button>

            <!-- Reset -->
            <button type="reset" @click="
                () => {
                    email = '';
                    password = '';
                    confirmPassword = '';
                    errorMessage = '';
                }
            " class="btn btn-ghost w-full">
                Reset
            </button>
        </form>
    </div>
</template>
