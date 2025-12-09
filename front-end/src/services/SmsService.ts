// src/services/MessageService.ts
import api from './api'

interface SendMessageRequest {
  expediteurId: number
  destinataire: string
  message: string
  // Optionnel : ajouter un champ pour différencier le type
  type?: 'sms' | 'whatsapp'
}

interface MessageResponse {
  expediteur: string
  destinataire: string
  message: string
  twilioResponse: string
  status: string
  error: string | null
}

export default {
  async sendSms(data: SendMessageRequest): Promise<MessageResponse> {
    const response = await api.post('/api/sms/send', data)
    return response.data
  },

  async sendWhatsApp(data: SendMessageRequest): Promise<MessageResponse> {
    const response = await api.post('/api/whatsapp/send', data)
    return response.data
  },

  // Méthode unique qui choisit le bon service
  async sendMessage(data: SendMessageRequest, platformType: string): Promise<MessageResponse> {
    if (platformType.toLowerCase().includes('whatsapp')) {
      return this.sendWhatsApp(data)
    } else {
      return this.sendSms(data)
    }
  }
}