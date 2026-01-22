<template>
  <div class="card card-round">
    <div class="card-header">
      <div class="card-head-row">
        <div class="card-title">Coûts par mois</div>
        <div class="card-tools">
          <!-- Filtre par année -->
          <select 
            v-model="anneeSelectionnee" 
            @change="onAnneeChange"
            class="form-select form-select-sm"
            style="width: auto; display: inline-block;"
          >
            <option v-for="annee in anneesDisponibles" :key="annee" :value="annee">
              {{ annee }}
            </option>
          </select>
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
import type { MessageDetail } from '../../types/messageDetail'
import Chart from 'chart.js/auto'

const props = defineProps<{
  messagesDetails: MessageDetail[]
  anneesDisponibles: number[]
}>()

const emit = defineEmits<{
  (e: 'yearChange', year: number): void
}>()

// Réactivité
const anneeSelectionnee = ref<number | null>(null)
const chartCanvas = ref<HTMLCanvasElement | null>(null)

// Données
const coutsParMois = ref<number[]>([])
const labelsMois = ["Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Août", "Sep", "Oct", "Nov", "Déc"]

let chart: Chart | null = null

// Calcul des coûts mensuels
const calculerCoutsMensuels = () => {
  const tableauCouts = Array(12).fill(0)
  
  // Filtrer par année si sélectionnée
  const messagesFiltres = anneeSelectionnee.value 
    ? props.messagesDetails.filter(msg => {
        if (!msg.sentAt) return false
        const annee = new Date(msg.sentAt).getFullYear()
        return annee === anneeSelectionnee.value
      })
    : props.messagesDetails
  
  messagesFiltres.forEach(msg => {
    if (msg.sentAt) {
      const date = new Date(msg.sentAt)
      const mois = date.getMonth()
      tableauCouts[mois] += (msg.pricePerMessage || 0)
    }
  })
  
  coutsParMois.value = tableauCouts
}

// Génération du graphique
const genererGraphique = () => {
  if (!chartCanvas.value) return
  
  if (chart) chart.destroy()
  
  chart = new Chart(chartCanvas.value, {
    type: "line",
    data: {
      labels: labelsMois,
      datasets: [
        {
          label: "Coûts (USD)",
          data: coutsParMois.value,
          backgroundColor: "rgba(59, 130, 246, 0.1)",
          borderColor: "#3B82F6",
          borderWidth: 2,
          fill: true,
          tension: 0.4,
          pointRadius: 4,
          pointBackgroundColor: "#3B82F6",
          pointBorderColor: "#fff",
          pointBorderWidth: 2,
          pointHoverRadius: 6,
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
          callbacks: {
            label: function(context) {
              const value = context.parsed.y
              return value !== null ? 'Coûts: $' + value.toFixed(4) : 'Coûts: $0'
            }
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            callback: function(value) {
              return '$' + value
            }
          }
        }
      }
    }
  })
}

// Fonction appelée lors du changement d'année
const onAnneeChange = () => {
  calculerCoutsMensuels()
  genererGraphique()
  emit('yearChange', anneeSelectionnee.value!)
}

// Initialisation
const init = () => {
  if (props.anneesDisponibles.length > 0) {
    anneeSelectionnee.value = props.anneesDisponibles[0]
  }
  calculerCoutsMensuels()
  nextTick(() => genererGraphique())
}

onMounted(init)

// Watch pour les changements de données
watch(() => props.messagesDetails, () => {
  calculerCoutsMensuels()
  genererGraphique()
}, { deep: true })

// Watch pour le changement d'année
watch(() => props.anneesDisponibles, (newAnnees) => {
  if (newAnnees.length > 0 && !anneeSelectionnee.value) {
    anneeSelectionnee.value = newAnnees[0]
    calculerCoutsMensuels()
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

