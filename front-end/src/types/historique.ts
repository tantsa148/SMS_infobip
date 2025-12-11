export interface SmsResponseLog {
  idEnvoi: number
  idUtilisateur: number
  expediteur: string
  numeroExpediteur: string
  numeroDestinataire: string
  message: string
  infobipMessageId: string
  dateEnvoi: string // ou Date si tu parsers la réponse
}
