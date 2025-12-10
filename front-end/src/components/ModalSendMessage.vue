<template>
  <div v-if="show" class="modal-overlay" @click.self="close">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Envoyer un message</h5>
        <button class="btn-close" @click="close">&times;</button>
      </div>

      <div class="modal-body">
        <form @submit.prevent="sendMessage">
          <!-- NUMERO DESTINATAIRE (pré-sélectionné depuis le tableau) -->
          <div class="mb-3">
            <label class="form-label">Numéro destinataire</label>
            <select v-model="selectedNumeroLocal" class="form-select" required>
              <option :value="numeroFromParent">{{ numeroFromParent?.valeur }}</option>
            </select>
          </div>

          <!-- MESSAGE (select prédéfini) -->
          <div class="mb-3">
            <label class="form-label">Message</label>
            <select v-model="selectedMessage" class="form-select" required>
              <option disabled value="">-- Choisir un message --</option>
              <option v-for="msg in messages" :key="msg.id" :value="msg.texte">
                {{ msg.texte }}
              </option>
            </select>
          </div>

          <div class="modal-footer">
            <button class="btn btn-secondary" type="button" @click="close">Annuler</button>
            <button class="btn btn-primary" type="submit" :disabled="!selectedMessage">Envoyer</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch , onMounted } from 'vue'
import messageService from '../services/messageService'
import type { MessageTexte } from '../types/MessageTexte'
import type { NumeroDestinataire } from '../types/NumeroDestinataire'

const props = defineProps<{
  show: boolean
  numeroFromParent: NumeroDestinataire | null
}>()

const emits = defineEmits<{
  (e: 'close'): void
  (e: 'send', payload: { idNumeroExpediteur: number; numeroDestinataire: string; message: string }): void
}>()

// Local state
const selectedNumeroLocal = ref<NumeroDestinataire | null>(null)
const messages = ref<MessageTexte[]>([])
const selectedMessage = ref('')

// Réinitialiser quand modal s'ouvre
watch(() => props.show, (val) => {
  if (val) {
    selectedNumeroLocal.value = props.numeroFromParent
    selectedMessage.value = ''
  }
})

// Fetch messages prédéfinis
const fetchMessages = async () => {
  try {
    const response = await messageService.getAll()
    messages.value = response.data
  } catch (err) {
    console.error('Erreur chargement messages :', err)
  }
}

const close = () => emits('close')

const sendMessage = () => {
  if (selectedNumeroLocal.value && selectedMessage.value) {
    emits('send', {
      idNumeroExpediteur: selectedNumeroLocal.value.idNumero,
      numeroDestinataire: selectedNumeroLocal.value.valeur,
      message: selectedMessage.value
    })
    close()
  }
}

onMounted(fetchMessages)
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top:0;
  left:0;
  width:100%;
  height:100%;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  justify-content:center;
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
