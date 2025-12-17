// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import DestinataireView from '../views/DestinataireView.vue'
import Expediteur from '../views/Expediteur.vue'
import FormulaireMessage from '../views/sms.vue'
import MessageTexte from '../views/MessageTexte.vue'
import historiqueView from '../views/historiqueView.vue'
import DashboardView from '../views/DashboardView.vue'
import ControllersView from '../views/ControllersView.vue'
const routes = [
  { path: '/', name: 'Login', component: LoginView },
  { path: '/acceuil', name: 'acceuil', component: DashboardView },  
  {path:'/destinataire', name: 'Destinataire',component:DestinataireView},
  {path:'/expediteur', name: 'Expediteur',component:Expediteur},
  {path:'/formulaireMessage',name: 'Envoyer un message',component:FormulaireMessage},
  {path:'/messageTexte',name: 'Liste Message',component:MessageTexte},
  {path:'/historique',name: 'Historique',component:historiqueView},
  {path:'/controller',name:'Controller',component:ControllersView},
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
