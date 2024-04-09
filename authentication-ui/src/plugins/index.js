/**
 * plugins/index.js
 *
 * Automatically included in `./src/main.js`
 */

// Plugins
import vuetify from './vuetify'
import pinia from '@/stores'
import router from '@/router'
import axios from "axios";
import { authData } from '@/stores/auth';

axios.defaults.baseURL = 'http://localhost:8082';
axios.interceptors.request.use((config) => {
  const authData = localStorage.getItem('authData');
  const { token, userId } = authData ? JSON.parse(authData) : {}
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
    // config.headers.set('X-USER-ID', userId)
  }
  return config;
}, (error) => {
  return Promise.reject(error)
})
axios.interceptors.response.use(response => {
  return response
}, (error) => {
  if (error.response && error.response.status === 401) {
    console.log(error)
    authData().logOut();
  }
})

export function registerPlugins(app) {
  app
    .use(vuetify)
    .use(router)
    .use(pinia)
}
