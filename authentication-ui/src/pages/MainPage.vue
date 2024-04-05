<template>
  <template v-if="!authData().isAuth">
    <MainPageNotLoggedInComponent/>
  </template>
  <template v-else>
    <MainPageLoggedInComponent/>
  </template>
</template>

<script setup>
import { authData } from '@/stores/auth';
import MainPageNotLoggedInComponent from '@/components/MainPageNotLoggedInComponent.vue';
import MainPageLoggedInComponent from '@/components/MainPageLoggedInComponent.vue';
</script>

<style scoped lang="sass">

</style>
