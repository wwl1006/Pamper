<template>
  <div class="user-layout">
    <UserHeader />
    <main class="main-content">
      <section class="welcome">
        <h1>欢迎回来，{{ profile.username || '宠友' }}！</h1>
        <p>这是根据后端登录接口返回的信息渲染的个人简介区域。</p>
      </section>
      <section class="profile-card">
        <h2>账号信息</h2>
        <p>用户名：{{ profile.username }}</p>
        <p>角色：{{ roleLabel }}</p>
        <p v-if="profile.email">邮箱：{{ profile.email }}</p>
        <p v-if="profile.description">简介：{{ profile.description }}</p>
        <p>注册时间：{{ profile.create_time }}</p>
      </section>
    </main>
    <UserFooter />
  </div>
</template>

<style scoped>
.user-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 32px 48px;
  background: #f5f6fa;
  display: grid;
  gap: 24px;
}

.welcome {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.profile-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  line-height: 1.8;
}
</style>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import UserHeader from '../components/UserHeader.vue';
import UserFooter from '../components/UserFooter.vue';

const profile = ref({})

const loadProfile = (payload) => {
  if (payload) {
    profile.value = payload
    return
  }
  try {
    const cache = JSON.parse(localStorage.getItem('userInfo') || '{}')
    profile.value = cache
  } catch (error) {
    profile.value = {}
  }
}

const handleProfileEvent = (event) => {
  loadProfile(event?.detail)
}

onMounted(() => {
  loadProfile()
  window.addEventListener('pamper-profile-updated', handleProfileEvent)
})

onBeforeUnmount(() => {
  window.removeEventListener('pamper-profile-updated', handleProfileEvent)
})

const roleLabel = computed(() => {
  if (profile.value.user_type === 0) return '管理员'
  if (profile.value.user_type === 2) return '商户用户'
  return '普通用户'
})
</script>