<template>
  <div class="dashboard-layout">
    <UserHeader />
    <main class="dashboard-content">
      <!-- 欢迎区域 -->
      <section class="welcome-section">
        <div class="welcome-card">
          <div class="welcome-text">
            <h1>欢迎来到宠伴数据中心，{{ profile.username || '宠友' }}！</h1>
            <p>实时查看平台运营数据，洞察宠物社区动态</p>
          </div>
          <div class="welcome-icon">📊</div>
        </div>
      </section>

      <!-- 搜索区域 -->
      <section class="search-section">
        <div class="search-container">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索帖子、宠物、用户..."
            class="search-input"
            clearable
            size="large"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
            <template #append>
              <el-button style="width: 100px;" type="primary" @click="handleSearch">搜索</el-button>
            </template>
          </el-input>
        </div>
      </section>

      <!-- 统计卡片区域 -->
      <section class="stats-cards">
        <div class="stat-card" v-for="(stat, index) in statsData" :key="index">
          <div class="stat-icon" :style="{ background: stat.color }">
            {{ stat.icon }}
          </div>
          <div class="stat-content">
            <div class="stat-label">{{ stat.label }}</div>
            <div class="stat-value">
              <span class="number">{{ animatedNumbers[index] }}</span>
              <el-tag v-if="stat.tag" :type="stat.tagType" size="small" style="margin-left: 8px">
                {{ stat.tag }}
              </el-tag>
            </div>
          </div>
        </div>
      </section>

      <!-- 图表区域 -->
      <section class="charts-section">
        <!-- 用户增长趋势 -->
        <el-card class="chart-card large">
          <template #header>
            <div class="card-header">
              <span class="header-title">📈 用户增长趋势</span>
              <el-radio-group v-model="userGrowthDays" size="small" @change="loadUserGrowth">
                <el-radio-button :value="7">近7天</el-radio-button>
                <el-radio-button :value="30">近30天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="userGrowthChart" class="chart-container"></div>
        </el-card>

        <!-- 内容分布 -->
        <el-card class="chart-card">
          <template #header>
            <span class="header-title">📝 帖子分类分布</span>
          </template>
          <div ref="postCategoryChart" class="chart-container"></div>
        </el-card>

        <el-card class="chart-card">
          <template #header>
            <span class="header-title">🐾 领养宠物类型</span>
          </template>
          <div ref="adoptionTypeChart" class="chart-container"></div>
        </el-card>

        <!-- 活动统计 -->
        <el-card class="chart-card">
          <template #header>
            <span class="header-title">🎉 活动类型统计</span>
          </template>
          <div ref="activityTypeChart" class="chart-container"></div>
        </el-card>

        <!-- 互动数据 -->
        <el-card class="chart-card">
          <template #header>
            <span class="header-title">💬 平台互动数据</span>
          </template>
          <div ref="interactionChart" class="chart-container"></div>
        </el-card>
      </section>

      <!-- 快捷功能区域 -->
      <section class="quick-actions">
        <h2>快速导航</h2>
        <div class="actions-grid">
          <div class="action-card" @click="goTo('/profile')">
            <div class="action-icon">👤</div>
            <h3>个人中心</h3>
          </div>
          <div class="action-card" @click="goTo('/community')">
            <div class="action-icon">💬</div>
            <h3>社区交流</h3>
          </div>
          <div class="action-card" @click="goTo('/adoption')">
            <div class="action-icon">🐾</div>
            <h3>领养中心</h3>
          </div>
          <div class="action-card" @click="goTo('/mypets')">
            <div class="action-icon">🐕</div>
            <h3>我的宠物</h3>
          </div>
          <div class="action-card" @click="goTo('/services')">
            <div class="action-icon">🏥</div>
            <h3>宠物服务</h3>
          </div>
          <div class="action-card" @click="goTo('/activities')">
            <div class="action-icon">🎉</div>
            <h3>活动中心</h3>
          </div>
          <div class="action-card" @click="goTo('/news')">
            <div class="action-icon">📰</div>
            <h3>宠物资讯</h3>
          </div>
          <div class="action-card" @click="goTo('/notice')">
            <div class="action-icon">🔔</div>
            <h3>通知中心</h3>
          </div>
          <div class="action-card" v-if="profile.user_type === 0" @click="goTo('/admin/users')">
            <div class="action-icon">⚙️</div>
            <h3>后台管理</h3>
          </div>
        </div>
      </section>
    </main>
    <UserFooter />
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import UserHeader from '../components/UserHeader.vue'
import UserFooter from '../components/UserFooter.vue'
import request from '../utils/request'

const router = useRouter()
const profile = ref({})
const userGrowthDays = ref(7)
const searchKeyword = ref('')

// 图表实例
const userGrowthChart = ref(null)
const postCategoryChart = ref(null)
const adoptionTypeChart = ref(null)
const activityTypeChart = ref(null)
const interactionChart = ref(null)

let charts = []

// 搜索功能
const handleSearch = () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  // 跳转到社区页面并传递搜索关键词
  router.push({ path: '/community', query: { keyword: searchKeyword.value } })
}

// 统计数据
const statsData = ref([
  { icon: '👥', label: '总用户数', value: 0, color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', tag: '', tagType: 'success' },
  { icon: '✅', label: '活跃用户', value: 0, color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)', tag: '', tagType: 'success' },
  { icon: '📝', label: '帖子总数', value: 0, color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)', tag: '', tagType: 'info' },
  { icon: '⏳', label: '待审核帖子', value: 0, color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)', tag: '', tagType: 'warning' },
  { icon: '🐾', label: '领养信息', value: 0, color: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)', tag: '', tagType: 'info' },
  { icon: '📋', label: '待审核领养', value: 0, color: 'linear-gradient(135deg, #30cfd0 0%, #330867 100%)', tag: '', tagType: 'warning' },
  { icon: '🎉', label: '活动总数', value: 0, color: 'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)', tag: '', tagType: 'info' },
  { icon: '🐶', label: '宠物档案', value: 0, color: 'linear-gradient(135deg, #ffd89b 0%, #19547b 100%)', tag: '', tagType: 'success' }
])

const animatedNumbers = ref([0, 0, 0, 0, 0, 0, 0, 0])

// 数字动画
const animateNumber = (index, target) => {
  const duration = 1000
  const start = animatedNumbers.value[index]
  const range = target - start
  const startTime = Date.now()

  const animate = () => {
    const now = Date.now()
    const progress = Math.min((now - startTime) / duration, 1)
    const easeOut = 1 - Math.pow(1 - progress, 3)
    animatedNumbers.value[index] = Math.floor(start + range * easeOut)

    if (progress < 1) {
      requestAnimationFrame(animate)
    }
  }

  requestAnimationFrame(animate)
}

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

// 加载统计数据
const loadDashboardStats = async () => {
  try {
    const res = await request.get('/api/admin/dashboard/stats')
    if (res.code === 200) {
      statsData.value[0].value = res.data.totalUsers || 0
      statsData.value[1].value = res.data.activeUsers || 0
      statsData.value[2].value = res.data.totalPosts || 0
      statsData.value[3].value = res.data.pendingPosts || 0
      statsData.value[4].value = res.data.totalAdoptions || 0
      statsData.value[5].value = res.data.pendingAdoptions || 0
      statsData.value[6].value = res.data.totalActivities || 0
      statsData.value[7].value = res.data.totalPets || 0

      // 触发数字动画
      statsData.value.forEach((stat, index) => {
        animateNumber(index, stat.value)
      })
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

// 加载用户增长数据
const loadUserGrowth = async () => {
  try {
    const res = await request.get('/api/admin/dashboard/user-growth', {
      params: { days: userGrowthDays.value }
    })
    if (res.code === 200) {
      renderUserGrowthChart(res.data)
    }
  } catch (error) {
    console.error('加载用户增长数据失败', error)
  }
}

// 加载内容统计
const loadContentStats = async () => {
  try {
    const res = await request.get('/api/admin/dashboard/content-stats')
    if (res.code === 200) {
      renderPostCategoryChart(res.data.postsByCategory || [])
      renderAdoptionTypeChart(res.data.adoptionsByPetType || [])
      renderInteractionChart({
        likes: res.data.totalLikes || 0,
        comments: res.data.totalComments || 0
      })
    }
  } catch (error) {
    console.error('加载内容统计失败', error)
  }
}

// 加载活动统计
const loadActivityStats = async () => {
  try {
    const res = await request.get('/api/admin/dashboard/activity-stats')
    if (res.code === 200) {
      renderActivityTypeChart(res.data.activitiesByType || [])
    }
  } catch (error) {
    console.error('加载活动统计失败', error)
  }
}

// 渲染用户增长图表
const renderUserGrowthChart = (data) => {
  const chart = echarts.init(userGrowthChart.value)
  charts.push(chart)

  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(50, 50, 50, 0.9)',
      borderColor: '#6f4bb0',
      borderWidth: 1,
      textStyle: { color: '#fff' }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: data.map(item => item.date),
      axisLine: { lineStyle: { color: '#ddd' } },
      axisLabel: { color: '#666' }
    },
    yAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#ddd' } },
      axisLabel: { color: '#666' },
      splitLine: { lineStyle: { color: '#f0f0f0' } }
    },
    series: [{
      data: data.map(item => item.count),
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: {
        width: 3,
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#6f4bb0' },
          { offset: 1, color: '#a27bff' }
        ])
      },
      itemStyle: {
        color: '#6f4bb0',
        borderWidth: 2,
        borderColor: '#fff'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(111, 75, 176, 0.3)' },
          { offset: 1, color: 'rgba(111, 75, 176, 0.05)' }
        ])
      }
    }]
  }

  chart.setOption(option)
}

// 渲染帖子分类图表
const renderPostCategoryChart = (data) => {
  const chart = echarts.init(postCategoryChart.value)
  charts.push(chart)

  const option = {
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(50, 50, 50, 0.9)',
      borderColor: '#6f4bb0',
      borderWidth: 1,
      textStyle: { color: '#fff' }
    },
    legend: {
      orient: 'vertical',
      right: '10%',
      top: 'center',
      textStyle: { color: '#666' }
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: true,
        formatter: '{b}: {c}'
      },
      emphasis: {
        label: { show: true, fontSize: 16, fontWeight: 'bold' }
      },
      data: data,
      color: ['#6f4bb0', '#a27bff', '#4b2e83', '#c9a8ff', '#8b5fcf', '#b794f6']
    }]
  }

  chart.setOption(option)
}

// 渲染领养类型图表
const renderAdoptionTypeChart = (data) => {
  const chart = echarts.init(adoptionTypeChart.value)
  charts.push(chart)

  const option = {
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(50, 50, 50, 0.9)',
      borderColor: '#6f4bb0',
      borderWidth: 1,
      textStyle: { color: '#fff' }
    },
    legend: {
      orient: 'vertical',
      right: '10%',
      top: 'center',
      textStyle: { color: '#666' }
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['35%', '50%'],
      roseType: 'area',
      itemStyle: {
        borderRadius: 8,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: true,
        formatter: '{b}: {c}'
      },
      data: data,
      color: ['#ff6b9d', '#ffa07a', '#ffd700', '#98d8c8', '#9fb8ff', '#b794f6']
    }]
  }

  chart.setOption(option)
}

// 渲染活动类型图表
const renderActivityTypeChart = (data) => {
  const chart = echarts.init(activityTypeChart.value)
  charts.push(chart)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: 'rgba(50, 50, 50, 0.9)',
      borderColor: '#6f4bb0',
      borderWidth: 1,
      textStyle: { color: '#fff' }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: data.map(item => item.name),
      axisLine: { lineStyle: { color: '#ddd' } },
      axisLabel: { color: '#666' }
    },
    yAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#ddd' } },
      axisLabel: { color: '#666' },
      splitLine: { lineStyle: { color: '#f0f0f0' } }
    },
    series: [{
      data: data.map(item => item.value),
      type: 'bar',
      barWidth: '50%',
      itemStyle: {
        borderRadius: [8, 8, 0, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#6f4bb0' },
          { offset: 1, color: '#a27bff' }
        ])
      },
      emphasis: {
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#8b5fcf' },
            { offset: 1, color: '#b794f6' }
          ])
        }
      }
    }]
  }

  chart.setOption(option)
}

// 渲染互动数据图表
const renderInteractionChart = (data) => {
  const chart = echarts.init(interactionChart.value)
  charts.push(chart)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: 'rgba(50, 50, 50, 0.9)',
      borderColor: '#6f4bb0',
      borderWidth: 1,
      textStyle: { color: '#fff' }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['点赞数', '评论数'],
      axisLine: { lineStyle: { color: '#ddd' } },
      axisLabel: { color: '#666', fontSize: 14 }
    },
    yAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#ddd' } },
      axisLabel: { color: '#666' },
      splitLine: { lineStyle: { color: '#f0f0f0' } }
    },
    series: [{
      data: [
        { value: data.likes, itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#ff6b9d' },
          { offset: 1, color: '#ffa07a' }
        ])}},
        { value: data.comments, itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#6f4bb0' },
          { offset: 1, color: '#a27bff' }
        ])}}
      ],
      type: 'bar',
      barWidth: '40%',
      itemStyle: {
        borderRadius: [8, 8, 0, 0]
      },
      label: {
        show: true,
        position: 'top',
        color: '#666',
        fontSize: 14,
        fontWeight: 'bold'
      }
    }]
  }

  chart.setOption(option)
}

const goTo = (path) => {
  router.push(path)
}

// 响应式调整
const handleResize = () => {
  charts.forEach(chart => chart.resize())
}

onMounted(() => {
  loadProfile()
  window.addEventListener('pamper-profile-updated', handleProfileEvent)
  window.addEventListener('resize', handleResize)

  nextTick(() => {
    loadDashboardStats()
    loadUserGrowth()
    loadContentStats()
    loadActivityStats()
  })
})

onBeforeUnmount(() => {
  window.removeEventListener('pamper-profile-updated', handleProfileEvent)
  window.removeEventListener('resize', handleResize)
  charts.forEach(chart => chart.dispose())
})
</script>

<style scoped>
.dashboard-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f6fa;
}

.dashboard-content {
  flex: 1;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.welcome-section {
  background: linear-gradient(135deg, #6f4bb0, #4b2e83);
  border-radius: 16px;
  padding: 32px;
  color: white;
  box-shadow: 0 8px 24px rgba(111, 75, 176, 0.3);
  position: relative;
  overflow: hidden;
}

.welcome-section::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -10%;
  width: 300px;
  height: 300px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.welcome-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
}

.search-section {
  margin-top: -12px;
}

.search-container {
  max-width: 800px;
  margin: 0 auto;
}

.search-input {
  border-radius: 50px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 50px 0 0 50px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.search-input :deep(.el-input-group__append) {
  border-radius: 0 50px 50px 0;
  background: linear-gradient(135deg, #6f4bb0, #4b2e83);
  border: none;
  padding: 0;
}

.search-input :deep(.el-input-group__append .el-button) {
  background: transparent;
  border: none;
  color: white;
  font-weight: 500;
}

.search-input :deep(.el-input-group__append .el-button:hover) {
  background: transparent;
  color: white;
}

.welcome-text h1 {
  margin: 0 0 12px 0;
  font-size: 32px;
  font-weight: 700;
}

.welcome-text p {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

.welcome-icon {
  font-size: 80px;
  opacity: 0.8;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.stat-value {
  display: flex;
  align-items: center;
}

.stat-value .number {
  font-size: 28px;
  font-weight: 700;
  color: #333;
}

.charts-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
}

.chart-card {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-radius: 16px;
  overflow: hidden;
}

.chart-card.large {
  grid-column: 1 / -1;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.chart-container {
  width: 100%;
  height: 320px;
}

.quick-actions h2 {
  margin: 0 0 16px 0;
  font-size: 20px;
  color: #333;
  font-weight: 600;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
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
  margin-bottom: 8px;
}

.action-card h3 {
  margin: 0;
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

@media (max-width: 768px) {
  .dashboard-content {
    padding: 16px;
  }

  .welcome-text h1 {
    font-size: 24px;
  }

  .welcome-icon {
    font-size: 50px;
  }

  .stats-cards {
    grid-template-columns: 1fr;
  }

  .charts-section {
    grid-template-columns: 1fr;
  }

  .actions-grid {
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  }
}
</style>
