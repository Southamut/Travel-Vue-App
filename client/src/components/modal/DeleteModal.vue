<script setup lang="ts">
import { defineProps, defineEmits } from 'vue';

const props = defineProps<{
  show: boolean;
  itemTitle?: string;
}>();

const emit = defineEmits<{
  (e: 'confirm'): void;
  (e: 'cancel'): void;
}>();
</script>

<template>
  <div v-if="show" class="fixed inset-0 flex items-center justify-center z-50">
    <div class="fixed inset-0 bg-black/50" @click="$emit('cancel')"></div>
    <div class="bg-[#DEDED1] dark:bg-base-100 card rounded-xl p-6 mx-6 sm:mx-0 max-w-96 z-50">
      <h3 class="text-xl font-bold dark:text-white mb-4">Delete "{{ itemTitle }}"?</h3>
      <p class="mb-6 text-gray-500">Are you sure you want to delete this item? This action cannot be undone.</p>
      <div class="flex justify-end gap-4">
        <button class="btn" @click="$emit('cancel')">Cancel</button>
        <button class="btn btn-error" @click="$emit('confirm')">Delete</button>
      </div>
    </div>
  </div>
</template>
