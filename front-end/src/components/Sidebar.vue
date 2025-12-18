<template>
  <div class="sidebar-custom">
    <!-- En-tête -->
    <div class="d-flex justify-center items-center mb-4" style="height: 80px;">
      <h1>SMS</h1>
    </div>

    <!-- Section Statistique -->
    <ul class="nav flex-column mb-4">
      <li class="nav-item">
        <RouterLink
          to="/acceuil"
          class="nav-link"
          :class="{ active: route.path === '/acceuil' }"
        >
          <i class="fas fa-home"></i>
          Dashboard
        </RouterLink>
      </li>
    </ul>

    
    <ul class="nav flex-column">
      <li class="nav-item">
        <RouterLink
          to="/historique"
          class="nav-link"
          :class="{ active: route.path === '/historique' }"
        >
          <i class="fa fa-history"></i>
        historique
        </RouterLink>
      </li>
    </ul>


    <ul class="nav flex-column">
      <li class="nav-item">
        <RouterLink
          to="/destinataire"
          class="nav-link"
          :class="{ active: route.path === '/destinataire' }"
        >
          <i class="fas fa-user-friends"></i>
        Numero utilisateur 
        </RouterLink>
      </li>
    </ul>

    <ul class="nav flex-column">
      <li class="nav-item">
        <RouterLink
          to="/messageTexte"
          class="nav-link"
          :class="{ active: route.path === '/messageTexte' }"
        >
          <i class="fas fa-user-friends"></i>
        Message enregistre 
        </RouterLink>
      </li>
    </ul>
    <ul class="nav flex-column">
      <li class="nav-item">
        <RouterLink
          to="/messageTexte"
          class="nav-link"
          :class="{ active: route.path === '/messageTexte' }"
        >
          <i class="fas fa-user-friends"></i>
        donne messages 
        </RouterLink>
      </li>
    </ul>
    <ul class="nav flex-column">
      <li class="nav-item">
        <RouterLink
          to="/messageTexte"
          class="nav-link"
          :class="{ active: route.path === '/messageTexte' }"
        >
          <i class="fas fa-user-friends"></i>
        Message enregistre 
        </RouterLink>
      </li>
    </ul>


 <!-- Bouton Déconnexion -->
    <ul class="nav flex-column mt-auto">
      <li class="nav-item">
        <button 
          class="btn btn-danger w-100" 
          @click="handleLogout"
          :disabled="loading"
        >
          <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
          <i v-else class="bi bi-box-arrow-right me-2"></i>
          {{ loading ? 'Déconnexion...' : 'Déconnexion' }}
        </button>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute, RouterLink } from 'vue-router'
import { logout } from '../services/authService'
import '../assets/css/sidebar.css'

const router = useRouter()
const route = useRoute()
const loading = ref(false)

const handleLogout = async () => {
  if (loading.value) return
  
  loading.value = true
  
  try {
    // Appeler le service de déconnexion
    const response = await logout()
    console.log('Déconnexion réussie:', response)
    
    // Redirection vers la page de login
    router.push('/')
    
  } catch (error: any) {
    console.error('Erreur lors de la déconnexion:', error)
    
    // Même en cas d'erreur, on nettoie le token local
    localStorage.removeItem('token')
    
    // Afficher un message à l'utilisateur si besoin
    alert('Déconnexion complétée (avec certaines erreurs techniques)')
    
    // Redirection quand même
    router.push('/')
    
  } finally {
    loading.value = false
  }
}
</script>