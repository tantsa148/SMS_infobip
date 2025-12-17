// src/services/NumeroDestinataireService.js
import api from "./api"; // ton fichier api.js avec l'instance Axios

export default {
  async getAll() {
    try {
      const response = await api.get("/api/numeros-destinataire");
      return response.data; // renvoie le tableau des numéros
    } catch (error) {
      console.error("Erreur lors de la récupération des numéros :", error);
      throw error; // on remonte l'erreur au composant
    }
  },
   // POST
   
  async addNumero(data: { valeur: string, plateforme: { id: number } }) {
    console.log("Données envoyées au backend :", data);


    try {
      const response = await api.post("/api/numeros-destinataire", data)
    
      return response.data
    } catch (error) {
      console.error("Erreur ajout numéro :", error)
    
      throw error
    }
    
  }
};
