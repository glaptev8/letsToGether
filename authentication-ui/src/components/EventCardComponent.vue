<template>
  <v-card class="event-card">
    <!--    style="line-height: 1rem; margin-top:7px"-->
    <v-card-title class="my-card-title pb-0">{{ event.name }}</v-card-title>
    <v-card-subtitle>
      <span>{{ event.activityType.toLowerCase() }}</span>
    </v-card-subtitle>
    <v-card-text class="pt-2 pb-0">
      <div class="cut-text" @click="loadParticipants">
        <span class="text-caption">{{ 'Участники ' + userIds.length + '/' + event.maxParticipant }} </span>
      </div>
      <div class="cut-text"><strong>Адрес:</strong> <span @click="mapDialog = true">{{ event.address }}</span></div>
      <div class="cut-text"><span @click="descriptionDialog = !descriptionDialog">{{ event.description }}</span></div>
      <v-chip-group>
        <v-chip x-small>{{ event.startDate.replace("T", " ") }}</v-chip>
      </v-chip-group>
    </v-card-text>
    <v-card-actions class="pt-0 pb-0 d-flex justify-space-between">
      <template v-if="event.creatorId !== userId">
        <v-btn x-small class="mr-1" color="secondary" @click="subscribe">
          <span class="text-caption">{{ subscribed ? 'UnSubscribe' : 'Subscribe' }}</span>
        </v-btn>
      </template>
      <template v-else>
        <v-btn x-small class="mr-1" color="secondary" @click="remove">
          <span class="text-caption">remove</span>
        </v-btn>
      </template>
      <ChatComponent :subscribed="subscribed" :event="event"/>
    </v-card-actions>
  </v-card>
  <ModalMapComponent :address="event.address" :center="{ lat: event.lat, lng: event.lng }" :zoom="15"
                     v-model="mapDialog"/>
  <DescriptionModalComponent :description="event.description" v-model="descriptionDialog"/>
  <ParticipantsModalComponent :participants="participants" v-model="participantsDialog"/>
</template>

<script setup>
import { defineProps, onMounted, ref } from 'vue';
import { fetchAvatarsByUserIds, fetchUserIdsByEvent } from '@/service/userService';
import ModalMapComponent from '@/components/ModalMapComponent.vue';
import DescriptionModalComponent from '@/components/DescriptionModalComponent.vue';
import ParticipantsModalComponent from '@/components/ParticipantsModalComponent.vue';
import axios from 'axios';
import { authData } from '@/stores/auth';
import ChatComponent from '@/components/ChatComponent.vue';

const emit = defineEmits(['eventRemoved', 'showError']);

const props = defineProps({
  event: {
    type: Object,
    required: true,
  },
  subscribed: {
    type: Boolean,
    required: true
  }
});
const userId = authData().userId;

console.log(authData().userId)

const usersAvatar = ref([])

const mapDialog = ref(false)
const descriptionDialog = ref(false)
const participantsDialog = ref(false)
const userIds = ref([])
const participants = ref([])
const loadParticipants = async () => {
  participantsDialog.value = !participantsDialog.value
  if (usersAvatar.value.length === 0) {
    try {
      participants.value = await fetchAvatarsByUserIds(userIds.value)
      console.log(participants)
    } catch (error) {
      console.log(error)
    }
  }
}

const token = authData().token

let discussion = async () => {
  // Указываем адрес сервера WebSocket
  let ws = new WebSocket(`ws://localhost:8082/chat-socket/2?token=${token}`);

  // Событие, вызываемое при успешном соединении
  ws.onopen = function(event) {
    console.log('Connected to the WebSocket server');
  };

  // Событие, вызываемое при получении сообщения от сервера
  ws.onmessage = function(event) {
    console.log('Message from server:', event.data);
    // Обработка сообщения от сервера
  };

  // Событие, вызываемое при закрытии соединения
  ws.onclose = function(event) {
    console.log('Disconnected from the WebSocket server');
    // Обработка закрытия соединения
  };

  // Событие, вызываемое при возникновении ошибки в соединении
  ws.onerror = function(event) {
    console.error('WebSocket error:', event);
    // Обработка ошибки соединения
  };
}

let remove = async () => {
  let updateStatusRequestDto = {
    eventId: props.event.id,
    eventStatus: 'CANCELED'
  }
  const response = await axios.post(`/event/v1/cancel`, updateStatusRequestDto)
  if (response && response.status === 200) {
    emit('eventRemoved', props.event.id);
  }
  else {
    emit('showError', 'Событие не было удалено, попробуйте еще раз');
  }
}

let subscribe = async () => {
  console.log(props.event.id)
  const url = !props.subscribed ? `/event/v1/subscribe?eventId=${props.event.id}` : `/event/v1/unsubscribe?eventId=${props.event.id}`
  const response = await axios.post(url)
  if (response && response.status === 200) {
    emit('eventRemoved', props.event.id);
  } else {
    emit('showError', `Не удалось ${props.subscribed ? 'подписаться' : 'отписаться'} на событие, попробуйте еще раз`);
  }
}

onMounted(async () => {
  if (userIds.value.length === 0) {
    try {
      userIds.value = await fetchUserIdsByEvent(props.event.id)
    } catch (error) {
      console.log(error)
    }
  }
})

</script>

<style scoped>
.cut-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: color 0.3s ease;
  cursor: pointer;
}

.cut-text:hover {
  text-decoration: underline;
  color: #0d47a1; /* Более темный цвет при наведении */
}
</style>