import { createRouter, createWebHistory } from 'vue-router'


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'index',
      component: () => import('../views/UserView.vue'),
      children: [

      ]
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/AdminManageView.vue')
    }
  ]
})

export default router
