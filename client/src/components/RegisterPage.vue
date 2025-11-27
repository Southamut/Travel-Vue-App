<script setup lang="ts">
import { ref } from "vue";
import axios from "axios";

const API_BASE = import.meta.env.VITE_API_BASE || "";

const email = ref("");
const password = ref("");
const confirmPassword = ref("");

const isLoading = ref(false);
const errorMessage = ref("");

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

        const res = await axios.post(`${API_BASE}/auth/register`, {
            email: email.value,
            password: password.value,
        });

        console.log(res.data);

        alert("Registration successful!");
        email.value = "";
        password.value = "";
        confirmPassword.value = "";
    } catch (err: any) {
        errorMessage.value =
            err.response?.data?.message || "Registration failed.";
    } finally {
        isLoading.value = false;
    }
};
</script>

<template>
    <div class="w-full min-h-screen flex flex-col justify-center items-center bg-[#EFECE3] dark:bg-[#222831] px-4">
        <form @submit="handleSubmit"
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
