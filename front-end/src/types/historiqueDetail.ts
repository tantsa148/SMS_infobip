// types/historiqueDetail.ts
export interface historiqueDetail {
  idEnvoi: number  
  messageId: string
  to: string
  from: string
  text: string
  sentAt: string
  doneAt: string
  smsCount: string
  mccMnc: string
  price: {
    pricePerMessage: string
    currency: string
  }
  status: {
    groupId: string
    groupName: string
    id: string
    name: string
    description: string
  }
  error: {
    groupId: string
    groupName: string
    id: string
    name: string
    description: string
    permanent: string
  }
  applicationId: string
}
