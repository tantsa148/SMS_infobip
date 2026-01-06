import api from './api' // Votre instance axios configurée
import type { MessageDetail } from '../types/messageDetail'

export const messageDetailService = {
  /**
   * Récupère tous les MessageDetail
   */
  async getAll(): Promise<MessageDetail[]> {
    try {
      const response = await api.get<MessageDetail[]>('/api/messagedetail')
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des messages:', error)
      throw error
    }
  },

  /**
   * Récupère un MessageDetail par son ID
   */
  async getById(id: number): Promise<MessageDetail> {
    try {
      const response = await api.get<MessageDetail>(`/api/messagedetail/${id}`)
      return response.data
    } catch (error) {
      console.error(`Erreur lors de la récupération du message ${id}:`, error)
      throw error
    }
  },

  /**
   * Formate une date ISO en format lisible
   */
  formatDate(isoDate: string): string {
    if (!isoDate) return 'N/A'
    
    const date = new Date(isoDate)
    return new Intl.DateTimeFormat('fr-FR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    }).format(date)
  },

  /**
   * Formate le prix avec la devise
   */
  formatPrice(price: number, currency: string): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: currency || 'USD'
    }).format(price)
  }
}

export default messageDetailService