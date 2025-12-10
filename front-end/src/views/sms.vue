<template>
  <div class="container mt-5">

    <!-- FORMULAIRE -->
    <form @submit.prevent="openModal">
      <!-- SELECT NUMERO expediteur -->
      <div class="mb-3">
        <label class="form-label">Numéro Expediteur</label>
        <select v-model="selectedNumero" class="form-select" required>
          <option disabled value="">-- Choisir un numéro --</option>
          <option 
            v-for="num in numeros" 
            :key="num.idNumero" 
            :value="num"
          >
            {{ num.numeroExpediteur }} ({{ num.username }})
          </option>
        </select>
      </div>

      <!-- SELECT MESSAGE -->
      <div class="mb-3">
        <label class="form-label">Message</label>
        <select v-model="selectedMessage" class="form-select" required>
          <option disabled value="">-- Choisir un message --</option>
          <option 
            v-for="msg in messages" 
            :key="msg.id" 
            :value="msg"
          >
            {{ msg.texte }}
          </option>
        </select>
      </div>

      <button type="submit" class="btn btn-success w-100">
        Envoyer
      </button>
    </form>

    <!-- MODAL -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Détails sélectionnés</h5>
          <button class="btn-close" @click="closeModal">&times;</button>
        </div>
        <div class="modal-body">
          <p><strong>ID Numéro :</strong> {{ selectedNumero?.idNumero }}</p>
          <p><strong>ID Message :</strong> {{ selectedMessage?.id }}</p>
        </div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="closeModal">Fermer</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue"
import UsersDetailService from "../services/usersDetailService"
import messageService from "../services/messageService"
import type { UsersDetail } from "../types/UsersDetail"
import type { MessageTexte } from "../types/MessageTexte"

// DATA
const numeros = ref<UsersDetail[]>([])
const messages = ref<MessageTexte[]>([])
const selectedNumero = ref<UsersDetail | null>(null)
const selectedMessage = ref<MessageTexte | null>(null)
const showModal = ref(false)

// FETCH DATA
const fetchNumeros = async () => {
  try {
    numeros.value = await UsersDetailService.getAll()
  } catch (err) {
    console.error("Erreur chargement numéros :", err)
  }
}

const fetchMessages = async () => {
  try {
    const response = await messageService.getAll()
    messages.value = response.data
  } catch (err) {
    console.error("Erreur chargement messages :", err)
  }
}

// MODAL
const openModal = () => {
  if (!selectedNumero.value || !selectedMessage.value) {
    alert("Veuillez sélectionner un numéro et un message.")
    return
  }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}

// ON MOUNT
onMounted(() => {
  fetchNumeros()
  fetchMessages()
})
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}
.modal-content {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  width: 400px;
  max-width: 90%;
}
.modal-header, .modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.modal-body {
  margin: 1rem 0;
}
.btn-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  line-height: 1;
  cursor: pointer;
}
</style>
