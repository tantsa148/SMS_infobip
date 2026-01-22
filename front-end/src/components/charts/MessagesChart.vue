<template>
  <div class="card card-round">
    <div class="card-header">
      <div class="card-head-row">
        <div class="card-title">
          {{ modeDetail ? 'Messages envoyés par jour' : 'Messages envoyés par mois' }}
        </div>
        <div class="card-tools">
          <!-- Filtre par année -->
          <select 
            v-model="anneeSelectionnee" 
            @change="onAnneeChange"
            class="form-select form-select-sm me-2"
            style="width: auto; display: inline-block;"
          >
            <option v-for="annee in anneesDisponibles" :key="annee" :value="annee">
              {{ annee }}
            </option>
          </select>
          
          <!-- Filtre par mois (visible uniquement en mode détail) -->
          <select 
            v-if="modeDetail"
            v-model="moisSelectionne" 
            @change="onMoisChange"
            class="form-select form-select-sm me-2"
            style="width: auto; display: inline-block;"
          >
            <option v-for="(mois, index) in labelsMois" :key="index" :value="index">
              {{ mois }}
            </option>
          </select>
          
          <!-- Bouton toggle vue -->
          <button 
            @click="toggleModeDetail"
            class="btn btn-sm"
            :class="modeDetail ? 'btn-secondary' : 'btn-primary'"
            style="display: inline-block;"
          >
            <i :class="modeDetail ? 'fas fa-chart-bar' : 'fas fa-calendar-day'" class="me-1"></i>
            {{ modeDetail ? 'Vue mensuelle' : 'Voir détails' }}
          </button>
        </div>
      </div>
    </div>
    <div class="card-body">
      <div class="chart-container" style="min-height: 375px">
        <canvas ref="chartCanvas"></canvas>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, nextTick } from 'vue'
import type { SmsResponseLog } from '../../types/historique'
import Chart from 'chart.js/auto'

const props = defineProps<{
  historique: SmsResponseLog[]
  anneesDisponibles: number[]
}>()

const emit = defineEmits<{
  (e: 'yearChange', year: number): void
  (e: 'monthChange', month: number): void
}>()

// Réactivité
const anneeSelectionnee = ref<number | null>(null)
const moisSelectionne = ref<number>(new Date().getMonth())
const modeDetail = ref(false)
const chartCanvas = ref<HTMLCanvasElement | null>(null)

// Données
const statsSmsParMois = ref<number[]>([])
const statsWhatsappParMois = ref<number[]>([])
const statsSmsParJour = ref<number[]>([])
const statsWhatsappParJour = ref<number[]>([])
const labelsMois = ["Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Août", "Sep", "Oct", "Nov", "Déc"]
const labelsJours = ref<string[]>([])

let chart: Chart | null = null

// Messages filtrés selon l'année sélectionnée
const historiqueFiltréGraphique = computed(() => {
  if (!anneeSelectionnee.value) {
    return props.historique
  }
  return props.historique.filter(log => {
    if (!log.dateEnvoi) return false
    const annee = new Date(log.dateEnvoi).getFullYear()
    return annee === anneeSelectionnee.value
  })
})

// Calcul des statistiques par jour pour le mois sélectionné
const calculerStatsJournalieres = () => {
  if (!anneeSelectionnee.value) return
  
  const annee = anneeSelectionnee.value
  const mois = moisSelectionne.value
  
  // Obtenir le nombre de jours dans le mois
  const nbJours = new Date(annee, mois + 1, 0).getDate()
  
  // Initialiser les tableaux
  const tableauSms = Array(nbJours).fill(0)
  const tableauWhatsapp = Array(nbJours).fill(0)
  labelsJours.value = Array.from({ length: nbJours }, (_, i) => String(i + 1))
  
  // Compter les messages par jour et par plateforme
  props.historique.forEach(log => {
    if (log.dateEnvoi) {
      const date = new Date(log.dateEnvoi)
      if (date.getFullYear() === annee && date.getMonth() === mois) {
        const jour = date.getDate() - 1
        
        if (log.plateforme === 'WHATSAPP') {
          tableauWhatsapp[jour]++
        } else {
          tableauSms[jour]++
        }
      }
    }
  })
  
  statsSmsParJour.value = tableauSms
  statsWhatsappParJour.value = tableauWhatsapp
}

// Calcul des statistiques mensuelles
const calculerStatsMensuelles = () => {
  const tableauSms = Array(12).fill(0)
  const tableauWhatsapp = Array(12).fill(0)
  
  // Messages filtrés pour le graphique
  historiqueFiltréGraphique.value.forEach(log => {
    if (log.dateEnvoi) {
      const date = new Date(log.dateEnvoi)
      const mois = date.getMonth()
      
      if (log.plateforme === 'WHATSAPP') {
        tableauWhatsapp[mois]++
      } else {
        tableauSms[mois]++
      }
    }
  })
  
  statsSmsParMois.value = tableauSms
  statsWhatsappParMois.value = tableauWhatsapp
}

// Génération du graphique
const genererGraphique = () => {
  if (!chartCanvas.value) return
  
  if (chart) chart.destroy()
  
  const labels = modeDetail.value ? labelsJours.value : labelsMois
  const dataSms = modeDetail.value ? statsSmsParJour.value : statsSmsParMois.value
  const dataWhatsapp = modeDetail.value ? statsWhatsappParJour.value : statsWhatsappParMois.value
  
  chart = new Chart(chartCanvas.value, {
    type: "bar",
    data: {
      labels: labels,
      datasets: [
        {
          label: "SMS",
          data: dataSms,
          backgroundColor: "rgba(59, 130, 246, 0.7)",
          borderColor: "#3B82F6",
          borderWidth: 1,
          borderRadius: 4,
        },
        {
          label: "WhatsApp",
          data: dataWhatsapp,
          backgroundColor: "rgba(34, 197, 94, 0.7)",
          borderColor: "#22C55E",
          borderWidth: 1,
          borderRadius: 4,
        }
      ]
    },
    options: { 
      responsive: true, 
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: true,
          position: 'top'
        },
        tooltip: {
          callbacks: modeDetail.value ? {
            title: function(context) {
              const moisNom = labelsMois[moisSelectionne.value]
              return `${context[0].label} ${moisNom} ${anneeSelectionnee.value}`
            }
          } : undefined
        }
      },
      scales: {
        x: {
          stacked: false,
          title: {
            display: modeDetail.value,
            text: 'Jour du mois'
          }
        },
        y: {
          stacked: false,
          beginAtZero: true,
          ticks: {
            stepSize: 1
          }
        }
      }
    }
  })
}

// Fonctions de gestion des événements
const toggleModeDetail = () => {
  modeDetail.value = !modeDetail.value
  if (modeDetail.value) {
    calculerStatsJournalieres()
  }
  genererGraphique()
}

const onAnneeChange = () => {
  calculerStatsMensuelles()
  if (modeDetail.value) {
    calculerStatsJournalieres()
  }
  genererGraphique()
  emit('yearChange', anneeSelectionnee.value!)
}

const onMoisChange = () => {
  calculerStatsJournalieres()
  genererGraphique()
  emit('monthChange', moisSelectionne.value)
}

// Initialisation
const init = () => {
  if (props.anneesDisponibles.length > 0) {
    anneeSelectionnee.value = props.anneesDisponibles[0]
  }
  calculerStatsMensuelles()
  calculerStatsJournalieres()
  nextTick(() => genererGraphique())
}

onMounted(init)

// Watch pour les changements de données
watch(() => props.historique, () => {
  calculerStatsMensuelles()
  calculerStatsJournalieres()
  genererGraphique()
}, { deep: true })

// Watch pour le changement d'année
watch(() => props.anneesDisponibles, (newAnnees) => {
  if (newAnnees.length > 0 && !anneeSelectionnee.value) {
    anneeSelectionnee.value = newAnnees[0]
    calculerStatsMensuelles()
    calculerStatsJournalieres()
    genererGraphique()
  }
}, { immediate: true })
</script>

<style scoped>
.chart-container {
  width: 100%;
  height: 100%;
}
</style>

