import api from './api'

export interface InfobipBalance {
  balance: number
  currency: string
}

/**
 * Récupère le solde depuis le endpoint Spring Boot Infobip
 * @param idNumero - L'ID du numéro expéditeur
 */
const getBalance = async (idNumero: number = 1): Promise<InfobipBalance> => {
  try {
    // Appel direct au endpoint Spring Boot
    const response = await api.get<InfobipBalance>(`/api/infobip/balance?idNumero=${idNumero}`)
    return response.data
  } catch (error) {
    console.error(`Erreur lors de la récupération du solde Infobip pour idNumero=${idNumero}:`, error)
    throw error
  }
}

export default {
  getBalance
}
