<template>
  <div class="admin-dashboard">
    <!-- 顶部欢迎区 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <h1>👋 欢迎回来，管理员</h1>
        <p>实时掌握平台运营数据，高效管理宠物社区</p>
      </div>
      <div class="banner-time">
        {{ currentTime }}
      </div>
    </div>

    <!-- 数据统计卡片 -->
    <div class="stats-grid">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-header">
          <el-icon :size="40" color="#3498db"><User /></el-icon>
        </div>
        <div class="stat-body">
          <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
          <div class="stat-label">总用户数</div>
          <div class="stat-extra">
            <el-tag type="success" size="small">
              活跃 {{ stats.activeUsers || 0 }}
            </el-tag>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-header">
          <el-icon :size="40" color="#e74c3c"><Document /></el-icon>
        </div>
        <div class="stat-body">
          <div class="stat-value">{{ stats.totalPosts || 0 }}</div>
          <div class="stat-label">已发布帖子</div>
          <div class="stat-extra">
            <el-tag type="warning" size="small">
              待审核 {{ stats.pendingPosts || 0 }}
            </el-tag>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-header">
          <el-icon :size="40" color="#f39c12"><Sunny /></el-icon>
        </div>
        <div class="stat-body">
          <div class="stat-value">{{ stats.totalAdoptions || 0 }}</div>
          <div class="stat-label">领养信息</div>
          <div class="stat-extra">
            <el-tag type="warning" size="small">
              待审核 {{ stats.pendingAdoptions || 0 }}
            </el-tag>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-header">
          <el-icon :size="40" color="#9b59b6"><Calendar /></el-icon>
        </div>
        <div class="stat-body">
          <div class="stat-value">{{ stats.totalActivities || 0 }}</div>
          <div class="stat-label">平台活动</div>
          <div class="stat-extra">
            <el-tag type="warning" size="small">
              待审核 {{ stats.pendingActivities || 0 }}
            </el-tag>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-header">
          <el-icon :size="40" color="#16a085"><Avatar /></el-icon>
        </div>
        <div class="stat-body">
          <div class="stat-value">{{ stats.totalPets || 0 }}</div>
          <div class="stat-label">宠物档案</div>
          <div class="stat-extra">
            <el-tag type="info" size="small">已创建</el-tag>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-header">
          <el-icon :size="40" color="#27ae60"><TrendCharts /></el-icon>
        </div>
        <div class="stat-body">
          <div class="stat-value">{{ totalPending }}</div>
          <div class="stat-label">待处理事项</div>
          <div class="stat-extra">
            <el-tag type="danger" size="small">需关注</el-tag>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card class="chart-card">
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
      </el-col>

      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <span class="header-title">📝 帖子分类分布</span>
          </template>
          <div ref="postCategoryChart" class="chart-container-small"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <span class="header-title">🐾 领养宠物类型</span>
          </template>
          <div ref="adoptionTypeChart" class="chart-container-small"></div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <span class="header-title">🎉 活动类型统计</span>
          </template>
          <div ref="activityTypeChart" class="chart-container-small"></div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <span class="header-title">💬 互动数据</span>
          </template>
          <div ref="interactionChart" class="chart-container-small"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-card class="quick-actions-card" style="margin-top: 20px">
      <template #header>
        <span class="header-title">⚡ 快捷操作</span>
      </template>
      <div class="quick-actions-grid">
        <div class="quick-action-item" @click="$router.push('/admin/review')">
          <el-icon :size="24" color="#3498db"><Select /></el-icon>
          <span>内容审核</span>
          <el-badge :value="totalPending" :max="99" v-if="totalPending > 0" />
        </div>
        <div class="quick-action-item" @click="$router.push('/admin/users')">
          <el-icon :size="24" color="#2ecc71"><User /></el-icon>
          <span>用户管理</span>
        </div>
        <div class="quick-action-item" @click="$router.push('/admin/notice')">
          <el-icon :size="24" color="#f39c12"><Bell /></el-icon>
          <span>发送公告</span>
        </div>
        <div class="quick-action-item" @click="$router.push('/')">
          <el-icon :size="24" color="#9b59b6"><HomeFilled /></el-icon>
          <span>返回首页</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  User,
  Document,
  Sunny,
  Calendar,
  Avatar,
  TrendCharts,
  Select,
  Bell,
  HomeFilled
} from '@element-plus/icons-vue'
import request from '../utils/request'
import * as echarts from 'echarts'

const stats = ref({})
const userGrowthDays = ref(7)
const currentTime = ref('')

const userGrowthChart = ref(null)
const postCategoryChart = ref(null)
const adoptionTypeChart = ref(null)
const activityTypeChart = ref(null)
const interactionChart = ref(null)

let chartInstances = []
let timeInterval = null

const totalPending = computed(() => {
  return (stats.value.pendingPosts || 0) +
         (stats.value.pendingAdoptions || 0) +
         (stats.value.pendingActivities || 0)
})

const updateTime = () => {
  const now = new Date()
  const options = {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  }
  currentTime.value = now.toLocaleString('zh-CN', options)
}

const loadDashboardStats = async () => {
  try {
    const res = await request.get('/api/admin/dashboard/stats')
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch (error) {
    ElMessage.error('加载统计数据失败')
  }
}

const loadUserGrowth = async () => {
  try {
    const res = await request.get('/api/admin/dashboard/user-growth', {
      params: { days: userGrowthDays.value }
    })
    if (res.code === 200 && userGrowthChart.value) {
      renderUserGrowthChart(res.data)
    }
  } catch (error) {
    ElMessage.error('加载用户增长数据失败')
  }
}

const loadContentStats = async () => {
  try {
    const res = await request.get('/api/admin/dashboard/content-stats')
    if (res.code === 200) {
      if (postCategoryChart.value) {
        renderPostCategoryChart(res.data.postsByCategory || [])
      }
      if (adoptionTypeChart.value) {
        renderAdoptionTypeChart(res.data.adoptionsByPetType || [])
      }
      if (interactionChart.value) {
        renderInteractionChart(res.data)
      }
    }
  } catch (error) {
    ElMessage.error('加载内容统计失败')
  }
}

const loadActivityStats = async () => {
  try {
    const res = await request.get('/api/admin/dashboard/activity-stats')
    if (res.code === 200 && activityTypeChart.value) {
      renderActivityTypeChart(res.data.activitiesByType || [])
    }
  } catch (error) {
    ElMessage.error('加载活动统计失败')
  }
}

const renderUserGrowthChart = (data) => {
  const chart = echarts.init(userGrowthChart.value)
  chartInstances.push(chart)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.date.substring(5)),
      axisLine: { lineStyle: { color: '#999' } }
    },
    yAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#999' } }
    },
    series: [{
      name: '新增用户',
      type: 'line',
      smooth: true,
      data: data.map(item => item.count),
      itemStyle: { color: '#3498db' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(52, 152, 219, 0.4)' },
          { offset: 1, color: 'rgba(52, 152, 219, 0.1)' }
        ])
      }
    }]
  }

  chart.setOption(option)
}

const renderPostCategoryChart = (data) => {
  const chart = echarts.init(postCategoryChart.value)
  chartInstances.push(chart)

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: true,
        formatter: '{b}\n{d}%'
      },
      data: data.map(item => ({
        name: item.category || '未分类',
        value: item.count
      })),
      color: ['#3498db', '#e74c3c', '#f39c12', '#9b59b6', '#2ecc71']
    }]
  }

  chart.setOption(option)
}

const renderAdoptionTypeChart = (data) => {
  const chart = echarts.init(adoptionTypeChart.value)
  chartInstances.push(chart)

  const option = {
    tooltip: {
      trigger: 'item'
    },
    series: [{
      type: 'pie',
      radius: '70%',
      data: data.map(item => ({
        name: item.pet_type || '其他',
        value: item.count
      })),
      color: ['#ff6b6b', '#4ecdc4', '#45b7d1', '#f9ca24', '#6c5ce7'],
      label: {
        show: true,
        formatter: '{b}\n{c}'
      }
    }]
  }

  chart.setOption(option)
}

const renderActivityTypeChart = (data) => {
  const chart = echarts.init(activityTypeChart.value)
  chartInstances.push(chart)

  const option = {
    tooltip: {
      trigger: 'item'
    },
    series: [{
      type: 'pie',
      radius: '70%',
      data: data.map(item => ({
        name: item.activity_type || '其他',
        value: item.count
      })),
      color: ['#9b59b6', '#3498db', '#e67e22'],
      label: {
        show: true,
        formatter: '{b}\n{c}'
      }
    }]
  }

  chart.setOption(option)
}

const renderInteractionChart = (data) => {
  const chart = echarts.init(interactionChart.value)
  chartInstances.push(chart)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['点赞', '评论'],
      axisLine: { lineStyle: { color: '#999' } }
    },
    yAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#999' } }
    },
    series: [{
      type: 'bar',
      data: [
        { value: data.totalLikes || 0, itemStyle: { color: '#e74c3c' } },
        { value: data.totalComments || 0, itemStyle: { color: '#3498db' } }
      ],
      barWidth: '50%'
    }]
  }

  chart.setOption(option)
}

onMounted(async () => {
  updateTime()
  timeInterval = setInterval(updateTime, 1000)

  await loadDashboardStats()
  await loadUserGrowth()
  await loadContentStats()
  await loadActivityStats()

  window.addEventListener('resize', () => {
    chartInstances.forEach(chart => chart.resize())
  })
})

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
  chartInstances.forEach(chart => chart.dispose())
  chartInstances = []
})
</script>

<style scoped>
.admin-dashboard {
  padding: 24px;
  background: #f5f6fa;
  min-height: 100vh;
}

.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 32px;
  border-radius: 16px;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
}

.banner-content h1 {
  margin: 0 0 8px;
  font-size: 28px;
  font-weight: 600;
}

.banner-content p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.banner-time {
  font-size: 18px;
  font-weight: 500;
  opacity: 0.95;
  font-family: 'Courier New', monospace;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 12px;
  transition: all 0.3s;
  cursor: pointer;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.stat-header {
  text-align: center;
  padding: 16px 0 8px;
}

.stat-body {
  text-align: center;
  padding: 0 0 16px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #2c3e50;
  margin: 8px 0;
}

.stat-label {
  font-size: 14px;
  color: #7f8c8d;
  margin-bottom: 8px;
}

.stat-extra {
  margin-top: 8px;
}

.chart-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.chart-container {
  height: 350px;
  width: 100%;
}

.chart-container-small {
  height: 280px;
  width: 100%;
}

.quick-actions-card {
  border-radius: 12px;
}

.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 16px;
}

.quick-action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px;
  border: 2px dashed #e0e0e0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.quick-action-item:hover {
  border-color: #3498db;
  background: rgba(52, 152, 219, 0.05);
  transform: translateY(-2px);
}

.quick-action-item span {
  margin-top: 12px;
  font-size: 14px;
  color: #2c3e50;
  font-weight: 500;
}

.quick-action-item .el-badge {
  position: absolute;
  top: 16px;
  right: 16px;
}

:deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.el-card__body) {
  padding: 20px;
}
</style>
