<template>
  <v-col cols="12" sm="3">
    <v-text-field
      label="Date*"
      type="datetime-local"
      v-model="filter.startDate"
      append-icon="mdi-close-circle"
      @click:append="() => filter.startDate = null"
    />
  </v-col>

  <v-col cols="12" sm="3">
    <v-select
      v-model="filter.activityGroup"
      :items="activityGroups"
      label="Группа активности"
      append-icon="mdi-close-circle"
      @click:append="() => filter.activityGroup = null"
    ></v-select>
  </v-col>

  <v-col cols="12" sm="3">
    <v-select
      v-model="filter.activityType"
      :items="activityTypes"
      item-text="name"
      item-value="value"
      label="Тип активности"
      append-icon="mdi-close-circle"
      @click:append="() => filter.activityType = null"
    ></v-select>
  </v-col>
  <v-col cols="12" sm="3">
    <v-select
      v-model="filter.radius"
      :items="radiusOptions"
      label="Radius"
      item-title="text"
      item-value="value"
      append-icon="mdi-close-circle"
      @click:append="() => filter.radius = null"
    ></v-select>
  </v-col>
</template>

<script setup>
import { ref, watch } from 'vue';
import { staticData } from '@/stores/static';

const emits = defineEmits(['updateFilter']);

const filter = ref({
  startDate: null,
  activityType: null,
  activityGroup: null,
  radius: null
});

function updateFilter() {
  emits('updateFilter', filter.value);
}

watch(filter, updateFilter, { deep: true });

const activityGroups = Object.keys(staticData().activityGroups)
let activityTypes = Object.values(staticData().activityGroups).flat()

const radiusOptions = ref([
  { text: '5 km', value: 5 },
  { text: '10 km', value: 10 },
  { text: '20 km', value: 20 }
])

</script>
