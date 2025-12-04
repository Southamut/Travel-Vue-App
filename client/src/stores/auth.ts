import { defineStore } from 'pinia';
import { ref } from 'vue';
import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_BASE;

export const useAuthStore = defineStore('auth', () => {
    // --- State ---
    const isAuth = ref(false);
    const user = ref<{ id: number; displayName: string | null; email: string } | null>(null);

    // --- Fetch user from token ---
    const fetchUser = async () => {
        const token = localStorage.getItem('accessToken');
        if (!token) {
            isAuth.value = false;
            user.value = null;
            return;
        }

        try {
            const response = await axios.get(`${API_BASE}/auth/me`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            user.value = response.data;
            isAuth.value = true;
        } catch (err) {
            console.error('AuthStore fetchUser error:', err);
            logout();
        }
    };

    // --- Login (optional if needed later) ---
    const setTokens = (access: string, refresh: string) => {
        localStorage.setItem('accessToken', access);
        localStorage.setItem('refreshToken', refresh);
        fetchUser();
    };

    // --- Logout ---
    const logout = async () => {
        const token = localStorage.getItem('accessToken');

        try {
            await axios.post(`${API_BASE}/auth/logout`, {}, {
                headers: { Authorization: `Bearer ${token}` },
            });
        } catch (e) {
            console.warn("Logout error ignored");
        }

        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');

        user.value = null;
        isAuth.value = false;
    };

    // --- Expose values ---
    return {
        isAuth,
        user,
        fetchUser,
        logout,
        setTokens,
    };
});
