<template>
  <div class="user-layout">
    <UserHeader />
    <main class="main-content">
      <section class="welcome">
        <h1>用户中心</h1>
        <p>管理您的个人资料和账户设置</p>
      </section>
      <section class="profile-card">
        <h2>账号信息</h2>
        <p>用户名：{{ profile.username }}</p>
        <p>角色：{{ roleLabel }}</p>
        <p v-if="profile.email">邮箱：{{ profile.email }}</p>
        <p v-if="profile.description">简介：{{ profile.description }}</p>
        <p>注册时间：{{ profile.create_time }}</p>
      </section>
      <section class="user-actions">
        <h2>用户操作</h2>
        <div class="actions-grid">
          <el-button type="primary" @click="goToProfile">编辑资料</el-button>
          <el-button type="info" @click="changePassword">修改密码</el-button>
          <el-button type="warning" @click="managePets">管理宠物</el-button>
          <el-button type="success" @click="viewHistory">查看历史</el-button>
        </div>
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
  padding: 32px;
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

.user-actions {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
  margin-top: 16px;
}
</style>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import UserHeader from '../components/UserHeader.vue';
import UserFooter from '../components/UserFooter.vue';

const router = useRouter()
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

const goToProfile = () => {
  router.push('/profile')
}

const changePassword = () => {
  // 密码修改逻辑
  console.log('跳转到密码修改页面')
}

const managePets = () => {
  // 宠物管理逻辑
  console.log('跳转到宠物管理页面')
}

const viewHistory = () => {
  // 查看历史记录逻辑
  console.log('跳转到历史记录页面')
}
</script>