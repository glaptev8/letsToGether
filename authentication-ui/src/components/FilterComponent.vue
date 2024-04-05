<template>
  <v-col cols="12" sm="4">
    <v-text-field
      label="Date*"
      type="datetime-local"
      v-model="filter.startDate"
    />
  </v-col>

  <v-col cols="12" sm="4">
    <v-select v-model="filter.activityGroup" :items="activityGroups" label="Группа активности"></v-select>
  </v-col>

  <v-col cols="12" sm="4">
    <v-select v-model="filter.activityType" :items="activityTypes" item-text="name" item-value="value" label="Тип активности"></v-select>
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
});

function updateFilter() {
  emits('updateFilter', filter.value);
}

watch(filter, updateFilter, { deep: true });

const activityGroups = ['CLEAN_FILTER', ...Object.keys(staticData().activityGroups)]
let activityTypes = ref(getFlatActivityTypes())

watch(() => filter.value.activityType, (newValue) => {
  if (newValue && newValue === 'CLEAN_FILTER') {
    filter.value.activityType = null
  }
});

watch(() => filter.value.activityGroup, (newValue) => {
  if (newValue && newValue === 'CLEAN_FILTER') {
    filter.value.activityGroup = null
    activityTypes.value = getFlatActivityTypes()
  }
  else if (newValue) {
    activityTypes.value = ['CLEAN_FILTER', ...staticData().activityGroups[newValue]]
    if (!staticData().activityGroups[newValue].includes(filter.value.activityType)) {
      filter.value.activityType = null
    }
  } else {
    activityTypes.value = getFlatActivityTypes()
  }
});

function getFlatActivityTypes() {
  return ['CLEAN_FILTER', ...Object.values(staticData().activityGroups).flat()];
}
</script>
