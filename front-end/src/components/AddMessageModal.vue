<template>
  <!-- MODAL PRINCIPAL -->
  <div class="modal-overlay" v-if="show" @click.self="closeModal">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Ajouter un message</h5>
        
      </div>

      <form @submit.prevent="handleSubmit" class="modal-body">
        <!-- TEXTE MESSAGE -->
        <div class="mb-3">
          <label class="form-label">Texte du message</label>
          <textarea
            v-model="messageText"
            class="form-control"
            rows="4"
            placeholder="Ex: sms test"
            :disabled="submitting"
            required
          ></textarea>
          <small class="text-muted">
            {{ messageText.length }} / {{ maxLength }} caractères
          </small>
        </div>

        <!-- MESSAGE INFO -->
        <div
          v-if="message"
          :class="[
            'alert',
            messageType === 'error' ? 'alert-danger' : 'alert-success',
            'mt-3'
          ]"
        >
          {{ message }}
        </div>

        <!-- ACTIONS -->
        <div class="modal-actions d-flex justify-content-end gap-2 mt-3">
          <button
            type="button"
            class="btn btn-secondary"
            @click="closeModal"
            :disabled="submitting"
          >
            Annuler
          </button>

          <button
            type="submit"
            class="btn btn-primary"
            :disabled="!isFormValid || submitting"
          >
            <span
              v-if="submitting"
              class="spinner-border spinner-border-sm me-2"
            ></span>
            Valider
          </button>
        </div>
      </form>
    </div>
  </div>

  <!-- MODAL CONFIRMATION -->
  <div v-if="showConfirmationModal" class="modal-overlay confirmation-overlay">
    <div class="modal-content confirmation-modal">
      <div class="modal-header">
        <h5 class="modal-title">Confirmer l'ajout</h5>
        <button
          type="button"
          class="btn-close"
          @click="cancelConfirmation"
          :disabled="confirming"
        ></button>
      </div>

      <div class="modal-body">
        <div class="confirmation-content">
          <div class="text-center mb-3">
            <i class="bi bi-chat-dots text-primary" style="font-size: 3rem;"></i>
          </div>

          <p class="text-center mb-2">
            Voulez-vous ajouter le message suivant :
          </p>

          <div class="alert alert-light text-center">
            <strong>{{ messageText }}</strong>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button
          class="btn btn-outline-secondary"
          @click="cancelConfirmation"
          :disabled="confirming"
        >
          Non
        </button>

        <button
          class="btn btn-primary"
          @click="confirmSubmit"
          :disabled="confirming"
        >
          <span
            v-if="confirming"
            class="spinner-border spinner-border-sm me-2"
          ></span>
          Oui, ajouter
        </button>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, computed, watch } from 'vue'

/* PROPS & EMITS */
interface Props {
  show: boolean
}
interface Emits {
  (e: 'close'): void
  (e: 'submit', payload: { texte: string }): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

/* ETATS */
const messageText = ref('')
const submitting = ref(false)
const confirming = ref(false)
const showConfirmationModal = ref(false)

const message = ref('')
const messageType = ref<'success' | 'error'>('success')

const maxLength = 500

/* VALIDATION */
const isFormValid = computed(() => {
  return messageText.value.trim().length > 0 &&
         messageText.value.length <= maxLength
})

/* ACTIONS */
const closeModal = () => {
  if (!submitting.value && !confirming.value) {
    emit('close')
  }
}

const cancelConfirmation = () => {
  if (!confirming.value) {
    showConfirmationModal.value = false
  }
}

const handleSubmit = () => {
  if (!isFormValid.value) return
  submitting.value = true
  showConfirmationModal.value = true
  submitting.value = false
}

const confirmSubmit = () => {
  confirming.value = true

  emit('submit', {
    texte: messageText.value.trim()
  })

  message.value = 'Ajout en cours...'
  messageType.value = 'success'
}

/* RESET */
const resetForm = () => {
  messageText.value = ''
  submitting.value = false
  confirming.value = false
  showConfirmationModal.value = false
  message.value = ''
}

watch(() => props.show, (val) => {
  if (!val) resetForm()
})
</script>
