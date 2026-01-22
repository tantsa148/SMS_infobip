<template>
  <div class="container mt-5">
    <!-- LOADING -->
    <div v-if="loading" class="text-center">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Chargement...</span>
      </div>
      <p class="mt-2 text-muted">Chargement de vos numéros...</p>
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

    <!-- CARD -->
    <div v-else class="card shadow">
      <div class="card-header d-flex justify-content-between align-items-center">
        <div class="card-title mb-0">Numéros Enregistrés</div>
        <button class="btn btn-primary btn-sm" style="width: 100px" @click="showAddModal = true">
          Ajouter
        </button>
      </div>

      <div class="card-body">
        <div v-if="numeros.length === 0" class="text-center py-4">
          <div class="text-muted mb-3">📱</div>
          <p class="text-muted mb-2">Aucun numéro trouvé</p>
        </div>

        <div v-else>
          <table class="table table-hover">
            <thead>
              <tr>
                <th></th>
                <th>Numéro</th>
                <th>Base URL</th>
                <th>Créé le</th>
                <th>Utilisateur</th>
                <th>Plateforme</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, index) in numeros" :key="row.idNumero">
                <td>{{ index + 1 }}</td>
                <td>{{ row.numeroExpediteur }}</td>
                <td>{{ row.baseUrl }}</td>
                <td>{{ formatDate(row.dateCreation) }}</td>
                <td>{{ row.username }}</td>
                <td>{{ row.nomPlateforme }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-if="numeros.length > 0" class="card-footer">
        <small class="text-muted">Total : {{ numeros.length }} numéro(s)</small>
      </div>
    </div>

    <!-- MODAL D'AJOUT -->
    <AddNumeroModal
      :show="showAddModal"
      @update:show="showAddModal = $event"
      @success="handleNumeroSuccess"
      @error="handleNumeroError"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import '../assets/css/numero-form.css'
import UsersDetailService from '../services/usersDetailService'
import AddNumeroModal from '../components/Numero.vue'

const numeros = ref<any[]>([])
const loading = ref(true)
const showAddModal = ref(false)
const apiMessage = ref('')
let timeoutId: number | null = null

// Charger les UsersDetail
const fetchData = async () => {
  loading.value = true
  try {
    numeros.value = await UsersDetailService.getAll()
  } catch (err) {
    console.error('Erreur chargement users detail :', err)
    apiMessage.value = 'Erreur chargement des numéros'
  } finally {
    loading.value = false
  }
}

// Fonction appelée quand le numéro est ajouté avec succès
const handleNumeroSuccess = async () => {
  try {
    // Recharger la liste
    await fetchData()

    // Afficher message
    apiMessage.value = 'Numéro ajouté avec succès !'

    if (timeoutId) clearTimeout(timeoutId)
    timeoutId = setTimeout(() => { apiMessage.value = '' }, 3000)
  } catch (err) {
    console.error('Erreur lors du rechargement :', err)
    apiMessage.value = 'Erreur lors de la mise à jour'
  }
}

// Fonction appelée en cas d'erreur lors de l'ajout du numéro
// Le modal se ferme automatiquement, on affiche juste la notification
const handleNumeroError = (errorMessage: string) => {
  apiMessage.value = errorMessage
  if (timeoutId) clearTimeout(timeoutId)
  timeoutId = setTimeout(() => { apiMessage.value = '' }, 5000)
}

function formatDate(date: string) {
  return new Date(date).toLocaleString()
}

// Nettoyer le timeout à la destruction
onUnmounted(() => { if (timeoutId) clearTimeout(timeoutId) })

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

