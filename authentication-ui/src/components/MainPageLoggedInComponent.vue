<template>
  <v-container>
    <v-row>
      <FilterComponent @updateFilter="handleFilterUpdate"/>
    </v-row>
    <v-row>
      <v-col
        cols="12"
        md="4"
        sm="4">
        <v-btn style="margin-bottom: 60px" color="primary" @click="applyFilters">Применить фильтр</v-btn>
      </v-col>
      <CreateEventFormComponent v-model="showCreateEvent" :dialog="false"/>
      <v-col
        cols="12"
        md="4"
        sm="4">
        <v-btn style="margin-bottom: 60px" color="primary" @click="showCreateEvent = !showCreateEvent">Создать событие</v-btn>
      </v-col>
      <v-col
        cols="12"
        md="4"
        sm="4">
        <v-btn style="margin-bottom: 60px" color="primary" @click="getUserEvents">ваши события</v-btn>
      </v-col>
    </v-row>
    <template v-if="events.length"><h2>События</h2></template>
    <v-row v-if="events.length">
      <v-col cols="12" sm="6" md="4" lg="3" v-for="event in events" :key="event.id">
        <EventCardComponent :event="event" :subscribed="subscribed" @showError="showError" @eventRemoved="handleEventRemoved"/>
      </v-col>
    </v-row>
    <div v-else>
      Событий пока нет.
    </div>
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

const events = ref([]);
const filterState = ref({ startDate: null, activityType: null, activityGroup: null });

function handleFilterUpdate(newFilter) {
  filterState.value = newFilter;
}

let showCreateEvent = ref(false)
let subscribed = ref(true)

watch(showCreateEvent, () => {
  console.log(showCreateEvent)
})

onMounted(async () => {
  try {
    const response = await axios.post('/event/v1/byfilter', filterState.value)
    if (response.status === 200) {
      subscribed.value = false
      events.value = response.data;
    }
  } catch (error) {
    console.error('Ошибка при получении событий:', error)
  }
});

const showErrorDialog = ref(false);
const errorMessage = ref('');

let showError = (message) => {
  console.log(message)
  errorMessage.value = message;
  showErrorDialog.value = true;
};

const getUserEvents = async () => {
  const response = await axios.post("/event/v1/byuser")
  if (response.status === 200) {
    subscribed.value = true
    events.value= response.data
  }
}

const applyFilters = async () => {
  const response = await axios.post("/event/v1/byfilter", filterState.value)
  if (response.status === 200) {
    subscribed.value = false
    events.value= response.data
  }
}
let handleEventRemoved = (removedEventId) => {
    events.value = events.value.filter(event => event.id !== removedEventId);
}
</script>


<style>
</style>
