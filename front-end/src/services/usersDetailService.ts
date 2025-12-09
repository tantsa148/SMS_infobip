import api from './api'
import type { UsersDetail } from '../types/UsersDetail'

export default {
  async getAll(): Promise<UsersDetail[]> {
    const response = await api.get<UsersDetail[]>('/api/users-detail')
    return response.data
  }
}
