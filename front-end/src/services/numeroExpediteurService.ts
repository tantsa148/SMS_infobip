// services/numeroExpediteurService.ts
import type { NumeroExpediteur } from '../types/NumeroExpediteur'
import api from './api'

export default {
  // Récupérer tous les numéros
  getAll(): Promise<NumeroExpediteur[]> {
    return api.get<NumeroExpediteur[]>('/api/numero-expediteur')
      .then(res => res.data)
  },

  // Ajouter un nouveau numéro
  add(data: { valeur: string; idPlateforme: number }): Promise<NumeroExpediteur> {
    return api.post<NumeroExpediteur>('/api/numero-expediteur', data)
      .then(res => res.data) // res.data contient maintenant id, valeur, message, etc.
  }
}
