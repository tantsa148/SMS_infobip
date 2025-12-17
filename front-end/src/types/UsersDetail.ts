export type UsersDetail = {
  idUtilisateur: number;

  // Numéro expéditeur
  idNumero: number;
  numeroExpediteur: string;        // correspond à "valeurNumero"
  dateCreation: string;            // date de création du UsersDetail / du numéro

  // Infobip info
  idInfobip: number;
  apiKey: string;
  baseUrl: string;

  // Infos utilisateur
  idUser: number;
  username: string;
  role: string;
  userCreatedAt: string;           // date de création du user

  idPlateforme: number;
  nomPlateforme: string;
  
};
