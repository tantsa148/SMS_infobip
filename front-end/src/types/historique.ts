// types/historique.ts
export interface SmsResponseLog {
  log_id: number;
  twilio_response: string | null;
  status: string;
  twilio_error_code: number | null;
  expediteur_id: number | null;
  destinataire_id: number | null;
  destinataire_numero: string | null;
  message_id: number | null;
  platform:string;
  created_at: string;
}
