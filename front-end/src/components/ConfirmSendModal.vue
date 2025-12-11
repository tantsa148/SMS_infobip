<template>
  <div v-if="show" class="modal-overlay" @click.self="emit('close')">
    <div class="modal-content">

      <div class="modal-header">
        <h5 class="modal-title">Confirmation d’envoi</h5>
      </div>

      <div class="modal-body">

        <div class="alert alert-secondary">
          <h6 class="mb-2">Informations de l’envoi</h6>

          <p>
            <strong>ID Destinataire :</strong> {{ data.idDestinataire }} <br>
            <strong>Numéro Destinataire :</strong> {{ data.numeroDestinataire }}
          </p>

          <p>
            <strong>ID Expéditeur :</strong> {{ data.idExpediteur }} <br>
            <strong>Numéro Expéditeur :</strong> {{ data.numeroExpediteur }}
          </p>

          <p>
            <strong>ID Message :</strong> {{ data.idMessage }} <br>
            <strong>Texte :</strong> {{ data.texteMessage }}
          </p>

        </div>

      </div>

      <div class="modal-footer">
        <button class="btn btn-secondary" @click="emit('close')">Annuler</button>
        <button class="btn btn-primary" @click="emit('confirm')">Confirmer</button>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">

const props = defineProps<{
  show: boolean,
  data: {
    idDestinataire: number,
    numeroDestinataire: string,
    idExpediteur: number,
    numeroExpediteur: string,
    idMessage: number,
    texteMessage: string
  }
}>()

const emit = defineEmits(["close", "confirm"])

</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.modal-content {
  background: white;
  padding: 1.5rem;
  border-radius: 10px;
  width: 420px;
  max-width: 90%;
  box-shadow: 0 4px 20px rgba(0,0,0,0.2);
}

.modal-header, .modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.btn-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}
</style>
