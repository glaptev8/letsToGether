<template>
  <v-card class="event-card">
    <!-- Заголовок и информация о событии -->
    <v-card-title class="my-card-title pb-0 d-flex justify-space-between">
      <span>{{ event.name }}</span>
      <v-chip x-small class="status-chip">{{ event.status }}</v-chip>
    </v-card-title>
    <v-card-subtitle>
      <span>{{ event.activityType.toLowerCase() }}</span>
    </v-card-subtitle>
    <v-card-text class="pt-2 pb-0">
      <div class="cut-text" @click="switchParticipantDialog">
        <span class="text-caption">{{ 'Участники ' + userIds.length + '/' + event.maxParticipant }}</span>
      </div>
      <div class="cut-text"><strong>Адрес:</strong> <span @click="mapDialog = true">{{ event.address }}</span></div>
      <div class="cut-text"><span @click="descriptionDialog = !descriptionDialog">{{ event.description }}</span></div>
      <v-chip-group>
        <v-chip x-small>{{ event.startDate.replace("T", " ") }}</v-chip>
      </v-chip-group>
    </v-card-text>
    <v-card-actions class="pt-0 pb-0 d-flex justify-space-between">
      <template v-if="event.status === 'COMPLETED' && event.creatorId !== userId">
        <v-btn x-small color="secondary" @click="showReviewDialogOpen">
          <span class="text-caption">Review</span>
        </v-btn>
      </template>
      <template v-else-if="event.creatorId === userId && event.status !== 'COMPLETED'">
        <v-btn x-small color="secondary" @click="remove">
          <span class="text-caption">Remove</span>
        </v-btn>
      </template>
      <template v-if="event.creatorId !== userId && event.status !== 'COMPLETED'">
        <v-btn x-small color="secondary" @click="subscribe">
          <span class="text-caption">{{ subscribed ? 'UnSubscribe' : 'Subscribe' }}</span>
        </v-btn>
      </template>
      <ChatComponent :subscribed="subscribed" :event="event" :participants="participants"/>
    </v-card-actions>
  </v-card>
  <review-modal-component @showSuccess="showSuccess" @showError="showError" v-model="showReviewDialog" :event-name="event.name" :event-id="event.id"/>
  <ModalMapComponent :address="event.address" :center="{ lat: event.lat, lng: event.lng }" :zoom="15" v-model="mapDialog"/>
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
import ReviewModalComponent from '@/components/ReviewModalComponent.vue';

const emit = defineEmits(['eventRemoved', 'showError', 'showSuccess', 'closeModalIfMap']);

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

const mapDialog = ref(false)
const descriptionDialog = ref(false)
const participantsDialog = ref(false)
const userIds = ref([])
const participants = ref([])
const switchParticipantDialog = async () => {
  participantsDialog.value = !participantsDialog.value
  await loadParticipants();
}
const showReviewDialog = ref(false);

const loadParticipants = async () => {
  if (participants.value.length === 0) {
    try {
      participants.value = await fetchAvatarsByUserIds(userIds.value)
    } catch (error) {
      console.log(error)
    }
  }
}

let showError = (message) => {
  emit('showError', message);
};

let showSuccess = (message) => {
  emit('showSuccess', message);
};


const showReviewDialogOpen = async () => {
  try {
    const response = await axios.post('/api/event/v1/review/check', {
      eventId: props.event.id
    });
    if (response.data) {
      showReviewDialog.value = true;
    }
  } catch (e) {
    console.error(e);
    emit('showError', 'It is not allowed to leave comments twice');
  }
}

const remove = async () => {
  let updateStatusRequestDto = {
    eventId: props.event.id,
    eventStatus: 'CANCELED'
  }
  const response = await axios.post(`/api/event/v1/updatestatus`, updateStatusRequestDto)
  if (response && response.status === 200) {
    emit('eventRemoved', props.event.id);
  }
  else {
    emit('showError', 'Событие не было удалено, попробуйте еще раз');
  }
}

let subscribe = async () => {
  const url = !props.subscribed ? `/api/event/v1/subscribe?eventId=${props.event.id}` : `/api/event/v1/unsubscribe?eventId=${props.event.id}`
  const response = await axios.post(url)
  if (response && response.status === 200) {
    emit('eventRemoved', props.event.id);
    emit('closeModalIfMap', props.event.id);
  } else {
    emit('showError', `Не удалось ${props.subscribed ? 'отписаться' : 'подписаться'} на событие, попробуйте еще раз`);
  }
}

onMounted(async () => {
  if (userIds.value.length === 0) {
    try {
      userIds.value = await fetchUserIdsByEvent(props.event.id)
      await loadParticipants()
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
