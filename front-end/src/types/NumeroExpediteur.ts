// types/NumeroExpediteur.ts

export interface InfobipInfo {
  id?: number;
  apiKey?: string;
  baseUrl?: string;
  numeros?: NumeroExpediteur[] | null;
}

export interface Plateforme {
  id: number;
  nomPlateforme: string;
  dateCreation: string;
}

export interface NumeroExpediteur {
  id: number;
  valeur: string;
  dateCreation: string;
  infobipInfo: InfobipInfo;
  plateforme: Plateforme;
}

// Pour les payloads d'envoi
export interface InfobipPayload {
  id?: number;
  apiKey?: string;
  baseUrl?: string;
}

export interface AddNumeroExpediteurRequest {
  valeur: string;
  infobipInfo: InfobipPayload;
  idPlateforme?: number;
}

// Optionnel : pour les réponses d'erreur si nécessaire
export interface ApiError {
  message: string;
  status?: number;
  details?: any;
}