<template>
  <nav class="navbar-header">
    <div class="container d-flex align-items-center justify-content-between">

      <!-- Logo -->
      <div class="navbar-brand">
        <h2>{{ pageTitle }}</h2>
      </div>

      <!-- Groupe solde + utilisateur -->
      <div class="user-balance-group d-flex align-items-center gap-3">

        <!-- Sélecteur de numéro + bouton Voir -->
        <div v-if="usersDetails.length > 0" class="d-flex align-items-center gap-2">
          <select class="form-select" v-model="selectedIdNumero">
            <option v-for="ud in usersDetails" :key="ud.idNumero" :value="ud.idNumero">
              {{ ud.numeroExpediteur }}
            </option>
          </select>
          <button class="btn btn-primary btn-sm" @click="fetchBalance">
            Solde
          </button>
        </div>

        <!-- Solde Infobip -->
        <div class="Infobip-balance-wrapper" v-if="balance">
          <div class="Infobip-balance d-flex align-items-center gap-2">
            <i class="fa fa-wallet balance-icon"></i>
            <div class="balance-text">
              <span class="balance-amount">{{ balance.balance }} {{ balance.currency }}</span>
            </div>
          </div>
        </div>

        <div class="vertical-separator" v-if="balance"></div>

        <!-- Menu utilisateur -->
        <div class="user-profile position-relative">
          <button 
            class="btn btn-outline-secondary btn-sm user-btn d-flex align-items-center gap-2"
            @click="toggleUserMenu"
          >
            <i class="fa fa-user user-icon"></i>
            <span class="username">{{ currentUser ? currentUser.username : 'Utilisateur' }}</span>
            <i class="fa fa-chevron-down dropdown-arrow" :class="{ 'rotate': showUserMenu }"></i>
          </button>

          <div v-if="showUserMenu" class="user-dropdown shadow-dropdown">
            <div class="user-info">
              <div class="user-avatar">
                <i class="fa fa-user-circle"></i>
              </div>
              <div class="user-details">
                <p class="user-name">{{ currentUser ? currentUser.username : 'Utilisateur' }}</p>
                <p class="user-role" v-if="currentUser">{{ currentUser.role }}</p>
              </div>
            </div>
            <div class="user-menu">
              <a href="/expediteur" class="menu-item">
                <i class="fa fa-user"></i> Mes numéros
              </a>
              <a href="#" class="menu-item">
                <i class="fa fa-cog"></i> Paramètres
              </a>
              <a href="#" class="menu-item">
                <i class="fa fa-question-circle"></i> Aide
              </a>
              <div class="menu-divider"></div>
              <a href="#" class="menu-item logout" @click="logout">
                <i class="fa fa-sign-out"></i> Déconnexion
              </a>
            </div>
          </div>
        </div>

      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import { getCurrentUser } from '../services/userService';
import BalanceService from '../services/balanceService';
import UsersDetailService from '../services/usersDetailService';
import type { InfobipBalance } from '../services/balanceService';
import type { UserDTO } from '../types/user';
import type { UsersDetail } from '../types/UsersDetail';
import '../assets/css/NavbarHeader.css';

const showUserMenu = ref(false);
const currentUser = ref<UserDTO | null>(null);
const balance = ref<InfobipBalance | null>(null);
const usersDetails = ref<UsersDetail[]>([]);
const selectedIdNumero = ref<number | null>(null);

const route = useRoute();
const pageTitle = ref<string>('');

// Formater le nom de page
function formatPageName(name: string) {
  return name.replace(/([A-Z])/g, ' $1').replace(/^./, str => str.toUpperCase());
}

// Récupérer l'utilisateur courant et les détails des numéros
onMounted(async () => {
  currentUser.value = await getCurrentUser();

  try {
    usersDetails.value = await UsersDetailService.getAll();
    if (usersDetails.value.length > 0) {
      selectedIdNumero.value = usersDetails.value[0].idNumero;
    }
  } catch (error) {
    console.error('Erreur récupération des numéros utilisateur', error);
  }

  pageTitle.value = route.name ? formatPageName(String(route.name)) : 'Accueil';
});

// Mettre à jour le titre si la route change
watch(() => route.name, (newName) => {
  pageTitle.value = newName ? formatPageName(String(newName)) : 'Accueil';
});

// Récupérer le solde selon l'idNumero sélectionné
async function fetchBalance() {
  if (!selectedIdNumero.value) return;
  try {
    balance.value = await BalanceService.getBalance(selectedIdNumero.value);
  } catch (error) {
    console.error('Erreur récupération solde Infobip', error);
  }
}

function toggleUserMenu() {
  showUserMenu.value = !showUserMenu.value;
}

function logout() {
  console.log('Déconnexion');
  showUserMenu.value = false;
  localStorage.removeItem('token');
  window.location.reload();
}

// Fermer le menu si clic à l'extérieur
document.addEventListener('click', (e) => {
  const target = e.target as HTMLElement | null;
  if (!target) return;
  if (!target.closest('.user-profile')) showUserMenu.value = false;
});
</script>
