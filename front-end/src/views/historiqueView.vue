<template>
  <div class="container mt-5">
    <!-- LOADING -->
    <div v-if="loading" class="text-center">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Chargement...</span>
      </div>
      <p class="mt-2 text-muted">Chargement de l'historique...</p>
    </div>

    <!-- NOTIFICATION FIXE -->
    <div v-if="apiMessage" class="fixed-notification">
      <div class="notification-content">
        <div class="notification-header">
          <span class="notification-title"></span>
          <button class="notification-close" @click="apiMessage = ''">&times;</button>
        </div>
        <div class="notification-body">{{ apiMessage }}</div>
      </div>
    </div>

    <!-- CARD HISTORIQUE -->
    <div v-else class="card shadow">
      <div class="card-header d-flex justify-content-between align-items-center">
        <div class="card-title mb-0">Historique SMS</div>
        <button class="btn btn-primary btn-sm" style="width: 100px" @click="ouvrirModalExport">
          Exporter
        </button>
      </div>

      <!-- PANNEAU DE FILTRES -->
      <div class="card-body border-bottom bg-light">
        <div class="row g-2 align-items-end">
          <div class="col">
            <label class="form-label small mb-1">Numéro Expéditeur</label>
            <input 
              v-model="filtres.numeroExpediteur" 
              type="text" 
              class="form-control form-control-sm" 
              placeholder="Rechercher..."
              @input="appliquerFiltres"
            />
          </div>
          <div class="col">
            <label class="form-label small mb-1">Destinataire</label>
            <input 
              v-model="filtres.numeroDestinataire" 
              type="text" 
              class="form-control form-control-sm" 
              placeholder="Rechercher..."
              @input="appliquerFiltres"
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
          <div class="col-auto">
            <button class="btn btn-sm btn-secondary" @click="reinitialiserFiltres">
              <i class="fas fa-redo"></i> Réinitialiser
            </button>
          </div>
        </div>
      </div>

      <div class="card-body">
        <!-- AUCUN LOG -->
        <div v-if="historiqueFiltre.length === 0" class="text-center py-4">
          <p class="text-muted mb-2">Aucun historique trouvé</p>
        </div>

        <!-- TABLEAU -->
        <div v-else class="table-responsive">
          <table class="table table-hover">
            <thead>
              <tr>
                <th></th>
                <th>Expéditeur</th>
                <th>Numéro Expediteur</th>
                <th>Destinataire</th>
                <th>Plateforme</th>
                <th>Date</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(log, index) in historiqueFiltre" :key="log.idEnvoi">
                <td>{{ index + 1 }}</td>
                <td>{{ log.expediteur }}</td>
                <td>{{ log.numeroExpediteur }}</td>
                <td>{{ log.numeroDestinataire }}</td>
                <td>{{ log.plateforme }}</td>
                <td>{{ formatDate(log.dateEnvoi) }}</td>
                <td>
                  <button class="btn btn-sm btn-outline-secondary" @click="ouvrirModal(log.idEnvoi)">
                    <i class="fas fa-eye"></i>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-if="historiqueFiltre.length > 0" class="card-footer">
        <small class="text-muted">
          Affichage : {{ historiqueFiltre.length }} / {{ historique.length }} SMS
        </small>
      </div>
    </div>

    <!-- MODAL DETAIL -->
    <HistoriqueDetailModal
      :show="showDetailModal"
      :logDetail="selectedLogDetail"
      :idEnvoi="selectedIdEnvoi"   
      @update:show="showDetailModal = $event"
    />

    <!-- MODAL CONFIRMATION EXPORT -->
    <ExportConfirmModal
      :show="showExportModal"
      :itemCount="historiqueFiltre.length"
      @confirm="confirmerExport"
      @cancel="fermerModalExport"
    />

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { getHistoriqueSmsById } from '../services/historiqueDetailService'
import type { SmsResponseLog } from '../types/historique'
import type { historiqueDetail } from '../types/historiqueDetail'
import HistoriqueDetailModal from '../components/HistoriqueDetailModal.vue'
import ExportConfirmModal from '../components/ExportConfirmModal.vue'
import { getHistoriqueSms, exportHistoriqueCsv } from '../services/historiqueService'


const loading = ref(true)
const historique = ref<SmsResponseLog[]>([])
const historiqueFiltre = ref<SmsResponseLog[]>([])
const apiMessage = ref('')

const showDetailModal = ref(false)
const selectedLogDetail = ref<historiqueDetail | null>(null)
const selectedIdEnvoi = ref<number | null>(null)

const showExportModal = ref(false)

const filtres = ref({
  numeroExpediteur: '',
  numeroDestinataire: '',
  plateforme: '',
  dateDebut: '',
  dateFin: ''
})

let timeoutId: number | null = null

// Liste unique des plateformes
const plateformes = computed(() => {
  const uniquePlateformes = [...new Set(historique.value.map(log => log.plateforme))]
  return uniquePlateformes.sort()
})

function formatDate(date: string | Date) {
  return new Date(date).toLocaleString()
}

const appliquerFiltres = () => {
  historiqueFiltre.value = historique.value.filter(log => {
    const matchNumeroExp = !filtres.value.numeroExpediteur || 
      log.numeroExpediteur.includes(filtres.value.numeroExpediteur)
    
    const matchNumeroDest = !filtres.value.numeroDestinataire || 
      log.numeroDestinataire.includes(filtres.value.numeroDestinataire)
    
    const matchPlateforme = !filtres.value.plateforme || 
      log.plateforme === filtres.value.plateforme

    // Filtre par date
    const dateLog = new Date(log.dateEnvoi)
    const matchDateDebut = !filtres.value.dateDebut || 
      dateLog >= new Date(filtres.value.dateDebut)
    
    const matchDateFin = !filtres.value.dateFin || 
      dateLog <= new Date(filtres.value.dateFin + 'T23:59:59')

    return matchNumeroExp && matchNumeroDest && matchPlateforme && matchDateDebut && matchDateFin
  })
}

const chargerHistorique = async () => {
  const data = await getHistoriqueSms()
  console.log(data)
}

const ouvrirModalExport = () => {
  showExportModal.value = true
}

const fermerModalExport = () => {
  showExportModal.value = false
}

const confirmerExport = async () => {
  try {
    await exportHistoriqueCsv()
    fermerModalExport()
    apiMessage.value = 'Export CSV réussi !'
    if (timeoutId) clearTimeout(timeoutId)
    timeoutId = setTimeout(() => (apiMessage.value = ''), 3000)
  } catch (error) {
    console.error('Erreur lors de l\'export:', error)
    apiMessage.value = 'Erreur lors de l\'export CSV.'
    if (timeoutId) clearTimeout(timeoutId)
    timeoutId = setTimeout(() => (apiMessage.value = ''), 3000)
  }
}

const reinitialiserFiltres = () => {
  filtres.value = {
    numeroExpediteur: '',
    numeroDestinataire: '',
    plateforme: '',
    dateDebut: '',
    dateFin: ''
  }
  appliquerFiltres()
}

const fetchHistorique = async () => {
  try {
    historique.value = await getHistoriqueSms()
    historiqueFiltre.value = historique.value
  } catch (error) {
    console.error('Erreur chargement historique:', error)
    apiMessage.value = 'Erreur lors du chargement de l\'historique.'
    if (timeoutId) clearTimeout(timeoutId)
    timeoutId = setTimeout(() => (apiMessage.value = ''), 3000)
  } finally {
    loading.value = false
  }
}

const ouvrirModal = async (idEnvoi: number) => {
  try {
    selectedLogDetail.value = await getHistoriqueSmsById(idEnvoi)
    selectedIdEnvoi.value = idEnvoi
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
})

onBeforeUnmount(() => {
  if (timeoutId) clearTimeout(timeoutId)
})
</script>

<style scoped>
.table-responsive { overflow-x: auto; }
.fixed-notification {
  position: fixed; top: 80px; right: 20px; z-index: 999;
}
.notification-content {
  background: #f8f9fa; border: 1px solid #ccc; border-radius: 6px; padding: 10px; width: 300px; box-shadow: 0 2px 6px rgba(0,0,0,0.2);
}
.notification-close { background: none; border: none; font-size: 18px; cursor: pointer; }
</style>