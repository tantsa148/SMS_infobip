import api from './api'
import type { NumeroDestinataire } from '../types/NumeroDestinataire'

export default {
  // Récupérer tous les numéros
  getAll(): Promise<{ data: NumeroDestinataire[] }> {
    return api.get('/api/numeros_destinataire')
  },

  // Ajouter un nouveau numéro (corrigé)
  addNumero(data: { valeur: string }): Promise<NumeroDestinataire> {
    return api.post('/api/numeros_destinataire', data)
      .then(res => res.data)
  }
}