<template>
  <v-dialog v-model="dialog" max-width="600px">
    <v-card>
      <v-card-title>
        <span class="text-h5">Описание</span>
      </v-card-title>
      <v-card-text>
        {{ description }}
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" text @click="close">Закрыть</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref, defineProps, watch } from 'vue';

// Входные параметры для описания и начального состояния диалога
const props = defineProps({
  description: String,
  modelValue: Boolean,
});

// Реактивное свойство для отслеживания состояния диалога
const dialog = ref(props.modelValue);

// Определение функции emit для обновления modelValue в родительском компоненте
const emit = defineEmits(['update:modelValue']);

// Функция закрытия диалога
const close = () => {
  emit('update:modelValue', false);
};

// Отслеживание изменений modelValue, чтобы синхронизировать его со свойством dialog
watch(() => props.modelValue, (newVal) => {
  dialog.value = newVal;
});

// Отслеживание изменений dialog для информирования родительского компонента
watch(dialog, (newVal) => {
  emit('update:modelValue', newVal);
});
</script>
