import { createRouter, createWebHistory } from 'vue-router'
import LandingPage from '../components/LandingPage.vue'
import TripViewPage from '../components/TripViewPage.vue'
import LoginPage from '../components/LoginPage.vue'
import RegisterPage from '../components/RegisterPage.vue'
import MyTripPage from '../components/MyTripPage.vue'
import ProfilePage from '../components/ProfilePage.vue'
import EditProfilePage from '../components/EditProfilePage.vue'
import EditTripPage from '../components/EditTripPage.vue'
import CreateTripPage from '../components/CreateTripPage.vue'

const routes = [
  { path: '/', component: LandingPage },
  { path: '/trips/:id', component: TripViewPage },
  { path: '/login', component: LoginPage },
  { path: '/register', component: RegisterPage },
  { path: `/my-trips`, component: MyTripPage},
  { path: `/my-trips/create`, component: CreateTripPage},
  { path: `/my-trips/edit/:id`, component: EditTripPage},
  { path: `/profile`, component: ProfilePage },
  { path: `/profile/edit`, component: EditProfilePage}
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
