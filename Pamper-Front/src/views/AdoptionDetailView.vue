<template>
  <div class="adoption-detail-layout">
    <UserHeader />
    <main class="adoption-detail-main">
      <div v-if="loading" class="loading-state">加载中...</div>
      <div v-else-if="!adoption" class="empty-state">领养信息不存在</div>
      <div v-else class="detail-container">
        <!-- 宠物详情卡片 -->
        <el-card class="adoption-detail-card">
          <!-- 图片展示 -->
          <div class="images-section">
            <el-carousel v-if="adoption.images" height="400px" indicator-position="outside">
              <el-carousel-item v-for="(img, index) in getImageUrls(adoption.images)" :key="index">
                <img :src="img" class="carousel-image" />
              </el-carousel-item>
            </el-carousel>
            <div v-else class="no-images">暂无图片</div>
          </div>

          <!-- 宠物信息 -->
          <div class="pet-info-section">
            <div class="header-row">
              <h2 class="pet-name">{{ adoption.pet_name }}</h2>
              <el-tag :type="getStatusType(adoption.status)" size="large">
                {{ getStatusText(adoption.status) }}
              </el-tag>
            </div>

            <div class="info-grid">
              <div class="info-item">
                <span class="label">宠物类型：</span>
                <span class="value">{{ adoption.pet_type }}</span>
              </div>
              <div class="info-item" v-if="adoption.pet_breed">
                <span class="label">品种：</span>
                <span class="value">{{ adoption.pet_breed }}</span>
              </div>
              <div class="info-item" v-if="adoption.pet_age">
                <span class="label">年龄：</span>
                <span class="value">{{ adoption.pet_age }}</span>
              </div>
              <div class="info-item" v-if="adoption.pet_gender">
                <span class="label">性别：</span>
                <span class="value">{{ adoption.pet_gender }}</span>
              </div>
              <div class="info-item" v-if="adoption.location">
                <span class="label">所在地区：</span>
                <span class="value">{{ adoption.location }}</span>
              </div>
              <div class="info-item" v-if="adoption.contact">
                <span class="label">联系方式：</span>
                <span class="value">{{ adoption.contact }}</span>
              </div>
            </div>

            <div class="description-section">
              <h3>详细描述</h3>
              <p class="description-text">{{ adoption.description }}</p>
            </div>

            <!-- 发布者信息 -->
            <div class="publisher-section">
              <div class="publisher-info">
                <img :src="getAvatarUrl(adoption.user_id)" class="publisher-avatar" />
                <div>
                  <div class="publisher-name">{{ adoption.username }}</div>
                  <div class="publish-time">发布于 {{ adoption.create_time }}</div>
                </div>
              </div>
              <el-button
                v-if="adoption.status === 1 && !isMyAdoption"
                type="primary"
                size="large"
                @click="showApplyDialog"
              >
                申请领养
              </el-button>
              <el-button
                v-if="isMyAdoption"
                type="warning"
                size="large"
                @click="viewApplications"
              >
                查看申请 ({{ adoption.application_count || 0 }})
              </el-button>
            </div>
          </div>
        </el-card>

        <!-- 申请列表（仅发布者可见） -->
        <el-card v-if="isMyAdoption && showApplicationsList" class="applications-card">
          <template #header>
            <div class="card-header">申请列表</div>
          </template>

          <div v-if="applicationsLoading" class="loading-state">加载中...</div>
          <div v-else-if="applications.length === 0" class="empty-state">暂无申请</div>
          <div v-else class="applications-list">
            <div v-for="app in applications" :key="app.id" class="application-item">
              <div class="applicant-info">
                <img :src="getAvatarUrl(app.applicant_id)" class="applicant-avatar" />
                <div class="applicant-details">
                  <div class="applicant-name">{{ app.applicant_username }}</div>
                  <div class="apply-time">{{ app.create_time }}</div>
                </div>
              </div>
              <div class="application-content">
                <div class="label">联系方式：</div>
                <div class="value">{{ app.contact }}</div>
                <div class="label">申请留言：</div>
                <div class="value">{{ app.message || '无' }}</div>
              </div>
              <div class="application-actions">
                <el-tag v-if="app.status === 1" type="success">已通过</el-tag>
                <el-tag v-else-if="app.status === 2" type="danger">已拒绝</el-tag>
                <template v-else>
                  <el-button type="success" size="small" @click="reviewApp(app.id, 1)">
                    通过
                  </el-button>
                  <el-button type="danger" size="small" @click="reviewApp(app.id, 2)">
                    拒绝
                  </el-button>
                </template>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </main>

    <!-- 申请对话框 -->
    <el-dialog v-model="applyDialogVisible" title="申请领养" width="500px">
      <el-form :model="applyForm" :rules="applyRules" ref="applyFormRef" label-width="100px">
        <el-form-item label="联系方式" prop="contact">
          <el-input v-model="applyForm.contact" placeholder="请输入您的联系方式" />
        </el-form-item>
        <el-form-item label="申请留言" prop="message">
          <el-input
            v-model="applyForm.message"
            type="textarea"
            :rows="4"
            placeholder="请简单介绍您的情况和领养意向..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="applying" @click="submitApplication">
          提交申请
        </el-button>
      </template>
    </el-dialog>

    <UserFooter />
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import UserHeader from '../components/UserHeader.vue'
import UserFooter from '../components/UserFooter.vue'
import request from '../utils/request'

const route = useRoute()
const loading = ref(false)
const applicationsLoading = ref(false)
const applying = ref(false)
const adoption = ref(null)
const applications = ref([])
const showApplicationsList = ref(false)
const applyDialogVisible = ref(false)
const applyFormRef = ref()

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const applyForm = reactive({
  contact: '',
  message: ''
})

const applyRules = {
  contact: [
    { required: true, message: '请输入联系方式', trigger: 'blur' }
  ]
}

const isMyAdoption = computed(() => {
  if (!adoption.value) return false
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return adoption.value.user_id === userInfo.id
})

const loadAdoption = async () => {
  loading.value = true
  try {
    const res = await request.get(`/adoption/${route.params.id}`)
    if (res.code === 200) {
      adoption.value = res.data
    } else {
      ElMessage.error(res.msg || '获取领养信息失败')
    }
  } catch (error) {
    ElMessage.error('服务器错误')
  } finally {
    loading.value = false
  }
}

const viewApplications = async () => {
  showApplicationsList.value = true
  applicationsLoading.value = true
  try {
    const res = await request.get(`/adoption/${route.params.id}/applications`)
    if (res.code === 200) {
      applications.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载申请列表失败')
  } finally {
    applicationsLoading.value = false
  }
}

const showApplyDialog = () => {
  applyDialogVisible.value = true
}

const submitApplication = async () => {
  try {
    await applyFormRef.value?.validate()
    applying.value = true

    const res = await request.post('/adoption/apply', {
      adoption_id: route.params.id,
      contact: applyForm.contact,
      message: applyForm.message
    })

    if (res.code === 200) {
      ElMessage.success('申请提交成功')
      applyDialogVisible.value = false
      applyForm.contact = ''
      applyForm.message = ''
    } else {
      ElMessage.error(res.msg || '申请失败')
    }
  } catch (error) {
    console.error(error)
  } finally {
    applying.value = false
  }
}

const reviewApp = async (appId, status) => {
  try {
    const res = await request.put(`/adoption/applications/${appId}/review`, null, {
      params: { status }
    })

    if (res.code === 200) {
      ElMessage.success('审核成功')
      viewApplications()
      loadAdoption()
    } else {
      ElMessage.error(res.msg || '审核失败')
    }
  } catch (error) {
    ElMessage.error('服务器错误')
  }
}

const getAvatarUrl = (userId) => {
  return `${API_BASE}/profile/avatar/id/${userId}`
}

const getImageUrls = (imagesStr) => {
  if (!imagesStr) return []
  return imagesStr.split(',').map(img => `${API_BASE}/${img}`)
}

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'danger', 3: 'info', 4: 'info' }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = { 0: '待审核', 1: '可领养', 2: '已拒绝', 3: '已领养', 4: '已下架' }
  return texts[status] || '未知'
}

onMounted(() => {
  loadAdoption()
})
</script>

<style scoped>
.adoption-detail-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.adoption-detail-main {
  flex: 1;
  padding: 32px;
  background: #f5f6fa;
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

.detail-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.adoption-detail-card,
.applications-card {
  border-radius: 16px;
}

.images-section {
  margin-bottom: 24px;
}

.carousel-image {
  width: 100%;
  height: 400px;
  object-fit: contain;
  background: #f0f0f0;
}

.no-images {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f0f0;
  color: #999;
  border-radius: 8px;
}

.pet-info-section {
  padding: 0 16px;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.pet-name {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: #333;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.info-item {
  padding: 12px;
  background: #f5f6fa;
  border-radius: 8px;
}

.info-item .label {
  color: #666;
  font-size: 14px;
}

.info-item .value {
  color: #333;
  font-size: 15px;
  font-weight: 500;
  margin-left: 8px;
}

.description-section {
  margin-bottom: 24px;
}

.description-section h3 {
  margin: 0 0 12px 0;
  font-size: 18px;
  color: #333;
}

.description-text {
  margin: 0;
  color: #666;
  line-height: 1.8;
  white-space: pre-wrap;
}

.publisher-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

.publisher-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.publisher-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.publisher-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.publish-time {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}

.card-header {
  font-weight: 600;
  font-size: 16px;
}

.applications-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.application-item {
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.applicant-info {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 150px;
}

.applicant-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.applicant-name {
  font-weight: 600;
  color: #333;
}

.apply-time {
  font-size: 12px;
  color: #999;
}

.application-content {
  flex: 1;
}

.application-content .label {
  color: #666;
  font-size: 13px;
  margin-top: 8px;
}

.application-content .value {
  color: #333;
  margin-bottom: 8px;
}

.application-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
</style>
