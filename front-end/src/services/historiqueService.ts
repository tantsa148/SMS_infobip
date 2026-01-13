import api from './api'
import type { SmsResponseLog } from '../types/historique'

/**
 * Récupération de l'historique SMS (JSON)
 */
export const getHistoriqueSms = async (): Promise<SmsResponseLog[]> => {
  try {
    const response = await api.get<SmsResponseLog[]>('/api/historique')
    return response.data
  } catch (error) {
    console.error('Erreur lors de la récupération de l’historique :', error)
    throw error
  }
}

/**
 * Export de l'historique SMS en CSV (fichier)
 */
export const exportHistoriqueCsv = async (): Promise<void> => {
  try {
    const response = await api.get('/api/historique/export/csv', {
      responseType: 'blob' // ⬅️ indispensable
    })

    const blob = new Blob([response.data], {
      type: 'text/csv;charset=utf-8;'
    })

    const url = window.URL.createObjectURL(blob)

    const link = document.createElement('a')
    link.href = url
    link.download = 'historique.csv'

    document.body.appendChild(link)
    link.click()

    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error('Erreur lors de l’export CSV :', error)
    throw error
  }
}
