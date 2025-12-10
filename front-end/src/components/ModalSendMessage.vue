<template>
  <div v-if="show" class="modal-overlay" @click.self="closeModal">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Envoyer un message</h5>
        <button class="btn-close" @click="closeModal">&times;</button>
      </div>

      <div class="modal-body">
        <!-- SELECT NUMERO EXPEDITEUR -->
        <div class="mb-3">
          <label class="form-label">Numéro Expediteur</label>
          <select v-model="selectedNumero" class="form-select" required>
            <option disabled value="">-- Choisir un numéro --</option>
            <option 
              v-for="num in numeros" 
              :key="num.idNumero" 
              :value="num"
            >
              {{ num.numeroExpediteur }}
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
      </div>

      <div class="modal-footer">
        <button class="btn btn-secondary" @click="closeModal">Fermer</button>
        <button class="btn btn-primary" @click="sendMessage">Envoyer</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import messageService from '../services/messageService'
import UsersDetailService from '../services/usersDetailService'
import type { MessageTexte } from '../types/MessageTexte'
import type { UsersDetail } from '../types/UsersDetail'

defineProps<{
  show: boolean
}>()

const emit = defineEmits(['close', 'send'])

const numeros = ref<UsersDetail[]>([])
const selectedNumero = ref<UsersDetail | null>(null)
const messages = ref<MessageTexte[]>([])
const selectedMessage = ref<MessageTexte | null>(null)

const fetchNumeros = async () => {
  try {
    numeros.value = await UsersDetailService.getAll()
  } catch (err) {
    console.error('Erreur chargement numéros :', err)
  }
}

const fetchMessages = async () => {
  try {
    const response = await messageService.getAll()
    messages.value = response.data
  } catch (err) {
    console.error('Erreur chargement messages :', err)
  }
}

const closeModal = () => {
  emit('close')
  selectedNumero.value = null
  selectedMessage.value = null
}

const sendMessage = () => {
  if (!selectedNumero.value || !selectedMessage.value) {
    alert('Veuillez sélectionner un numéro et un message.')
    return
  }
  emit('send', {
    idNumeroExpediteur: selectedNumero.value.idNumero,
    numeroDestinataire: selectedNumero.value.numeroExpediteur,
    message: selectedMessage.value.texte
  })
  closeModal()
}

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
