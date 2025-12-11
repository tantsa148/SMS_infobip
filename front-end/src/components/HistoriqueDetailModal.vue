<template>
  <div v-if="show" class="modal-overlay" @click.self="close">
    <div class="modal-content">
      <div class="modal-header">
        <h5>Détails du SMS</h5>
      </div>

      <div class="modal-body" v-if="logDetail">
        <p><strong>Message ID:</strong> {{ logDetail.messageId }}</p>
        <p><strong>De:</strong> {{ logDetail.from }}</p>
        <p><strong>À:</strong> {{ logDetail.to }}</p>
        <p><strong>Texte:</strong> {{ logDetail.text }}</p>
        <p><strong>Envoyé à:</strong> {{ formatDate(logDetail.sentAt) }}</p>
        <p><strong>Livré à:</strong> {{ formatDate(logDetail.doneAt) }}</p>
        <p><strong>Status:</strong> {{ logDetail.status.groupName }} - {{ logDetail.status.description }}</p>
        <p><strong>Erreur:</strong> {{ logDetail.error.name }} - {{ logDetail.error.description }}</p>
        <p><strong>Prix:</strong> {{ logDetail.price.pricePerMessage }} {{ logDetail.price.currency }}</p>
      </div>

      <div class="modal-footer">
        <button class="btn btn-secondary btn-sm" @click="close">Fermer</button>
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

const close = () => emit('update:show', false)

function formatDate(date: string | Date) {
  return new Date(date).toLocaleString()
}
</script>

<style scoped>
.modal-overlay {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 1000;
}
.modal-content {
  background: #fff; border-radius: 6px; width: 450px; max-width: 90%; padding: 20px; box-shadow: 0 2px 10px rgba(0,0,0,0.3);
}
.modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.modal-body p { margin: 5px 0; }
.modal-footer { text-align: right; margin-top: 10px; }
.btn-close { background: none; border: none; font-size: 18px; cursor: pointer; }
</style>
