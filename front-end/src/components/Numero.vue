<template>
  <!-- MODAL PRINCIPAL -->
  <div class="modal-overlay" v-if="show" @click.self="closeModal">
    <div class="modal-content">
      <div class="modal-header">
        <h2>Ajouter un numéro</h2>
      </div>

      <form @submit.prevent="handleSubmit" class="modal-body">
        <!-- Champ Numéro + Indicatif -->
        <div class="form-group">
          <label for="numero">Numéro de téléphone :</label>
          <div class="phone-input-wrapper">
            <select v-model="selectedCountryCode" class="country-select">
              <option v-for="c in countries" :key="c.code" :value="c.code">
                {{ c.flag }} {{ c.name }} ({{ c.code }})
              </option>
            </select>

            <input
              type="text"
              id="numero"
              v-model="numeroLocal"
              placeholder="Ex: 612345678"
              required
              class="phone-input"
            />
          </div>
        </div>

        <!-- Zone de texte pour l'API Key -->
        <div class="form-group">
          <label for="apiKey">Clé API Infobip :</label>
          <input
            type="password"
            id="apiKey"
            v-model="apiKey"
            placeholder="Saisissez votre clé API Infobip"
            required
            class="form-control"
          />
          <small class="form-text text-muted">
            La clé API est nécessaire pour valider le numéro
          </small>
        </div>

        <!-- Zone de texte pour l'URL de base Infobip -->
        <div class="form-group">
          <label for="baseUrl">URL de base Infobip :</label>
          <input
            type="text"
            id="baseUrl"
            v-model="baseUrl"
            placeholder="https://api.infobip.com"
            required
            class="form-control"
          />
          <small class="form-text text-muted">
            URL du serveur Infobip (ex: https://api.infobip.com)
          </small>
          
          <!-- Suggestions d'URL si disponibles -->
          <div v-if="urlSuggestions.length > 0" class="suggestions-container">
            <small>URLs disponibles :</small>
            <div class="suggestions">
              <button
                type="button"
                v-for="url in urlSuggestions"
                :key="url"
                @click="selectUrlSuggestion(url)"
                class="suggestion-btn"
              >
                {{ url }}
              </button>
            </div>
          </div>
        </div>

        <!-- Boutons -->
        <div class="modal-actions">
          <button type="button" class="btn-cancel" @click="closeModal">
            Annuler
          </button>
          <button
            type="submit"
            :disabled="!isFormValid"
            :class="{ 'btn-disabled': !isFormValid }"
            class="btn-submit"
          >
            Ajouter
          </button>
        </div>
      </form>
    </div>
  </div>

  <!-- MODALE DE CONFIRMATION -->
  <div class="modal-overlay" v-if="showConfirm" @click.self="closeConfirmModal">
    <div class="modal-content">
      <div class="modal-header">
        <h2>Confirmer l'ajout</h2>
      </div>
      <div class="modal-body">
        <p>Voulez-vous vraiment ajouter ce numéro ?</p>
        <ul>
          <li><strong>Numéro :</strong> {{ fullNumero }}</li>
          <li><strong>URL Infobip :</strong> {{ baseUrl }}</li>
          <li><strong>Clé API :</strong> {{ maskedApiKey }}</li>
        </ul>

        <div v-if="responseMessage" class="alert alert-danger mt-3">
          {{ responseMessage }}
        </div>
      </div>

      <div class="modal-footer">
        <button class="btn-cancel" @click="closeConfirmModal" :disabled="adding">
          Annuler
        </button>
        <button class="btn-submit" @click="confirmAdd" :disabled="adding">
          <span v-if="adding" class="spinner-border spinner-border-sm"></span>
          {{ adding ? 'Ajout en cours...' : 'Confirmer' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import countriesData from '../assets/countries.json' with { type: 'json' }

import UsersDetailService from '../services/usersDetailService'
import NumeroExpediteurService from '../services/numeroExpediteurService'
import type { UsersDetail } from '../types/UsersDetail'

// Props & Emits
const props = defineProps<{ show: boolean }>()
const emit = defineEmits<{
  (e: 'update:show', value: boolean): void
  (e: 'numero-added', value: any): void
}>()

// Données
const countries = ref(countriesData)
countries.value.sort((a, b) => a.name.localeCompare(b.name))

const selectedCountryCode = ref('+33')
const numeroLocal = ref('')
const apiKey = ref('')
const baseUrl = ref('')  // Changé de selectedBaseUrl à baseUrl
const infobips = ref<UsersDetail[]>([])

const showConfirm = ref(false)
const adding = ref(false)
const responseMessage = ref<string | null>(null)

// Suggestions d'URL uniques
const urlSuggestions = computed(() => {
  if (!infobips.value || infobips.value.length === 0) return []
  
  // Extraire les URLs uniques
  const urls = infobips.value
    .filter(item => item?.baseUrl && item.baseUrl.trim() !== '')
    .map(item => item.baseUrl)
  
  // Supprimer les doublons
  return [...new Set(urls)]
})

// Validation
const isFormValid = computed(() =>
  numeroLocal.value.trim().length > 0 && 
  apiKey.value.trim().length > 0 && 
  baseUrl.value.trim() !== ''
)

// Numéro complet
const fullNumero = computed(() => {
  const local = numeroLocal.value.trim()
  return local ? `${selectedCountryCode.value}${local}` : ''
})

// Masquer la clé API pour l'affichage
const maskedApiKey = computed(() => {
  if (!apiKey.value) return ''
  const length = apiKey.value.length
  if (length <= 8) return '••••••••'
  return `${apiKey.value.substring(0, 4)}${'•'.repeat(Math.min(8, length - 4))}${apiKey.value.substring(length - 4)}`
})

// Sélectionner une suggestion d'URL
const selectUrlSuggestion = (url: string) => {
  baseUrl.value = url
}

// Charger les comptes Infobip
onMounted(async () => {
  try {
    infobips.value = await UsersDetailService.getAll()
    
    // Pré-remplir avec la première URL disponible si le champ est vide
    if (baseUrl.value === '' && infobips.value.length > 0) {
      const firstInfobip = infobips.value[0]
      if (firstInfobip && firstInfobip.baseUrl) {
        baseUrl.value = firstInfobip.baseUrl
      }
    }
  } catch (err) {
    console.error('Erreur chargement infobip:', err)
  }
})

// Fermer modal principal
const closeModal = () => {
  emit('update:show', false)
  resetForm()
}

// Fermer confirmation
const closeConfirmModal = () => {
  showConfirm.value = false
  responseMessage.value = null
}

// Reset form
const resetForm = () => {
  numeroLocal.value = ''
  apiKey.value = ''
  selectedCountryCode.value = '+33'
  responseMessage.value = null
  
  // Réinitialiser l'URL avec la première suggestion disponible
  if (urlSuggestions.value.length > 0) {
    baseUrl.value = urlSuggestions.value[0]
  } else {
    baseUrl.value = ''
  }
}

// Soumission form
const handleSubmit = () => {
  if (!isFormValid.value) return
  responseMessage.value = null
  showConfirm.value = true
}

// Confirmation ajout
const confirmAdd = async () => {
  adding.value = true
  responseMessage.value = null

  try {
    const data = {
      valeur: fullNumero.value,
      infobipInfo: {
        apiKey: apiKey.value.trim(),
        baseUrl: baseUrl.value.trim()
      }
    }

    const newNumero = await NumeroExpediteurService.add(data)

    emit('numero-added', newNumero)

    showConfirm.value = false
    closeModal()
  } catch (err: any) {
    console.error('Erreur ajout numéro:', err)
    responseMessage.value = err.response?.data?.message || err.message || 'Erreur inconnue'
  } finally {
    adding.value = false
  }
}
</script>

<style scoped>
/* Styles existants... */

.form-control {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 14px;
  margin-top: 4px;
  margin-bottom: 8px;
}

.form-text {
  display: block;
  margin-top: 4px;
  color: #6c757d;
  font-size: 12px;
}

.suggestions-container {
  margin-top: 10px;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.suggestions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.suggestion-btn {
  padding: 4px 12px;
  font-size: 12px;
  background-color: #e9ecef;
  border: 1px solid #ced4da;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.suggestion-btn:hover {
  background-color: #007bff;
  color: white;
  border-color: #007bff;
}

.suggestion-btn:active {
  transform: scale(0.95);
}
</style>