import api from "./api";

export default {
  sendSms(payload: {
    idNumeroExpediteur: number,
    idNumeroDestinataire: number,
    idMessage: number
    
  }) {
    return api.post("/api/sms/send", payload);
  },

  sendSmsWithMessage(payload: {
    idNumeroExpediteur: number,
    idNumeroDestinataire: number,
    message: string
  }) {
    return api.post("/api/sms/send", payload);
  }
}
