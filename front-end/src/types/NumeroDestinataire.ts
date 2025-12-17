// types/NumeroDestinataire.ts
export interface Plateforme {
  id: number;
}

export interface NumeroDestinataire {
  idNumero: number;
  valeur: string;
  dateCreation: string;
  plateforme: Plateforme; // ajouté pour correspondre au JSON attendu
}
