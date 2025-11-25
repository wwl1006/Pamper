<template>
  <div class="home-layout">
    <UserHeader />
    <main class="main-content">
      <!-- 欢迎区域 -->
      <section class="welcome-section">
        <div class="welcome-card">
          <h1>欢迎来到宠伴平台，{{ profile.username || '宠友' }}！</h1>
          <p>与您的毛孩子们一起享受美好时光，分享快乐和关爱。</p>
        </div>
      </section>

      <!-- 快捷功能区域 -->
      <section class="quick-actions">
        <h2>快速服务</h2>
        <div class="actions-grid">
          <div class="action-card" @click="goTo('/profile')">
            <div class="action-icon">👤</div>
            <h3>个人中心</h3>
            <p>查看和编辑个人信息</p>
          </div>
          <div class="action-card" @click="goTo('/community')">
            <div class="action-icon">💬</div>
            <h3>社区交流</h3>
            <p>与宠物爱好者互动</p>
          </div>
          <div class="action-card" @click="goTo('/adoption')">
            <div class="action-icon">🐾</div>
            <h3>领养中心</h3>
            <p>寻找或提供宠物领养</p>
          </div>
          <div class="action-card" @click="goTo('/shop')">
            <div class="action-icon">🛒</div>
            <h3>宠物商城</h3>
            <p>购买宠物用品</p>
          </div>
          <div class="action-card" @click="goTo('/services')">
            <div class="action-icon">🏥</div>
            <h3>宠物服务</h3>
            <p>预约专业服务</p>
          </div>
          <div class="action-card" @click="goTo('/health')">
            <div class="action-icon">💉</div>
            <h3>健康档案</h3>
            <p>管理宠物健康记录</p>
          </div>
        </div>
      </section>

      <!-- 宠物动态区域 -->
      <section class="pet-activity">
        <h2>宠物动态</h2>
        <div class="activity-list">
          <div class="activity-item" v-for="(item, index) in activities" :key="index">
            <div class="avatar">{{ item.avatar }}</div>
            <div class="activity-content">
              <h4>{{ item.title }}</h4>
              <p>{{ item.content }}</p>
              <span class="time">{{ item.time }}</span>
            </div>
          </div>
        </div>
      </section>
    </main>
    <UserFooter />
  </div>
</template>

<style scoped>
.home-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 32px;
  background: #f5f6fa;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.welcome-section {
  background: linear-gradient(135deg, #6f4bb0, #4b2e83);
  border-radius: 16px;
  padding: 32px;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.welcome-card h1 {
  margin: 0 0 12px 0;
  font-size: 28px;
  font-weight: 600;
}

.welcome-card p {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

.quick-actions h2, .pet-activity h2 {
  margin: 0 0 16px 0;
  font-size: 20px;
  color: #333;
  font-weight: 600;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.action-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.action-icon {
  font-size: 36px;
  margin-bottom: 12px;
}

.action-card h3 {
  margin: 10px 0 6px 0;
  font-size: 16px;
  color: #333;
}

.action-card p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.pet-activity {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  background: #f9f9f9;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #6f4bb0;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.activity-content h4 {
  margin: 0 0 6px 0;
  font-size: 16px;
  color: #333;
}

.activity-content p {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

.time {
  font-size: 12px;
  color: #999;
}

@media (max-width: 768px) {
  .main-content {
    padding: 16px;
  }

  .welcome-card h1 {
    font-size: 24px;
  }

  .actions-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 12px;
  }

  .activity-item {
    flex-direction: column;
  }

  .avatar {
    align-self: flex-start;
  }
}
</style>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import UserHeader from '../components/UserHeader.vue';
import UserFooter from '../components/UserFooter.vue';

const router = useRouter()
const profile = ref({})
const activities = ref([
  { id: 1, avatar: '🐶', title: '小明的金毛犬', content: '今天带我家的金毛去公园玩，它玩得可开心了！', time: '2小时前' },
  { id: 2, avatar: '🐱', title: '小红的布偶猫', content: '分享我家布偶猫的新照片，它最近长胖了点。', time: '4小时前' },
  { id: 3, avatar: '🐰', title: '小刚的兔子', content: '兔子打喷嚏了，是不是感冒了？求问大家怎么护理？', time: '6小时前' },
  { id: 4, avatar: '🦜', title: '小李的鹦鹉', content: '教我的玄凤鹦鹉说话，它今天学到了新单词！', time: '8小时前' }
])

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

const goTo = (path) => {
  // 临时导航函数，显示页面路径
  console.log('导航到:', path)
  // 在实际项目中可以使用：router.push(path)
}
</script>