<template>
  <v-dialog v-model="dialog" max-width="600px">
    <v-card>
      <v-card-title>
        <div class="dialog-header">
          <span class="text-h5">Местоположение</span>
          <span class="address">{{ address }}</span>
        </div>
      </v-card-title>
      <v-card-text style="height: 300px; width: 100%;">
        <GoogleMap
          clickable
          :api-key="apiKey"
          style='width:100%;  height: 100%'
          :center="center"
          :zoom='zoom'
        >
          <Marker
            :options="{position: center}"
          />
        </GoogleMap>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" @click="close">Закрыть</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch } from 'vue';
import { GoogleMap, Marker } from 'vue3-google-map';

const props = defineProps({
  center: {
    type: Object,
    required: true,
  },
  address: {
    type: String,
    required: true
  },
  zoom: {
    type: Number,
    default: 10,
  },
  modelValue: Boolean
});

const dialog = ref(props.modelValue);
const emits = defineEmits(['update:modelValue']);
const apiKey = import.meta.env.VITE_GOOGLE_MAPS_API_KEY;

const close = () => {
  emits('update:modelValue', false)
};
watch(() => props.modelValue, (newVal) => {
  dialog.value = newVal
});

watch(dialog, (newVal) => {
  emits('update:modelValue', newVal)
});
</script>

<style scoped>
.dialog-header {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.address {
  font-size: 20px;
  margin-top: 5px;
}
</style>
