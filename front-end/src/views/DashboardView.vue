<template>
  <div class="container">
    <div class="page-inner">
      <div class="row">
        <!-- Enregistrés -->
        <div class="col-sm-6 col-md-3">
          <div class="card card-stats card-round">
            <div class="card-body d-flex align-items-center">
              <!-- Icône à gauche -->
              <div class="icon-square icon-primary me-3">
                <i class="fas fa-users"></i>
              </div>
              <!-- Texte à droite -->
              <div class="numbers">
                <p class="card-category mb-1">Enregistrés</p>
                <h4 class="card-title mb-0">
                  <span v-if="numeros.length > 0">{{ numeros.length }} numéro(s)</span>
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
              <!-- Icône à gauche -->
              <div class="icon-square icon-primary me-3">
                <i class="fas fa-user-check"></i>
              </div>
              <!-- Texte à droite -->
              <div class="numbers">
                <p class="card-category mb-1">Messages Envoyés</p>
                <h4 class="card-title mb-0">
                  <span v-if="historique.length > 0">{{ historique.length }} message(s)</span>
                  <span v-else>0 message</span>
                </h4>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="row">
          <div class="col-md-8">
							<div class="card card-round">
								<div class="card-header">
									<div class="card-head-row">
										<div class="card-title">User Statistics</div>
										<div class="card-tools">
											<a href="#" class="btn btn-label-success btn-round btn-sm me-2">
												<span class="btn-label">
													<i class="fa fa-pencil"></i>
												</span>
												Export
											</a>
											<a href="#" class="btn btn-label-info btn-round btn-sm">
												<span class="btn-label">
													<i class="fa fa-print"></i>
												</span>
												Print
											</a>
										</div>
									</div>
								</div>
								<div class="card-body">
									<div class="chart-container" style="min-height: 375px">
										<canvas id="statisticsChart"></canvas>
									</div>
									<div id="myChartLegend"></div>
								</div>
							</div>
						</div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import numeroDestinataireService from '../services/numeroDestinataireService'
import type { NumeroDestinataire } from '../types/NumeroDestinataire'

import { getHistoriqueSms } from '../services/historiqueService'
import type { SmsResponseLog } from '../types/historique'

import Chart from 'chart.js/auto'
import "../assets/css/dashboard.css";

const loading = ref(true)
const numeros = ref<NumeroDestinataire[]>([])
const historique = ref<SmsResponseLog[]>([])

// ---------------------- STATISTIQUES SMS PAR MOIS ----------------------
const statsParMois = ref<number[]>([]);
const labelsMois = [
  "Jan", "Fév", "Mar", "Avr", "Mai", "Juin",
  "Juil", "Août", "Sep", "Oct", "Nov", "Déc"
];

let chart: Chart | null = null;

// Calcul des messages envoyés par mois
const calculerStatsMensuelles = () => {
  const tableau = Array(12).fill(0);

  historique.value.forEach(log => {
    if (log.created_at) {
      const date = new Date(log.created_at);
      const mois = date.getMonth(); // 0 = Janvier
      tableau[mois]++;
    }
  });

  statsParMois.value = tableau;
};

// Création du graphique
const genererGraphique = () => {
  const ctx = document.getElementById("statisticsChart") as HTMLCanvasElement;

  if (chart) chart.destroy();

  chart = new Chart(ctx, {
    type: "bar",
    data: {
      labels: labelsMois,
      datasets: [
        {
          label: "Messages envoyés par mois",
          data: statsParMois.value,
          backgroundColor: "rgba(59, 130, 246, 0.6)",
          borderColor: "#3B82F6",
          borderWidth: 1,
          borderRadius: 6,
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false
    }
  });
};

// ---------------------- CHARGEMENT DES DONNÉES ----------------------
const fetchData = async () => {
  try {
    historique.value = await getHistoriqueSms()
    const response = await numeroDestinataireService.getAll()
    numeros.value = response.data

    // Une fois les données chargées → calcul + graphique
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
