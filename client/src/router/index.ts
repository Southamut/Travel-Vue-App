import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../stores/auth";
import LandingPage from "../components/LandingPage.vue";
import TripViewPage from "../components/TripViewPage.vue";
import LoginPage from "../components/LoginPage.vue";
import RegisterPage from "../components/RegisterPage.vue";
import MyTripPage from "../components/MyTripPage.vue";
import ProfilePage from "../components/ProfilePage.vue";
import EditProfilePage from "../components/EditProfilePage.vue";
import EditTripPage from "../components/EditTripPage.vue";
import CreateTripPage from "../components/CreateTripPage.vue";

const routes = [
  { path: "/", component: LandingPage },
  { path: "/trips/:id", component: TripViewPage },
  { path: "/login", component: LoginPage },
  { path: "/register", component: RegisterPage },
  { path: `/my-trips`, component: MyTripPage, meta: { requiresAuth: true } },
  {
    path: `/my-trips/create`,
    component: CreateTripPage,
    meta: { requiresAuth: true },
  },
  {
    path: `/my-trips/edit/:id`,
    component: EditTripPage,
    meta: { requiresAuth: true },
  },
  { path: `/profile`, component: ProfilePage, meta: { requiresAuth: true } },
  {
    path: `/profile/edit`,
    component: EditProfilePage,
    meta: { requiresAuth: true },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach(async (to, from, next) => {
  const auth = useAuthStore();

  // If the route requires auth
  if (to.meta.requiresAuth) {
    // ensure user info is loaded
    if (!auth.userLoaded) {
      await auth.fetchUser();
    }

    if (!auth.isAuth) {
      return next("/login");
    }
  }

  next();
});

export default router;
