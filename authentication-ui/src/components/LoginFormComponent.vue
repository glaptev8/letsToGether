<template>
  <v-card class="pa-4">
    <v-card-title class="text-h5">Вход в систему</v-card-title>
    <v-card-text>
      <v-form ref="form" v-model="valid" @submit.prevent="login">
        <v-text-field
          label="Email"
          v-model="user.email"
          required
        ></v-text-field>
        <v-text-field
          label="Пароль"
          v-model="user.password"
          :type="'password'"
          :rules="passwordRules"
          required
        ></v-text-field>
        <v-btn :disabled="!valid" color="primary" type="submit">Войти</v-btn>
      </v-form>
      <v-alert v-if="loginError" type="error" class="mt-3">
        {{ loginError }}
      </v-alert>
    </v-card-text>
  </v-card>
</template>


<script>
import axios from 'axios';
import { authData } from '@/stores/auth';
import router from '@/router';

export default {
  name: 'LoginFormComponent',
  data() {
    return {
      loginError: null,
      valid: true,
      user: {
        email: 'admin@admin.ru',
        password: 'admin',
      },
      emailRules: [
        v => !!v || 'E-mail is required',
        v => /.+@.+\..+/.test(v) || 'E-mail must be valid',
      ],
      passwordRules: [
        v => !!v || 'Password is required',
      ],
    };
  },
  methods: {
    login() {
      axios.post('/auth/v1/login', this.user)
        .then(response => {
          console.log('Успешная аутентификация:', response.data);
          authData().updateAuthData(response.data);
          router.push({ name: '/' });
        })
        .catch(error => {
          if (error.response && error.response.data && error.response.data.body && error.response.data.body.message) {
            this.loginError = error.response.data.body.message
          }
          else {
            this.loginError = "Произошла ошибка авторизации, попробуйте еще раз"
          }
        });
    }
  }
};
</script>



<style scoped lang="sass">

</style>
