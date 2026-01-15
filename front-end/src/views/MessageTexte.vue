<template>
  <div class="container mt-5">
    <!-- LOADING -->
    <div v-if="loading" class="text-center">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Chargement...</span>
      </div>
      <p class="mt-2 text-muted">Chargement de vos messages...</p>
    </div>

    <!-- CARD -->
    <div v-else class="card shadow">
      <div class="card-header d-flex justify-content-between align-items-center">
        <div class="card-title mb-0">Messages enregistrés</div>

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
            <label class="form-label small mb-1">Message</label>
            <input 
              v-model="filtres.texte" 
              type="text" 
              class="form-control form-control-sm" 
              placeholder="Rechercher dans le message..."
              @input="appliquerFiltres"
            />
          </div>
          <div class="col">
            <label class="form-label small mb-1">Événement</label>
            <select 
              v-model="filtres.evenement" 
              class="form-select form-select-sm"
              @change="appliquerFiltres"
            >
              <option value="">Tous</option>
              <option value="AUCUN">Aucun événement</option>
              <option v-for="evt in evenements" :key="evt" :value="evt">
                {{ evt }}
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
        <!-- AUCUN MESSAGE -->
        <div v-if="messagesFiltres.length === 0" class="text-center py-4">

          <p class="text-muted mb-2">Aucun message trouvé</p>
        </div>

        <!-- TABLEAU -->
        <div v-else>
          <table class="table table-hover">
          <thead>
            <tr>
              <th></th>
              <th>Message</th>
              <th>Événement</th>
              <th>Action</th>
            </tr>
          </thead>
            <tbody>
              <tr v-for="(msg, index) in messagesFiltres" :key="msg.id">
                <td>{{ index + 1 }}</td>
                <td>{{ msg.texte }}</td>
                

                <td>
                  <span v-if="msg.evenement" >
                    {{ msg.evenement.code }}
                  </span>
                  <span v-else class="text-muted">Aucun</span>
                </td>
<td>
                    <button class="btn btn-sm btn-outline-secondary" @click="openDeleteModal(msg)">
                      <i class="fas fa-trash-alt"></i>
                    </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- FOOTER -->
      <div v-if="messagesFiltres.length > 0" class="card-footer">
        <small class="text-muted">
          Affichage : {{ messagesFiltres.length }} / {{ messages.length }} message(s)
        </small>
      </div>
    </div>
  </div>
<AddMessageModal
  :show="showAddModal"
  @close="showAddModal = false"
  @submit="handleAddMessage"
/>

<!-- Modal de confirmation de suppression -->
<div v-if="showDeleteModal" class="modal-backdrop-custom" @click.self="showDeleteModal = false; deleteError = ''">
  <div class="modal-custom">
    <div class="modal-header">
      <h5 class="modal-title">Confirmation de suppression</h5>
      <button type="button" class="btn-close" @click="showDeleteModal = false; deleteError = ''">&times;</button>
    </div>
    <div class="modal-body">
      <p>Êtes-vous sûr de vouloir supprimer ce message ?</p>
      <p class="text-muted mb-0">"{{ messageToDelete?.texte }}"</p>
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-secondary" @click="showDeleteModal = false; deleteError = ''">Annuler</button>
      <button type="button" class="btn btn-danger" @click="confirmDelete">Supprimer</button>
    </div>
  </div>
</div>

<!-- Notification toast -->
<div v-if="deleteError" class="notification-toast alert alert-danger alert-dismissible fade show" role="alert">
  {{ deleteError }}
  <button type="button" class="btn-close" @click="deleteError = ''"></button>
</div>

</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue"
import messageService from "../services/messageService"
import type { MessageTexte } from "../types/MessageTexte"
import AddMessageModal from '../components/AddMessageModal.vue'

const loading = ref(true)
const messages = ref<MessageTexte[]>([])
const messagesFiltres = ref<MessageTexte[]>([])

const filtres = ref({
  texte: '',
  evenement: ''
})

// Liste unique des événements
const evenements = computed(() => {
  const uniqueEvenements = [...new Set(
    messages.value
      .filter(m => m.evenement)
      .map(m => m.evenement!.code)
  )]
  return uniqueEvenements.sort()
})

const appliquerFiltres = () => {
  messagesFiltres.value = messages.value.filter(msg => {
    const matchTexte = !filtres.value.texte || 
      msg.texte.toLowerCase().includes(filtres.value.texte.toLowerCase())
    
    const matchEvenement = !filtres.value.evenement || 
      (filtres.value.evenement === 'AUCUN' && !msg.evenement) ||
      (msg.evenement && msg.evenement.code === filtres.value.evenement)

    return matchTexte && matchEvenement
  })
}

const reinitialiserFiltres = () => {
  filtres.value = {
    texte: '',
    evenement: ''
  }
  appliquerFiltres()
}

const fetchMessages = async () => {
  try {
    const response = await messageService.getAll()
    messages.value = response.data
    messagesFiltres.value = messages.value
  } catch (err) {
    console.error("Erreur chargement messages :", err)
  } finally {
    loading.value = false
  }
}

const showAddModal = ref(false)
const showDeleteModal = ref(false)
const messageToDelete = ref<MessageTexte | null>(null)

const handleAddMessage = async (payload: { texte: string; evenementId: number }) => {
  try {
    await messageService.create(payload)
    showAddModal.value = false
    await fetchMessages()
  } catch (err) {
    console.error(err)
  }
}

const openDeleteModal = (msg: MessageTexte) => {
  messageToDelete.value = msg
  showDeleteModal.value = true
}

const confirmDelete = async () => {
  if (!messageToDelete.value) return
  try {
    await messageService.delete(messageToDelete.value.id)
    showDeleteModal.value = false
    messageToDelete.value = null
    await fetchMessages()
  } catch (err: any) {
    console.error("Erreur suppression:", err)
    // Afficher l'erreur dans le modal si disponible
    if (err.response?.data?.error) {
      deleteError.value = err.response.data.error
    } else {
      deleteError.value = "Une erreur est survenue lors de la suppression"
    }
  }
}

const deleteError = ref('')


onMounted(fetchMessages)
</script>

<style scoped>
.card-body.border-bottom {
  border-bottom: 1px solid #dee2e6;
}

.modal-backdrop-custom {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1050;
}

.modal-custom {
  background: #fff;
  border-radius: 8px;
  width: 500px;
  max-width: 95%;
  box-shadow: 0 5px 20px rgba(0,0,0,0.3);
}

.modal-header,
.modal-footer {
  padding: 12px 16px;
  border-bottom: 1px solid #dee2e6;
}

.modal-footer {
  border-top: 1px solid #dee2e6;
  border-bottom: none;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.modal-body {
  padding: 16px;
}

.btn-close {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
}

.notification-toast {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
  max-width: 400px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}
</style>
