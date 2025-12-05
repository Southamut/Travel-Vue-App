<script setup lang="ts">
import { ref } from "vue";
import axios from "axios";
import { useAuthStore } from "../stores/auth";
import { useRouter } from 'vue-router';
import { useToastStore } from '../stores/toast'

const API_BASE = import.meta.env.VITE_API_BASE || "";

const auth = useAuthStore();
const router = useRouter();
const toast = useToastStore()

const displayName = ref(auth.user?.displayName ?? "");
const selectedFile = ref<File | null>(null);
const previewUrl = ref<string | null>(null);
const isSubmitting = ref(false);
const fileInputKey = ref(0);

function onFileChange(e: Event) {
    const input = e.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    selectedFile.value = input.files[0];
    previewUrl.value = URL.createObjectURL(selectedFile.value);
}

function removeImage() {
    previewUrl.value = null;
    selectedFile.value = null;

    fileInputKey.value++;
}

async function submitAll() {
    if (!auth.token) {
        toast.error("No token. Please login again.");
        return;
    }

    isSubmitting.value = true;

    try {
        // 1️⃣ Update username
        await axios.put(
            `${API_BASE}/auth/profile`,
            { displayName: displayName.value },
            { headers: { Authorization: `Bearer ${auth.token}` } }
        );

        auth.user.displayName = displayName.value;

        // 2️⃣ Upload image (ถ้ามีเลือกไฟล์)
        if (selectedFile.value) {
            const formData = new FormData();
            formData.append("file", selectedFile.value);

            await axios.post(`${API_BASE}/files/upload`, formData, {
                headers: {
                    Authorization: `Bearer ${auth.token}`,
                    "Content-Type": "multipart/form-data",
                },
            });
        }

        router.push('/profile');
         toast.success("Profile updated!");
    } catch (err) {
        console.error(err);
        toast.error("Failed to update profile");
    } finally {
        isSubmitting.value = false;
    }
}
</script>

<template>
    <div class="bg-[#EFECE3] dark:bg-[#222831] w-full font-prompt min-h-screen">
        <div class="max-w-3xl mx-auto">
            <div class="flex flex-col items-center justify-center pt-15 pb-8">
                <h1 class="text-5xl font-medium text-[#4A70A9] dark:text-[#DEDED1]">Edit Profile</h1>
            </div>

            <div class="card h-full rounded-2xl! bg-[#DEDED1] dark:bg-base-100 p-10">
                <div>
                    <label class="block text-lg text-[#4A70A9] dark:text-[#DEDED1] font-medium mb-2">Username</label>
                    <input v-model="displayName" type="text" class="w-full border rounded px-3 py-2" />
                </div>

                <div class="mt-8">
                    <label class="block text-lg text-[#4A70A9] dark:text-[#DEDED1] font-medium mb-2">
                        Profile Image
                    </label>

                    <input :key="fileInputKey" id="fileInput" type="file" accept="image/*"
                        class="file-input file-input-ghost" @change="onFileChange" />

                    <div v-if="previewUrl" class="mt-4 flex flex-col items-center gap-3">

                        <img :src="previewUrl" class="avatar w-40 rounded-full" />

                        <button class="btn btn-sm bg-red-500 text-white rounded-full" @click="removeImage">
                            Remove Image
                        </button>
                    </div>
                </div>


                <button @click="submitAll" class="btn bg-[#4A70A9] text-[#DEDED1] mt-4 rounded-full"
                    :disabled="isSubmitting">
                    <span v-if="isSubmitting" class="loading loading-spinner loading-sm mr-2"></span>
                    Submit
                </button>
            </div>
        </div>
    </div>
</template>
