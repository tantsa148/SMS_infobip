import api from './api'
import type { MessageTexte } from '../types/MessageTexte'

export default {
  // Récupérer tous les messages 
  getAll(): Promise<{ data: MessageTexte[] }> {
    return api.get('/api/messages')
  },
}