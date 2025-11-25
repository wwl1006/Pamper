import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HomePage.vue'),
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
      meta: { requiresAuth: true, roles: [0] },
      children: [
        {
          path: 'notice',
          name: 'admin-notice',
          component: () => import('../views/AdminNoticeView.vue'),
          meta: { requiresAuth: true, roles: [0] }
        }
      ]
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
    },
    {
      path: '/user',
      name: 'user',
      component: () => import('../views/UserView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/notice',
      name: 'notice',
      component: () => import('../views/NoticeView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/community',
      name: 'community',
      component: () => import('../views/PostSquareView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/post/create',
      name: 'post-create',
      component: () => import('../views/PostCreateView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/post/:id',
      name: 'post-detail',
      component: () => import('../views/PostDetailView.vue'),
      meta: { requiresAuth: true }
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
