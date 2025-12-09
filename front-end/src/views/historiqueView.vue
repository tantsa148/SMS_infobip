<template>
  <div class="container mt-5">
    <!-- LOADING -->
    <div v-if="loading" class="text-center">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Chargement...</span>
      </div>
      <p class="mt-2 text-muted">Chargement de l'historique...</p>
    </div>

    <!-- NOTIFICATION FIXE À DROITE -->
    <div v-if="apiMessage" class="fixed-notification">
      <div class="notification-content">
        <div class="notification-header">
          <span class="notification-title"></span>
          <button class="notification-close" @click="apiMessage = ''">&times;</button>
        </div>
        <div class="notification-body">{{ apiMessage }}</div>
      </div>
    </div>

    <!-- CARD -->
    <div v-else class="card shadow">
      <div class="card-header d-flex justify-content-between align-items-center">
        <div class="card-title mb-0">Historique SMS</div>
      
        <!-- FILTRE PAR DATE DROPDOWN -->
        <div class="filter-container mb-0 px-2 ">
          <div class="dropdown position-relative">
            <button 
              ref="dropdownButton" 
              class="btn btn-sm btn-outline-secondary" 
              @click="toggleFilterDropdown"
            >
              <i class="fas fa-filter"></i> Filtres
            </button>
            <div 
              v-show="showDropdown" 
              ref="dropdownMenu"
              class="filter-dropdown p-3 border rounded shadow bg-white mt-2"
            >
              <div class="filter-group mb-2">
                <label>Date début</label>
                <input type="date" class="form-control form-control-sm" v-model="dateDebut" />
              </div>
              <div class="filter-group mb-2">
                <label>Date fin</label>
                <input type="date" class="form-control form-control-sm" v-model="dateFin" />
              </div>
              <div class="d-flex justify-content-between mt-2">
                <button class="btn btn-sm btn-secondary" @click="resetFilters">Réinitialiser</button>
                <button class="btn btn-sm btn-primary" @click="filtrerHistorique">Appliquer</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="card-body">
        <!-- AUCUN LOG -->
        <div v-if="historiqueFiltre.length === 0" class="text-center py-4">
          <div class="text-muted mb-3">📩</div>
          <p class="text-muted mb-2">Aucun historique trouvé</p>
        </div>

        <!-- TABLEAU -->
        <div v-else class="table-responsive">
          <table class="table table-hover">
            <thead>
              <tr>
                <th></th>
                <th>Destinataire</th>
                <th>Status</th>
                <th>Twilio Error Code</th>
                <th>Plateforme</th>
                <th>Date</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(log, index) in historiqueFiltre" :key="log.log_id">
                <td>{{ index + 1 }}</td>
                <td>{{ log.destinataire_numero }}</td>
                <td>{{ log.status }}</td>
                <td>{{ log.twilio_error_code || '-' }}</td>
                <td>{{ log.platform || '-' }}</td>
                <td>{{ formatDate(log.created_at) }}</td>
                <td>
                  <button class="btn btn-sm btn-outline-secondary" @click="ouvrirModal(log.log_id)">
                    <i class="fas fa-eye"></i>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-if="historiqueFiltre.length > 0" class="card-footer">
        <small class="text-muted">Total : {{ historiqueFiltre.length }} SMS</small>
      </div>
    </div>

    <!-- MODAL DETAIL -->
    <HistoriqueDetailModal
      :show="showDetailModal"
      :logDetail="selectedLogDetail"
      @update:show="showDetailModal = $event"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, onBeforeUnmount } from 'vue'
import { getHistoriqueSms } from '../services/historiqueService'
import { getHistoriqueSmsById } from '../services/historiqueDetailService'
import type { SmsResponseLog } from '../types/historique'
import type { historiqueDetail } from '../types/historiqueDetail'
import HistoriqueDetailModal from '../components/HistoriqueDetailModal.vue'

const loading = ref(true)
const historique = ref<SmsResponseLog[]>([])
const historiqueFiltre = ref<SmsResponseLog[]>([])
const apiMessage = ref('')

const dateDebut = ref<string | null>(null)
const dateFin = ref<string | null>(null)
const showDropdown = ref(false)

// Références pour le dropdown
const dropdownButton = ref<HTMLElement | null>(null)
const dropdownMenu = ref<HTMLElement | null>(null)

let timeoutId: number | null = null

// Modal
const showDetailModal = ref(false)
const selectedLogDetail = ref<historiqueDetail | null>(null)

// Fetch historique
const fetchHistorique = async () => {
  try {
    historique.value = await getHistoriqueSms()
    historiqueFiltre.value = historique.value  // Affiche tout au départ
  } catch (error) {
    console.error('Impossible de charger l\'historique', error)
    apiMessage.value = 'Erreur lors du chargement de l\'historique.'
    if (timeoutId) clearTimeout(timeoutId)
    timeoutId = setTimeout(() => (apiMessage.value = ''), 3000)
  } finally {
    loading.value = false
  }
}

// Toggle dropdown
const toggleFilterDropdown = () => {
  showDropdown.value = !showDropdown.value
}

// Fermer le dropdown si on clique en dehors
const handleClickOutside = (event: MouseEvent) => {
  if (
    showDropdown.value && 
    dropdownButton.value && 
    dropdownMenu.value &&
    !dropdownButton.value.contains(event.target as Node) &&
    !dropdownMenu.value.contains(event.target as Node)
  ) {
    showDropdown.value = false
  }
}

// Réinitialiser filtre
const resetFilters = () => {
  dateDebut.value = null
  dateFin.value = null
  historiqueFiltre.value = historique.value
  showDropdown.value = false
}

// Filtrer par date
const filtrerHistorique = () => {
  const debut = dateDebut.value ? new Date(dateDebut.value) : null
  const fin = dateFin.value ? new Date(dateFin.value) : null

  historiqueFiltre.value = historique.value.filter(log => {
    const logDate = new Date(log.created_at)

    if (debut && logDate < debut) return false
    if (fin) {
      const finInclusive = new Date(fin)
      finInclusive.setHours(23, 59, 59)
      if (logDate > finInclusive) return false
    }
    return true
  })

  showDropdown.value = false  // ferme le dropdown après application
}

function formatDate(date: string) {
  return new Date(date).toLocaleString()
}

const ouvrirModal = async (id: number) => {
  try {
    selectedLogDetail.value = await getHistoriqueSmsById(id)
    showDetailModal.value = true
  } catch (err) {
    console.error('Erreur chargement détail SMS', err)
    apiMessage.value = 'Erreur lors du chargement du détail.'
    if (timeoutId) clearTimeout(timeoutId)
    timeoutId = setTimeout(() => (apiMessage.value = ''), 3000)
  }
}

onMounted(() => {
  fetchHistorique()
  // Ajouter l'écouteur d'événement pour les clics en dehors
  document.addEventListener('click', handleClickOutside)
})

onBeforeUnmount(() => {
  // Nettoyer l'écouteur d'événement
  document.removeEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  if (timeoutId) clearTimeout(timeoutId)
})
</script>

<style scoped>
.table-responsive { overflow-x: auto; }

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

.filter-dropdown {
  position: absolute;
  z-index: 1000;
  min-width: 220px;
  top: 100%; /* juste en dessous du bouton */
  right: 0;  /* aligne la fin du dropdown avec la fin du bouton */
}
</style>