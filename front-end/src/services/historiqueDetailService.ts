import api from './api' // ton axios déjà configuré
import type { historiqueDetail } from '../types/historiqueDetail'

export const getHistoriqueSmsById = async (id: number): Promise<historiqueDetail> => {
  try {
    const response = await api.get<historiqueDetail>(`/api/historique-sms/${id}`)
    return response.data
  } catch (error) {
    console.error(`Erreur lors de la récupération du log ${id}`, error)
    throw error
  }
}
