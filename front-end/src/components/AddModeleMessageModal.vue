<template>
  <div v-if="show" class="modal-backdrop-custom">
    <div class="modal-custom">
      <!-- HEADER -->
      <div class="modal-header">
        <h5 class="modal-title">Ajouter un modèle de message</h5>
      </div>

      <!-- BODY -->
      <div class="modal-body">
        <!-- Select Méthode -->
        <div class="mb-3">
          <label for="methodeSelect" class="form-label">Méthode</label>
          <select id="methodeSelect" class="form-select" v-model="form.methode">
            <option value="" disabled>Sélectionner une méthode</option>
            <option v-for="ctrl in controllers" :key="ctrl.url" :value="ctrl.method">
              {{ ctrl.method }} ({{ ctrl.controller }})
            </option>
          </select>
        </div>

        <!-- Select Numéro Expéditeur -->
        <div class="mb-3">
          <label for="numeroSelect" class="form-label">Numéro Expéditeur</label>
          <select id="numeroSelect" class="form-select" v-model="form.idExpediteur">
            <option value="" disabled>Sélectionner un numéro</option>
            <option v-for="num in numeros" :key="num.idNumero" :value="num.idNumero">
              {{ num.numeroExpediteur }}
            </option>
          </select>
        </div>

        <!-- Select Texte Message -->
        <div class="mb-3">
          <label for="texteSelect" class="form-label">Texte du message</label>
          <select id="texteSelect" class="form-select" v-model="form.texteMessage">
            <option value="" disabled>Sélectionner un message</option>
            <option v-for="msg in messages" :key="msg.id" :value="msg.texte">
              {{ msg.texte }}
            </option>
          </select>
        </div>
      </div>

      <!-- FOOTER -->
      <div class="modal-footer">
        <button class="btn btn-secondary" @click="$emit('close')">Annuler</button>
        <button class="btn btn-primary" @click="openConfirmation">Enregistrer</button>
      </div>
    </div>
  </div>

  <!-- MODAL DE CONFIRMATION -->
  <div v-if="showConfirmModal" class="modal-backdrop-custom">
    <div class="modal-custom">
      <div class="modal-header">
        <h5 class="modal-title">Confirmer l'envoi</h5>
      </div>
      <div class="modal-body">
        <p><strong>Méthode :</strong> {{ confirmationData.methode }}</p>
        <p><strong>Message :</strong> {{ confirmationData.message }}</p>
      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary" @click="showConfirmModal = false">Annuler</button>
        <button class="btn btn-primary" @click="confirmSubmit">Confirmer</button>
      </div>
    </div>
  </div>

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
import { ref, reactive, onMounted } from 'vue'
import ControllerService from '../services/ControllerService'
import type { ControllerInfo } from '../services/ControllerService'
import UsersDetailService from '../services/usersDetailService'
import type { UsersDetail } from '../types/UsersDetail'
import MessageService from '../services/messageService'
import type { MessageTexte } from '../types/MessageTexte'
import ModeleMessageService from '../services/modeleMessageService'

defineProps<{ show: boolean }>()
const emit = defineEmits(['close', 'submit'])

// Formulaire réactif
const form = reactive({
  methode: '',
  idExpediteur: null as number | null,
  texteMessage: ''
})

// Données select
const controllers = ref<ControllerInfo[]>([])
const numeros = ref<UsersDetail[]>([])
const messages = ref<MessageTexte[]>([])

// Modal de confirmation
const showConfirmModal = ref(false)
const confirmationData = reactive({
  methode: '',
  numero: '',
  message: '',
  idNumero: null as number | null,
  idMessage: null as number | null
})

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

// Charger les données
onMounted(async () => {
  try {
    controllers.value = await ControllerService.getControllers()
    numeros.value = await UsersDetailService.getAll()
    const res = await MessageService.getAll()
    messages.value = res.data
  } catch (err) {
    console.error('Erreur récupération données pour le modal:', err)
  }
})

// Ouvrir modal confirmation
const openConfirmation = () => {
  if (!form.methode || !form.idExpediteur || !form.texteMessage) {
    showNotification('Veuillez remplir tous les champs !', 'error')
    return
  }

  const numeroObj = numeros.value.find(n => n.idNumero === form.idExpediteur)
  const messageObj = messages.value.find(m => m.texte === form.texteMessage)

  confirmationData.methode = form.methode
  confirmationData.numero = numeroObj ? numeroObj.numeroExpediteur : ''
  confirmationData.message = form.texteMessage
  confirmationData.idNumero = form.idExpediteur
  confirmationData.idMessage = messageObj ? messageObj.id : null

  showConfirmModal.value = true
}

// Confirmer et envoyer à l'API
const confirmSubmit = async () => {
  const payload = {
    methode: confirmationData.methode,
    idExpediteur: confirmationData.idNumero!,
    idMessage: confirmationData.idMessage!
  }

  try {
    const res = await ModeleMessageService.create(payload)
    console.log('Modèle ajouté :', res)
    showNotification('Modèle ajouté avec succès !', 'success')
    showConfirmModal.value = false
    emit('submit', { ...form }) // notifie le parent
    emit('close') // ferme le modal d'ajout
  } catch (err) {
    console.error('Erreur création modèle :', err)
    showNotification('Erreur lors de l\'ajout du modèle.', 'error')
  }
}
</script>

<style scoped>
.modal-backdrop-custom {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1050;
}

.modal-custom {
  background: #fff;
  border-radius: 8px;
  width: 600px;
  max-width: 95%;
  box-shadow: 0 5px 20px rgba(0,0,0,0.3);
}

.modal-header,
.modal-footer {
  padding: 12px 16px;
  border-bottom: 1px solid #dee2e6;
}

.modal-footer {
  border-top: 1px solid #dee2e6;
  border-bottom: none;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.modal-body {
  padding: 16px;
}

.btn-close {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
}

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
