<template>
  <div class="modal-overlay" v-if="show" @click.self="closeModal">
    <div class="modal-content">
      <div class="modal-header">
        <h2>Ajouter un numéro destinataire</h2>
      </div>

      <form @submit.prevent="handleSubmit" class="modal-body">
        <!-- Champ Numéro avec indicatif -->
        <div class="form-group">
          <label for="numero">Numéro de téléphone :</label>
          <div class="phone-input-wrapper">
            <select v-model="formData.countryCode" class="country-select" required>
              <option value="">-- Sélectionnez un pays --</option>
              <option v-for="c in countries" :key="c.code" :value="c.code">
                {{ c.flag }} {{ c.name }} ({{ c.code }})
              </option>
            </select>

            <input
              type="text"
              id="numero"
              v-model="formData.localNumber"
              placeholder="Ex: 612345678"
              required
              class="phone-input"
            />
          </div>
          <small class="form-text text-muted">
            Format international recommandé
          </small>
        </div>

        <!-- Message d'erreur/succès -->
        <div v-if="message" 
             :class="['alert', messageType === 'error' ? 'alert-danger' : 'alert-success', 'mt-3']">
          {{ message }}
        </div>

        <!-- Boutons -->
        <div class="modal-actions">
          <button type="button" class="btn-cancel" @click="closeModal" :disabled="submitting">
            Annuler
          </button>
          <button
            type="submit"
            :disabled="!isFormValid || submitting"
            :class="{ 'btn-disabled': !isFormValid || submitting }"
            class="btn-submit"
          >
            <span v-if="submitting" class="spinner-border spinner-border-sm me-2"></span>
            {{ submitting ? 'Ajout en cours...' : 'Ajouter' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, reactive } from 'vue'
import countriesData from '../assets/countries.json' with { type: 'json' }
import "../assets/css/destinataire.css"
interface Props {
  show: boolean
  defaultCountryCode?: string
}

interface Emits {
  (e: 'close'): void
  (e: 'submit', numero: string): void
}

const props = withDefaults(defineProps<Props>(), {
  show: false,
  defaultCountryCode: '+261'
})

const emit = defineEmits<Emits>()

// Données du formulaire
const formData = reactive({
  countryCode: props.defaultCountryCode,
  localNumber: ''
})

const countries = ref(countriesData)
countries.value.sort((a, b) => a.name.localeCompare(b.name))

const submitting = ref(false)
const message = ref('')
const messageType = ref<'success' | 'error'>('success')

// Validation du formulaire
const isFormValid = computed(() => {
  return formData.countryCode.trim() !== '' && 
         formData.localNumber.trim().length > 0
})

// Numéro complet formaté
const fullNumber = computed(() => {
  const local = formData.localNumber.trim()
  return local ? `${formData.countryCode}${local}` : ''
})

// Fermer le modal
const closeModal = () => {
  emit('close')
}

// Soumettre le formulaire
const handleSubmit = async () => {
  if (!isFormValid.value) return
  
  submitting.value = true
  message.value = ''

  try {
    emit('submit', fullNumber.value)
  } catch (err: any) {
    console.error('Erreur dans le modal:', err)
    
    let errorMsg = 'Erreur lors de l\'ajout du numéro'
    if (err.message) {
      errorMsg = err.message
    }
    
    message.value = errorMsg
    messageType.value = 'error'
  } finally {
    submitting.value = false
  }
}

// Réinitialiser le formulaire
const resetForm = () => {
  formData.countryCode = props.defaultCountryCode
  formData.localNumber = ''
  message.value = ''
}

// Watch pour réinitialiser quand le modal s'ouvre
watch(() => props.show, (newVal) => {
  if (newVal) {
    resetForm()
  }
})

// Exposer certaines méthodes si nécessaire
defineExpose({
  resetForm,
  setMessage: (msg: string, type: 'success' | 'error' = 'success') => {
    message.value = msg
    messageType.value = type
  }
})
</script>
