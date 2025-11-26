<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, ArrowDown } from '@element-plus/icons-vue'
import { getCurrentTime, getGreet } from "../assets/Js/Date.js"
import DefaultAvatar from '../assets/Img/dog.webp'

const router = useRouter()
const activeMenu = ref("1")
const time = ref("")
const greet = ref("")
const userInfo = ref({})
const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

let clockTimer = null

// 根据当前路由更新菜单高亮
const updateActiveMenu = () => {
  const path = router.currentRoute.value.path
  if (path === '/') {
    activeMenu.value = '1'
  } else if (path.startsWith('/community') || path.startsWith('/post')) {
    activeMenu.value = '2'
  } else if (path.startsWith('/activities') || path.startsWith('/activity')) {
    activeMenu.value = '3'
  } else if (path.startsWith('/adoption')) {
    activeMenu.value = '4'
  } else if (path.startsWith('/mypets') || path.startsWith('/pet')) {
    activeMenu.value = '5'
  } else if (path.startsWith('/service') || path.startsWith('/appointment')) {
    activeMenu.value = '6'
  }
}

const handleSelect = (index) => {
  activeMenu.value = index
  if (index === '1') {
    router.push('/')
  } else if (index === '2') {
    router.push('/community')
  } else if (index === '3') {
    router.push('/activities')
  } else if (index === '4') {
    router.push('/adoption')
  } else if (index === '5') {
    router.push('/mypets')
  } else if (index === '6') {
    router.push('/services')
  }
}

const loadUserInfo = (payload) => {
  try {
    if (payload) {
      userInfo.value = payload
      return
    }
    const cache = localStorage.getItem('userInfo')
    userInfo.value = cache ? JSON.parse(cache) : {}
  } catch (error) {
    userInfo.value = {}
  }
}

const avatarSrc = computed(() => {
  if (userInfo.value?.id) {
    return `${API_BASE}/profile/avatar/id/${userInfo.value.id}`
  }
  return DefaultAvatar
})

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  router.push('/login')
}

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/profile')
    return
  }
  if (command === 'notice') {
    router.push('/notice')
    return
  }
  if (command === 'myposts') {
    router.push('/myposts')
    return
  }
  if (command === 'logout') {
    logout()
  }
}

const handleProfileEvent = (event) => {
  const detail = event?.detail
  if (detail) {
    loadUserInfo(detail)
  } else {
    loadUserInfo()
  }
}

onMounted(() => {
  time.value = getCurrentTime();
  greet.value = getGreet();
  loadUserInfo()
  updateActiveMenu() // 初始化菜单高亮
  window.addEventListener('pamper-profile-updated', handleProfileEvent)
  clockTimer = setInterval(() => {
    time.value = getCurrentTime()
  }, 1000)

  // 监听路由变化更新菜单高亮
  router.afterEach(() => {
    updateActiveMenu()
  })
})

onBeforeUnmount(() => {
  window.removeEventListener('pamper-profile-updated', handleProfileEvent)
  if (clockTimer) {
    clearInterval(clockTimer)
  }
})
</script>

<template>
  <div class="header">
    <!-- 左侧 -->
    <div class="left">
      <img src="../assets/Img/dog.webp" class="logo" />
      <span class="title">宠伴 - 宠物交流与服务平台</span>
    </div>

    <!-- 中间菜单 -->
    <el-menu :default-active="activeMenu" class="menu" mode="horizontal" @select="handleSelect">
      <el-menu-item index="1">首页</el-menu-item>
      <el-menu-item index="2">社区</el-menu-item>
      <el-menu-item index="3">活动中心</el-menu-item>
      <el-menu-item index="4">领养中心</el-menu-item>
      <el-menu-item index="5">我的宠物</el-menu-item>
      <el-menu-item index="6">医疗服务</el-menu-item>
    </el-menu>

    <!-- 右侧 -->
    <div class="right">
      <span class="time">{{ time }}</span>
      <el-dropdown class="user-dropdown" trigger="hover" @command="handleCommand">
        <div class="user-info">
          <img class="avatar" :src="avatarSrc" alt="用户头像">
          <span class="greet">
            <el-icon>
              <User />
            </el-icon>
            {{ greet }}, {{ userInfo.username || '宠友' }}！
          </span>
          <el-icon class="arrow">
            <ArrowDown />
          </el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人主页</el-dropdown-item>
            <el-dropdown-item command="notice">通知中心</el-dropdown-item>
            <el-dropdown-item command="myposts">我的发布</el-dropdown-item>
            <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>

</template>


<style scoped>
@import "../assets/css/header.css";

.header {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  background: #fff;
  border-bottom: 1px solid #eee;
}

.search {
  width: 200px;
}

/* 左侧固定 */
.left {
  gap: 10px;
  margin-right: 30px;
}

/* logo */
.logo {
  width: 36px;
  height: 36px;
}

/* 标题 */
.title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

/* 中间菜单自动撑开并居中 */
.menu {
  flex: 1;
  display: flex;
  justify-content: center;
  /* ⭐ 让菜单居中 */
  border-bottom: none;
  /* 去掉底线 */
}

/* 右侧内容 */
.right {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-left: 40px;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #f0f0f0;
}

.arrow {
  font-size: 12px;
  color: #666;
}
</style>