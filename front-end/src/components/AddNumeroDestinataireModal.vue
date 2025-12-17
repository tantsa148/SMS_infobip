<template>
  <!-- Modal principal (saisie) -->
  <div class="modal-overlay" v-if="show" @click.self="closeModal">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Ajouter un numéro destinataire</h5>
        <button 
          type="button" 
          class="btn-close" 
          @click="closeModal"
          :disabled="submitting"
        ></button>
      </div>

      <form @submit.prevent="handleSubmit" class="modal-body">
        <!-- Champ Numéro avec indicatif -->
        <div class="mb-3">
          <label class="form-label">Numéro de téléphone</label>
          <div class="d-flex gap-2">
            <select 
              v-model="formData.countryCode" 
              class="form-select w-25" 
              required
              :disabled="submitting"
            >
              <option value="">Pays</option>
              <option v-for="c in countries" :key="c.code" :value="c.code">
                {{ c.flag }} {{ c.name }} ({{ c.code }})
              </option>
            </select>
            <input
              type="text"
              v-model="formData.localNumber"
              placeholder="612345678"
              required
              class="form-control"
              :disabled="submitting"
            />
          </div>
          <small class="text-muted">Format : indicatif + numéro local</small>
        </div>

        <!-- Select Plateforme -->
        <div class="mb-3">
          <label class="form-label">Plateforme (optionnel)</label>
          <select
            v-model="selectedPlateformeId"
            class="form-select"
            :disabled="submitting"
          >
            <option :value="undefined">-- Aucune --</option>
            <option v-for="p in plateformes" :key="p.id" :value="p.id">
              {{ p.nomPlateforme }}
            </option>
          </select>
        </div>

        <!-- Message d'erreur/succès -->
        <div
          v-if="message"
          :class="['alert', messageType === 'error' ? 'alert-danger' : 'alert-success', 'mt-3']"
          role="alert"
        >
          {{ message }}
        </div>

        <!-- Boutons -->
        <div class="modal-actions d-flex justify-content-end gap-2 mt-3">
          <button 
            type="button" 
            class="btn btn-secondary" 
            @click="closeModal"
            :disabled="submitting"
          >
            Annuler
          </button>
          <button
            type="submit"
            class="btn btn-primary"
            :disabled="!isFormValid || submitting"
          >
            <span v-if="submitting" class="spinner-border spinner-border-sm me-2"></span>
            {{ submitting ? 'Validation...' : 'Valider' }}
          </button>
        </div>
      </form>
    </div>
  </div>

  <!-- Modal de confirmation -->
  <div v-if="showConfirmationModal" class="modal-overlay confirmation-overlay">
    <div class="modal-content confirmation-modal">
      <div class="modal-header">
        <h5 class="modal-title">Confirmer l'ajout</h5>
        <button 
          type="button" 
          class="btn-close" 
          @click="cancelConfirmation"
          :disabled="confirming"
        ></button>
      </div>
      
      <div class="modal-body">
        <div class="confirmation-content">
          <div class="text-center mb-3">
            <i class="bi bi-question-circle text-primary" style="font-size: 3rem;"></i>
          </div>
          
          <p class="text-center mb-3">
            Êtes-vous sûr de vouloir ajouter le numéro :
          </p>
          
          <div class="alert alert-light text-center mb-3">
            <strong class="text-primary" style="font-size: 1.2rem;">{{ fullNumber }}</strong>
          </div>
          
          <!-- Affichage de la plateforme -->
          <div v-if="selectedPlateformeName" class="alert alert-info">
            <small>
              <i class="bi bi-info-circle me-1"></i>
              Ce numéro sera associé à la plateforme : 
              <strong>{{ selectedPlateformeName }}</strong>
            </small>
          </div>
        </div>
      </div>
      
      <div class="modal-footer">
        <button 
          type="button" 
          class="btn btn-outline-secondary" 
          @click="cancelConfirmation"
          :disabled="confirming"
        >
          Non, revenir
        </button>
        <button 
          type="button" 
          class="btn btn-primary" 
          @click="confirmSubmit"
          :disabled="confirming"
        >
          <span v-if="confirming" class="spinner-border spinner-border-sm me-2"></span>
          Oui, ajouter
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, reactive } from 'vue'
import countriesData from '../assets/countries.json' with { type: 'json' }
import { fetchPlateformes } from '../services/plateformeService'
import type { Plateforme } from '../types/Plateforme'

// Props & Emits
interface Props {
  show: boolean
  defaultCountryCode?: string
}
interface Emits {
  (e: 'close'): void
  (e: 'submit', payload: { valeur: string; plateforme?: { id: number } }): void
}
const props = withDefaults(defineProps<Props>(), { show: false, defaultCountryCode: '+261' })
const emit = defineEmits<Emits>()

// Formulaire
const formData = reactive({
  countryCode: props.defaultCountryCode,
  localNumber: ''
})
const countries = ref(countriesData)
countries.value.sort((a, b) => a.name.localeCompare(b.name))
const plateformes = ref<Plateforme[]>([])
const selectedPlateformeId = ref<number | undefined>(undefined)

// États
const submitting = ref(false) // Pour la validation
const confirming = ref(false) // Pour la confirmation
const message = ref('')
const messageType = ref<'success' | 'error'>('success')

// Modals
const showConfirmationModal = ref(false)

// Validation
const isFormValid = computed(() => {
  const localNumber = formData.localNumber.trim()
  return formData.countryCode.trim() !== '' &&
         /^[0-9]{5,15}$/.test(localNumber)
})

// Numéro complet
const fullNumber = computed(() => {
  const local = formData.localNumber.trim()
  return local ? `${formData.countryCode}${local}` : ''
})

// Nom de la plateforme sélectionnée
const selectedPlateformeName = computed(() => {
  if (selectedPlateformeId.value === undefined) return null
  const plateforme = plateformes.value.find(p => p.id === selectedPlateformeId.value)
  return plateforme ? plateforme.nomPlateforme : null
})

// Fermer modals
const closeModal = () => {
  if (!submitting.value && !confirming.value) {
    emit('close')
  }
}

const cancelConfirmation = () => {
  if (!confirming.value) {
    showConfirmationModal.value = false
  }
}

// Réinitialiser le formulaire
const resetForm = () => {
  formData.countryCode = props.defaultCountryCode
  formData.localNumber = ''
  selectedPlateformeId.value = undefined
  message.value = ''
  showConfirmationModal.value = false
  submitting.value = false
  confirming.value = false
}

// Étape 1 : Validation et affichage de la confirmation
const handleSubmit = async () => {
  if (!isFormValid.value || submitting.value) return
  
  submitting.value = true
  
  try {
    // Validation simple (pas encore d'appel API)
    console.log('📋 Formulaire validé, affichage de la confirmation')
    showConfirmationModal.value = true
  } catch (err: any) {
    console.error('Erreur validation:', err)
    showMessage(err.message || 'Erreur de validation', 'error')
  } finally {
    submitting.value = false
  }
}

// Étape 2 : Confirmation et envoi au parent
const confirmSubmit = async () => {
  if (confirming.value) return
  
  confirming.value = true
  console.log('✅ Confirmation, envoi des données au parent...')
  
  try {
    // Émettre les données au composant parent
    emit('submit', {
      valeur: fullNumber.value,
      plateforme: selectedPlateformeId.value ? { id: selectedPlateformeId.value } : undefined
    })
    
    // Le parent gère l'appel API et nous fermera le modal
    showMessage('Ajout en cours...', 'success')
    
  } catch (err: any) {
    console.error('Erreur dans confirmSubmit:', err)
    showMessage('Erreur lors de la préparation de l\'ajout', 'error')
    confirming.value = false
  }
}

// Afficher un message
const showMessage = (msg: string, type: 'success' | 'error') => {
  message.value = msg
  messageType.value = type
  
  if (type === 'error') {
    // Les messages d'erreur restent plus longtemps
    setTimeout(() => {
      if (message.value === msg) message.value = ''
    }, 7000)
  } else {
    // Les messages de succès disparaissent plus vite
    setTimeout(() => {
      if (message.value === msg) message.value = ''
    }, 3000)
  }
}

// Charger plateformes à l'ouverture
watch(() => props.show, async (newVal) => {
  if (newVal) {
    resetForm()
    try {
      plateformes.value = await fetchPlateformes()
    } catch (err) {
      console.error('Erreur chargement plateformes:', err)
      showMessage('Impossible de charger les plateformes', 'error')
    }
  }
})

// Observer la fermeture du modal pour reset
watch(() => props.show, (newVal) => {
  if (!newVal) {
    resetForm()
  }
})
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1050;
  padding: 1rem;
}

.modal-content {
  background: white;
  border-radius: 0.5rem;
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
}

.modal-header {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #dee2e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-title {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 500;
}

.modal-body {
  padding: 1.5rem;
}

.modal-footer {
  padding: 1rem 1.5rem;
  border-top: 1px solid #dee2e6;
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
}

/* Styles spécifiques pour le modal de confirmation */
.confirmation-overlay {
  z-index: 1060; /* Au-dessus du premier modal */
}

.confirmation-modal {
  max-width: 450px;
}

.confirmation-content {
  text-align: center;
}

.btn-close {
  background: none;
  border: none;
  font-size: 1.25rem;
  line-height: 1;
  color: #6c757d;
  cursor: pointer;
  padding: 0.5rem;
  margin: -0.5rem -0.5rem -0.5rem auto;
}

.btn-close:hover {
  color: #000;
}

.btn-close:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Responsive */
@media (max-width: 576px) {
  .modal-content {
    max-width: 95%;
  }
  
  .d-flex.gap-2 {
    flex-direction: column;
    gap: 0.5rem !important;
  }
  
  .form-select.w-25 {
    width: 100% !important;
  }
}
</style>