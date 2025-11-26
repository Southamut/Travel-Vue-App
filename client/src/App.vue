<script setup lang="ts">
import Navbar from './components/layout/Navbar.vue'
import Footer from './components/layout/Footer.vue'
</script>

<template>
  <div class="min-h-screen">
    <Navbar />
    <router-view />
    <Footer />
  </div>
</template>
