<template>
  <!-- MODAL PRINCIPAL -->
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
        <button class="btn btn-primary btn-sm" @click="handleSubmit">
          Ajouter
        </button>
      </div>
    </div>
  </div>

  <!-- MODAL DE CONFIRMATION -->
  <div v-if="showConfirmationModal" class="modal-backdrop-custom">
    <div class="modal-card confirmation-modal">
      <h5 class="mb-3">Confirmer l'ajout</h5>

      <div class="confirmation-details">
        <h6 class="mb-3">Détails :</h6>
        <ul class="list-group list-group-flush">
          <li class="list-group-item d-flex justify-content-between">
            <span class="fw-medium">Code :</span>
            <span class="text-primary">{{ code }}</span>
          </li>
          <li class="list-group-item d-flex justify-content-between">
            <span class="fw-medium">Description :</span>
            <span>{{ description }}</span>
          </li>
        </ul>
      </div>

      <div class="d-flex justify-content-end gap-2 mt-3">
        <button 
          class="btn btn-outline-secondary btn-sm" 
          @click="cancelConfirmation"
          :disabled="confirming"
        >
          Annuler
        </button>
        <button 
          class="btn btn-primary btn-sm" 
          @click="confirmSubmit"
          :disabled="confirming"
        >
          <span 
            v-if="confirming" 
            class="spinner-border spinner-border-sm me-1" 
            role="status"
          ></span>
          {{ confirming ? 'Ajout en cours...' : 'Confirmer l\'ajout' }}
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

// États
const confirming = ref(false)
const showConfirmationModal = ref(false)

// Ouvrir la confirmation
const handleSubmit = () => {
  if (!code.value || !description.value) return
  showConfirmationModal.value = true
}

// Annuler la confirmation
const cancelConfirmation = () => {
  if (!confirming.value) {
    showConfirmationModal.value = false
  }
}

// Confirmer et envoyer
const confirmSubmit = () => {
  if (confirming.value) return
  
  confirming.value = true

  emit('submit', {
    code: code.value,
    description: description.value,
  })

  // Reset après emission
  code.value = ''
  description.value = ''
  showConfirmationModal.value = false
  confirming.value = false
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

.confirmation-modal {
  width: 450px;
}

.confirmation-details {
  margin-top: 10px;
}

.list-group-item {
  padding: 8px 0;
}

.fw-medium {
  font-weight: 500;
}

.text-primary {
  color: #0d6efd !important;
}

.d-flex {
  display: flex;
}

.gap-2 {
  gap: 0.5rem;
}

.justify-content-end {
  justify-content: flex-end;
}

.mt-3 {
  margin-top: 1rem;
}

.mb-3 {
  margin-bottom: 1rem;
}
</style>

