<template>
  <v-app>
    <nav-bar-component/>
    <v-main>
      <router-view />
    </v-main>
    <app-footer/>
  </v-app>
</template>

<script>
import NavBarComponent from '@/components/NavBarComponent.vue';
import AppFooter from '@/components/AppFooterComponent.vue';

export default {
  name: "App",
  components: { AppFooter, NavBarComponent },
};
</script>
