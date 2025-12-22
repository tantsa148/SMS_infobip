<template>
  <!-- Modal principal pour ajouter un numéro -->
  <div v-if="show" class="modal-overlay" @click.self="closeModal">
    <div class="modal-content">
      <!-- Header -->
      <div class="modal-header">
        <h5 class="modal-title">Ajouter un numéro expéditeur</h5>
      </div>

      <!-- Body -->
      <div class="modal-body">
        <!-- Alert pour erreurs générales -->
        <div v-if="errors.generalError" class="alert alert-danger">
          {{ errors.generalError }}
        </div>

        <form @submit.prevent="showConfirmation">
          <!-- Numéro -->
          <div class="mb-3">
            <label for="numeroInput" class="form-label">Numéro <span class="text-danger">*</span></label>
            <input 
              type="text" 
              id="numeroInput"
              v-model="numero" 
              class="form-control" 
              :class="{ 'is-invalid': errors.numero }"
              required 
              placeholder="Ex: 23242528"
            />
            <div v-if="errors.numero" class="invalid-feedback">
              {{ errors.numero }}
            </div>
          </div>

          <!-- Checkbox pour nouveau Infobip -->
          <div class="form-check mb-3">
            <input 
              type="checkbox" 
              v-model="isNewInfobip" 
              class="form-check-input" 
              id="newInfobipCheck"
            >
            <label class="form-check-label" for="newInfobipCheck">
              Créer un nouvel Infobip
            </label>
          </div>

          <!-- Sélection ou création Infobip -->
          <div v-if="!isNewInfobip" class="mb-3">
            <label for="infobipSelect" class="form-label">Infobip existant <span class="text-danger">*</span></label>
            <select 
              id="infobipSelect"
              v-model="selectedInfobipId" 
              class="form-select"
              :class="{ 'is-invalid': errors.selectedInfobipId }"
              required
            >
              <option :value="null" disabled>-- Sélectionner Infobip --</option>
              <option v-for="infobip in infobips" :key="infobip.idInfobip" :value="infobip.idInfobip">
                {{ infobip.baseUrl || `Infobip #${infobip.idInfobip}` }}
              </option>
            </select>
            <div v-if="errors.selectedInfobipId" class="invalid-feedback">
              {{ errors.selectedInfobipId }}
            </div>
          </div>

          <div v-else>
            <div class="mb-3">
              <label for="apiKeyInput" class="form-label">API Key <span class="text-danger">*</span></label>
              <input 
                type="text" 
                id="apiKeyInput"
                v-model="newInfobip.apiKey" 
                class="form-control" 
                :class="{ 'is-invalid': errors.apiKey }"
                required 
                placeholder="Votre clé API Infobip"
              />
              <div v-if="errors.apiKey" class="invalid-feedback">
                {{ errors.apiKey }}
              </div>
            </div>
            <div class="mb-3">
              <label for="baseUrlInput" class="form-label">Base URL <span class="text-danger">*</span></label>
              <input 
                type="url" 
                id="baseUrlInput"
                v-model="newInfobip.baseUrl" 
                class="form-control" 
                :class="{ 'is-invalid': errors.baseUrl }"
                required 
                placeholder="https://api.infobip.com/v2"
              />
              <div v-if="errors.baseUrl" class="invalid-feedback">
                {{ errors.baseUrl }}
              </div>
            </div>
          </div>

          <!-- Select Plateforme -->
          <div class="mb-3">
            <label for="plateformeSelect" class="form-label">Plateforme (optionnel)</label>
            <select 
              id="plateformeSelect"
              v-model="idPlateforme"
              class="form-select"
            >
              <option :value="null">-- Aucune plateforme --</option>
              <option v-for="plateforme in plateformes" :key="plateforme.id" :value="plateforme.id">
                {{ plateforme.nomPlateforme }}
              </option>
            </select>
          </div>
        </form>
      </div>

      <!-- Footer -->
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" @click="closeModal">
          Annuler
        </button>
        <button 
          type="button" 
          class="btn btn-primary" 
          @click="showConfirmation"
          :disabled="isSubmitting"
        >
          <span v-if="isSubmitting" class="spinner-border spinner-border-sm me-1" role="status"></span>
          {{ isSubmitting ? 'Ajout en cours...' : 'Ajouter' }}
        </button>
      </div>
    </div>
  </div>

  <!-- Utilisation du composant ConfirmationModal -->
  <ConfirmationModal
    :show="showConfirmationModal"
    :numero="numero"
    :is-new-infobip="isNewInfobip"
    :selected-infobip="selectedInfobip"
    :selected-infobip-id="selectedInfobipId"
    :new-infobip="newInfobip"
    :id-plateforme="idPlateforme"
    :is-submitting="isSubmitting"
    @update:show="closeConfirmationModal"
    @confirm="submitForm"
  />
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, watch, computed } from 'vue'
import ConfirmationModal from './ConfirmationNumeroModal.vue'
import UsersDetailService from '../services/usersDetailService'
import numeroExpediteurService from '../services/numeroExpediteurService'
import * as plateformeService from '../services/plateformeService'
import type { UsersDetail } from '../types/UsersDetail'
import type { Plateforme } from '../types/Plateforme'
import type { AddNumeroExpediteurRequest } from '../types/NumeroExpediteur'

interface FormErrors {
  numero?: string;
  selectedInfobipId?: string;
  apiKey?: string;
  baseUrl?: string;
  generalError?: string;
}

export default defineComponent({
  name: 'AddNumeroModal',
  components: { ConfirmationModal },
  props: { show: { type: Boolean, required: true } },
  emits: ['update:show', 'success'],
  setup(props, { emit }) {
    const numero = ref('')
    const isNewInfobip = ref(false)
    const selectedInfobipId = ref<number | null>(null)
    const newInfobip = ref({ apiKey: '', baseUrl: '' })
    const infobips = ref<UsersDetail[]>([])
    const plateformes = ref<Plateforme[]>([])
    const idPlateforme = ref<number | undefined>(undefined)
    const errors = ref<FormErrors>({})
    const isSubmitting = ref(false)
    const showConfirmationModal = ref(false)

    const selectedInfobip = computed(() => {
      if (!selectedInfobipId.value) return null
      return infobips.value.find(infobip => infobip.idInfobip === selectedInfobipId.value)
    })

    onMounted(async () => {
      await loadInfobips()
      await loadPlateformes()
    })

    watch(() => props.show, (newVal) => {
      if (newVal) resetForm()
    })

    const loadInfobips = async () => {
      try { infobips.value = await UsersDetailService.getAll() } 
      catch (err) { console.error('Erreur chargement Infobip:', err) }
    }

    const loadPlateformes = async () => {
      try { plateformes.value = await plateformeService.fetchPlateformes() } 
      catch (err) { console.error('Erreur chargement plateformes:', err) }
    }

    const validateForm = (): boolean => {
      errors.value = {}

      if (!numero.value.trim()) errors.value.numero = 'Le numéro est requis'

      if (!isNewInfobip.value) {
        if (!selectedInfobipId.value) errors.value.selectedInfobipId = 'Veuillez sélectionner un Infobip'
      } else {
        if (!newInfobip.value.apiKey.trim()) errors.value.apiKey = "L'API Key est requise"
        if (!newInfobip.value.baseUrl.trim()) errors.value.baseUrl = 'La Base URL est requise'
      }

      return Object.keys(errors.value).length === 0
    }

    const showConfirmation = () => {
      if (!validateForm()) return
      showConfirmationModal.value = true
    }

    const closeConfirmationModal = () => showConfirmationModal.value = false
    const closeModal = () => emit('update:show', false)

    const resetForm = () => {
      numero.value = ''
      isNewInfobip.value = false
      selectedInfobipId.value = null
      newInfobip.value = { apiKey: '', baseUrl: '' }
      idPlateforme.value = undefined
      errors.value = {}
      isSubmitting.value = false
      showConfirmationModal.value = false
    }

    const submitForm = async () => {
      if (isSubmitting.value) return
      isSubmitting.value = true
      errors.value.generalError = undefined

      try {
        const payload: AddNumeroExpediteurRequest = {
          valeur: numero.value.trim(),
          infobipInfo: isNewInfobip.value
            ? { apiKey: newInfobip.value.apiKey.trim(), baseUrl: newInfobip.value.baseUrl.trim() }
            : { id: selectedInfobipId.value! },
          ...(idPlateforme.value && { idPlateforme: idPlateforme.value })
        }

        await numeroExpediteurService.add(payload)
        closeConfirmationModal()
        closeModal()
        emit('success')
        resetForm()

      } catch (err: any) {
        console.error('Erreur lors de l\'ajout:', err)
        errors.value.generalError = err.response?.data?.error || err.message || 'Erreur lors de l\'ajout du numéro'
      } finally {
        isSubmitting.value = false
      }
    }

    return {
      numero,
      isNewInfobip,
      selectedInfobipId,
      selectedInfobip,
      newInfobip,
      infobips,
      plateformes,
      idPlateforme,
      errors,
      isSubmitting,
      showConfirmationModal,
      closeModal,
      closeConfirmationModal,
      showConfirmation,
      submitForm
    }
  }
})
</script>
