<template>
  <div class="container mt-5">

    <!-- LOADING -->
    <div v-if="loading" class="text-center">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-2 text-muted">Chargement des modèles...</p>
    </div>

    <!-- NOTIFICATION FIXE -->
    <div v-if="apiMessage" class="fixed-notification">
      <div class="notification-content">
        <div class="notification-header">
          <span class="notification-title">Notification</span>
          <button class="notification-close" @click="apiMessage = ''">&times;</button>
        </div>
        <div class="notification-body">{{ apiMessage }}</div>
      </div>
    </div>

    <!-- CARD -->
    <div v-else class="card shadow">
      <div class="card-header d-flex justify-content-between align-items-center">
        <div class="card-title mb-0">Liste des modèles de message</div>
        <button
          class="btn btn-primary btn-sm"
          style="width: 100px"
          @click="showAddModal = true"
        >
          Ajouter
        </button>
      </div>

      <!-- PANNEAU DE FILTRES -->
      <div class="card-body border-bottom bg-light">
        <div class="row g-2 align-items-end">
          <div class="col">
            <label class="form-label small mb-1">Méthode</label>
            <select 
              v-model="filtres.methode" 
              class="form-select form-select-sm"
              @change="appliquerFiltres"
            >
              <option value="">Toutes</option>
              <option v-for="methode in methodes" :key="methode" :value="methode">
                {{ methode }}
              </option>
            </select>
          </div>
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
            <label class="form-label small mb-1">Texte Message</label>
            <input 
              v-model="filtres.texteMessage" 
              type="text" 
              class="form-control form-control-sm" 
              placeholder="Rechercher..."
              @input="appliquerFiltres"
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
        <!-- AUCUN MODELE -->
        <div v-if="modelesFiltres.length === 0" class="text-center py-4">
        
          <p class="text-muted mb-2">Aucun modèle trouvé</p>
        </div>

        <!-- TABLEAU -->
        <div v-else class="table-responsive">
          <table class="table table-hover w-100">
            <thead>
              <tr>
                <th></th>
                <th>Méthode</th>
                <th>Expéditeur</th>
                <th>Texte</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(m, index) in modelesFiltres" :key="m.id">
                <td>{{ index + 1 }}</td>
                <td>{{ m.methode }}</td>
                <td>{{ m.valeurExpediteur }}</td>
                <td>{{ m.texteMessage }}</td>
                <td>
                    <button class="btn btn-sm btn-outline-secondary" >
                      <i class="fas fa-trash-alt"></i>
                    </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-if="modelesFiltres.length > 0" class="card-footer">
        <small class="text-muted">
           {{ modelesFiltres.length }} / {{ modeles.length }} modele
        </small>
      </div>
    </div>

    <!-- MODAL AJOUT -->
    <AddModeleMessageModal
      :show="showAddModal"
      @close="showAddModal = false"
      @submit="handleAddModele"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import ModeleMessageService from '../services/modeleMessageService'
import type { ModeleMessageDTO } from '../types/ModeleMessage'
import AddModeleMessageModal from '../components/AddModeleMessageModal.vue'

const loading = ref(true)
const modeles = ref<ModeleMessageDTO[]>([])
const modelesFiltres = ref<ModeleMessageDTO[]>([])
const apiMessage = ref('')
const showAddModal = ref(false)

const filtres = ref({
  methode: '',
  numeroExpediteur: '',
  texteMessage: ''
})

let timeoutId: number | null = null

// Liste unique des méthodes
const methodes = computed(() => {
  const uniqueMethodes = [...new Set(modeles.value.map(m => m.methode))]
  return uniqueMethodes.sort()
})

const appliquerFiltres = () => {
  modelesFiltres.value = modeles.value.filter(modele => {
    const matchMethode = !filtres.value.methode || 
      modele.methode === filtres.value.methode
    
    const matchNumeroExp = !filtres.value.numeroExpediteur || 
      modele.valeurExpediteur.includes(filtres.value.numeroExpediteur)
    
    const matchTexte = !filtres.value.texteMessage || 
      modele.texteMessage.toLowerCase().includes(filtres.value.texteMessage.toLowerCase())

    return matchMethode && matchNumeroExp && matchTexte
  })
}

const reinitialiserFiltres = () => {
  filtres.value = {
    methode: '',
    numeroExpediteur: '',
    texteMessage: ''
  }
  appliquerFiltres()
}

// Charger les modèles
const fetchModeles = async () => {
  loading.value = true
  try {
    modeles.value = await ModeleMessageService.getAll()
    modelesFiltres.value = modeles.value
  } catch (error) {
    console.error('Erreur chargement modèles:', error)
    apiMessage.value = 'Erreur lors du chargement des modèles.'
    timeoutId = setTimeout(() => (apiMessage.value = ''), 3000)
  } finally {
    loading.value = false
  }
}

// Ajouter un modèle
const handleAddModele = async (payload: any) => {
  try {
    await ModeleMessageService.create(payload)
    apiMessage.value = 'Modèle ajouté avec succès'
    showAddModal.value = false
    fetchModeles()
  } catch (error) {
    console.error(error)
    apiMessage.value = 'Erreur lors de l ajout du modèle'
  }

  if (timeoutId) clearTimeout(timeoutId)
  timeoutId = setTimeout(() => (apiMessage.value = ''), 3000)
}

onMounted(fetchModeles)

onBeforeUnmount(() => {
  if (timeoutId) clearTimeout(timeoutId)
})
</script>

<style scoped>
.table-responsive {
  overflow-x: auto;
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
</style>