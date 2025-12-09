<template>
  <!-- MODAL DE VALIDATION -->
  <div v-if="showValidationModal" class="modal-overlay" @click.self="closeValidationModal">
    <div class="modal-content">
      <div class="modal-header">
        <h2>Confirmer l'envoi</h2>
        <button class="close-btn" @click="closeValidationModal">&times;</button>
      </div>
      <div class="modal-body">
        <p><strong>Type :</strong> {{ selectedPlatformName }}</p>
        <p><strong>Numéro expéditeur :</strong> {{ getNumeroExpediteur }}</p>
        <p><strong>Destinataire :</strong> {{ getDestinataireDisplay }}</p>
        <p><strong>Message :</strong> {{ messagePreview }}</p>
        <p>Voulez-vous vraiment envoyer ce message ?</p>
      </div>
      <div class="modal-actions">
        <button class="btn btn-submit" @click="sendMessage" :disabled="sending">
          <span v-if="sending" class="spinner-border spinner-border-sm me-2"></span>
          {{ sending ? 'Envoi...' : 'Oui, envoyer' }}
        </button>
        <button class="btn btn-cancel" @click="closeValidationModal" :disabled="sending">
          Annuler
        </button>
      </div>
    </div>
  </div>

  <!-- MODAL DE REPONSE API -->
  <div v-if="showResponseModal" class="modal-overlay" @click.self="closeResponseModal">
    <div class="modal-content">
      <div class="modal-body">
        <!-- Détails de l'envoi -->
        <div class="response-details">
          <h4>Détails de l'envoi :</h4>
          <div class="details-grid">
            <div class="detail-item">
              <span class="detail-label">Status :</span>
              <span class="detail-value">{{ apiResponse?.status }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Plateforme :</span>
              <span class="detail-value">{{ selectedPlatformName }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Expéditeur :</span>
              <span class="detail-value">{{ apiResponse?.expediteur || getNumeroExpediteur }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Destinataire :</span>
              <span class="detail-value">{{ apiResponse?.destinataire || getDestinataireDisplay }}</span>
            </div>
            <div v-if="apiResponse?.twilioResponse" class="detail-item">
              <span class="detail-label">ID Message :</span>
              <span class="detail-value code">{{ apiResponse.twilioResponse }}</span>
            </div>
            <div v-if="apiResponse?.sid" class="detail-item">
              <span class="detail-label">SID Twilio :</span>
              <span class="detail-value code">{{ apiResponse.sid }}</span>
            </div>
            <div v-if="apiResponse?.error" class="detail-item error">
              <span class="detail-label">Erreur :</span>
              <span class="detail-value">{{ apiResponse.error }}</span>
            </div>
            <div class="detail-item full-width">
              <span class="detail-label">Message :</span>
              <span class="detail-value message-text">{{ apiResponse?.message || message }}</span>
            </div>
          </div>
        </div>

        <!-- Informations supplémentaires -->
        <div v-if="apiResponse?.timestamp" class="additional-info">
          <small>Envoyé le : {{ formatDate(apiResponse.timestamp) }}</small>
        </div>
      </div>
      <div class="modal-actions">
        <button class="btn btn-submit" @click="handleResponseClose">
          {{ apiResponse?.status === 'success' ? 'Fermer' : 'Réessayer' }}
        </button>
        <button v-if="apiResponse?.status === 'success'" 
                class="btn btn-secondary" 
                @click="resetAndClose">
          Envoyer un autre message
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// Import des props et emits
const props = defineProps<{
  showValidationModal: boolean
  showResponseModal: boolean
  apiResponse: any
  selectedPlatformName: string
  getNumeroExpediteur: string
  getDestinataireDisplay: string
  message: string
  messagePreview: string
  sending: boolean
}>()

const emit = defineEmits<{
  'update:showValidationModal': [value: boolean]
  'update:showResponseModal': [value: boolean]
  'close-validation': []
  'close-response': []
  'send-message': []
  'handle-response-close': []
  'reset-and-close': []
}>()

// Méthodes pour fermer les modales
const closeValidationModal = () => {
  emit('update:showValidationModal', false)
  emit('close-validation')
}

const closeResponseModal = () => {
  emit('update:showResponseModal', false)
  emit('close-response')
}

// Méthodes pour les actions
const sendMessage = () => {
  emit('send-message')
}

const handleResponseClose = () => {
  emit('handle-response-close')
}

const resetAndClose = () => {
  emit('reset-and-close')
}

// Formatage de la date
const formatDate = (dateString: string) => {
  if (!dateString) return ''
  try {
    return new Date(dateString).toLocaleString('fr-FR')
  } catch {
    return dateString
  }
}
</script>

<style scoped>
/* Les styles sont déjà dans le fichier CSS principal */
</style>