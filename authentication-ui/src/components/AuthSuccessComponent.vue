<template>
  <div>
    <h1>Authentication successful!</h1>
    <p>You will be redirected shortly...</p>
  </div>
</template>

<script>
import { authData } from '@/stores/auth';

export default {
  name: 'AuthSuccess',
  mounted() {
    this.handleAuthentication();
  },
  methods: {
    handleAuthentication() {
      const params = new URLSearchParams(window.location.search);
      const token = params.get('token');
      const userId = params.get('userId');
      const isFirstEnter = params.get('isFirstEnter');
      if (token && userId) {
        authData().updateAuthData({token: token, userId: userId});
        this.$router.push({
          path: '/',
          query: { isFirstEnter: isFirstEnter }});
      } else {
        // Если токена нет, перенаправляем на страницу входа
        this.$router.push('/login');
      }
    }
  }
}
</script>
