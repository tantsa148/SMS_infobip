<template>
  <div class="container mt-5">

    <!-- LOADING -->
    <div v-if="loading" class="text-center">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Chargement...</span>
      </div>
      <p class="mt-2 text-muted">Chargement de vos numéros...</p>
    </div>

    <!-- NOTIFICATION -->
    <div v-if="apiMessage" class="fixed-notification">
      <div class="notification-content">
        <div class="notification-header">
          <span class="notification-title"></span>
          <button class="notification-close" @click="apiMessage = ''">&times;</button>
        </div>
        <div class="notification-body">{{ apiMessage }}</div>
      </div>
    </div>

    <!-- CARD: NUMEROS -->
    <div v-else class="card shadow">
      <div class="card-header d-flex justify-content-between align-items-center">
        <div class="card-title mb-0">Numéros Enregistrés</div>
        <button class="btn btn-primary btn-sm" style="width: 100px" @click="showAddModal = true">
          Ajouter
        </button>
      </div>

      <div class="card-body">
        <!-- AUCUN NUMERO -->
        <div v-if="numeros.length === 0" class="text-center py-4">
          <div class="text-muted mb-3">📱</div>
          <p class="text-muted mb-2">Aucun numéro trouvé</p>
        </div>

        <!-- TABLEAU -->
        <div v-else>
          <table class="table table-hover">
            <thead>
              <tr>
                <th>#</th>
                <th>Numéro</th>
                <th>Créé le</th>
                <th>Plateforme</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, index) in numeros" :key="row.idNumero">
                <td>{{ index + 1 }}</td>
                <td>{{ row.valeur }}</td>
                <td>{{ formatDate(row.dateCreation) }}</td>
                
                <td>{{ row.plateforme ? row.plateforme.nomPlateforme : 'Aucune' }}</td>
                <td class="d-flex gap-2">
                  <button
                    class="btn btn-outline-secondary btn-sm"
                    title="Send Message"
                    @click="openSendModal(row)"
                  >
                    <i class="fa-solid fa-paper-plane"></i>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-if="numeros.length > 0" class="card-footer">
        <small class="text-muted">Total : {{ numeros.length }} numéro(s)</small>
      </div>
    </div>

    <!-- MODAL AJOUT -->
    <ModalAddNumero
      :show="showAddModal"
      @close="showAddModal = false"
      @submit="handleAddNumero"
    />

    <!-- MODAL ENVOYER MESSAGE -->
    <ModalSendMessage
      :show="showSendModal"
      :numeroFromParent="selectedNumero"
      @close="showSendModal = false"
      @send="handleSendMessage"
    />

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import numeroDestinataireService from '../services/numeroDestinataireService'
import type { NumeroDestinataire } from '../types/NumeroDestinataire'
import ModalAddNumero from '../components/AddNumeroDestinataireModal.vue'
import ModalSendMessage from '../components/ModalSendMessage.vue'
import "../assets/css/destinataire.css"

const loading = ref(true)
const numeros = ref<NumeroDestinataire[]>([])
const showAddModal = ref(false)
const apiMessage = ref('')

// Modal envoyer message
const showSendModal = ref(false)
const selectedNumero = ref<NumeroDestinataire | null>(null)

let timeoutId: number | null = null

// Charger les numéros
const fetchData = async () => {
  loading.value = true
  try {
    const data = await numeroDestinataireService.getAll()
    numeros.value = data
  } catch (err) {
    console.error('Erreur chargement des numéros :', err)
    showNotification('Erreur lors du chargement des numéros', 'error')
  } finally {
    loading.value = false
  }
}

function formatDate(date: string) {
  return new Date(date).toLocaleString()
}

// Notification
const showNotification = (message: string, type: 'success' | 'error' = 'success') => {
  apiMessage.value = message
  if (timeoutId) clearTimeout(timeoutId)
  timeoutId = setTimeout(() => apiMessage.value = '', 5000)
}

// Ajouter numéro
const handleAddNumero = async (payload: { valeur: string; plateforme?: { id: number } }) => {
  try {
    // Construire l'objet JSON conforme au backend
    const data = {
      valeur: payload.valeur,
      plateforme: payload.plateforme || null
    }

    console.log("Envoi POST :", data)

    // Appel au service
    const response = await numeroDestinataireService.addNumero(data)

    // Ajouter le nouveau numéro dans la liste
    numeros.value.unshift(response.data || response)

    showNotification(`Numéro ${payload.valeur} ajouté avec succès`, 'success')
    showAddModal.value = false

  } catch (err: any) {
    console.error('Erreur ajout numéro:', err)

    let errorMsg = 'Erreur lors de l\'ajout du numéro'
    if (err.response?.data?.message) errorMsg = err.response.data.message
    else if (err.message) errorMsg = err.message

    showNotification(errorMsg, 'error')
  }
}

// Envoyer message depuis le modal
const handleSendMessage = (payload: {
  idNumeroExpediteur: number
  numeroDestinataire: string
  message: string
}) => {
  console.log('Données à envoyer :', payload)
  showNotification('Message envoyé !', 'success')
}

// Ouvrir modal envoyer message
const openSendModal = (numero: NumeroDestinataire) => {
  selectedNumero.value = numero
  showSendModal.value = true
}

onMounted(fetchData)
</script>