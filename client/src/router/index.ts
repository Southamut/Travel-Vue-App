import { createRouter, createWebHistory } from 'vue-router'
import LandingPage from '../components/LandingPage.vue'
import TripViewPage from '../components/TripViewPage.vue'
import LoginPage from '../components/LoginPage.vue'
import RegisterPage from '../components/RegisterPage.vue'
import MyTripPage from '../components/MyTripPage.vue'

const routes = [
  { path: '/', component: LandingPage },
  { path: '/trips/:id', component: TripViewPage },
  { path: '/login', component: LoginPage },
  { path: '/register', component: RegisterPage },
  { path: `/my-trips`, component: MyTripPage}
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
