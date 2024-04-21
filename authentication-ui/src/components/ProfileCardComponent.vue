<template>
  <div class="profile-card">
    <div class="profile-header">
      <v-avatar size="140">
        <img v-if="!imageError" width="130" height="130" :src="`http://localhost:8082/auth/v1/img/${participant.pathToAvatar}`" @error="handleImageError" alt="User Avatar">
        <span v-else class="avatar-initials">{{ initials }}</span>
      </v-avatar>
    </div>
    <div class="profile-body">
      <p><strong>About:</strong> {{ participant.aboutMe }}</p>
      <p><strong>Email:</strong> {{ participant.email }}</p>
      <p><strong>Phone:</strong> {{ participant.phone }}</p>
      <p><strong>Age:</strong> {{ participant.age }}</p>
      <p><strong>Gender:</strong> {{ participant.gender }}</p>
      <p><strong>Hobbies:</strong> {{ participant.hobbies ? participant.hobbies.join(', ') : 'No hobbies listed' }}</p>
      <CompletedEventsComponent :userId="participant.id"/>
    </div>
  </div>
</template>


<script setup>
import { ref, computed } from 'vue';
import CompletedEventsComponent from '@/components/CompletedEventsComponent.vue';
import { defineProps } from 'vue';

const props = defineProps({
  participant: Object
});

const imageError = ref(false);

function handleImageError() {
  imageError.value = true;
}

const initials = computed(() => {
  return `${props.participant.firstName[0]}${props.participant.lastName[0]}`;
});
</script>


<style scoped>
.profile-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.profile-header {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.profile-header v-avatar {
  margin-bottom: 15px;
}

.profile-header h2 {
  margin: 0;
  color: #424242;
  font-size: 24px;
}

.profile-body {
  width: 100%;
  padding-top: 20px;
}

.avatar-initials {
  font-size: 48px; /* Большой размер шрифта для видимости */
  color: #757575; /* Цвет шрифта */
  line-height: 140px; /* Выравнивание текста по центру аватара */
  text-align: center; /* Центрирование текста */
  width: 100%; /* Ширина текста равна ширине аватара */
}

.profile-body p {
  margin: 5px 0;
  color: #666;
  font-size: 16px;
}
</style>
