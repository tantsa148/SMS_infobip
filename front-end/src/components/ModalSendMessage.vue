<template>
  <div v-if="show" class="modal-overlay" @click.self="closeModal">
    <div class="modal-content">

      <div class="modal-header">
        <h5 class="modal-title">Envoyer un message</h5>
      </div>

      <div class="modal-body">

      <!-- Info Destinataire -->
      <div v-if="numeroFromParent" class="mb-3">
        <strong>Numéro :</strong> {{ numeroFromParent.valeur || numeroFromParent.numeroDestinataire }}
      </div>

        <!-- SELECT NUMERO EXPEDITEUR -->
        <div class="mb-3">
          <label class="form-label">Numéro Expéditeur</label>
          <select v-model="selectedNumero" class="form-select" required>
            <option disabled value="">-- Choisir un numéro --</option>
            <option
              v-for="num in numeros"
              :key="num.idNumero"
              :value="num"
            >
              {{ num.numeroExpediteur }}
            </option>
          </select>
        </div>

        <!-- CHECKBOX: UTILISER UN MESSAGE PERSONNALISÉ -->
        <div class="mb-3">
          <div class="form-check">
            <input 
              type="checkbox" 
              v-model="useCustomMessage" 
              id="customMessageCheck" 
              class="form-check-input"
            >
            <label class="form-check-label" for="customMessageCheck">
              Utiliser un message personnalisé
            </label>
          </div>
        </div>

        <!-- CHAMP DE TEXTE: MESSAGE PERSONNALISÉ (si coché) -->
        <div v-if="useCustomMessage" class="mb-3">
          <label class="form-label">Message personnalisé</label>
          <textarea
            v-model="customMessageText"
            class="form-control"
            rows="4"
            placeholder="Entrez votre message..."
          ></textarea>
        </div>

        <!-- SELECT MESSAGE (si non coché) -->
        <div v-else class="mb-3">
          <label class="form-label">Message</label>
          <select v-model="selectedMessage" class="form-select" required>
            <option disabled value="">-- Choisir un message --</option>
            <option
              v-for="msg in messages"
              :key="msg.id"
              :value="msg"
            >
              {{ msg.texte }}
            </option>
          </select>
        </div>

      </div>

      <div class="modal-footer">
        <button class="btn btn-secondary" @click="closeModal">Fermer</button>
        <button class="btn btn-primary" @click="showConfirm()">Envoyer</button>
      </div>

    </div>
  </div>

  <!-- MODAL DE CONFIRMATION -->
  <ConfirmSendModal
    :show="showConfirmModal"
    :data="confirmationData"
    @close="showConfirmModal = false"
    @confirm="finalSend"
  />

  <!-- Notification toast -->
  <div v-if="notificationMessage" class="fixed-notification">
    <div class="notification-content">
      <div class="notification-header">
        <span class="notification-title"></span>
        <button class="notification-close" @click="notificationMessage = ''">&times;</button>
      </div>
      <div class="notification-body" :class="notificationType === 'error' ? 'text-danger' : 'text-success'">
        {{ notificationMessage }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

import messageService from '../services/messageService'
import UsersDetailService from '../services/usersDetailService'

import ConfirmSendModal from './ConfirmSendModal.vue'

import type { MessageTexte } from '../types/MessageTexte'
import type { UsersDetail } from '../types/UsersDetail'

import smsService from "../services/smsService"
import "../assets/css/modalsendSms.css"
const props = defineProps<{
  show: boolean,
  numeroFromParent: {
    idNumero: number,
    valeur?: string,
    numeroDestinataire?: string
  } | null
}>()

const emit = defineEmits(['close', 'send'])

const numeros = ref<UsersDetail[]>([])
const selectedNumero = ref<UsersDetail | null>(null)

const messages = ref<MessageTexte[]>([])
const selectedMessage = ref<MessageTexte | null>(null)
const useCustomMessage = ref(false)
const customMessageText = ref("")

const showConfirmModal = ref(false)

// Notification variables
const notificationMessage = ref('')
const notificationType = ref<'success' | 'error'>('success')
let notificationTimeout: number | null = null

// Fonction pour afficher une notification
const showNotification = (message: string, type: 'success' | 'error') => {
  notificationMessage.value = message
  notificationType.value = type
  
  if (notificationTimeout) clearTimeout(notificationTimeout)
  notificationTimeout = setTimeout(() => {
    notificationMessage.value = ''
  }, 3000)
}

const confirmationData = ref({
  idDestinataire: 0,
  idExpediteur: 0,
  idMessage: 0,
  numeroDestinataire: "",
  numeroExpediteur: "",
  texteMessage: ""
})

const fetchNumeros = async () => {
  try {
    numeros.value = await UsersDetailService.getAll()
  } catch (err) {
    console.error('Erreur chargement numéros :', err)
  }
}

const fetchMessages = async () => {
  try {
    const response = await messageService.getAll()
    messages.value = response.data
  } catch (err) {
    console.error('Erreur chargement messages :', err)
  }
}

const closeModal = () => {
  emit('close')
  selectedNumero.value = null
  selectedMessage.value = null
  useCustomMessage.value = false
  customMessageText.value = ""
}

const showConfirm = () => {
  if (!selectedNumero.value) {
    showNotification('Veuillez sélectionner un numéro expéditeur.', 'error')
    return
  }

  // Vérifier si message personnalisé ou prédéfini
  if (useCustomMessage.value) {
    if (!customMessageText.value.trim()) {
      showNotification('Veuillez entrer un message personnalisé.', 'error')
      return
    }
  } else {
    if (!selectedMessage.value) {
      showNotification('Veuillez sélectionner un message.', 'error')
      return
    }
  }

  confirmationData.value = {
  idDestinataire: props.numeroFromParent!.idNumero,
  numeroDestinataire:
    props.numeroFromParent?.valeur
    ?? props.numeroFromParent?.numeroDestinataire
    ?? "",

  idExpediteur: selectedNumero.value.idNumero,
  numeroExpediteur: selectedNumero.value.numeroExpediteur ?? "",

  idMessage: useCustomMessage.value ? 0 : selectedMessage.value!.id,
  texteMessage: useCustomMessage.value ? customMessageText.value : (selectedMessage.value!.texte ?? "")
}

  showConfirmModal.value = true
}

const finalSend = async () => {
  try {
    let response;
    
    if (useCustomMessage.value) {
      response = await smsService.sendSmsWithMessage({
        idNumeroExpediteur: confirmationData.value.idExpediteur,
        idNumeroDestinataire: confirmationData.value.idDestinataire,
        message: confirmationData.value.texteMessage
      });
    } else {
      response = await smsService.sendSms({
        idNumeroExpediteur: confirmationData.value.idExpediteur,
        idNumeroDestinataire: confirmationData.value.idDestinataire,
        idMessage: confirmationData.value.idMessage
      });
    }

    // Récupération des données
    const data = response.data;

    // Affichage de la notification de succès
    showNotification(`Message envoyé avec succès ! Statut: ${data.statut}`, 'success');

    // Fermer modals
    showConfirmModal.value = false;
    closeModal();

  } catch (err: any) {
    console.error("Erreur lors de l'envoi :", err);

    // Si le serveur renvoie un JSON d'erreur
    if (err.response?.data) {
      const data = err.response.data;
      showNotification(`Erreur: ${data.description || data.statut || "Erreur lors de l'envoi"}`, 'error');
    } else {
      showNotification("Erreur lors de l'envoi du message.", 'error');
    }
  }
}
onMounted(() => {
  fetchNumeros()
  fetchMessages()
})
</script>

<style scoped>
.fixed-notification {
  position: fixed;
  top: 80px;
  right: 20px;
  z-index: 999;
}

.notification-content {
  background: #f8f9fa;
  border: 1px solid #ccc;
  border-radius: 6px;
  padding: 10px;
  width: 300px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.2);
}

.notification-close {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
}

.text-success {
  color: #198754;
}

.text-danger {
  color: #dc3545;
}
</style>

