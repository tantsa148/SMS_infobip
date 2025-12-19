export interface Plateforme {
  id: number
  nomPlateforme: string
  dateCreation: string
}

export interface NumeroDestinataire {
  idNumero: number
  valeur: string
  dateCreation: string
  plateforme: Plateforme | null
  idUser: number
}
