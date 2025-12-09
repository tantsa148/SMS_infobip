export interface NumeroExpediteur {
  id: number;
  valeur: string;
  dateCreation: string;
  userId: number;
  userUsername: string;
  plateformes: string[];  // 🔹 liste de noms de plateformes
  message: string;        // 🔹 le message d'erreur/succès
}
