// services/numeroExpediteurService.ts
import type { 
  NumeroExpediteur, 
  AddNumeroExpediteurRequest,
  InfobipPayload 
} from '../types/NumeroExpediteur'
import api from './api'

export default {
  add(data: AddNumeroExpediteurRequest): Promise<NumeroExpediteur> {
    console.log('Envoi des données au serveur:', data)

    return api.post<NumeroExpediteur>('/api/numeros-expediteur', data)
      .then(res => {
        console.log('Réponse du serveur:', res.data)
        return res.data
      })
      .catch(error => {
        console.error('Erreur détaillée lors de l’ajout du numéro:', error.response?.data || error)
        throw error
      })
  }
}