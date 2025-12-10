<template>
  <div class="modal-overlay" v-if="show" @click.self="closeModal">
    <div class="modal-content">
      <div class="modal-header">
        <h2>Détails du Message</h2>
      </div>

      <div class="modal-body" v-if="logDetail">
        <p><strong>Expéditeur :</strong> {{ logDetail.utilisateur }}</p>
        <p><strong>Expéditeur numero :</strong> {{ logDetail.expediteurNumero }}</p>
        <p><strong>Destinataire :</strong> {{ logDetail.destinataireNumero }}</p>
        <p><strong>Message :</strong> {{ logDetail.messageTexte }}</p>
        <p><strong>Status :</strong> {{ logDetail.status }}</p>
        <p><strong>Twilio Error Code :</strong> {{ logDetail.twilioErrorCode ?? '-' }}</p>
        <p><strong>Twilio Error Message :</strong> {{ logDetail.twilioErrorMessage ?? '-' }}</p>
        <p v-if="logDetail.twilioErrorMoreInfo">
          <strong>Twilio More Info :</strong>
          <a :href="logDetail.twilioErrorMoreInfo" target="_blank">{{ logDetail.twilioErrorMoreInfo }}</a>
        </p>
        <p><strong>Plateforme :</strong> {{ logDetail.platform ?? '-' }}</p>
        <p><strong>Créé le :</strong> {{ formatDate(logDetail.createdAt) }}</p>
      </div>

      <div class="modal-footer">
        <button class="btn-cancel" @click="closeModal">Fermer</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { historiqueDetail } from '../types/historiqueDetail'

const props = defineProps<{
  show: boolean
  logDetail: historiqueDetail | null
}>()

const emit = defineEmits<{
  (e: 'update:show', value: boolean): void
}>()

function closeModal() {
  emit('update:show', false)
}

function formatDate(date: string) {
  return new Date(date).toLocaleString()
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1050;
}
.modal-content {
  background: #fff;
  border-radius: 6px;
  max-width: 500px;
  width: 100%;
  padding: 1rem;
  margin: 1rem;
  max-height: 90vh;
  overflow-y: auto;
}
.modal-header, .modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.btn-cancel {
  background: #ccc;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
}
</style>
