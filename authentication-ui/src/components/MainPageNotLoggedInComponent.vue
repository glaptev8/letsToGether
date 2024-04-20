<template>
  <v-container fluid>
    <v-row justify="center" class="pa-5" style="background-color: #f5f5f5;">
      <v-col cols="12" class="text-center mb-5">
        <h1>Найдите занятие по душе на Lets Together</h1>
        <p>Мы предлагаем широкий спектр событий для всех.</p>
      </v-col>
      <v-col cols="12" md="4" v-for="(item, index) in items" :key="index">
        <ActivityCard :item="item"/>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import ActivityCard from '@/components/ActivityCardComponent.vue';
import { ref } from 'vue';

const items = ref([
  {
    title: 'Спорт',
    image: 'https://picsum.photos/seed/sport/600/300',
    description: 'Присоединяйтесь к футбольным, баскетбольным матчам или утреннему бегу. Идеально для поддержания формы и встречи новых друзей.',
  },
  {
    title: 'Обучение',
    image: 'https://picsum.photos/seed/education/600/300',
    description: 'Разговорные клубы, лекции и книжные встречи. Обогащайте свои знания и делитесь ими с единомышленниками.',
  },
  {
    title: 'Развлечения',
    image: 'https://picsum.photos/seed/entertainment/600/300',
    description: 'Свидания, походы в кино или катание на велосипедах. Отличный способ расслабиться и насладиться жизнью.',
  }
]);

</script>


<style scoped lang="sass">

</style>
