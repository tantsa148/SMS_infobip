// src/services/twilioService.ts
import api from './api'

export interface TwilioBalance {
  balance: string
  currency: string
}

const getBalance = async (): Promise<TwilioBalance> => {
  try {
    const response = await api.get<TwilioBalance>('api/twilio/balance')
    return response.data
  } catch (error) {
    console.error('Erreur lors de la récupération du solde Twilio:', error)
    throw error
  }
}

export default {
  getBalance
}
