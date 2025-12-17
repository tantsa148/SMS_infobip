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
      </div>

      <!-- Graphique -->
      <div class="row mt-4">
        <div class="col-md-8">
          <div class="card card-round">
            <div class="card-header">
              <div class="card-head-row">
                <div class="card-title">Messages envoyés par mois</div>
                <div class="card-tools">
                  <a href="#" class="btn btn-label-success btn-round btn-sm me-2">
                    <span class="btn-label"><i class="fa fa-pencil"></i></span> Export
                  </a>
                  <a href="#" class="btn btn-label-info btn-round btn-sm">
                    <span class="btn-label"><i class="fa fa-print"></i></span> Print
                  </a>
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
import Chart from 'chart.js/auto'
import "../assets/css/dashboard.css";

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

// Computed pour sécuriser affichage des longueurs
const nombreNumeros = computed(() => numeros.value.length)
const nombreMessages = computed(() => historique.value.length)

// ---------------------- STATISTIQUES SMS PAR MOIS ----------------------
const statsParMois = ref<number[]>([])
const labelsMois = ["Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Août", "Sep", "Oct", "Nov", "Déc"]

let chart: Chart | null = null

const calculerStatsMensuelles = () => {
  const tableau = Array(12).fill(0)
  historique.value.forEach(log => {
    if (log.dateEnvoi) {
      const date = new Date(log.dateEnvoi)
      const mois = date.getMonth()
      tableau[mois]++
    }
  })
  statsParMois.value = tableau
}

const genererGraphique = () => {
  const ctx = document.getElementById("statisticsChart") as HTMLCanvasElement
  if (!ctx) return
  if (chart) chart.destroy()
  chart = new Chart(ctx, {
    type: "bar",
    data: {
      labels: labelsMois,
      datasets: [
        {
          label: "Messages envoyés",
          data: statsParMois.value,
          backgroundColor: "rgba(59, 130, 246, 0.6)",
          borderColor: "#3B82F6",
          borderWidth: 1,
          borderRadius: 6,
        }
      ]
    },
    options: { responsive: true, maintainAspectRatio: false }
  })
}

// ---------------------- CHARGEMENT DES DONNÉES ----------------------
const fetchData = async () => {
  try {
    const respHistorique = await getHistoriqueSms()
    historique.value = respHistorique || []

    const respNumeros = await numeroDestinataireService.getAll()
    numeros.value = respNumeros || []

    calculerStatsMensuelles()
    setTimeout(genererGraphique, 100)

  } catch (err) {
    console.error('Erreur chargement :', err)
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)
</script>
