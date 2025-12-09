import axios from 'axios'

// Création d'une instance axios personnalisée
const api = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
})

// Intercepteur pour ajouter le token avant chaque requête
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  
  return config
})

// Intercepteur de réponse pour gérer les erreurs d'authentification
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token invalide ou expiré - nettoyer le localStorage
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
      
      // Rediriger vers la page de login si on n'y est pas déjà
      if (window.location.pathname !== '/') {
        window.location.href = '/'
      }
    }
    return Promise.reject(error)
  }
)

export default api