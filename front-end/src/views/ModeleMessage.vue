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

      <div class="card-body">
        <!-- AUCUN MODELE -->
        <div v-if="modeles.length === 0" class="text-center py-4">
          <div class="text-muted mb-3">📩</div>
          <p class="text-muted mb-2">Aucun modèle trouvé</p>
        </div>

        <!-- TABLEAU -->
        <div v-else class="table-responsive">
          <table class="table table-hover">
            <thead>
              <tr>
                <th>#</th>
                <th>Méthode</th>
                <th>ID Expéditeur</th>
                <th>Numéro Expéditeur</th>
                <th>ID Message</th>
                <th>Texte</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(m, index) in modeles" :key="m.id">
                <td>{{ index + 1 }}</td>
                <td>{{ m.methode }}</td>
                <td>{{ m.idExpediteur }}</td>
                <td>{{ m.valeurExpediteur }}</td>
                <td>{{ m.idMessage }}</td>
                <td>{{ m.texteMessage }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-if="modeles.length > 0" class="card-footer">
        <small class="text-muted">
          Total : {{ modeles.length }} modèle(s)
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
import { ref, onMounted, onBeforeUnmount } from 'vue'
import ModeleMessageService from '../services/modeleMessageService'
import type { ModeleMessageDTO } from '../types/ModeleMessage'
import AddModeleMessageModal from '../components/AddModeleMessageModal.vue'

const loading = ref(true)
const modeles = ref<ModeleMessageDTO[]>([])
const apiMessage = ref('')
const showAddModal = ref(false)

let timeoutId: number | null = null

// Charger les modèles
const fetchModeles = async () => {
  loading.value = true
  try {
    modeles.value = await ModeleMessageService.getAll()
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
    apiMessage.value = 'Erreur lors de l’ajout du modèle'
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
