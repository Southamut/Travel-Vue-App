import { defineStore } from "pinia";
import { ref } from "vue";
import axios from "axios";

const API_BASE = import.meta.env.VITE_API_BASE;

export const useAuthStore = defineStore("auth", () => {
  // --- State ---
  const isAuth = ref(false);
  const user = ref<{
    id: number;
    displayName: string | null;
    email: string;
  } | null>(null);
  const token = ref<string | null>(localStorage.getItem("accessToken"));

  // NEW: track if fetchUser has already been called
  const userLoaded = ref(false);

  // --- Fetch user from token ---
  const fetchUser = async () => {
    if (!token.value) {
      isAuth.value = false;
      user.value = null;
      userLoaded.value = true; // <-- Mark as loaded
      return;
    }

    try {
      const response = await axios.get(`${API_BASE}/auth/me`, {
        headers: { Authorization: `Bearer ${token.value}` },
      });
      user.value = response.data;
      isAuth.value = true;
    } catch (err) {
      isAuth.value = false;
      user.value = null;
    }

    userLoaded.value = true; // <-- Fetch finished (success or fail)
  };

  // --- Login ---
  const setTokens = (access: string, refresh: string) => {
    localStorage.setItem("accessToken", access);
    localStorage.setItem("refreshToken", refresh);
    token.value = access;
    fetchUser();
  };

  // --- Logout ---
  const logout = async () => {
    try {
      await axios.post(
        `${API_BASE}/auth/logout`,
        {},
        {
          headers: { Authorization: `Bearer ${token.value}` },
        }
      );
    } catch (e) {
      console.warn("Logout error ignored");
    }

    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");

    token.value = null;
    user.value = null;
    isAuth.value = false;
    userLoaded.value = false;
  };

  return {
    isAuth,
    user,
    token,
    fetchUser,
    logout,
    setTokens,
    userLoaded, // <-- Export this
  };
});
