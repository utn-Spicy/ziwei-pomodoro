import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import StrategyView from '../views/StrategyView.vue'
import PomodoroView from '../views/PomodoroView.vue'
import BreakView from '../views/BreakView.vue'

const routes = [
  { path: '/', name: 'login', component: LoginView },
  { path: '/strategy', name: 'strategy', component: StrategyView },
  { path: '/pomodoro', name: 'pomodoro', component: PomodoroView },
  { path: '/break', name: 'break', component: BreakView },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
