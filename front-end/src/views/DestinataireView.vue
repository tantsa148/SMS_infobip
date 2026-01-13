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

      <!-- PANNEAU DE FILTRES -->
      <div class="card-body border-bottom bg-light">
        <div class="row g-2 align-items-end">
          <div class="col">
            <label class="form-label small mb-1">Numéro</label>
            <input 
              v-model="filtres.numero" 
              type="text" 
              class="form-control form-control-sm" 
              placeholder="Rechercher..."
              @input="appliquerFiltres"
            />
          </div>
          
          <div class="col">
            <label class="form-label small mb-1">Date de début</label>
            <input 
              v-model="filtres.dateDebut" 
              type="date" 
              class="form-control form-control-sm"
              @change="appliquerFiltres"
            />
          </div>
          <div class="col">
            <label class="form-label small mb-1">Date de fin</label>
            <input 
              v-model="filtres.dateFin" 
              type="date" 
              class="form-control form-control-sm"
              @change="appliquerFiltres"
            />
          </div>
          <div class="col">
            <label class="form-label small mb-1">Plateforme</label>
            <select 
              v-model="filtres.plateforme" 
              class="form-select form-select-sm"
              @change="appliquerFiltres"
            >
              <option value="">Toutes</option>
              <option v-for="plateforme in plateformes" :key="plateforme" :value="plateforme">
                {{ plateforme }}
              </option>
            </select>
          </div>
          <div class="col-auto">
            <button class="btn btn-sm btn-secondary" @click="reinitialiserFiltres">
              <i class="fas fa-redo"></i> Réinitialiser
            </button>
          </div>
        </div>
      </div>

      <div class="card-body">
        <!-- AUCUN NUMERO -->
        <div v-if="numerosFiltre.length === 0" class="text-center py-4">
          <div class="text-muted mb-3">📱</div>
          <p class="text-muted mb-2">Aucun numéro trouvé</p>
        </div>

        <!-- TABLEAU -->
        <div v-else>
          <table class="table table-hover">
            <thead>
              <tr>
                <th></th>
                <th>Numéro</th>
                <th>Créé le</th>
                <th>Plateforme</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, index) in numerosFiltre" :key="row.idNumero">
                <td>{{ index + 1 }}</td>
                <td>{{ row.valeur }}</td>
                <td>{{ formatDate(row.dateCreation) }}</td>
                <td>{{ row.plateforme?.nomPlateforme ?? '-' }}</td>
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

      <div v-if="numerosFiltre.length > 0" class="card-footer">
        <small class="text-muted">
          Affichage : {{ numerosFiltre.length }} / {{ numeros.length }} numéro(s)
        </small>
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
import { ref, computed, onMounted } from 'vue'
import numeroDestinataireService from '../services/numeroDestinataireService'
import type { NumeroDestinataire } from '../types/NumeroDestinataire'
import ModalAddNumero from '../components/AddNumeroDestinataireModal.vue'
import ModalSendMessage from '../components/ModalSendMessage.vue'
import "../assets/css/destinataire.css"

const loading = ref(true)
const numeros = ref<NumeroDestinataire[]>([])
const numerosFiltre = ref<NumeroDestinataire[]>([])
const showAddModal = ref(false)
const apiMessage = ref('')

// Modal envoyer message
const showSendModal = ref(false)
const selectedNumero = ref<NumeroDestinataire | null>(null)

// Filtres
const filtres = ref({
  numero: '',
  plateforme: '',
  dateDebut: '',
  dateFin: ''
})

let timeoutId: number | null = null

// Liste unique des plateformes
const plateformes = computed(() => {
  const uniquePlateformes = [...new Set(
    numeros.value
      .map(n => n.plateforme?.nomPlateforme)
      .filter(Boolean)
  )]
  return uniquePlateformes.sort()
})

const appliquerFiltres = () => {
  numerosFiltre.value = numeros.value.filter(numero => {
    const matchNumero = !filtres.value.numero || 
      numero.valeur.includes(filtres.value.numero)
    
    const matchPlateforme = !filtres.value.plateforme || 
      numero.plateforme?.nomPlateforme === filtres.value.plateforme

    // Filtre par date
    const dateCreation = new Date(numero.dateCreation)
    const matchDateDebut = !filtres.value.dateDebut || 
      dateCreation >= new Date(filtres.value.dateDebut)
    
    const matchDateFin = !filtres.value.dateFin || 
      dateCreation <= new Date(filtres.value.dateFin + 'T23:59:59')

    return matchNumero && matchPlateforme && matchDateDebut && matchDateFin
  })
}

const reinitialiserFiltres = () => {
  filtres.value = {
    numero: '',
    plateforme: '',
    dateDebut: '',
    dateFin: ''
  }
  appliquerFiltres()
}

// Charger les numéros
const fetchData = async () => {
  loading.value = true
  try {
    const data = await numeroDestinataireService.getAll()
    numeros.value = data
    numerosFiltre.value = data
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
    const newNumero = response.data || response
    numeros.value.unshift(newNumero)
    appliquerFiltres()

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
</style>