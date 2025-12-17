<template>
  <div v-if="show" class="modal-overlay" @click.self="closeModal">
    <div class="modal-content confirmation-modal">
      <div class="modal-header">
        <h5 class="modal-title">Confirmer l'ajout</h5>
        <button type="button" class="btn-close" @click="closeModal" aria-label="Fermer"></button>
      </div>

      <div class="modal-body">
        <div class="confirmation-content">
          <div class="mb-4 text-center">
            <i class="bi bi-question-circle text-primary confirmation-icon"></i>
            <p class="confirmation-text">Voulez-vous vraiment ajouter ce numéro expéditeur ?</p>
          </div>

          <div class="confirmation-details">
            <h6 class="mb-3">Détails :</h6>
            <ul class="list-group list-group-flush">
              <li class="list-group-item d-flex justify-content-between">
                <span class="fw-medium">Numéro :</span>
                <span class="text-primary">{{ numero }}</span>
              </li>
              <li v-if="!isNewInfobip && selectedInfobip" class="list-group-item d-flex justify-content-between">
                <span class="fw-medium">Infobip :</span>
                <span>{{ selectedInfobip.baseUrl || `Infobip #${selectedInfobipId}` }}</span>
              </li>
              <li v-if="isNewInfobip" class="list-group-item d-flex justify-content-between">
                <span class="fw-medium">Nouvel Infobip :</span>
                <span>API Key: {{ newInfobip.apiKey.substring(0, 8) }}...</span>
              </li>
              <li v-if="idPlateforme" class="list-group-item d-flex justify-content-between">
                <span class="fw-medium">ID Plateforme :</span>
                <span>{{ idPlateforme }}</span>
              </li>
            </ul>
          </div>

          <div v-if="isNewInfobip" class="alert alert-warning mt-3">
            <i class="bi bi-exclamation-triangle me-2"></i>
            <small>Un nouvel Infobip sera créé avec les informations fournies.</small>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button type="button" class="btn btn-outline-secondary" @click="closeModal">
          Annuler
        </button>
        <button 
          type="button" 
          class="btn btn-primary" 
          @click="confirm"
          :disabled="isSubmitting"
        >
          <span v-if="isSubmitting" class="spinner-border spinner-border-sm me-1" role="status"></span>
          {{ isSubmitting ? 'Ajout en cours...' : 'Confirmer l\'ajout' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import type { UsersDetail } from '../types/UsersDetail'

interface NewInfobip {
  apiKey: string;
  baseUrl: string;
}

export default defineComponent({
  name: 'ConfirmationModal',
  props: {
    show: {
      type: Boolean,
      required: true
    },
    numero: {
      type: String,
      required: true
    },
    isNewInfobip: {
      type: Boolean,
      required: true
    },
    selectedInfobip: {
      type: Object as () => UsersDetail | null,
      default: null
    },
    selectedInfobipId: {
      type: Number as () => number | null,
      default: null
    },
    newInfobip: {
      type: Object as () => NewInfobip,
      required: true
    },
    idPlateforme: {
      type: Number as () => number | undefined,
      default: undefined
    },
    isSubmitting: {
      type: Boolean,
      default: false
    }
  },
  emits: ['update:show', 'confirm'],
  setup(props, { emit }) {
    const closeModal = () => {
      emit('update:show', false)
    }

    const confirm = () => {
      emit('confirm')
    }

    return {
      closeModal,
      confirm
    }
  }
})
</script>

<style scoped>
/* Ajoute ici les styles spécifiques au modal de confirmation si nécessaire */
</style>