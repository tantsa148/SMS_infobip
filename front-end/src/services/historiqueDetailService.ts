// services/historiqueDetailService.ts
import api from './api'
import type { historiqueDetail } from '../types/historiqueDetail'

export const getHistoriqueSmsById = async (idEnvoi: number): Promise<historiqueDetail> => {
  try {
    const response = await api.get<{ results: { result: historiqueDetail } }>(`/api/historique/${idEnvoi}`)
    // On récupère directement le résultat Infobip
    return response.data.results.result
  } catch (error) {
    console.error(`Erreur lors de la récupération du détail du SMS ${idEnvoi}:`, error)
    throw error
  }
}
