import * as bootstrap from 'bootstrap'

let modalInstance: bootstrap.Modal | null = null

onMounted(() => {
  if (modalRef.value) {
    modalInstance = new bootstrap.Modal(modalRef.value, {
      backdrop: 'static', // empêche la fermeture en cliquant derrière (optionnel)
      keyboard: false      // empêche fermeture au clavier (optionnel)
    })
  }
})

const ouvrirModal = async (id: number) => {
  try {
    logDetail.value = await getHistoriqueSmsById(id)
    modalInstance?.show()
  } catch (error) {
    console.error('Erreur récupération détail', error)
    apiMessage.value = 'Erreur lors du chargement du détail.'
  }
}

const fermerModal = () => {
  modalInstance?.hide()
}
