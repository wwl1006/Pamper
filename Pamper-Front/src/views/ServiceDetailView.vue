<template>
  <div class="service-detail-container">
    <div v-loading="loading" class="content-wrapper">
      <!-- 服务基本信息卡片 -->
      <el-card class="service-info-card" shadow="hover">
        <div class="service-header">
          <div class="header-tags">
            <el-tag :type="serviceDetail.publish_type === 0 ? 'warning' : 'success'">
              {{ serviceDetail.publish_type === 0 ? '需求发布' : '服务提供' }}
            </el-tag>
            <el-tag>{{ serviceDetail.service_type }}</el-tag>
          </div>
          <div class="header-actions" v-if="isOwner">
            <el-button size="small" @click="goToEdit">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete">删除</el-button>
          </div>
        </div>

        <h2 class="service-title">{{ serviceDetail.title }}</h2>

        <div class="service-content">{{ serviceDetail.content }}</div>

        <div class="service-meta">
          <div class="meta-item" v-if="serviceDetail.pet_type">
            <span class="label">适用宠物:</span>
            <span class="value">{{ serviceDetail.pet_type }}</span>
          </div>
          <div class="meta-item" v-if="serviceDetail.location">
            <span class="label">地点:</span>
            <span class="value">{{ serviceDetail.location }}</span>
          </div>
          <div class="meta-item" v-if="serviceDetail.contact">
            <span class="label">联系方式:</span>
            <span class="value">{{ serviceDetail.contact }}</span>
          </div>
          <div class="meta-item" v-if="serviceDetail.price">
            <span class="label">价格:</span>
            <span class="value price">¥{{ serviceDetail.price }}</span>
          </div>
        </div>

        <div class="service-stats">
          <span><el-icon><View /></el-icon> 浏览 {{ serviceDetail.view_count }}</span>
          <span><el-icon><Document /></el-icon> 申请 {{ serviceDetail.application_count }}</span>
        </div>

        <div class="publisher-section">
          <div class="publisher-info">
            <img :src="`http://localhost:8080/profile/avatar/id/${serviceDetail.avatar}`" class="avatar" alt="发布者">
            <div>
              <div class="username">{{ serviceDetail.username }}</div>
              <div class="publish-time">发布于 {{ serviceDetail.create_time }}</div>
            </div>
          </div>
          <el-button v-if="!isOwner" type="primary" @click="showApplyDialog">申请服务</el-button>
          <el-button v-if="isOwner" @click="showApplications">查看申请列表</el-button>
        </div>
      </el-card>

      <!-- 申请列表（仅发布者可见） -->
      <el-card v-if="isOwner && applications.length > 0" class="applications-card" shadow="hover">
        <template #header>
          <h3>申请列表</h3>
        </template>

        <div v-for="app in applications" :key="app.id" class="application-item">
          <div class="app-header">
            <div class="applicant-info">
              <img :src="`http://localhost:8080/profile/avatar/id/${app.avatar}`" class="avatar" alt="申请者">
              <div>
                <div class="username">{{ app.username }}</div>
                <div class="apply-time">{{ app.create_time }}</div>
              </div>
            </div>
            <el-tag :type="getStatusType(app.status)">{{ getStatusText(app.status) }}</el-tag>
          </div>

          <div class="app-content">
            <div v-if="app.pet_name"><strong>宠物:</strong> {{ app.pet_name }}</div>
            <div v-if="app.contact"><strong>联系方式:</strong> {{ app.contact }}</div>
            <div v-if="app.message"><strong>留言:</strong> {{ app.message }}</div>
            <div v-if="app.reply" class="reply"><strong>回复:</strong> {{ app.reply }}</div>
          </div>

          <div class="app-actions" v-if="app.status === 0">
            <el-button size="small" type="success" @click="reviewApp(app.id, 1)">通过</el-button>
            <el-button size="small" type="danger" @click="reviewApp(app.id, 2)">拒绝</el-button>
          </div>

          <div class="app-actions" v-if="app.status === 1 && !hasAppointment(app.id)">
            <el-button size="small" type="primary" @click="showAppointmentDialog(app.id)">
              创建预约
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 申请对话框 -->
    <el-dialog v-model="applyDialogVisible" title="申请服务" width="600px" :close-on-click-modal="false">
      <el-form :model="applyForm" :rules="applyRules" ref="applyFormRef" label-width="90px">
        <el-form-item label="选择宠物">
          <el-select v-model="applyForm.pet_id" placeholder="请选择宠物" style="width: 100%">
            <el-option
              v-for="pet in myPets"
              :key="pet.id"
              :label="pet.pet_name"
              :value="pet.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="联系方式" prop="contact">
          <el-input v-model="applyForm.contact" placeholder="请输入联系方式" maxlength="100" />
        </el-form-item>

        <el-form-item label="申请留言" prop="message">
          <el-input
            v-model="applyForm.message"
            type="textarea"
            :rows="4"
            placeholder="请说明您的需求或情况"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApply" :loading="submitting">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- 创建预约对话框 -->
    <el-dialog v-model="appointmentDialogVisible" title="创建预约" width="600px" :close-on-click-modal="false">
      <el-form :model="appointmentForm" :rules="appointmentRules" ref="appointmentFormRef" label-width="90px">
        <el-form-item label="预约时间" prop="appointment_time">
          <el-date-picker
            v-model="appointmentForm.appointment_time"
            type="datetime"
            placeholder="请选择预约时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="预约地点" prop="location">
          <el-input v-model="appointmentForm.location" placeholder="请输入预约地点" maxlength="200" />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="appointmentForm.notes"
            type="textarea"
            :rows="3"
            placeholder="其他备注信息"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="appointmentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAppointment" :loading="submitting">创建预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { View, Document } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const submitting = ref(false)
const applyDialogVisible = ref(false)
const appointmentDialogVisible = ref(false)
const applyFormRef = ref(null)
const appointmentFormRef = ref(null)

const serviceDetail = ref({})
const applications = ref([])
const myPets = ref([])
const currentUserId = ref(null)

const applyForm = reactive({
  service_id: null,
  pet_id: null,
  contact: '',
  message: ''
})

const appointmentForm = reactive({
  application_id: null,
  appointment_time: '',
  location: '',
  notes: ''
})

const applyRules = {
  contact: [{ required: true, message: '请输入联系方式', trigger: 'blur' }],
  message: [{ required: true, message: '请输入申请留言', trigger: 'blur' }]
}

const appointmentRules = {
  appointment_time: [{ required: true, message: '请选择预约时间', trigger: 'change' }],
  location: [{ required: true, message: '请输入预约地点', trigger: 'blur' }]
}

const isOwner = computed(() => {
  return currentUserId.value && serviceDetail.value.user_id === currentUserId.value
})

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'danger', 3: 'info' }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = { 0: '待审核', 1: '已通过', 2: '已拒绝', 3: '已取消' }
  return texts[status] || '未知'
}

const hasAppointment = (applicationId) => {
  // 这里可以检查是否已创建预约
  return false
}

const loadServiceDetail = async () => {
  loading.value = true
  try {
    const response = await request.get(`/service/${route.params.id}`, {
      headers: {
        'Authorization': localStorage.getItem('token')
      }
    })
    if (response.code === 200) {
      serviceDetail.value = response.data
    } else {
      ElMessage.error(response.msg || '加载失败')
      router.back()
    }
  } catch (error) {
    console.error('加载服务详情失败:', error)
    ElMessage.error('加载失败')
    router.back()
  } finally {
    loading.value = false
  }
}

const loadApplications = async () => {
  try {
    const response = await request.get(`/service/applications/${route.params.id}`, {
      headers: {
        'Authorization': localStorage.getItem('token')
      }
    })
    if (response.code === 200) {
      applications.value = response.data
    }
  } catch (error) {
    console.error('加载申请列表失败:', error)
  }
}

const loadMyPets = async () => {
  try {
    const response = await request.get('/pet/profile/my', {
      headers: {
        'Authorization': localStorage.getItem('token')
      }
    })
    if (response.code === 200) {
      myPets.value = response.data
    }
  } catch (error) {
    console.error('加载宠物列表失败:', error)
  }
}

const showApplyDialog = () => {
  applyForm.service_id = route.params.id
  loadMyPets()
  applyDialogVisible.value = true
}

const showApplications = () => {
  loadApplications()
}

const submitApply = async () => {
  if (!applyFormRef.value) return

  await applyFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      const response = await request.post('/service/apply', applyForm, {
        headers: {
          'Authorization': localStorage.getItem('token')
        }
      })
      if (response.code === 200) {
        ElMessage.success('申请成功')
        applyDialogVisible.value = false
        loadServiceDetail()
      } else {
        ElMessage.error(response.msg || '申请失败')
      }
    } catch (error) {
      console.error('申请失败:', error)
      ElMessage.error('申请失败')
    } finally {
      submitting.value = false
    }
  })
}

const reviewApp = async (applicationId, status) => {
  try {
    await ElMessageBox.prompt('请输入回复内容（选填）', status === 1 ? '通过申请' : '拒绝申请', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputType: 'textarea'
    })

    const response = await request.put('/service/application/review', null, {
      params: {
        applicationId,
        status,
        reply: ''
      },
      headers: {
        'Authorization': localStorage.getItem('token')
      }
    })

    if (response.code === 200) {
      ElMessage.success('操作成功')
      loadApplications()
    } else {
      ElMessage.error(response.msg || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审核失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

const showAppointmentDialog = (applicationId) => {
  appointmentForm.application_id = applicationId
  appointmentDialogVisible.value = true
}

const submitAppointment = async () => {
  if (!appointmentFormRef.value) return

  await appointmentFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      const response = await request.post('/service/appointment/create', appointmentForm, {
        headers: {
          'Authorization': localStorage.getItem('token')
        }
      })
      if (response.code === 200) {
        ElMessage.success('创建预约成功')
        appointmentDialogVisible.value = false
      } else {
        ElMessage.error(response.msg || '创建失败')
      }
    } catch (error) {
      console.error('创建预约失败:', error)
      ElMessage.error('创建失败')
    } finally {
      submitting.value = false
    }
  })
}

const goToEdit = () => {
  router.push(`/service/edit/${route.params.id}`)
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这条服务信息吗？删除后将无法恢复！', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await request.delete(`/service/${route.params.id}`, {
      headers: {
        'Authorization': localStorage.getItem('token')
      }
    })

    if (response.code === 200) {
      ElMessage.success('删除成功')
      router.push('/services')
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  currentUserId.value = userInfo.id
  loadServiceDetail()
  if (isOwner.value) {
    loadApplications()
  }
})
</script>

<style scoped>
.service-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.service-info-card {
  border-radius: 16px;
}

.service-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.header-tags {
  display: flex;
  gap: 10px;
}

.service-title {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.service-content {
  font-size: 16px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 24px;
  white-space: pre-wrap;
}

.service-meta {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  gap: 8px;
}

.meta-item .label {
  color: #999;
  min-width: 80px;
}

.meta-item .value {
  color: #333;
  font-weight: 500;
}

.meta-item .price {
  color: #f56c6c;
  font-size: 18px;
}

.service-stats {
  display: flex;
  gap: 24px;
  font-size: 14px;
  color: #999;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 20px;
}

.service-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.publisher-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.publisher-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #f0f0f0;
}

.username {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.publish-time {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}

.applications-card {
  border-radius: 16px;
}

.application-item {
  padding: 16px;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  margin-bottom: 16px;
}

.application-item:last-child {
  margin-bottom: 0;
}

.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.applicant-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.apply-time {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.app-content {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 12px;
}

.app-content div {
  margin-bottom: 8px;
}

.reply {
  color: #6f4bb0;
  background: #f5f0ff;
  padding: 8px;
  border-radius: 6px;
  margin-top: 8px;
}

.app-actions {
  display: flex;
  gap: 8px;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #6f4bb0 0%, #4b2e83 100%);
  border: none;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #5a3d91 0%, #3d2569 100%);
}

@media (max-width: 768px) {
  .service-meta {
    grid-template-columns: 1fr;
  }
}
</style>
