import api from './api'
import type { LoginData } from '../store/auth'

export interface LoginResponse {
  success: boolean
  message: string
  token: string
  user: {
    username: string
    role: string
  }
}

export interface LogoutResponse {
  success: boolean
  message: string
  timestamp: string
  tokenRevoked: boolean
}

export interface ValidateTokenResponse {
  valid: boolean
  error?: string
  revoked?: boolean
  username?: string
  expiresAt?: string
  expiresIn?: number
}

const API_PREFIX = '/api/auth'

// Fonction login à importer dans le store
export async function login(data: LoginData): Promise<LoginResponse> {
  const response = await api.post<LoginResponse>(`${API_PREFIX}/login`, data)
  return response.data
}

export async function logout(): Promise<LogoutResponse> {
  try {
    const response = await api.post<LogoutResponse>(`${API_PREFIX}/logout`)
    return response.data
  } catch (error: any) {
    throw error
  }
}

export async function validateToken(): Promise<ValidateTokenResponse> {
  try {
    const response = await api.get<ValidateTokenResponse>(`${API_PREFIX}/validate`)
    return response.data
  } catch (error: any) {
    return {
      valid: false,
      error: error.response?.data?.error || 'Token validation failed'
    }
  }
}

export async function getCurrentUser() {
  try {
    const response = await api.get(`${API_PREFIX}/me`)
    return response.data
  } catch (error: any) {
    throw error
  }
}