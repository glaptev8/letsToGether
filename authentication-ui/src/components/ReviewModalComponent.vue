<template>
  <v-dialog v-model="visible" max-width="500px">
    <v-card>
      <v-card-title class="headline">Leave a Review for {{ eventName }}</v-card-title>
      <v-card-text>
        <v-rating v-model="rating" dense color="amber" half-increments hover></v-rating>
        <v-textarea v-model="comment" label="Your comment" hint="Optional" outlined></v-textarea>
        <v-alert v-if="error" type="error" class="mt-3">{{ error }}</v-alert> <!-- Отображение ошибки -->
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="green darken-1" text @click="submitReview">Submit</v-btn>
        <v-btn color="red darken-1" text @click="close">Cancel</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref, defineProps, watch } from 'vue';
import axios from 'axios';

const props = defineProps({
  eventName: String,
  eventId: Number,
  modelValue: Boolean,
});

const visible = ref(props.modelValue);
const rating = ref(0);
const comment = ref('');
const emit = defineEmits(['update:modelValue', 'showError']);
const error = ref(''); // Добавлено для хранения сообщения об ошибке

watch(() => props.modelValue, (newVal) => {
  visible.value = newVal;
});

const submitReview = async () => {
  try {
    await axios.post('/event/v1/review', {
      eventId: props.eventId,
      grade: rating.value,
      message: comment.value
    });
    emit('update:modelValue', false);
    emit('showSuccess', 'saved');

  } catch (e) {
    console.error(e);
    emit('showError', 'Error submitting review, please try again later.');
  }
  visible.value = false;
};

const close = () => {
  emit('update:modelValue', false);
};
</script>
