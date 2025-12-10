// services/numeroExpediteurService.ts
import type { NumeroExpediteur } from '../types/NumeroExpediteur'
import api from './api'

export default {
  // Ajouter un nouveau numéro avec les infos Infobip
  add(data: { 
    valeur: string; 
    infobipInfo: {
      apiKey: string;
      baseUrl: string;
    }
  }): Promise<NumeroExpediteur> {
    console.log('Envoi des données au serveur:', {
      ...data,
      infobipInfo: {
        ...data.infobipInfo,
        apiKey: data.infobipInfo.apiKey ? '***' + data.infobipInfo.apiKey.slice(-4) : 'non fournie'
      }
    })
    
    return api.post<NumeroExpediteur>('/api/numeros-Expediteur', data)
      .then(res => {
        console.log('Réponse du serveur:', res.data)
        return res.data
      })
      .catch(error => {
        console.error('Erreur détaillée:', {
          status: error.response?.status,
          statusText: error.response?.statusText,
          data: error.response?.data,
          headers: error.response?.headers,
          config: {
            url: error.config?.url,
            method: error.config?.method,
            data: error.config?.data
          }
        })
        throw error
      })
  }
}