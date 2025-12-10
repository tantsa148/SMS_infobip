<template>
  <div class="container mt-5">
    <!-- LOADING -->
    <div v-if="loading" class="text-center">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Chargement...</span>
      </div>
      <p class="mt-2 text-muted">Chargement de vos numéros...</p>
    </div>

    <!-- NOTIFICATION FIXE À DROITE -->
    <div v-if="apiMessage" class="fixed-notification">
      <div class="notification-content">
        <div class="notification-header">
          <span class="notification-title"></span>
          <button class="notification-close" @click="apiMessage = ''">
            &times;
          </button>
        </div>
        <div class="notification-body">
          {{ apiMessage }}
        </div>
      </div>
    </div>

    <!-- CARD -->
    <div v-else class="card shadow">
      <div class="card-header d-flex justify-content-between align-items-center">
        <div class="card-title mb-0">Numéros Enregistrés</div>
        <button 
          class="btn btn-primary btn-sm"
          style="width: 100px"
          @click="showAddModal = true"
        >
          Ajouter
        </button>
      </div>

      <div class="card-body">
        <!-- AUCUN NUMÉRO -->
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
                <th>Base URL</th>
                <th>Créé le</th>
                <th>Utilisateur</th>
              </tr>
            </thead>

            <tbody>
              <tr v-for="(row, index) in numeros" :key="row.idNumero">
                <td>{{ index + 1 }}</td>
                <td>{{ row.numeroExpediteur }}</td>
                <td>{{ row.baseUrl }}</td>
                <td>{{ formatDate(row.dateCreation) }}</td>
                <td>{{ row.username }}</td>
              </tr>

            </tbody>

          </table>
        </div>
      </div>

      <!-- FOOTER -->
      <div v-if="numeros.length > 0" class="card-footer">
        <small class="text-muted">Total : {{ numeros.length }} numéro(s)</small>
      </div>
    </div>

  <!-- MODAL D'AJOUT -->
  <AddNumeroModal
    :show="showAddModal"
    @update:show="showAddModal = $event"
    @numero-added="handleNumeroAdded"
  />

    
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import '../assets/css/numero-form.css'
import UsersDetailService from '../services/usersDetailService'
import type { UsersDetail } from '../types/UsersDetail'
import AddNumeroModal from '../components/Numero.vue'

const numeros = ref<UsersDetail[]>([])

const loading = ref(true)
const showAddModal = ref(false)
const apiMessage = ref('') // 🔹 message à afficher après ajout
let timeoutId: number | null = null

const fetchData = async () => {
  loading.value = true
  try {
    numeros.value = await UsersDetailService.getAll() // renvoie UsersDetail[]
  } catch (err) {
    console.error('Erreur chargement users detail :', err)
  } finally {
    loading.value = false
  }
}

// 🔹 Gérer l'ajout depuis le modal et afficher le message
// Fonction appelée quand un numéro est ajouté
const handleNumeroAdded = (newNumero: any) => {
  // Recharger la liste après ajout
  fetchData()

  // Message de succès
  apiMessage.value = 'Numéro ajouté avec succès !'

  // Effacer le message après 3s
  if (timeoutId) clearTimeout(timeoutId)
  timeoutId = setTimeout(() => {
    apiMessage.value = ''
  }, 3000)
}


function formatDate(date: string) {
  return new Date(date).toLocaleString()
}

// Nettoyer le timeout quand le composant est détruit
import { onUnmounted } from 'vue'
onUnmounted(() => {
  if (timeoutId) {
    clearTimeout(timeoutId)
  }
})

onMounted(fetchData)
</script>
