<template>

  <v-row justify="center">
    <v-dialog v-model="dialog" persistent width="1024">
      <v-card>
        <v-card-title>
          <span class="text-h5">Create Event</span>
        </v-card-title>
        <v-card-text>
          <v-form ref="form" @submit.prevent="saveEvent">
            <v-container>
              <v-row>
                <v-col cols="12" sm="6">
                  <v-text-field label="Event Name*" v-model="event.name" required />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-textarea label="Description" v-model="event.description" />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field label="Start Date*" type="datetime-local" v-model="event.startDate" required />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-select :items="activityTypes" label="Activity Type*" v-model="event.activityType" required />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field label="Max Participants" type="number" v-model="event.maxParticipant" />
                </v-col>
                <v-col cols="12" sm="8" md="7">
                  <span v-if="rules.addressError">{{rules.addressError}}</span>
                  <v-text-field :rules="rules.addressRule"
                                v-model="event.address"
                                label="Address*" id="address"
                                autocomplete="on"></v-text-field>
                  <GoogleMap
                    ref="mapRef"
                    clickable
                    @click="addMarkerByClick"
                    api-key="AIzaSyAFj7qH5ub0o-wT-ecPalLQcQZRQrBh11M"
                    :center='event.center'
                    :zoom='12'
                    style='width:100%;  height: 400px;'
                  >
                    <Marker
                      v-for="(marker, index) of event.markers" :key="index"
                      :options="marker"
                      @click="removeMarker"
                    />
                  </GoogleMap>
                </v-col>
              </v-row>
            </v-container>
          </v-form>
          <v-alert v-if="event.error" type="error" class="mt-3">
            {{ event.error }}
          </v-alert>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" text @click="dialog = false">Close</v-btn>
          <v-btn color="primary" @click="saveEvent">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script setup>
import { defineProps, onMounted, ref, watch } from 'vue';
import { staticData } from '@/stores/static';
import { GoogleMap, Marker } from 'vue3-google-map';
import axios from 'axios';

const props = defineProps({
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
let rules = {
  addressRule: [
    value => {
      return !!value
    },
  ]
}

const event = ref({
  name: '',
  description: '',
  startDate: '',
  endDate: '',
  activityType: '',
  minParticipant: null,
  maxParticipant: null,
  address: '',
  addressError: '',
  markers: [],
  center: {
    lat: '',
    lng: ''
  },
  error: ''
});


const activityTypes = Object.values(staticData().activityGroups).flat();

async function saveEvent() {
  const eventData = {
    name: event.value.name,
    description: event.value.description,
    startDate: event.value.startDate,
    endDate: event.value.endDate,
    activityType: event.value.activityType,
    minParticipant: event.value.minParticipant,
    maxParticipant: event.value.maxParticipant,
    address: event.value.address,
    lng: event.value.center.lng,
    lat: event.value.center.lat
  };

  try {
    const response = await axios.post('/event/v1', eventData);
    console.log(response)
    if (response.status === 200) {
      console.log('Event saved successfully', response.data);
      dialog.value = false;
      resetForm();
    }
    else {
      event.value.error = response.data.body.message
    }
  } catch (error) {
    console.error('Error saving event:', error);
  }
}

let removeMarker = () => {
  event.value.markers = []
  event.value.address = ''
}

let autocomplete = async () => {
  let autocomplete = new window.google.maps.places.Autocomplete(document.getElementById('address'))
  autocomplete.addListener('place_changed', () => {
    try {
      let lat = autocomplete.getPlace().geometry.location.lat();
      let lng = autocomplete.getPlace().geometry.location.lng();
      event.value.center = {
        lat,
        lng,
      };
      event.value.markers = []
      const marker = {
        position: event.value.center
      }
      event.value.markers.push(marker)
      event.value.address = autocomplete.getPlace().formatted_address;
      event.value.addressError = ''
    } catch (e) {
      event.value.address = ''
      event.value.addressError = 'Address not found'
    }
  })
}

let addMarkerByClick = (e) => {
  event.value.markers = [];
  const marker = {
    position: e.latLng.toJSON(),
  };
  event.value.markers.push(marker)
  event.value.addressError = ''
  setStreetAddressFrom(e.latLng.toJSON().lat, e.latLng.toJSON().lng)
}

let geolocate = () => {
  navigator.geolocation.getCurrentPosition(position => {
    event.value.center = {
      lat: position.coords.latitude,
      lng: position.coords.longitude,
    };
    setStreetAddressFrom(event.value.center.lat, event.value.center.lng)
    const marker = {
      position: event.value.center,
    };
    event.value.markers.push(marker)
  });
}

onUpdated( () => {
  removeMarker()
  geolocate()
  autocomplete()
})

let setStreetAddressFrom = async (lat, long) => {
  try {
    const { data } = await axios.get(

      "https://maps.googleapis.com/maps/api/geocode/json?latlng=" +
      lat +
      "," +
      long +
      "&key=AIzaSyAFj7qH5ub0o-wT-ecPalLQcQZRQrBh11M"
    );
    if(data.error_message) {
      console.log(data.error_message)
    } else {
      event.value.address = data.results[0].formatted_address
    }
  } catch (error) {
    console.log(error.message);
  }
}

function resetForm() {
  event.value = {
    name: '',
    description: '',
    startDate: '',
    activityType: '',
    maxParticipant: null,
  };
}
</script>

<style>
.pac-container {
  z-index: 9999999 !important;
}
</style>
