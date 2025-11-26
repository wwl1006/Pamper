<template>
  <div class="admin-layout">
    <div class="admin-header">
      <div class="header-left">
        <img src="../assets/Img/dog.webp" class="logo" />
        <span class="title">宠伴 - 管理后台</span>
      </div>
      <div class="header-right">
        <span class="admin-name">管理员：{{ userInfo.username || '管理员' }}</span>
        <el-button type="danger" size="small" @click="logout">退出登录</el-button>
      </div>
    </div>
    <div class="layout">
      <AdminSidebar />
      <div class="content">
        <router-view v-if="$route.matched.length > 1" />
        <div v-else class="welcome-placeholder">
          <h2>请从左侧菜单选择功能</h2>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AdminSidebar from '../components/AdminSidebar.vue'

const router = useRouter()
const userInfo = ref({})

const loadUserInfo = () => {
  try {
    const cache = localStorage.getItem('userInfo')
    userInfo.value = cache ? JSON.parse(cache) : {}
  } catch (error) {
    userInfo.value = {}
  }
}

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  router.push('/login')
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.admin-header {
  height: 60px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  width: 40px;
  height: 40px;
  border-radius: 8px;
}

.title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.admin-name {
  color: #606266;
  font-size: 14px;
}

.layout {
  flex: 1;
  display: flex;
  flex-direction: row;
  overflow: hidden;
}

.content {
  flex: 1;
  padding: 20px;
  background: #f5f6fa;
  overflow-y: auto;
}

.welcome-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
}
</style>
