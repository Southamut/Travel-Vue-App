<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';

//auth
const auth = useAuthStore();
const router = useRouter();

onMounted(async () => {
    await auth.fetchUser();
    if (!auth.isAuth) router.push("/login");
});

const user = ref({
    id: 0,
    email: '',
    display_name: '',
    created_at: '',
});

const isLoading = ref(false);

function formattedDate(dateStr: string) {
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
    });
}

function goToEditProfile() {
    router.push('/profile/edit');
}
</script>

<template>
    <div class="bg-[#EFECE3] dark:bg-[#222831] w-full font-prompt min-h-screen">
        <div class="max-w-7xl mx-auto">
            <!-- title -->
            <div class="flex flex-col items-center justify-center pt-15">
                <h1 class="text-5xl font-medium text-[#4A70A9] dark:text-[#DEDED1]">Profile</h1>
            </div>

            <!-- user info -->
            <div v-if="isLoading" class="flex justify-center items-center mt-20 min-h-[30vh]">
                <span class="loading loading-dots loading-xl text-[#4A70A9]"></span>
            </div>
            <div v-else>
                <div class="mt-16 flex flex-col items-center gap-6">
                    <!-- display name -->
                    <div class="flex flex-col items-center">
                        <label class="text-lg lg:text-xl font-medium text-gray-500 dark:text-[#DFD0B8]">Display
                            Name</label>
                        <p class="text-2xl font-semibold text-[#4A70A9] dark:text-[#DEDED1]">
                            {{ user.display_name || 'No display name' }}
                        </p>
                    </div>

                    <!-- email -->
                    <div class="flex flex-col items-center">
                        <label class="text-lg lg:text-xl font-medium text-gray-500 dark:text-[#DFD0B8]">Email</label>
                        <p class="text-xl text-gray-700 dark:text-[#DFD0B8]">{{ user.email }}</p>
                    </div>

                    <!-- edit button -->
                    <button @click="goToEditProfile"
                        class="mt-6 px-6 py-2 rounded-lg text-white bg-[#4A70A9] hover:bg-[#3c5a8b] dark:bg-[#DEDED1] dark:text-[#222831] dark:hover:bg-[#cfc9b8] transition-colors duration-300">
                        Edit Profile
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>
