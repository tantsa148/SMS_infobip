export interface MessageDetail {
  id: number
  messageId: string
  text: string
  sentAt: string // Format ISO: "2026-01-04T17:58:52.675"
  doneAt: string // Format ISO: "2026-01-04T17:58:54.742"
  statusId: number
  statusName: string
  statusDescription: string
  pricePerMessage: number
  currency: string
  dateCreation: string // Format ISO: "2026-01-05T11:09:44.793459"
}
