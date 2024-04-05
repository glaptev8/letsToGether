<template>
  <v-app-bar app color="#f7f7f7" dark>
    <!-- Название компании в левом углу -->
    <v-toolbar-title class="text-h6">
      <v-btn text to="/" class="white--text">
        Lets Together
      </v-btn>
    </v-toolbar-title>
    <!-- Пространство, чтобы кнопки были выровнены по правому краю -->
    <v-spacer></v-spacer>
    <template v-if="!authStore.isAuth">
      <!-- Кнопки входа и регистрации в правом углу -->
      <v-btn text @click="showDialogLogIn = true" class="white--text mr-2">Log In</v-btn>
      <v-btn text @click="showDialogRegistration = true" class="white--text">Registration</v-btn>
      <ModalWindowRegistrationComponent v-model="showDialogRegistration" />
      <ModalWindowLogInComponent v-model="showDialogLogIn" />
    </template>
    <template v-else>
      <v-btn text @click="logOut" class="white--text mr-2">LogOut</v-btn>
    </template>
  </v-app-bar>
</template>
<script setup>
import {authData} from '@/stores/auth';

const authStore = authData();
</script>
<script>
import ModalWindowRegistrationComponent from '@/components/ModalWindowRegistrationComponent.vue';
import ModalWindowLogInComponent from '@/components/ModalWindowLogInComponent.vue';
import { authData } from '@/stores/auth';

export default {
  name: "NavBarComponent",
  components: { ModalWindowLogInComponent, ModalWindowRegistrationComponent },
  data() {
    return {
      showDialogRegistration: false,
      showDialogLogIn: false,
    };
  },
  methods: {
    logOut() {
      authData().logOut();
    }
  }
}
</script>


<style scoped lang="sass">

</style>
