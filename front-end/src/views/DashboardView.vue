<template>
  <div class="container">
    <div class="page-inner">

      <!-- Statistiques rapides -->
      <div class="row">
        <!-- Enregistrés -->
        <div class="col-sm-6 col-md-3">
          <div class="card card-stats card-round">
            <div class="card-body d-flex align-items-center">
              <div class="icon-square icon-primary me-3">
                <i class="fas fa-users"></i>
              </div>
              <div class="numbers">
                <p class="card-category mb-1">Enregistrés</p>
                <h4 class="card-title mb-0">
                  <span v-if="nombreNumeros > 0">{{ nombreNumeros }} numéro(s)</span>
                  <span v-else>0 numéro</span>
                </h4>
              </div>
            </div>
          </div>
        </div>

        <!-- Messages -->
        <div class="col-sm-6 col-md-3">
          <div class="card card-stats card-round">
            <div class="card-body d-flex align-items-center">
              <div class="icon-square icon-primary me-3">
                <i class="fas fa-user-check"></i>
              </div>
              <div class="numbers">
                <p class="card-category mb-1">Messages Envoyés</p>
                <h4 class="card-title mb-0">
                  <span v-if="nombreMessages > 0">{{ nombreMessages }} message(s)</span>
                  <span v-else>0 message</span>
                </h4>
              </div>
            </div>
          </div>
        </div>

        <!-- Coûts -->
        <div class="col-sm-6 col-md-3">
          <div class="card card-stats card-round">
            <div class="card-body d-flex align-items-center">
              <div class="icon-square icon-success me-3">
                <i class="fas fa-dollar-sign"></i>
              </div>
              <div class="numbers">
                <p class="card-category mb-1">Coûts totaux</p>
                <h4 class="card-title mb-0">
                  <span v-if="coutTotal > 0">{{ formatCout(coutTotal) }}</span>
                  <span v-else>0.00 USD</span>
                </h4>
              </div>
            </div>
          </div>
        </div>

        <!-- Durée moyenne d'envoi -->
        <div class="col-sm-6 col-md-3">
          <div class="card card-stats card-round">
            <div class="card-body d-flex align-items-center">
              <div class="icon-square icon-info me-3">
                <i class="fas fa-clock"></i>
              </div>
              <div class="numbers">
                <p class="card-category mb-1">Durée moyenne D'envoi</p>
                <h4 class="card-title mb-0">
                  <span v-if="dureeMoyenneEnvoi > 0">{{ formatDuree(dureeMoyenneEnvoi) }}</span>
                  <span v-else>N/A</span>
                </h4>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Graphiques -->
      <div class="row mt-4">
        <!-- Graphique Messages (Barres) -->
        <div class="col-md-8">
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
                <canvas id="statisticsChart"></canvas>
              </div>
            </div>
          </div>
        </div>

        <!-- Graphique Coûts (Ligne) -->
        <div class="col-md-4">
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
                <canvas id="costsChart"></canvas>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="text-center mt-3">
        <div class="spinner-border text-primary" role="status"></div>
        <p class="mt-2 text-muted">Chargement des données...</p>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import numeroDestinataireService from '../services/numeroDestinataireService'
import { getHistoriqueSms } from '../services/historiqueService'
import messageDetailService from '../services/messageDetailService'
import type { MessageDetail } from '../types/messageDetail'
import Chart from 'chart.js/auto'
import "../assets/css/dashboard.css"

interface NumeroDestinataire {
  idNumero: number
  valeur: string
  dateCreation: string
  plateforme: any
}

interface SmsResponseLog {
  messageId: string
  dateEnvoi: string
}

// Réactivité
const loading = ref(true)
const numeros = ref<NumeroDestinataire[]>([])
const historique = ref<SmsResponseLog[]>([])
const messagesDetails = ref<MessageDetail[]>([])
const anneeSelectionnee = ref<number | null>(null)
const moisSelectionne = ref<number>(new Date().getMonth()) // Mois actuel par défaut
const modeDetail = ref(false) // Mode détail activé/désactivé

// Computed pour sécuriser affichage des longueurs
const nombreNumeros = computed(() => numeros.value.length)
const nombreMessages = computed(() => historique.value.length)

// Calcul du coût total
const coutTotal = computed(() => {
  return messagesDetails.value.reduce((total, message) => {
    return total + (message.pricePerMessage || 0)
  }, 0)
})

// Calcul de la durée moyenne d'envoi (en secondes)
const dureeMoyenneEnvoi = computed(() => {
  if (messagesDetails.value.length === 0) return 0
  
  let totalDuree = 0
  let compteur = 0
  
  messagesDetails.value.forEach(message => {
    if (message.sentAt && message.doneAt) {
      const dateSent = new Date(message.sentAt).getTime()
      const dateDone = new Date(message.doneAt).getTime()
      const duree = (dateDone - dateSent) / 1000 // Conversion en secondes
      
      if (duree >= 0) { // Vérifier que la durée est valide
        totalDuree += duree
        compteur++
      }
    }
  })
  
  return compteur > 0 ? totalDuree / compteur : 0
})

// Formatage du coût avec devise
const formatCout = (montant: number): string => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    minimumFractionDigits: 2,
    maximumFractionDigits: 4
  }).format(montant)
}

// Formatage de la durée (secondes → format lisible)
const formatDuree = (secondes: number): string => {
  if (secondes < 1) {
    return `${(secondes * 1000).toFixed(0)} ms`
  } else if (secondes < 60) {
    return `${secondes.toFixed(2)} sec`
  } else {
    const minutes = Math.floor(secondes / 60)
    const sec = (secondes % 60).toFixed(0)
    return `${minutes} min ${sec} sec`
  }
}

// Liste des années disponibles dans l'historique
const anneesDisponibles = computed(() => {
  const annees = new Set<number>()
  historique.value.forEach(log => {
    if (log.dateEnvoi) {
      const annee = new Date(log.dateEnvoi).getFullYear()
      annees.add(annee)
    }
  })
  return Array.from(annees).sort((a, b) => b - a) // Tri décroissant
})

// Messages filtrés selon l'année sélectionnée (uniquement pour le graphique)
const historiqueFiltréGraphique = computed(() => {
  if (!anneeSelectionnee.value) {
    return historique.value
  }
  return historique.value.filter(log => {
    if (!log.dateEnvoi) return false
    const annee = new Date(log.dateEnvoi).getFullYear()
    return annee === anneeSelectionnee.value
  })
})

// ---------------------- STATISTIQUES SMS PAR MOIS ----------------------
const statsParMois = ref<number[]>([])
const statsParJour = ref<number[]>([])
const coutsParMois = ref<number[]>([])
const labelsMois = ["Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Août", "Sep", "Oct", "Nov", "Déc"]
const labelsJours = ref<string[]>([])

let chart: Chart | null = null
let costsChart: Chart | null = null

// Calcul des statistiques par jour pour le mois sélectionné
const calculerStatsJournalieres = () => {
  if (!anneeSelectionnee.value) return
  
  const annee = anneeSelectionnee.value
  const mois = moisSelectionne.value
  
  // Obtenir le nombre de jours dans le mois
  const nbJours = new Date(annee, mois + 1, 0).getDate()
  
  // Initialiser les tableaux
  const tableauMessages = Array(nbJours).fill(0)
  labelsJours.value = Array.from({ length: nbJours }, (_, i) => String(i + 1))
  
  // Compter les messages par jour
  historique.value.forEach(log => {
    if (log.dateEnvoi) {
      const date = new Date(log.dateEnvoi)
      if (date.getFullYear() === annee && date.getMonth() === mois) {
        const jour = date.getDate() - 1 // Index 0-based
        tableauMessages[jour]++
      }
    }
  })
  
  statsParJour.value = tableauMessages
}

const calculerStatsMensuelles = () => {
  const tableauMessages = Array(12).fill(0)
  const tableauCouts = Array(12).fill(0)
  
  // Messages filtrés pour le graphique
  historiqueFiltréGraphique.value.forEach(log => {
    if (log.dateEnvoi) {
      const date = new Date(log.dateEnvoi)
      const mois = date.getMonth()
      tableauMessages[mois]++
    }
  })
  
  // Coûts filtrés par année
  const messagesFiltres = anneeSelectionnee.value 
    ? messagesDetails.value.filter(msg => {
        if (!msg.sentAt) return false
        const annee = new Date(msg.sentAt).getFullYear()
        return annee === anneeSelectionnee.value
      })
    : messagesDetails.value
  
  messagesFiltres.forEach(msg => {
    if (msg.sentAt) {
      const date = new Date(msg.sentAt)
      const mois = date.getMonth()
      tableauCouts[mois] += (msg.pricePerMessage || 0)
    }
  })
  
  statsParMois.value = tableauMessages
  coutsParMois.value = tableauCouts
}

const genererGraphique = () => {
  const ctx = document.getElementById("statisticsChart") as HTMLCanvasElement
  if (ctx) {
    if (chart) chart.destroy()
    
    // Choisir les données selon le mode
    const labels = modeDetail.value ? labelsJours.value : labelsMois
    const data = modeDetail.value ? statsParJour.value : statsParMois.value
    
    chart = new Chart(ctx, {
      type: "bar",
      data: {
        labels: labels,
        datasets: [
          {
            label: "Messages envoyés",
            data: data,
            backgroundColor: "rgba(59, 130, 246, 0.6)",
            borderColor: "#3B82F6",
            borderWidth: 1,
            borderRadius: 6,
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
            title: {
              display: modeDetail.value,
              text: 'Jour du mois'
            }
          },
          y: {
            beginAtZero: true,
            ticks: {
              stepSize: 1
            }
          }
        }
      }
    })
  }

  // Graphique Coûts (Ligne)
  const ctxCosts = document.getElementById("costsChart") as HTMLCanvasElement
  if (ctxCosts) {
    if (costsChart) costsChart.destroy()
    costsChart = new Chart(ctxCosts, {
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
                return 'Coûts: $' + context.parsed.y.toFixed(4)
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
}

// Fonction pour basculer entre vue mensuelle et détaillée
const toggleModeDetail = () => {
  modeDetail.value = !modeDetail.value
  if (modeDetail.value) {
    calculerStatsJournalieres()
  }
  genererGraphique()
}

// Fonction appelée lors du changement d'année
const onAnneeChange = () => {
  calculerStatsMensuelles()
  if (modeDetail.value) {
    calculerStatsJournalieres()
  }
  genererGraphique()
}

// Fonction appelée lors du changement de mois
const onMoisChange = () => {
  calculerStatsJournalieres()
  genererGraphique()
}

// ---------------------- CHARGEMENT DES DONNÉES ----------------------
const fetchData = async () => {
  try {
    // Chargement des données en parallèle
    const [respHistorique, respNumeros, respMessagesDetails] = await Promise.all([
      getHistoriqueSms(),
      numeroDestinataireService.getAll(),
      messageDetailService.getAll()
    ])

    historique.value = respHistorique || []
    numeros.value = respNumeros || []
    messagesDetails.value = respMessagesDetails || []

    console.log('Messages détails chargés:', messagesDetails.value.length)
    console.log('Coût total calculé:', coutTotal.value)
    console.log('Durée moyenne d\'envoi:', dureeMoyenneEnvoi.value, 'secondes')

    // Sélectionner l'année la plus récente par défaut
    if (anneesDisponibles.value.length > 0) {
      anneeSelectionnee.value = anneesDisponibles.value[0]
    }

    calculerStatsMensuelles()
    calculerStatsJournalieres()
    setTimeout(genererGraphique, 100)

  } catch (err) {
    console.error('Erreur chargement :', err)
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)
</script>