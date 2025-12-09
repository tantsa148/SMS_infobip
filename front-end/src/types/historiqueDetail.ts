export interface historiqueDetail {
  logId: number
  utilisateur: string
  expediteurNumero: string
  destinataireNumero: string
  messageTexte: string
  status: string
  twilioErrorCode: number | null
  twilioErrorMessage: string | null
  twilioErrorMoreInfo: string | null
  twilioSid: string | null
  createdAt: string
  platform: string | null
}
