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

      <div class="card-body">
        <!-- AUCUN EVENEMENT -->
        <div v-if="evenements.length === 0" class="text-center py-4">
          <div class="text-muted mb-3">📅</div>
          <p class="text-muted mb-2">Aucun événement trouvé</p>
        </div>

        <!-- TABLEAU -->
        <div v-else>
          <table class="table table-hover">
            <thead>
              <tr>
                <th>#</th>
                <th>Code</th>
                <th>Description</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(ev, index) in evenements" :key="ev.id">
                <td>{{ index + 1 }}</td>
                <td>{{ ev.code }}</td>
                <td>{{ ev.description }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- FOOTER -->
      <div v-if="evenements.length > 0" class="card-footer">
        <small class="text-muted">
          Total : {{ evenements.length }} événement(s)
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
const showAddModal = ref(false)

const fetchEvenements = async () => {
  try {
    const response = await evenementService.getAll()
    evenements.value = response.data
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
