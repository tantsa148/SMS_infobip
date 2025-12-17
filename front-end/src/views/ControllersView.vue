<template>
  <div>
    <h2>Liste des Controllers de B</h2>
    <ul>
      <li v-for="ctrl in controllers" :key="ctrl.controller + ctrl.method">
        {{ ctrl.controller }} - {{ ctrl.method }} - {{ ctrl.url }}
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from "vue";
import ControllerService from "../services/ControllerService";
import type { ControllerInfo } from "../services/ControllerService";

export default defineComponent({
  name: "ControllersView",
  setup() {
    const controllers = ref<ControllerInfo[]>([]);

    const loadControllers = async () => {
      try {
        controllers.value = await ControllerService.getControllers();
      } catch (error) {
        console.error("Erreur lors du chargement des controllers :", error);
      }
    };

    onMounted(() => {
      loadControllers();
    });

    return { controllers };
  },
});
</script>
