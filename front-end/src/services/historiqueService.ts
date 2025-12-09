// services/historiqueService.ts
import api from './api'
import type { SmsResponseLog } from '../types/historique'

export const getHistoriqueSms = async (): Promise<SmsResponseLog[]> => {
  try {
    const response = await api.get<SmsResponseLog[]>('/api/historique')
    return response.data
  } catch (error) {
    console.error('Erreur lors de la récupération de l\'historique :', error)
    throw error
  }
}
