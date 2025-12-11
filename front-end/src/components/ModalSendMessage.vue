<template>
  <div v-if="show" class="modal-overlay" @click.self="closeModal">
    <div class="modal-content">

      <div class="modal-header">
        <h5 class="modal-title">Envoyer un message</h5>
      </div>
      <!-- TOAST NOTIFICATION -->


      <div class="modal-body">

      <!-- Info Destinataire -->
      <div v-if="numeroFromParent" class="mb-3">
        <strong>ID Destinataire :</strong> {{ numeroFromParent.idNumero }} <br>
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

        <!-- SELECT MESSAGE -->
        <div class="mb-3">
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

const showConfirmModal = ref(false)

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
}

const showConfirm = () => {
  if (!selectedNumero.value || !selectedMessage.value) {
    alert('Veuillez sélectionner un numéro et un message.')
    return
  }

  confirmationData.value = {
  idDestinataire: props.numeroFromParent!.idNumero,
  numeroDestinataire:
    props.numeroFromParent?.valeur
    ?? props.numeroFromParent?.numeroDestinataire
    ?? "",

  idExpediteur: selectedNumero.value.idNumero,
  numeroExpediteur: selectedNumero.value.numeroExpediteur ?? "",

  idMessage: selectedMessage.value.id,
  texteMessage: selectedMessage.value.texte ?? ""
}

  showConfirmModal.value = true
}

const finalSend = async () => {
  try {
    // Envoi du SMS
    const response = await smsService.sendSms({
      idNumeroExpediteur: confirmationData.value.idExpediteur,
      idNumeroDestinataire: confirmationData.value.idDestinataire,
      idMessage: confirmationData.value.idMessage
    });

    // Récupération des données
    const data = response.data;

    // Affichage du status et de la description
    alert(`Statut: ${data.statut}\nDescription: ${data.description || "Aucune description"}`);

    // Fermer modals
    showConfirmModal.value = false;
    closeModal();

  } catch (err: any) {
    console.error("Erreur lors de l'envoi :", err);

    // Si le serveur renvoie un JSON d'erreur
    if (err.response?.data) {
      const data = err.response.data;
      alert(`Erreur !\nStatut: ${data.statut || "ERROR"}\nDescription: ${data.description || "Aucune description"}`);
    } else {
      alert("Erreur lors de l'envoi du message.");
    }
  }
}
onMounted(() => {
  fetchNumeros()
  fetchMessages()
})
</script>
