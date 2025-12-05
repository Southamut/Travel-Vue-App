<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import { User, Mail } from 'lucide-vue-next';

//auth
const auth = useAuthStore();
const router = useRouter();

interface UserProfile {
    id: number;
    displayName: string | null;
    email: string;
}

const user = ref<UserProfile>({
    id: 0,
    displayName: null,
    email: "",
});

const isLoading = ref(true);

onMounted(async () => {
    try {
        await auth.fetchUser();
        if (auth.user) {
            user.value = auth.user;
        }
    } catch (error) {
        console.error('Error fetching user data:', error);
    } finally {
        isLoading.value = false;
    }
});

function goToEditProfile() {
    router.push('/profile/edit');
}
</script>

<template>
    <div class="bg-[#EFECE3] dark:bg-[#222831] w-full font-prompt min-h-screen">
        <div class="max-w-7xl mx-auto">
            <!-- title -->
            <div class="flex flex-col items-center justify-center pt-15">
                <h1 class="text-5xl font-medium text-[#4A70A9] dark:text-[#DEDED1]">Your Profile</h1>
            </div>

            <!-- user info -->
            <div v-if="isLoading" class="flex justify-center items-center mt-20 min-h-[30vh]">
                <span class="loading loading-dots loading-xl text-[#4A70A9]"></span>
            </div>
            <div v-else>
                <div class="mt-10 flex flex-col items-center gap-2">
                    <!-- profile pic -->
                    <div class="avatar">
                        <div class="w-60 rounded-full bg-gray-300 flex items-center justify-center text-gray-600 mb-8">
                            {{ user?.displayName ? user.displayName[0].toUpperCase() : 'U' }}
                        </div>
                    </div>
                    <!-- display name -->
                    <div class="flex gap-2 items-center">
                        <User />
                        <p class="text-xl font-semibold text-[#4A70A9] dark:text-[#DEDED1]">
                            {{ user.displayName || 'No display name' }}
                        </p>
                    </div>

                    <!-- email -->
                    <div class="flex gap-2 items-center">
                        <Mail />
                        <p class="text-lg text-gray-700 dark:text-[#DFD0B8]">{{ user.email }}</p>
                    </div>

                    <!-- edit button -->
                    <button @click="goToEditProfile" class="btn bg-[#4A70A9] text-[#DEDED1] mt-4 rounded-full">
                        Edit Profile
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>
