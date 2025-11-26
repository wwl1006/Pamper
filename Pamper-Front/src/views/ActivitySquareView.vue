<template>
  <div class="activity-square-container">
    <div class="header-section">
      <h2>活动中心</h2>
      <el-button v-if="isAdmin" type="primary" @click="goToCreate">
        <el-icon><Plus /></el-icon>
        发布活动
      </el-button>
    </div>

    <div class="filter-section">
      <el-radio-group v-model="activityType" @change="loadActivities">
        <el-radio-button value="">全部活动</el-radio-button>
        <el-radio-button value="线上">线上活动</el-radio-button>
        <el-radio-button value="线下">线下活动</el-radio-button>
        <el-radio-button value="混合">混合活动</el-radio-button>
      </el-radio-group>
    </div>

    <div class="activities-grid" v-loading="loading">
      <el-empty v-if="!loading && activityList.length === 0" description="暂无活动" />

      <div v-for="activity in activityList" :key="activity.id" class="activity-card" @click="goToDetail(activity.id)">
        <div class="card-cover" v-if="activity.cover_image">
          <el-image :src="getCoverUrl(activity.cover_image)" fit="cover" style="width: 100%; height: 100%" />
        </div>
        <div class="card-content">
          <div class="card-header">
            <el-tag :type="getStatusColor(activity.status)" size="small">{{ getStatusText(activity.status) }}</el-tag>
            <el-tag size="small">{{ activity.activity_type }}</el-tag>
            <el-tag v-if="activity.is_registered" type="success" size="small">已报名</el-tag>
          </div>

          <h3 class="activity-title">{{ activity.title }}</h3>

          <div class="activity-info">
            <div class="info-item">
              <el-icon><Calendar /></el-icon>
              <span>{{ activity.start_time }}</span>
            </div>
            <div class="info-item" v-if="activity.location">
              <el-icon><Location /></el-icon>
              <span>{{ activity.location }}</span>
            </div>
            <div class="info-item">
              <el-icon><User /></el-icon>
              <span>{{ activity.current_participants }}/{{ activity.max_participants || '不限' }}</span>
              <el-tag v-if="activity.is_full" type="danger" size="small">已满</el-tag>
            </div>
          </div>

          <div class="activity-footer">
            <div class="organizer">
              <span>主办: {{ activity.organizer || activity.creator_name }}</span>
            </div>
            <div class="stats">
              <span><el-icon><View /></el-icon> {{ activity.view_count }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="pagination-section" v-if="total > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadActivities"
        @current-change="loadActivities"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Calendar, Location, User, View } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()
const loading = ref(false)
const activityList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const activityType = ref('')
const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const userInfo = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('userInfo') || '{}')
  } catch {
    return {}
  }
})

const isAdmin = computed(() => userInfo.value.user_type === 0)

const getCoverUrl = (coverPath) => {
  if (!coverPath) return ''
  if (coverPath.startsWith('http')) return coverPath
  return `${API_BASE}/${coverPath}`
}

const getStatusColor = (status) => {
  const colors = {
    0: 'warning',
    1: 'success',
    2: 'danger',
    3: 'info',
    4: 'primary',
    5: 'info'
  }
  return colors[status] || ''
}

const getStatusText = (status) => {
  const texts = {
    0: '待审核',
    1: '已发布',
    2: '已拒绝',
    3: '已取消',
    4: '进行中',
    5: '已结束'
  }
  return texts[status] || '未知'
}

const loadActivities = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    }

    if (activityType.value) {
      params.activityType = activityType.value
    }

    const response = await request.get('/activity/list', {
      params,
      headers: {
        'Authorization': localStorage.getItem('token')
      }
    })

    if (response.code === 200) {
      activityList.value = response.data.list
      total.value = response.data.total
    } else {
      ElMessage.error(response.msg || '加载失败')
    }
  } catch (error) {
    console.error('加载活动列表失败:', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const goToCreate = () => {
  router.push('/activity/create')
}

const goToDetail = (activityId) => {
  router.push(`/activity/${activityId}`)
}

onMounted(() => {
  loadActivities()
})
</script>

<style scoped>
.activity-square-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-section h2 {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.filter-section {
  margin-bottom: 24px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.activities-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  min-height: 300px;
  margin-bottom: 24px;
}

.activity-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
}

.activity-card:hover {
  box-shadow: 0 4px 20px rgba(111, 75, 176, 0.15);
  transform: translateY(-4px);
}

.card-cover {
  width: 100%;
  height: 200px;
  background: #f5f5f5;
  overflow: hidden;
}

.card-content {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex: 1;
}

.card-header {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.activity-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.activity-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
}

.activity-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  margin-top: auto;
}

.organizer {
  font-size: 13px;
  color: #999;
}

.stats {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #999;
}

.stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-section {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #6f4bb0 0%, #4b2e83 100%);
  border: none;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #5a3d91 0%, #3d2569 100%);
}

:deep(.el-radio-button__original-radio:checked+.el-radio-button__inner) {
  background-color: #6f4bb0;
  border-color: #6f4bb0;
}
</style>
