<template>
  <v-dialog v-model="dialog" max-width="600px">
    <v-card>
      <v-card-title>
        <span class="text-h5">{{ viewingProfile ? `${selectedParticipant.firstName} ${selectedParticipant.lastName}` : 'Участники' }}</span>
      </v-card-title>
      <v-card-text>
        <div v-if="!viewingProfile">
          <v-virtual-scroll
            :items="participants"
            height="300"
            item-height="50"
          >
            <template v-slot:default="{ item }">
              <v-list-item>
                <template v-slot:prepend>
                  <v-avatar>
                    <img v-if="!imageError" width="50" height="50" :src="`https://lets-to-gether.online/api/auth/v1/img/${item.pathToAvatar}`" @error="handleImageError" alt="User Avatar">
                    <span v-else class="avatar-initials">{{ `${item.firstName[0]}${item.lastName[0]}` }}</span>
                  </v-avatar>
                </template>
                <v-list-item-title>{{ item.firstName + " " + item.lastName }}</v-list-item-title>
                <template v-slot:append>
                  <v-btn
                    style="opacity: 0.8"
                    x-small
                    color="primary"
                    @click="viewProfile(item)"
                  >
                    <span class="text-caption">View Profile</span>
                  </v-btn>
                </template>
              </v-list-item>
            </template>
          </v-virtual-scroll>
        </div>
        <div v-else>
          <ProfileCardComponent :participant="selectedParticipant" />
        </div>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" text @click="closeOrBack">{{ viewingProfile ? 'Назад' : 'Закрыть' }}</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { defineProps, ref, reactive } from 'vue';
import ProfileCardComponent from '@/components/ProfileCardComponent.vue';

const props = defineProps({
  participants: Array,
  modelValue: Boolean
});

const dialog = ref(false);
const emit = defineEmits(['update:modelValue']);
const viewingProfile = ref(false);
const selectedParticipant = reactive({});

const viewProfile = (participant) => {
  Object.assign(selectedParticipant, participant);
  viewingProfile.value = true;
};

const closeOrBack = () => {
  if (viewingProfile.value) {
    viewingProfile.value = false;
  } else {
    dialog.value = false;
    emit('update:modelValue', false);
  }
};

const imageError = ref(false);

function handleImageError() {
  imageError.value = true;
}

watch(() => props.modelValue, (newVal) => {
  dialog.value = newVal;
});
watch(dialog, (newVal) => {
  emit('update:modelValue', newVal);
});
</script>

<style scoped>
.avatar-initials {
font-size: 48px; /* Большой размер шрифта для видимости */
color: #757575; /* Цвет шрифта */
line-height: 140px; /* Выравнивание текста по центру аватара */
text-align: center; /* Центрирование текста */
width: 100%; /* Ширина текста равна ширине аватара */
}
</style>
