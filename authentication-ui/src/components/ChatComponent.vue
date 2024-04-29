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

        <v-card-text id="q" ref="messagesContainer" style="height: 300px; overflow-y: auto;">
          <div v-for="message in messages" :key="message.id" :class="['message-item', { 'mine': message.userId == authData().userId }]">
            <div class="message-content" id="q">
              <v-avatar class="mr-2" left>
                <img v-if="!imageError" width="40" height="40" :src="`http://localhost:8082/api/auth/v1/img/${getPathToAvatar(message.userId)}`" @error="handleImageError" alt="User Avatar">
                <span v-else class="avatar-initials">{{ getInitials(message.userId) }}</span>
              </v-avatar>
              <span>{{ message.text }}</span>

              <v-icon v-if="message.userId == props.event.creatorId" color="gold">mdi-crown</v-icon>
            </div>
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
import { ref, onUnmounted, defineProps, nextTick } from 'vue';
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
  participants: Array
});

const messagesContainer = ref(null);

function scrollToBottom() {
  // Здесь используется nextTick для гарантии, что DOM обновлен
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.$el.scrollTop = messagesContainer.value.$el.scrollHeight;
    }
  });
}

const getInitials = (senderId) => {
  const participant = props.participants.find(participant => participant.id == senderId)
  return `${participant.firstName[0]}${participant.lastName[0]}`;
}

const imageError = ref(false);

function handleImageError() {
  imageError.value = true;
}

const getPathToAvatar = (senderId) => {
  const participant = props.participants.find(participant => participant.id == senderId)
  return participant.pathToAvatar
}

const chatVisible = ref(false);
const ws = ref(null);
const messages = ref([]);
const newMessage = ref('');
const token = authData().token

function showChat() {
  chatVisible.value = true;
  initializeWebSocket();
  nextTick(() => {
    var div = document.getElementById('q');
    div.scrollTop = 200
    scrollToBottom(); // Прокрутка при открытии чата
  });
}

function closeChat() {
  chatVisible.value = false;
  if (ws.value) {
    ws.value.close();
    ws.value = null;
  }
}

function initializeWebSocket() {
  ws.value = new WebSocket(`wss://lets-to-gether.online/api/event/chat/socket/${props.event.id}?token=${token}`);

  ws.value.onopen = async () => {
    const response = await axios.post(`/api/event/v1/${props.event.id}`)
    if (response.status === 200) {
      messages.value = response.data.messages;
      await nextTick(scrollToBottom);
    }
  };

  ws.value.onmessage = (event) => {
    const message = JSON.parse(event.data);
    messages.value.push(message);
    nextTick(scrollToBottom);
  };

  ws.value.onerror = (error) => {
    console.error('WebSocket error:', error);
  };

  ws.value.onclose = () => {
    console.log('WebSocket connection closed');
  };
}

function sendMessage() {
  if (newMessage.value.trim() !== '' && ws.value && ws.value.readyState === WebSocket.OPEN) {
    const messageToSend = {
      eventId: props.event.id,
      text: newMessage.value.trim()
    };

    ws.value.send(JSON.stringify(messageToSend));
    scrollToBottom(); // Прокрутка после загрузки сообщений
    newMessage.value = ''; // Очистка поля ввода после отправки
  } else {
    console.error('WebSocket не подключен или сообщение пустое.');
  }
}

onUnmounted(() => {
  if (ws.value) {
    ws.value.close();
  }
});
</script>


<style scoped>
.message-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.message-item.mine {
  justify-content: flex-end;
}

.message-item .message-content {
  max-width: 60%;
  padding: 10px;
  border-radius: 10px;
  background-color: #f0f0f0;
}



.message-item.mine .message-content {
  background-color: #e0e0ff; /* Цвет фона для сообщений от текущего пользователя */
}
</style>
