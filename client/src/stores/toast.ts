// stores/toast.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useToastStore = defineStore('toast', () => {
  const show = ref(false)
  const message = ref('')
  const type = ref<'success' | 'error'>('success')

  function success(msg: string) {
    message.value = msg
    type.value = 'success'
    show.value = true
    setTimeout(() => (show.value = false), 2500)
  }

  function error(msg: string) {
    message.value = msg
    type.value = 'error'
    show.value = true
    setTimeout(() => (show.value = false), 2500)
  }

  return { show, message, type, success, error }
})
