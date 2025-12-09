import api from './api'; // ton axios personnalisé
import type { UserDTO } from '../types/user'; // interface TS optionnelle pour le DTO

export const getCurrentUser = async (): Promise<UserDTO | null> => {
  try {
    const response = await api.get('/api/auth/me');

    // Vérifie que la requête a réussi et qu'il y a un utilisateur
    if (response.data && response.data.success && response.data.user) {
      return response.data.user as UserDTO; // 🔑 on retourne uniquement "user"
    }

    return null;
  } catch (error: any) {
    console.error(
      'Erreur lors de la récupération de l’utilisateur connecté :',
      error.response?.data || error.message
    );
    return null;
  }
};
