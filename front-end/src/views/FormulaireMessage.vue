<template>
  <div class="container mt-4">
    <!-- FORMULAIRE -->
    <div class="form-card">
      <h2>Envoyer un message</h2>

      <!-- SELECT PLATEFORME -->
      <div class="form-group">
        <label>Plateforme :</label>
        <select v-model="selectedPlateformeId" class="form-control" @change="onPlateformeChange">
          <option value="" disabled>Sélectionnez une plateforme...</option>
          <option v-for="plateforme in plateformes" :key="plateforme.id" :value="plateforme.id">
            {{ plateforme.nomPlateforme }}
          </option>
        </select>
      </div>

      <!-- SELECT NUMERO EXPEDITEUR -->
      <div class="form-group">
        <label>Numéro Expéditeur :</label>
        <select v-model="selectedNumeroId" class="form-control" :disabled="!selectedPlateformeId">
          <option value="" disabled>Sélectionnez un numéro...</option>
          <option v-for="n in filteredNumeros" :key="n.id" :value="n.id">
            {{ n.valeur }}<span v-if="n.plateformes?.length"> — {{ n.plateformes.join(' / ') }}</span>
          </option>
        </select>
      </div>

      <!-- SELECT NUMERO DESTINATAIRE AVEC CHECKBOX -->
      <div class="form-group">
        <div class="destinataire-header">
          <label>Numéro destinataire :</label>
          <div class="checkbox-wrapper">
            <input 
              type="checkbox" 
              id="useExistingDestinataire" 
              v-model="useExistingDestinataire"
              class="checkbox-input"
            />
            <label for="useExistingDestinataire" class="checkbox-label">
              Sélectionner dans la liste
            </label>
          </div>
        </div>

        <!-- MODE SELECT (si checkbox cochée) -->
        <div v-if="useExistingDestinataire" class="destinataire-select">
          <select v-model="selectedDestinataireId" class="form-control">
            <option value="" disabled>Sélectionnez un destinataire...</option>
            <option v-for="dest in destinataires" :key="dest.id" :value="dest.id">
              {{ dest.valeur }}<span v-if="dest.plateformes?.length"> — {{ dest.plateformes.join(' / ') }}</span>
            </option>
          </select>
          <small class="help-text">Choisissez un numéro parmi vos destinataires enregistrés</small>
        </div>

        <!-- MODE MANUEL (si checkbox décochée) -->
        <div v-else class="destinataire-manual">
          <div class="phone-input-wrapper">
            <div class="country-select-wrapper">
              <select v-model="selectedCountry" class="country-select">
                <option value="" disabled>Sélectionner un pays...</option>
                <option v-for="c in countries" :key="c.code" :value="c.code">
                  {{ c.name }} ({{ c.code }})
                </option>
              </select>
            </div>
            <input 
              type="text" 
              v-model="phoneNumber" 
              placeholder="Numéro" 
              class="phone-input" 
            />
          </div>
          <small class="help-text">Entrez manuellement un numéro avec l'indicatif pays</small>
          <div v-if="phoneError" class="error-text">{{ phoneError }}</div>
        </div>
      </div>

      <!-- INPUT MESSAGE AVEC CHECKBOX -->
      <div class="form-group">
        <div class="message-header">
          <label>Message :</label>
          <div class="checkbox-wrapper">
            <input 
              type="checkbox" 
              id="useExistingMessage" 
              v-model="useExistingMessage"
              class="checkbox-input"
            />
            <label for="useExistingMessage" class="checkbox-label">
              Sélectionner un message existant
            </label>
          </div>
        </div>

        <!-- MODE SELECT MESSAGE (si checkbox cochée) -->
        <div v-if="useExistingMessage" class="message-select">
          <select v-model="selectedMessageId" class="form-control" @change="onMessageSelect">
            <option value="" disabled>Sélectionnez un message...</option>
            <option v-for="msg in messages" :key="msg.id" :value="msg.id">
              {{ msg.texte.length > 50 ? msg.texte.substring(0, 50) + '...' : msg.texte }}
            </option>
          </select>
          <small class="help-text">Choisissez un message parmi vos modèles enregistrés</small>
          <!-- Aperçu du message sélectionné -->
          <div v-if="selectedMessagePreview" class="message-preview">
            <strong>Aperçu :</strong>
            <p class="preview-text">{{ selectedMessagePreview }}</p>
          </div>
        </div>

        <!-- MODE MANUEL MESSAGE (si checkbox décochée) -->
        <div v-else>
          <textarea 
            v-model="message" 
            rows="5" 
            placeholder="Écrivez votre message ici..." 
            class="form-control"
            @input="onManualMessageInput"
          ></textarea>
          <div class="char-counter" :class="{ 'text-warning': message.length > 140, 'text-danger': message.length > 160 }">
            {{ message.length }}/160 caractères
          </div>
        </div>
      </div>

      <!-- BOUTON ENVOYER -->
      <div class="modal-actions">
        <button class="btn btn-submit" @click="openValidationModal" :disabled="!isFormValid">
          <span v-if="sending" class="spinner-border spinner-border-sm me-2"></span>
          {{ sending ? 'Envoi en cours...' : `Envoyer via ${selectedPlatformName}` }}
        </button>
      </div>
    </div>

    <!-- MODALES (composant séparé) -->
    <MessageModals
      :show-validation-modal="showValidationModal"
      :show-response-modal="showResponseModal"
      :api-response="apiResponse"
      :selected-platform-name="selectedPlatformName"
      :get-numero-expediteur="getNumeroExpediteur"
      :get-destinataire-display="getDestinataireDisplay"
      :message="message"
      :message-preview="messagePreview"
      :sending="sending"
      @update:show-validation-modal="showValidationModal = $event"
      @update:show-response-modal="showResponseModal = $event"
      @close-validation="closeValidationModal"
      @close-response="closeResponseModal"
      @send-message="sendMessage"
      @handle-response-close="handleResponseClose"
      @reset-and-close="resetAndClose"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import MessageModals from '../components/MessageModals.vue'
import type { NumeroExpediteur } from '../types/NumeroExpediteur'
import type { NumeroDestinataire } from '../types/NumeroDestinataire'
import type { Plateforme } from '../types/Plateforme'
import type { MessageTexte } from '../types/MessageTexte'
import NumeroExpediteurService from '../services/numeroExpediteurService'
import NumeroDestinataireService from '../services/numeroDestinataireService'
import { fetchPlateformes } from '../services/plateformeService'
import MessageService from '../services/SmsService'
import MessageTexteService from '../services/messageService'
import countries from '../assets/countries.json'
import '../assets/css/formulaireMessage.css'

const loading = ref(true)
const sending = ref(false)
const numeros = ref<NumeroExpediteur[]>([])
const destinataires = ref<NumeroDestinataire[]>([])
const plateformes = ref<Plateforme[]>([])
const messages = ref<MessageTexte[]>([])

// États du formulaire
const selectedPlateformeId = ref<number | ''>('')
const selectedNumeroId = ref<number | ''>('')
const selectedDestinataireId = ref<number | ''>('')
const selectedCountry = ref('+33')
const phoneNumber = ref('')
const message = ref('')
const useExistingDestinataire = ref(false)
const useExistingMessage = ref(false)
const selectedMessageId = ref<number | ''>('')
const selectedMessagePreview = ref('')
const phoneError = ref('')

// MODALS
const showValidationModal = ref(false)
const showResponseModal = ref(false)
const apiResponse = ref<any>({})

// Computed properties
const selectedPlateforme = computed(() => {
  return plateformes.value.find(p => p.id === selectedPlateformeId.value)
})

const selectedPlatformName = computed(() => {
  return selectedPlateforme.value?.nomPlateforme || 'SMS'
})

const filteredNumeros = computed(() => {
  if (!selectedPlateforme.value) {
    return numeros.value
  }
  
  const platformName = selectedPlateforme.value.nomPlateforme.toLowerCase()
  return numeros.value.filter(numero => {
    if (!numero.plateformes) return false
    return numero.plateformes.some(p => 
      p.toLowerCase().includes(platformName)
    )
  })
})

const getNumeroExpediteur = computed(() => {
  const n = numeros.value.find(n => n.id === selectedNumeroId.value)
  return n ? n.valeur : ''
})

const getDestinataireDisplay = computed(() => {
  if (useExistingDestinataire.value) {
    const dest = destinataires.value.find(d => d.id === selectedDestinataireId.value)
    return dest ? dest.valeur : 'Non sélectionné'
  } else {
    return `${selectedCountry.value}${phoneNumber.value}`
  }
})

const getDestinataireValue = computed(() => {
  if (useExistingDestinataire.value) {
    const dest = destinataires.value.find(d => d.id === selectedDestinataireId.value)
    return dest ? dest.valeur : ''
  } else {
    return `${selectedCountry.value}${phoneNumber.value}`
  }
})

const messagePreview = computed(() => {
  const msg = message.value
  return msg.length > 50 ? msg.substring(0, 50) + '...' : msg
})

const isFormValid = computed(() => {
  const hasExpediteur = !!selectedNumeroId.value
  const hasDestinataire = useExistingDestinataire.value 
    ? !!selectedDestinataireId.value
    : !!selectedCountry.value && phoneNumber.value.trim() !== '' && !phoneError.value
  const hasMessage = message.value.trim() !== ''
  
  return hasExpediteur && hasDestinataire && hasMessage
})

// Fonctions
const fetchData = async () => {
  try {
    // Charger les plateformes
    plateformes.value = await fetchPlateformes()
    
    // Charger les numéros expéditeurs
    const resExpediteurs = await NumeroExpediteurService.getAll()
    numeros.value = resExpediteurs
    
    // Charger les numéros destinataires
    const resDestinataires = await NumeroDestinataireService.getAll()
    destinataires.value = resDestinataires.data || []
    
    // Charger les messages existants
    const resMessages = await MessageTexteService.getAll()
    messages.value = resMessages.data || []
    
  } catch (error) {
    console.error('Erreur lors du chargement des données:', error)
  } finally {
    loading.value = false
  }
}

const onPlateformeChange = () => {
  selectedNumeroId.value = ''
}

const onMessageSelect = () => {
  if (selectedMessageId.value) {
    const selectedMsg = messages.value.find(msg => msg.id === selectedMessageId.value)
    if (selectedMsg) {
      message.value = selectedMsg.texte
      selectedMessagePreview.value = selectedMsg.texte
    }
  } else {
    message.value = ''
    selectedMessagePreview.value = ''
  }
}

const onManualMessageInput = () => {
  // Si l'utilisateur modifie manuellement le message, décocher la sélection de message
  if (useExistingMessage.value) {
    useExistingMessage.value = false
    selectedMessageId.value = ''
    selectedMessagePreview.value = ''
  }
}

const openValidationModal = () => {
  if (!isFormValid.value) {
    alert('Veuillez remplir tous les champs correctement.')
    return
  }
  
  showValidationModal.value = true
}

const closeValidationModal = () => showValidationModal.value = false

const closeResponseModal = () => {
  showResponseModal.value = false
}

const handleResponseClose = () => {
  if (apiResponse.value?.status === 'success') {
    resetForm()
  }
  closeResponseModal()
}

const resetAndClose = () => {
  resetForm()
  closeResponseModal()
}

const resetForm = () => {
  message.value = ''
  selectedMessageId.value = ''
  selectedMessagePreview.value = ''
  if (!useExistingDestinataire.value) {
    phoneNumber.value = ''
    phoneError.value = ''
  }
  // Garder les autres sélections
}

const sendMessage = async () => {
  sending.value = true
  showValidationModal.value = false
  
  const destinataire = getDestinataireValue.value
  
  if (!selectedNumeroId.value || !destinataire) {
    apiResponse.value = {
      status: 'error',
      error: 'Données manquantes',
      timestamp: new Date().toISOString()
    }
    sending.value = false
    showResponseModal.value = true
    return
  }
  
  const data = {
    expediteurId: selectedNumeroId.value as number,
    destinataire,
    message: message.value
  }
  
  try {
    const platformName = selectedPlatformName.value.toLowerCase()
    
    if (platformName.includes('whatsapp')) {
      apiResponse.value = await MessageService.sendWhatsApp(data)
    } else {
      apiResponse.value = await MessageService.sendSms(data)
    }
    
    // Ajouter un timestamp si non présent
    if (!apiResponse.value.timestamp) {
      apiResponse.value.timestamp = new Date().toISOString()
    }
  } catch (err: any) {
    apiResponse.value = { 
      status: 'error', 
      error: err.response?.data?.message || err.message || 'Erreur inconnue',
      timestamp: new Date().toISOString()
    }
  } finally {
    sending.value = false
    showResponseModal.value = true
  }
}

onMounted(fetchData)

// Watchers
watch(useExistingDestinataire, (newValue) => {
  if (newValue) {
    selectedCountry.value = '+33'
    phoneNumber.value = ''
    phoneError.value = ''
  } else {
    selectedDestinataireId.value = ''
  }
})

watch(useExistingMessage, (newValue) => {
  if (newValue) {
    // Si on coche pour utiliser un message existant, réinitialiser le texte manuel
    if (selectedMessageId.value) {
      const selectedMsg = messages.value.find(msg => msg.id === selectedMessageId.value)
      if (selectedMsg) {
        message.value = selectedMsg.texte
        selectedMessagePreview.value = selectedMsg.texte
      }
    } else {
      message.value = ''
    }
  } else {
    // Si on décoche, réinitialiser la sélection de message
    selectedMessageId.value = ''
    selectedMessagePreview.value = ''
  }
})
</script>

<style scoped>
/* Styles spécifiques au composant (peuvent être déplacés dans le CSS principal) */
.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.message-select {
  margin-top: 5px;
}

.message-preview {
  margin-top: 10px;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
  border-left: 4px solid #007bff;
}

.preview-text {
  margin: 5px 0 0 0;
  white-space: pre-line;
}

.checkbox-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.checkbox-input {
  margin: 0;
}

.checkbox-label {
  font-size: 0.9em;
  color: #495057;
  cursor: pointer;
}
</style>