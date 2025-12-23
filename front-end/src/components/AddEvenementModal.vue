<template>
  <div v-if="show" class="modal-backdrop-custom">
    <div class="modal-card">
      <h5 class="mb-3">Ajouter un événement</h5>

      <input
        v-model="code"
        class="form-control mb-2"
        placeholder="Code événement"
      />

      <textarea
        v-model="description"
        class="form-control mb-3"
        placeholder="Description"
      ></textarea>

      <div class="d-flex justify-content-end gap-2">
        <button class="btn btn-secondary btn-sm" @click="$emit('close')">
          Annuler
        </button>
        <button class="btn btn-primary btn-sm" @click="submit">
          Ajouter
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

defineProps<{ show: boolean }>()
const emit = defineEmits(['close', 'submit'])

const code = ref('')
const description = ref('')

const submit = () => {
  if (!code.value || !description.value) return

  emit('submit', {
    code: code.value,
    description: description.value,
  })

  code.value = ''
  description.value = ''
}
</script>

<style scoped>
.modal-backdrop-custom {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-card {
  background: white;
  padding: 20px;
  width: 400px;
  border-radius: 8px;
}
</style>
