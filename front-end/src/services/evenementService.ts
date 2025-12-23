import api from './api'
import type { Evenement } from '../types/Evenement'

export default {
  getAll(): Promise<{ data: Evenement[] }> {
    return api.get('/api/evenements')
  },

  create(payload: { code: string; description: string }) {
    return api.post('/api/evenements', payload)
  }
}
