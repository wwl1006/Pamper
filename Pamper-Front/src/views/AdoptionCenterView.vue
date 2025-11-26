<template>
  <div class="adoption-center-layout">
    <UserHeader />
    <main class="adoption-center-main">
      <!-- 顶部筛选栏 -->
      <div class="filter-bar">
        <el-radio-group v-model="currentPetType" @change="handlePetTypeChange">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="猫">猫咪</el-radio-button>
          <el-radio-button label="狗">狗狗</el-radio-button>
          <el-radio-button label="兔子">兔子</el-radio-button>
          <el-radio-button label="仓鼠">仓鼠</el-radio-button>
          <el-radio-button label="其他">其他</el-radio-button>
        </el-radio-group>

        <el-button type="primary" @click="goToCreate">发布领养信息</el-button>
      </div>

      <!-- 领养列表 -->
      <div v-if="loading" class="loading-state">加载中...</div>
      <div v-else-if="adoptionList.length === 0" class="empty-state">
        暂无领养信息，快来发布第一个吧！
      </div>
      <div v-else class="adoption-grid">
        <el-card
          v-for="adoption in adoptionList"
          :key="adoption.id"
          class="adoption-card"
          shadow="hover"
          @click="goToDetail(adoption.id)"
        >
          <!-- 宠物图片 -->
          <div class="pet-image-wrapper">
            <img
              v-if="adoption.images"
              :src="getFirstImage(adoption.images)"
              class="pet-image"
              alt="宠物照片"
            />
            <div v-else class="no-image">暂无图片</div>
            <el-tag
              class="status-tag"
              :type="getStatusType(adoption.status)"
              size="small"
            >
              {{ getStatusText(adoption.status) }}
            </el-tag>
          </div>

          <!-- 宠物信息 -->
          <div class="pet-info">
            <h3 class="pet-name">{{ adoption.pet_name }}</h3>
            <div class="pet-details">
              <el-tag size="small" type="info">{{ adoption.pet_type }}</el-tag>
              <span v-if="adoption.pet_breed" class="pet-breed">{{ adoption.pet_breed }}</span>
            </div>
            <div class="pet-meta">
              <span v-if="adoption.pet_age">{{ adoption.pet_age }}</span>
              <span v-if="adoption.pet_gender">{{ adoption.pet_gender }}</span>
              <span v-if="adoption.location">{{ adoption.location }}</span>
            </div>
            <div class="description">{{ truncateText(adoption.description, 50) }}</div>
          </div>

          <!-- 发布者信息 -->
          <div class="publisher-info">
            <img :src="getAvatarUrl(adoption.user_id)" class="publisher-avatar" />
            <div class="publisher-details">
              <div class="publisher-name">{{ adoption.username }}</div>
              <div class="publish-time">{{ adoption.create_time }}</div>
            </div>
            <div class="application-count">
              {{ adoption.application_count || 0 }} 人申请
            </div>
          </div>
        </el-card>
      </div>

      <!-- 分页器 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next, total"
          @current-change="handlePageChange"
        />
      </div>
    </main>
    <UserFooter />
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import UserHeader from '../components/UserHeader.vue'
import UserFooter from '../components/UserFooter.vue'
import request from '../utils/request'

const router = useRouter()
const loading = ref(false)
const adoptionList = ref([])
const currentPetType = ref('')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const loadAdoptions = async () => {
  loading.value = true
  try {
    const res = await request.get('/adoption/list', {
      params: {
        petType: currentPetType.value || undefined,
        page: currentPage.value,
        pageSize: pageSize.value
      }
    })

    if (res.code === 200) {
      adoptionList.value = res.data.list || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '获取领养列表失败')
    }
  } catch (error) {
    ElMessage.error('服务器错误')
  } finally {
    loading.value = false
  }
}

const handlePetTypeChange = () => {
  currentPage.value = 1
  loadAdoptions()
}

const handlePageChange = () => {
  loadAdoptions()
}

const goToCreate = () => {
  router.push('/adoption/create')
}

const goToDetail = (id) => {
  router.push(`/adoption/${id}`)
}

const getAvatarUrl = (userId) => {
  return `${API_BASE}/profile/avatar/id/${userId}`
}

const getFirstImage = (imagesStr) => {
  if (!imagesStr) return ''
  const images = imagesStr.split(',')
  return `${API_BASE}/${images[0]}`
}

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'danger', 3: 'info', 4: 'info' }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = { 0: '待审核', 1: '可领养', 2: '已拒绝', 3: '已领养', 4: '已下架' }
  return texts[status] || '未知'
}

const truncateText = (text, maxLength) => {
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}

onMounted(() => {
  loadAdoptions()
})
</script>

<style scoped>
.adoption-center-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.adoption-center-main {
  flex: 1;
  padding: 32px;
  background: #f5f6fa;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 16px;
  background: #fff;
  border-radius: 16px;
}

.adoption-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 24px;
}

.adoption-card {
  cursor: pointer;
  transition: all 0.3s;
  overflow: hidden;
}

.adoption-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.pet-image-wrapper {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f0f0f0;
  border-radius: 8px;
  margin-bottom: 12px;
}

.pet-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 14px;
}

.status-tag {
  position: absolute;
  top: 12px;
  right: 12px;
}

.pet-info {
  margin-bottom: 12px;
}

.pet-name {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.pet-details {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.pet-breed {
  color: #666;
  font-size: 14px;
}

.pet-meta {
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
  color: #666;
  font-size: 13px;
}

.description {
  color: #888;
  font-size: 13px;
  line-height: 1.5;
}

.publisher-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.publisher-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.publisher-details {
  flex: 1;
}

.publisher-name {
  font-size: 13px;
  font-weight: 600;
  color: #333;
}

.publish-time {
  font-size: 11px;
  color: #999;
}

.application-count {
  font-size: 12px;
  color: #a27bff;
  font-weight: 600;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 24px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}
</style>
