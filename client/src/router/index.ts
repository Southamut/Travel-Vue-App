import { createRouter, createWebHistory } from 'vue-router'
import LandingPage from '../components/LandingPage.vue'
import TripViewPage from '../components/TripViewPage.vue'
import LoginPage from '../components/LoginPage.vue'
import RegisterPage from '../components/RegisterPage.vue'
import MyTripPage from '../components/MyTripPage.vue'
import ProfilePage from '../components/ProfilePage.vue'

const routes = [
  { path: '/', component: LandingPage },
  { path: '/trips/:id', component: TripViewPage },
  { path: '/login', component: LoginPage },
  { path: '/register', component: RegisterPage },
  { path: `/my-trips`, component: MyTripPage},
  { path: `/profile`, component: ProfilePage }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
