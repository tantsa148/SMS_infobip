import api from "./api";

export default {
  sendSms(payload: {
    idNumeroExpediteur: number,
    idNumeroDestinataire: number,
    idMessage: number
  }) {
    return api.post("/api/sms/send", payload);
  }
}
