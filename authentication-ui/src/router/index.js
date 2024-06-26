/**
 * router/index.ts
 *
 * Automatic routes for `./src/pages/*.vue`
 */

// Composables
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {path: '/', component: () => import('@/pages/MainPage.vue')},
  {path: '/auth-success', component: () => import('@/components/AuthSuccessComponent.vue')},
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
