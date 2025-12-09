import api from './api'
import type { NumeroDestinataire } from '../types/NumeroDestinataire'

export default {
  // Récupérer tous les numéros
  getAll(): Promise<{ data: NumeroDestinataire[] }> {
    return api.get('/api/numero-destinataire')
  },

  // Ajouter un nouveau numéro avec une plateforme
  addNumero(data: { valeur: string; plateformeId: number }): Promise<NumeroDestinataire> {
    return api.post('/api/numero-destinataire', data)
    .then(res=> res.data)
  }
}
