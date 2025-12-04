<script setup lang="ts">
import Navbar from './components/layout/Navbar.vue'
import Footer from './components/layout/Footer.vue'
import { useToastStore } from './stores/toast'

const toast = useToastStore()
</script>

<template>
  <div class="min-h-screen">
    <Navbar />
    <router-view />
    <Footer />
  </div>
    <!-- Toast Notification -->
  <div v-if="toast.show" class="fixed bottom-6 right-6 z-50">
    <div :class="['alert', toast.type === 'success' ? 'alert-success' : 'alert-error']">
      <span class="text-white">{{ toast.message }}</span>
    </div>
  </div>
</template>
