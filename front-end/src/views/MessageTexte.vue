<template>
  <div class="container mt-5">
    <!-- LOADING -->
    <div v-if="loading" class="text-center">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Chargement...</span>
      </div>
      <p class="mt-2 text-muted">Chargement de vos messages...</p>
    </div>

    <!-- CARD -->
    <div v-else class="card shadow">
      <div class="card-header d-flex justify-content-between align-items-center">
        <div class="card-title mb-0">Messages enregistrés</div>

        <button 
          class="btn btn-primary btn-sm"
          style="width: 100px"
          @click="refreshMessages"
        >
          Rafraîchir
        </button>
      </div>

      <div class="card-body">
        <!-- AUCUN MESSAGE -->
        <div v-if="messages.length === 0" class="text-center py-4">
          <div class="text-muted mb-3">💬</div>
          <p class="text-muted mb-2">Aucun message trouvé</p>
        </div>

        <!-- TABLEAU -->
        <div v-else>
          <table class="table table-hover">
            <thead>
              <tr>
                <th>#</th>
                <th>Message</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(msg, index) in messages" :key="msg.id">
                <td>{{ index + 1 }}</td>
                <td>{{ msg.texte }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- FOOTER -->
      <div v-if="messages.length > 0" class="card-footer">
        <small class="text-muted">Total : {{ messages.length }} message(s)</small>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue"
import messageService from "../services/messageService"
import type { MessageTexte } from "../types/MessageTexte"

const loading = ref(true)
const messages = ref<MessageTexte[]>([])

const fetchMessages = async () => {
  try {
    const response = await messageService.getAll()
    messages.value = response.data
  } catch (err) {
    console.error("Erreur chargement messages :", err)
  } finally {
    loading.value = false
  }
}

const refreshMessages = () => {
  loading.value = true
  fetchMessages()
}

onMounted(fetchMessages)
</script>


