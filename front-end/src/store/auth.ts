import { defineStore } from 'pinia'
import { login as loginApi, logout as logoutApi, validateToken, getCurrentUser } from '../services/authService'

export interface LoginData {
  username: string
  password: string
}

interface AuthState {
  token: string | null
  username: string | null
  role: string | null
  isAuthenticated: boolean
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: localStorage.getItem('token') || null,
    username: localStorage.getItem('username') || null,
    role: localStorage.getItem('role') || null,
    isAuthenticated: !!localStorage.getItem('token')
  }),
  
  getters: {
    // Getter pour vérifier si l'utilisateur est admin
    isAdmin: (state) => state.role === 'ADMIN',
    // Getter pour obtenir l'utilisateur complet
    currentUser: (state) => ({
      username: state.username,
      role: state.role
    })
  },
  
  actions: {
    async login(data: LoginData) {
      try {
        const result = await loginApi(data)
        
        if (result.success && result.token) {
          this.token = result.token
          this.username = result.user.username
          this.role = result.user.role
          this.isAuthenticated = true
          
          // Sauvegarder dans localStorage
          localStorage.setItem('token', result.token)
          localStorage.setItem('username', result.user.username)
          localStorage.setItem('role', result.user.role)
          
          return { success: true, data: result }
        } else {
          return { success: false, message: result.message }
        }
      } catch (error: any) {
        return { 
          success: false, 
          message: error.response?.data?.message || 'Erreur de connexion' 
        }
      }
    },
    
    async logout() {
      try {
        // Appeler le logout backend
        await logoutApi()
      } catch (error) {
        console.error('Erreur lors du logout backend:', error)
        // On continue quand même avec le logout local
      } finally {
        // Nettoyer le store
        this.clearAuth()
      }
    },
    
    clearAuth() {
      this.token = null
      this.username = null
      this.role = null
      this.isAuthenticated = false
      
      // Nettoyer le localStorage
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
    },
    
    async checkAuth() {
      if (!this.token) {
        this.clearAuth()
        return false
      }
      
      try {
        const validation = await validateToken()
        
        if (validation.valid && !validation.revoked) {
          // Token valide, on peut rafraîchir les infos utilisateur
          if (validation.username && validation.username !== this.username) {
            await this.refreshUserInfo()
          }
          return true
        } else {
          // Token invalide
          this.clearAuth()
          return false
        }
      } catch (error) {
        console.error('Erreur lors de la vérification du token:', error)
        this.clearAuth()
        return false
      }
    },
    
    async refreshUserInfo() {
      try {
        const userData = await getCurrentUser()
        if (userData.success && userData.user) {
          this.username = userData.user.username
          this.role = userData.user.role
          
          localStorage.setItem('username', userData.user.username)
          localStorage.setItem('role', userData.user.role)
        }
      } catch (error) {
        console.error('Erreur lors du rafraîchissement des infos utilisateur:', error)
      }
    },
    
    // Initialiser depuis le localStorage
    initialize() {
      const token = localStorage.getItem('token')
      const username = localStorage.getItem('username')
      const role = localStorage.getItem('role')
      
      if (token) {
        this.token = token
        this.username = username
        this.role = role
        this.isAuthenticated = true
        
        // Vérifier la validité du token
        this.checkAuth()
      }
    }
  }
})