import api from './api'
import type { MessageTexte } from '../types/MessageTexte'

export default {
  // Récupérer tous les messages
  getAll(): Promise<{ data: MessageTexte[] }> {
    return api.get('/api/messages')
  },

  // 🔹 Créer un nouveau message avec l'événement
  create(payload: { texte: string; evenementId: number }): Promise<{ data: MessageTexte }> {
    return api.post('/api/messages', {
      texte: payload.texte,
      evenement: {
        id: payload.evenementId
      }
    })
  }
}
