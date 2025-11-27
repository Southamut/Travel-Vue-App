import { createRouter, createWebHistory } from 'vue-router'
import LandingPage from '../components/LandingPage.vue'
import TripViewPage from '../components/TripViewPage.vue'
import LoginPage from '../components/LoginPage.vue'

const routes = [
  { path: '/', component: LandingPage },
  { path: '/trips/:id', component: TripViewPage },
  { path: '/login', component: LoginPage }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
