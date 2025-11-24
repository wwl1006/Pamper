<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { User } from '@element-plus/icons-vue'
import { getCurrentTime, getGreet } from '../assets/Js/Date';
import DefaultAvatar from '../assets/Img/dog.webp'

const router = useRouter()
const time = ref("");
const greet = ref("");
const userInfo = ref({})
const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const loadUser = (payload) => {
  try {
    if (payload) {
      userInfo.value = payload
      return
    }
    const cache = JSON.parse(localStorage.getItem('userInfo') || '{}')
    userInfo.value = cache
  } catch (e) {
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
  if (command === 'logout') {
    logout()
  }
}

const handleProfileEvent = (event) => {
  const detail = event?.detail
  loadUser(detail)
}

let clockTimer = null

onMounted(() => {
  time.value = getCurrentTime();
  greet.value = getGreet();
  loadUser()
  window.addEventListener('pamper-profile-updated', handleProfileEvent)
  clockTimer = setInterval(() => {
    time.value = getCurrentTime()
  }, 1000);
});

onBeforeUnmount(() => {
  window.removeEventListener('pamper-profile-updated', handleProfileEvent)
  if (clockTimer) {
    clearInterval(clockTimer)
  }
});
</script>

<template>
  <header class="header">
    <div class="left">
      <img src="../assets/Img/dog.webp" alt="logo" class="logo">
      <span class="title">宠伴 - 宠物交流与服务平台</span>
    </div>
    <div class="right">
      <span class="time">{{ time }}</span>
      <el-dropdown trigger="hover" @command="handleCommand">
        <div class="user-info">
          <img class="avatar" :src="avatarSrc" alt="用户头像">
          <span class="greet"><el-icon>
              <User />
            </el-icon> {{ greet }}, {{ userInfo.username || '管理员' }}！</span>
          <el-icon class="arrow">
            <ArrowDown />
          </el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人主页</el-dropdown-item>
            <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>
</template>

<style scoped>
@import "../assets/css/header.css";

.header {
  height: 70px;
  background: linear-gradient(90deg, #ffecd2, #fcb69f);
  /* 渐变背景 */
  color: #333;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  /* 阴影 */
  border-radius: 0 0 12px 12px;
  font-family: "Segoe UI", "PingFang SC", "Helvetica Neue", sans-serif;
}

.right {
  display: flex;
  align-items: center;
  gap: 16px;
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
  border: 2px solid rgba(255, 255, 255, 0.6);
}

.arrow {
  font-size: 12px;
  color: #555;
}
</style>

