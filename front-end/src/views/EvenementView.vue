<template>
  <div class="container mt-5">
    <!-- LOADING -->
    <div v-if="loading" class="text-center">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-2 text-muted">Chargement des événements...</p>
    </div>

    <!-- CARD -->
    <div v-else class="card shadow">
      <div class="card-header d-flex justify-content-between align-items-center">
        <div class="card-title mb-0">Événements</div>

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
            <label class="form-label small mb-1">Code</label>
            <input 
              v-model="filtres.code" 
              type="text" 
              class="form-control form-control-sm" 
              placeholder="Rechercher par code..."
              @input="appliquerFiltres"
            />
          </div>
          <div class="col">
            <label class="form-label small mb-1">Description</label>
            <input 
              v-model="filtres.description" 
              type="text" 
              class="form-control form-control-sm" 
              placeholder="Rechercher dans la description..."
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
        <!-- AUCUN EVENEMENT -->
        <div v-if="evenementsFiltres.length === 0" class="text-center py-4">
         
          <p class="text-muted mb-2">Aucun événement trouvé</p>
        </div>

        <!-- TABLEAU -->
        <div v-else>
          <table class="table table-hover">
            <thead>
              <tr>
                <th></th>
                <th>Code</th>
                <th>Description</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(ev, index) in evenementsFiltres" :key="ev.id">
                <td>{{ index + 1 }}</td>
                <td>{{ ev.code }}</td>
                <td>{{ ev.description }}</td>
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

      <!-- FOOTER -->
      <div v-if="evenementsFiltres.length > 0" class="card-footer">
        <small class="text-muted">
          Affichage : {{ evenementsFiltres.length }} / {{ evenements.length }} événement(s)
        </small>
      </div>
    </div>
  </div>

  <!-- MODAL -->
  <AddEvenementModal
    :show="showAddModal"
    @close="showAddModal = false"
    @submit="handleAddEvenement"
  />
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import evenementService from '../services/evenementService'
import type { Evenement } from '../types/Evenement'
import AddEvenementModal from '../components/AddEvenementModal.vue'

const loading = ref(true)
const evenements = ref<Evenement[]>([])
const evenementsFiltres = ref<Evenement[]>([])
const showAddModal = ref(false)

const filtres = ref({
  code: '',
  description: ''
})

const appliquerFiltres = () => {
  evenementsFiltres.value = evenements.value.filter(evt => {
    const matchCode = !filtres.value.code || 
      evt.code.toLowerCase().includes(filtres.value.code.toLowerCase())
    
    const matchDescription = !filtres.value.description || 
      evt.description.toLowerCase().includes(filtres.value.description.toLowerCase())

    return matchCode && matchDescription
  })
}

const reinitialiserFiltres = () => {
  filtres.value = {
    code: '',
    description: ''
  }
  appliquerFiltres()
}

const fetchEvenements = async () => {
  try {
    const response = await evenementService.getAll()
    evenements.value = response.data
    evenementsFiltres.value = evenements.value
  } catch (err) {
    console.error('Erreur chargement événements :', err)
  } finally {
    loading.value = false
  }
}

const handleAddEvenement = async (payload: { code: string; description: string }) => {
  try {
    await evenementService.create(payload)
    showAddModal.value = false
    await fetchEvenements()
  } catch (err) {
    console.error(err)
  }
}

onMounted(fetchEvenements)
</script>

<style scoped>
.card-body.border-bottom {
  border-bottom: 1px solid #dee2e6;
}
</style>