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

        <!-- Sélecteur plateforme -->
        <div class="form-group">
          <label for="plateforme">Plateforme :</label>
          <select v-model="selectedPlateformeId" class="platform-select" required>
            <option value="">-- Sélectionnez une plateforme --</option>
            <option v-for="p in plateformes" :key="p.id" :value="p.id">
              {{ p.nomPlateforme }}
            </option>
          </select>
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

  <!-- MODALE DE CONFIRMATION (séparée, pas imbriquée) -->
  <div class="modal-overlay" v-if="showConfirm" @click.self="closeConfirmModal">
    <div class="modal-content">
      <div class="modal-header">
        <h2>Confirmer l'ajout</h2>
      </div>
      <div class="modal-body">
        <p>Voulez-vous vraiment ajouter ce numéro ?</p>
        <ul>
          <li><strong>Numéro :</strong> {{ fullNumero }}</li>
          <li><strong>Plateforme :</strong> {{ selectedPlateformeName }}</li>
        </ul>
        
        <!-- Message d'erreur -->
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
import { ref, computed, onMounted, defineProps, defineEmits } from 'vue'
import { useRoute } from 'vue-router'
import countriesData from '../assets/countries.json' with { type: 'json' }
import { fetchPlateformes } from '../services/plateformeService'
import NumeroExpediteurService from '../services/numeroExpediteurService'
import NumeroDestinataireService from '../services/numeroDestinataireService'
import type { Plateforme } from '../types/Plateforme'

const route = useRoute()
const isDestinataire = computed(() => route.path.includes('/destinataire'))

// Props et emits
const props = defineProps<{ show: boolean }>()
const emit = defineEmits<{
  (e: 'update:show', value: boolean): void
  (e: 'numero-added', value: any): void
}>()

// Variables
const countries = ref(countriesData)
countries.value.sort((a, b) => a.name.localeCompare(b.name))

const selectedCountryCode = ref('+33')
const numeroLocal = ref('')

const plateformes = ref<Plateforme[]>([])
const selectedPlateformeId = ref<number | ''>('')

const showConfirm = ref(false)
const adding = ref(false)
const responseMessage = ref<string | null>(null)

// Validation
const isFormValid = computed(() =>
  numeroLocal.value.trim().length > 0 && selectedPlateformeId.value !== ''
)

// Numéro complet
const fullNumero = computed(() => {
  const local = numeroLocal.value.trim()
  return local ? `${selectedCountryCode.value}${local}` : ''
})

// Nom de la plateforme sélectionnée
const selectedPlateformeName = computed(() => {
  const p = plateformes.value.find(p => p.id === selectedPlateformeId.value)
  return p ? p.nomPlateforme : ''
})

// Chargement des plateformes
onMounted(async () => {
  try {
    plateformes.value = await fetchPlateformes()
  } catch (err) {
    console.error('Erreur chargement plateformes:', err)
  }
})

// Fermeture modal principal
const closeModal = () => {
  emit('update:show', false)
  resetForm()
}

// Fermeture modal de confirmation
const closeConfirmModal = () => {
  showConfirm.value = false
  responseMessage.value = null
}

const resetForm = () => {
  numeroLocal.value = ''
  selectedPlateformeId.value = ''
  selectedCountryCode.value = '+33'
  responseMessage.value = null
}

// Soumission formulaire
const handleSubmit = () => {
  if (!isFormValid.value) return
  responseMessage.value = null // Réinitialiser les erreurs
  showConfirm.value = true
}

// Confirmation ajout
const confirmAdd = async () => {
  adding.value = true
  responseMessage.value = null

  try {
    let newNumero

    if (isDestinataire.value) {
      // Destinataire
      const dataDestinataire = {
        valeur: fullNumero.value,
        plateformeId: selectedPlateformeId.value as number
      }
      newNumero = await NumeroDestinataireService.addNumero(dataDestinataire)
      
    } else {
      // Expéditeur
      const dataExpediteur = {
        valeur: fullNumero.value,
        idPlateforme: selectedPlateformeId.value as number
      }
      newNumero = await NumeroExpediteurService.add(dataExpediteur)
    }

    // Émettre l'événement vers le parent
    emit('numero-added', newNumero)

    // Fermer les modales
    showConfirm.value = false
    closeModal()
  } catch (err: any) {
    console.error('Erreur ajout numéro:', err)
    responseMessage.value = err.response?.data?.message || err.message || 'Erreur inconnue'
    
    // Ne pas fermer la modale en cas d'erreur, laisser l'utilisateur corriger
  } finally {
    adding.value = false
  }
}
</script>

<style scoped>
/* Styles identiques à ton modal actuel */
.modal-overlay { 
  position: fixed; 
  top: 0; 
  left: 0; 
  width: 100%; 
  height: 100%; 
  background: rgba(0,0,0,0.5); 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  z-index: 1050; 
}
.modal-content { 
  background: #fff; 
  border-radius: 6px; 
  max-width: 450px; 
  width: 100%; 
  padding: 1rem; 
  margin: 1rem;
  max-height: 90vh;
  overflow-y: auto;
}
.modal-header, .modal-footer { 
  display: flex; 
  justify-content: space-between; 
  align-items: center; 
}
.btn-cancel { 
  background: #ccc; 
  border: none; 
  padding: 0.5rem 1rem; 
  border-radius: 4px; 
  cursor: pointer; 
}
.btn-submit { 
  background: #007bff; 
  color: white; 
  border: none; 
  padding: 0.5rem 1rem; 
  border-radius: 4px; 
  cursor: pointer; 
}
.btn-disabled, .btn-submit:disabled, .btn-cancel:disabled { 
  opacity: 0.5; 
  cursor: not-allowed; 
}
.phone-input-wrapper { 
  display: flex; 
  border: 1px solid #d1d5db; 
  border-radius: 8px; 
  overflow: hidden; 
  background: #fff; 
}
.country-select { 
  appearance: none; 
  background: #f8fafc; 
  border: none; 
  padding: 0.5rem 0.75rem; 
  font-size: 1rem; 
  font-weight: 600; 
  width: 120px; 
  cursor: pointer; 
  position: relative; 
}
.phone-input { 
  flex: 1; 
  border: none; 
  padding: 0.5rem 0.75rem; 
  font-size: 1rem; 
}
.phone-input:focus { 
  outline: none; 
}
.platform-select { 
  width: 100%; 
  padding: 0.5rem 0.75rem; 
  font-size: 1rem; 
  border-radius: 8px; 
  border: 1px solid #d1d5db; 
  background: #fff; 
  appearance: none; 
  cursor: pointer; 
  font-weight: 600; 
}
.platform-select:focus { 
  outline: none; 
  border-color: #0d6efd; 
  box-shadow: 0 0 0 2px rgba(13, 110, 253, 0.25); 
}

/* Style pour le spinner */
.spinner-border-sm {
  margin-right: 0.5rem;
  vertical-align: middle;
}

.alert {
  margin: 0;
  padding: 0.5rem 1rem;
  border-radius: 4px;
}
</style>