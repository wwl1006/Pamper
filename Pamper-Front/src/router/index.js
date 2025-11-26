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
          path: 'users',
          name: 'admin-users',
          component: () => import('../views/AdminUsersView.vue'),
          meta: { requiresAuth: true, roles: [0] }
        },
        {
          path: 'review',
          name: 'admin-review',
          component: () => import('../views/AdminReviewView.vue'),
          meta: { requiresAuth: true, roles: [0] }
        },
        {
          path: 'content',
          name: 'admin-content',
          component: () => import('../views/AdminContentView.vue'),
          meta: { requiresAuth: true, roles: [0] }
        },
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
      path: '/myposts',
      name: 'myposts',
      component: () => import('../views/MyPostsView.vue'),
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
    },
    {
      path: '/adoption',
      name: 'adoption',
      component: () => import('../views/AdoptionCenterView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/adoption/create',
      name: 'adoption-create',
      component: () => import('../views/AdoptionCreateView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/adoption/:id',
      name: 'adoption-detail',
      component: () => import('../views/AdoptionDetailView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/mypets',
      name: 'mypets',
      component: () => import('../views/MyPetsView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/pet/add',
      name: 'pet-add',
      component: () => import('../views/PetProfileFormView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/pet/edit/:id',
      name: 'pet-edit',
      component: () => import('../views/PetProfileFormView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/pet/:id',
      name: 'pet-detail',
      component: () => import('../views/PetDetailView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/services',
      name: 'services',
      component: () => import('../views/ServiceSquareView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/service/create',
      name: 'service-create',
      component: () => import('../views/ServiceFormView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/service/edit/:id',
      name: 'service-edit',
      component: () => import('../views/ServiceFormView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/service/:id',
      name: 'service-detail',
      component: () => import('../views/ServiceDetailView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/appointments',
      name: 'appointments',
      component: () => import('../views/MyAppointmentsView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/activities',
      name: 'activities',
      component: () => import('../views/ActivitySquareView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/activity/create',
      name: 'activity-create',
      component: () => import('../views/ActivityCreateView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/activity/edit/:id',
      name: 'activity-edit',
      component: () => import('../views/ActivityCreateView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/activity/:id',
      name: 'activity-detail',
      component: () => import('../views/ActivityDetailView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/news',
      name: 'news',
      component: () => import('../views/NewsListView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/news/:id',
      name: 'news-detail',
      component: () => import('../views/NewsDetailView.vue'),
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
