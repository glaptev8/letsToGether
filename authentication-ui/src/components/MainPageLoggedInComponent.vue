<template>
  <v-container>
    <v-row>
      <FilterComponent @updateFilter="handleFilterUpdate"/>
    </v-row>
    <v-row>
      <v-col
        cols="12"
        md="3"
        sm="3">
        <v-btn style="margin-bottom: 60px" color="primary" @click="applyFilters">Применить фильтр</v-btn>
      </v-col>
      <CreateEventFormComponent v-model="showCreateEvent" :dialog="false"/>
      <v-col
        cols="12"
        md="3"
        sm="3">
        <v-btn style="margin-bottom: 60px" color="primary" @click="showCreateEvent = !showCreateEvent">Создать событие
        </v-btn>
      </v-col>
      <v-col
        cols="12"
        md="3"
        sm="3">
        <v-btn style="margin-bottom: 60px" color="primary" @click="getUserEvents">ваши события</v-btn>
      </v-col>
      <v-col
        cols="12"
        md="3"
        sm="3">
        <v-checkbox v-model="showOnMap" label="on map"></v-checkbox>
      </v-col>
    </v-row>
    <template v-if="events.length"><h2>События</h2></template>
    <v-row v-if="events.length" style="padding-top: 20px">
      <template v-if="showOnMap" style="width: 100%; height: 500px;">
        <GoogleMap
          ref="mapRef"
          clickable
          :api-key="apiKey"
          :center="{
            lat: filterState.lat,
            lng: filterState.lng
          }"
          :zoom="14"
          style='width:100%;  height: 500px;'
        >
          <Marker
            v-for="event in events"
            :key="event.id"
            :options="{position: { lat: event.lat, lng: event.lng }}"
            @click="openEventCard(event)"
          />
        </GoogleMap>
        <v-dialog v-model="dialogVisible" max-width="600px">
          <EventCardComponent
            :event="selectedEvent"
            :subscribed="subscribed"
            @closeModalIfMap="closeModalIfMap"
            @showSuccess="showSuccess"
            @showError="showError"
            @eventRemoved="handleEventRemoved"
          />
        </v-dialog>
      </template>
      <template v-else>
        <!-- Отображение карточек событий -->
        <v-col cols="12" sm="6" md="4" lg="3" v-for="event in events" :key="event.id">
          <EventCardComponent :event="event" :subscribed="subscribed" @showSuccess="showSuccess" @showError="showError"
                              @eventRemoved="handleEventRemoved"/>
        </v-col>
      </template>
    </v-row>
    <div v-else>
      Событий пока нет.
    </div>
    <v-dialog v-model="showSuccessDialog" persistent max-width="300px">
      <v-card>
        <v-card-title class="text-h5">Success</v-card-title>
        <v-card-text>
          <v-alert v-if="successMessage" type="success" dense dismissible>
            {{ successMessage }}
          </v-alert>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" text @click="showSuccessDialog = false">OK</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-dialog v-model="showErrorDialog" persistent max-width="300px">
      <v-card>
        <v-card-title class="text-h5">Ошибка</v-card-title>
        <v-card-text>
          <v-alert type="error" dense text>{{ errorMessage }}</v-alert>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" text @click="showErrorDialog = false">OK</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import EventCardComponent from '@/components/EventCardComponent.vue';
import axios from 'axios';
import { onMounted, ref } from 'vue';
import FilterComponent from '@/components/FilterComponent.vue';
import CreateEventFormComponent from '@/components/CreateEventFormComponent.vue';
import { GoogleMap, Marker } from 'vue3-google-map';

const events = ref([]);
const filterState = ref({
  startDate: null,
  activityType: null,
  activityGroup: null,
  lat: null,
  lng: null,
  radius: null
});

navigator.geolocation.getCurrentPosition(position => {
  filterState.value.lat = position.coords.latitude;
  filterState.value.lng = position.coords.longitude;
});

function handleFilterUpdate(newFilter) {
  filterState.value = { ...filterState.value, ...newFilter };
}

const apiKey = import.meta.env.VITE_GOOGLE_MAPS_API_KEY

let closeModalIfMap = () => {
  dialogVisible.value = false;
}
let showCreateEvent = ref(false)
let subscribed = ref(true)
const dialogVisible = ref(false);
const selectedEvent = ref(null);

onMounted(async () => {
  try {
    const params = new URLSearchParams(window.location.search);
    if (params.has('isFirstEnter')) { // Проверяем, существует ли параметр
      params.delete('isFirstEnter');
      const query = params.toString();
      window.history.replaceState({}, '', `${window.location.pathname}${query ? '?' + query : ''}`);
    }
    const response = await axios.post('/event/v1/byfilter', filterState.value)
    if (response.status === 200) {
      subscribed.value = false
      events.value = response.data;
    }
  } catch (error) {
    console.error('Ошибка при получении событий:', error)
  }
});

const showOnMap = ref(false);
const showErrorDialog = ref(false);
const showSuccessDialog = ref(false);

const errorMessage = ref('');
const successMessage = ref('');

let showError = (message) => {
  errorMessage.value = message;
  showErrorDialog.value = true;
};

const openEventCard = (event) => {
  selectedEvent.value = event;
  dialogVisible.value = true;
};

let showSuccess = (message) => {
  successMessage.value = message;
  showSuccessDialog.value = true;
};

const getUserEvents = async () => {
  const response = await axios.post("/event/v1/byuser")
  if (response.status === 200) {
    subscribed.value = true
    events.value = response.data
  }
}

const applyFilters = async () => {
  const response = await axios.post("/event/v1/byfilter", filterState.value)
  if (response.status === 200) {
    subscribed.value = false
    events.value = response.data
  }
}
let handleEventRemoved = (removedEventId) => {
  events.value = events.value.filter(event => event.id !== removedEventId);
}
</script>

<style>
</style>
