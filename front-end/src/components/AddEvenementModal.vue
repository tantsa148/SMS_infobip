<template>
  <!-- MODAL PRINCIPAL -->
  <div v-if="show" class="modal-overlay" @click.self="closeModal">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Ajouter un événement</h5>
      </div>

      <form @submit.prevent="handleSubmit" class="modal-body">
        <div class="mb-3">
          <label class="form-label">Code événement</label>
          <input
            v-model="code"
            type="text"
            class="form-control"
            placeholder="Entrez le code événement"
            required
          />
        </div>

        <div class="mb-3">
          <label class="form-label">Description</label>
          <textarea
            v-model="description"
            class="form-control"
            placeholder="Entrez la description"
            rows="3"
            required
          ></textarea>
        </div>

        <!-- Message d'erreur/succès -->
        <div
          v-if="message"
          :class="['alert', messageType === 'error' ? 'alert-danger' : 'alert-success']"
          role="alert"
        >
          {{ message }}
        </div>

        <div class="modal-footer">
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
            <span v-if="submitting" class="spinner-border spinner-border-sm me-2"></span>
            {{ submitting ? 'Validation...' : 'Ajouter' }}
          </button>
        </div>
      </form>
    </div>
  </div>

  <!-- MODAL DE CONFIRMATION -->
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
            <i class="bi bi-question-circle text-primary" style="font-size: 3rem;"></i>
          </div>
          
          <p class="text-center mb-3">
            Êtes-vous sûr de vouloir ajouter cet événement ?
          </p>
          
          <div class="alert alert-light">
            <div class="mb-2">
              <strong>Code :</strong> <span class="text-primary">{{ code }}</span>
            </div>
            <div class="mb-0">
              <strong>Description :</strong> {{ description }}
            </div>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button 
          type="button" 
          class="btn btn-outline-secondary" 
          @click="cancelConfirmation"
          :disabled="confirming"
        >
          Non, revenir
        </button>
        <button 
          type="button" 
          class="btn btn-primary" 
          @click="confirmSubmit"
          :disabled="confirming"
        >
          <span v-if="confirming" class="spinner-border spinner-border-sm me-2"></span>
          Oui, ajouter
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const props = defineProps<{ show: boolean }>()
const emit = defineEmits(['close', 'submit'])

// Données du formulaire
const code = ref('')
const description = ref('')

// États
const submitting = ref(false)
const confirming = ref(false)
const message = ref('')
const messageType = ref<'success' | 'error'>('success')
const showConfirmationModal = ref(false)

// Validation
const isFormValid = computed(() => {
  return code.value.trim() !== '' && description.value.trim() !== ''
})

// Fermer le modal principal
const closeModal = () => {
  if (!submitting.value && !confirming.value) {
    emit('close')
    resetForm()
  }
}

// Annuler la confirmation
const cancelConfirmation = () => {
  if (!confirming.value) {
    showConfirmationModal.value = false
  }
}

// Réinitialiser le formulaire
const resetForm = () => {
  code.value = ''
  description.value = ''
  message.value = ''
  showConfirmationModal.value = false
  submitting.value = false
  confirming.value = false
}

// Étape 1 : Validation et affichage de la confirmation
const handleSubmit = () => {
  if (!isFormValid.value || submitting.value) return
  
  submitting.value = true
  
  try {
    console.log('📋 Formulaire validé, affichage de la confirmation')
    showConfirmationModal.value = true
  } catch (err: any) {
    console.error('Erreur validation:', err)
    showMessage(err.message || 'Erreur de validation', 'error')
  } finally {
    submitting.value = false
  }
}

// Étape 2 : Confirmation et envoi au parent
const confirmSubmit = () => {
  if (confirming.value) return
  
  confirming.value = true
  console.log('✅ Confirmation, envoi des données au parent...')
  
  try {
    emit('submit', {
      code: code.value,
      description: description.value,
    })
    
    showMessage('Événement ajouté avec succès !', 'success')
    resetForm()
    
  } catch (err: any) {
    console.error('Erreur dans confirmSubmit:', err)
    showMessage('Erreur lors de l\'ajout', 'error')
    confirming.value = false
  }
}

// Afficher un message
const showMessage = (msg: string, type: 'success' | 'error') => {
  message.value = msg
  messageType.value = type
  
  setTimeout(() => {
    if (message.value === msg) message.value = ''
  }, type === 'error' ? 7000 : 3000)
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1050;
  padding: 1rem;
}

.modal-content {
  background: white;
  border-radius: 0.5rem;
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
}

.modal-header {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #dee2e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-title {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 500;
}

.modal-body {
  padding: 1.5rem;
}

.modal-footer {
  padding: 1rem 1.5rem;
  border-top: 1px solid #dee2e6;
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
}

/* Styles spécifiques pour le modal de confirmation */
.confirmation-overlay {
  z-index: 1060;
}

.confirmation-modal {
  max-width: 450px;
}

.confirmation-content {
  text-align: center;
}

.btn-close {
  background: none;
  border: none;
  font-size: 1.25rem;
  line-height: 1;
  color: #6c757d;
  cursor: pointer;
  padding: 0.5rem;
  margin: -0.5rem -0.5rem -0.5rem auto;
}

.btn-close:hover {
  color: #000;
}

.btn-close:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.text-primary {
  color: #0d6efd !important;
}

/* Responsive */
@media (max-width: 576px) {
  .modal-content {
    max-width: 95%;
  }
}
</style>

