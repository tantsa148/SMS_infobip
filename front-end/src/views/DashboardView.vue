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
          <MessagesChart 
            :historique="historique" 
            :annees-disponibles="anneesDisponibles"
            @year-change="onYearChange"
            @month-change="onMonthChange"
          />
        </div>

        <!-- Graphique Coûts (Ligne) -->
        <div class="col-md-4">
          <CostsChart 
            :messages-details="messagesDetails"
            :annees-disponibles="anneesDisponibles"
            @year-change="onYearChange"
          />
        </div>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="text-center mt-3">
        <div class="spinner-border text-primary" role="status"></div>
        <p class="mt-2 text-muted">Chargement des données...</p>
      </div>

      <!-- Tableau des messages par numéro -->
      <div v-if="!loading" class="row mt-4">
        <div class="col-md-12">
          <div class="card card-round">
            <div class="card-header">
              <div class="card-head-row">
                <div class="card-title">Messages envoyés par numéro</div>
              </div>
            </div>
            <div class="card-body">
              <div class="table-responsive">
                <table class="table table-hover">
                  <thead>
                    <tr>
                      <th></th>
                      <th>Numéro destinataire</th>
                      <th>Nombre de messages</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="(item, index) in messagesParNumero" :key="item.numero">
                      <td>{{ index + 1 }}</td>
                      <td>{{ item.numero }}</td>
                      <td><strong>{{ item.count }}</strong></td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
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
import type { SmsResponseLog } from '../types/historique'
import MessagesChart from '../components/charts/MessagesChart.vue'
import CostsChart from '../components/charts/CostsChart.vue'
import "../assets/css/dashboard.css"

interface NumeroDestinataire {
  idNumero: number
  valeur: string
  dateCreation: string
  plateforme: any
}

// Réactivité
const loading = ref(true)
const numeros = ref<NumeroDestinataire[]>([])
const historique = ref<SmsResponseLog[]>([])
const messagesDetails = ref<MessageDetail[]>([])

// Computed pour sécuriser affichage des longueurs
const nombreNumeros = computed(() => numeros.value.length)
const nombreMessages = computed(() => historique.value.length)

// Messages par numéro et plateforme
const messagesParNumero = computed(() => {
  const counts: Record<string, { count: number; plateforme: string }> = {}
  
  historique.value.forEach(log => {
    if (log.numeroDestinataire) {
      const key = log.numeroDestinataire
      if (!counts[key]) {
        counts[key] = { count: 0, plateforme: log.plateforme || 'SMS' }
      }
      counts[key].count++
    }
  })
  
  return Object.entries(counts)
    .map(([numero, data]) => ({
      numero,
      count: data.count,
      plateforme: data.plateforme
    }))
    .sort((a, b) => b.count - a.count) // Tri par nombre de messages décroissant
})

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

// Gestionnaires d'événements pour les charts
const onYearChange = (year: number) => {
  console.log('Année sélectionnée:', year)
}

const onMonthChange = (month: number) => {
  console.log('Mois sélectionné:', month)
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

  } catch (err) {
    console.error('Erreur chargement :', err)
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)
</script>
