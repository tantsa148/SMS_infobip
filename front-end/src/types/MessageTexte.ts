export interface Evenement {
  id: number
  code: string
  description: string
  dateCreation: string // ISO date string
}

export interface MessageTexte {
  id: number
  texte: string
  evenement: Evenement | null
}
