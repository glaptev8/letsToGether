<template>
  <div>
    <template v-if="subscribed">
      <v-btn x-small class="mr-1" color="secondary" @click="showChat">
        <span class="text-caption">Discussion</span>
      </v-btn>
    </template>

    <v-dialog v-model="chatVisible" persistent max-width="600px">
      <v-card>
        <v-card-title>
          Chat
          <v-spacer></v-spacer>
          <v-btn icon @click="closeChat">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>

        <v-card-text style="height: 300px; overflow-y: auto;">
          <div v-for="message in messages" :key="message.id">
            {{ message.text }}
          </div>
        </v-card-text>

        <v-card-actions>
          <v-text-field v-model="newMessage" label="Type your message..." outlined dense full-width></v-text-field>
          <v-btn color="primary" @click="sendMessage">Send</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, onUnmounted, defineProps } from 'vue';
import { authData } from '@/stores/auth';
import axios from 'axios';

const props = defineProps({
  subscribed: {
    type: Boolean,
    required: true
  },
  event: {
    type: Object,
    required: true,
  },
});

const chatVisible = ref(false);
const ws = ref(null);
const messages = ref([]);
const newMessage = ref('');
const token = authData().token

function showChat() {
  chatVisible.value = true;
  initializeWebSocket();
}

function closeChat() {
  chatVisible.value = false;
  if (ws.value) {
    ws.value.close();
    ws.value = null;
  }
}

function initializeWebSocket() {
  ws.value = new WebSocket(`ws://localhost:8082/chat-socket/${props.event.id}?token=${token}`);

  ws.value.onopen = async () => {
    const response = await axios.post(`/chat/v1/${props.event.id}`)
    console.log('reee  ' + response)
  };

  ws.value.onmessage = (event) => {
    const message = JSON.parse(event.data);
    messages.value.push(message);
  };

  ws.value.onerror = (error) => {
    console.error('WebSocket error:', error);
  };

  ws.value.onclose = () => {
    console.log('WebSocket connection closed');
  };
}

function sendMessage() {
  if (newMessage.value !== '') {
    ws.value.send(JSON.stringify({ text: newMessage.value }));
    newMessage.value = ''; // Очистка поля ввода после отправки
  }
}

onUnmounted(() => {
  if (ws.value) {
    ws.value.close();
  }
});
</script>


<style scoped lang="sass">

</style>
