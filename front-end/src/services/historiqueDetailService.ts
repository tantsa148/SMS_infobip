// services/historiqueDetailService.ts
import api from './api'
import type { historiqueDetail } from '../types/historiqueDetail'

export const getHistoriqueSmsById = async (idEnvoi: number): Promise<historiqueDetail> => {
  try {
    const response = await api.get<{ results: { result: historiqueDetail } }>(
      `/api/historique/${idEnvoi}`
    )
    // On récupère directement le résultat Infobip
    return response.data.results.result
  } catch (error) {
    console.error(`Erreur lors de la récupération du détail du SMS ${idEnvoi}:`, error)
    throw error
  }
}

// --------------------------------------------------
// Nouvelle fonction : télécharger le PDF du SMS
// --------------------------------------------------
export const exportHistoriqueSmsPdf = async (idEnvoi: number): Promise<void> => {
  try {
    const response = await api.get(`/api/historique/${idEnvoi}/export/pdf`, {
      responseType: 'blob', // 🔴 Indispensable pour un PDF
    })

    const blob = response.data as Blob
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')

    link.href = url
    link.download = `sms_detail_${idEnvoi}.pdf`

    document.body.appendChild(link)
    link.click()

    link.remove()
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error(`Erreur lors du téléchargement du PDF du SMS ${idEnvoi}:`, error)
    alert('Impossible de télécharger le PDF')
  }
}
