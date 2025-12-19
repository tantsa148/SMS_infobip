import api from './api'
import type { ModeleMessageDTO } from '../types/ModeleMessage'

const MODELE_MESSAGE_URL = '/api/modele-message'

export default class ModeleMessageService {
  
  static async getAll(): Promise<ModeleMessageDTO[]> {
    const response = await api.get<ModeleMessageDTO[]>(MODELE_MESSAGE_URL)
    return response.data
  }

  static async getById(id: number): Promise<ModeleMessageDTO> {
    const response = await api.get<ModeleMessageDTO>(`${MODELE_MESSAGE_URL}/${id}`)
    return response.data
  }

    static async create(payload: {
      methode: string,
      idExpediteur: number,
      idMessage: number
    }): Promise<ModeleMessageDTO> {
      // Reformater le payload pour l'API
      const body = {
        methode: payload.methode,
        expediteur: { id: payload.idExpediteur },
        message: { id: payload.idMessage }
      }

      const response = await api.post<ModeleMessageDTO>(MODELE_MESSAGE_URL, body)
      return response.data
    }

  static async update(id: number, payload: Partial<ModeleMessageDTO>): Promise<ModeleMessageDTO> {
    const response = await api.put<ModeleMessageDTO>(`${MODELE_MESSAGE_URL}/${id}`, payload)
    return response.data
  }

  static async delete(id: number): Promise<void> {
    await api.delete(`${MODELE_MESSAGE_URL}/${id}`)
  }
}
