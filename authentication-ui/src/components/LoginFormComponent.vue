<template>
  <v-card class="pa-4">
    <v-card-title class="text-h5">Вход в систему через Google</v-card-title>
    <v-card-text>
      <v-btn @click="loginWithGoogle" color="white" class="google-login-button">
        <v-img
          src="https://developers.google.com/identity/images/g-logo.png"
          class="google-logo"
          contain
          height="30"
          alt="Google logo"
        ></v-img>
        Войти с помощью Google
      </v-btn>
      <v-alert v-if="loginError" type="error" class="mt-3">
        {{ loginError }}
      </v-alert>
    </v-card-text>
  </v-card>
</template>

<script>
export default {
  name: 'LoginFormComponent',
  data() {
    return {
      loginError: null,
    };
  },
  methods: {
    loginWithGoogle() {
      // Перенаправление на endpoint сервера, который инициирует OAuth с Google
      window.location.href = "http://localhost:8082/api/auth/v1/login/google";
    }
  }
};
</script>

<style scoped lang="sass">
.google-login-button
  background-color: #4285F4
  color: white
  justify-content: center

.google-logo
  margin-right: 10px
</style>
