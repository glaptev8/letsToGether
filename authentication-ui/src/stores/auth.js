import { defineStore } from 'pinia';
import router from '@/router';

export const authData = defineStore('auth',{
  state: () => {
    const storedData = JSON.parse(localStorage.getItem('authData'))
    return {
      isAuth: !!storedData?.token,
      token: storedData?.token,
      userId: storedData?.userId
    };
  },
  actions: {
    updateAuthData({ token, userId }) {
      if (token && userId) {
        localStorage.setItem('authData', JSON.stringify({ token, userId }))
        this.isAuth = true
        this.token = token
        this.userId = userId
      } else {
        this.logOut();
      }
    },
    logOut() {
      localStorage.removeItem('authData')
      this.isAuth = false
      this.token = null
      this.userId = null
      router.push('/')
    }
  }
})
