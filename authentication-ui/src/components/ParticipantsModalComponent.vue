<template>
  <v-dialog v-model="dialog" max-width="600px">
    <v-card>
      <v-card-title>
        <span class="text-h5">Участники</span>
      </v-card-title>
      <v-card-text>

        <v-virtual-scroll
          :items="participants"
          height="300"
          item-height="50"
        >
          <template v-slot:default="{ item }">
            <v-list-item>
              <template v-slot:prepend>
                <v-avatar>
                  <img :src="`http://localhost:8082/auth/v1/img/${item.pathToAvatar}`" width="50" height="50">
                </v-avatar>
              </template>

              <v-list-item-title>{{ item.firstName + " " + item.lastName}}</v-list-item-title>
              <template v-slot:append>
                <v-btn
                  style="opacity: 0.8"
                  x-small
                  color="primary">
                  <span class="text-caption">View User</span>
                </v-btn>
              </template>
            </v-list-item>
          </template>
        </v-virtual-scroll>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" text @click="dialog = false">Закрыть</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { defineProps, ref, watch } from 'vue';

const props = defineProps({
  participants: Array,
  modelValue: Boolean
});

const dialog = ref(false);
const emit = defineEmits(['update:modelValue']);
watch(() => props.modelValue, (newVal) => {
  dialog.value = newVal;
});
watch(dialog, (newVal) => {
  emit('update:modelValue', newVal);
});
</script>
