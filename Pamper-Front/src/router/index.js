import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'index',
      component: () => import('../views/UserView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/ProfileView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/AdminManageView.vue'),
      meta: { requiresAuth: true, roles: [0] }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Register.vue')
    }
  ]
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next({ path: '/login', replace: true })
    return
  }
  if ((to.path === '/login' || to.path === '/register') && token) {
    next({ path: '/', replace: true })
    return
  }
  if (to.meta.roles && to.meta.roles.length) {
    try {
      const info = JSON.parse(localStorage.getItem('userInfo') || '{}')
      if (!to.meta.roles.includes(info.user_type)) {
        next({ path: '/', replace: true })
        return
      }
    } catch (e) {
      localStorage.removeItem('userInfo')
      next({ path: '/login', replace: true })
      return
    }
  }
  next()
})

export default router
