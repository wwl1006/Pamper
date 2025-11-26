<template>
  <div class="activity-detail-page">
    <UserHeader />
    <div class="activity-detail-container">
      <div v-loading="loading" class="content-wrapper">
        <el-card class="activity-info-card" shadow="hover">
          <div class="activity-header">
            <div class="header-tags">
              <el-tag :type="getStatusColor(activityDetail.status)">{{ getStatusText(activityDetail.status) }}</el-tag>
              <el-tag>{{ activityDetail.activity_type }}</el-tag>
              <el-tag v-if="activityDetail.is_registered" type="success">已报名</el-tag>
              <el-tag v-if="activityDetail.is_full" type="danger">已满</el-tag>
            </div>
            <div class="header-actions" v-if="isCreator">
              <el-button size="small" @click="goToEdit">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete">删除</el-button>
            </div>
          </div>

          <h2 class="activity-title">{{ activityDetail.title }}</h2>

          <div class="cover-image" v-if="activityDetail.cover_image">
            <el-image :src="getCoverUrl(activityDetail.cover_image)" fit="cover" style="width: 100%; height: 100%" />
          </div>

          <div class="activity-content" v-html="activityDetail.content"></div>

        <div class="activity-meta">
          <div class="meta-item">
            <span class="label">活动时间:</span>
            <span class="value">{{ activityDetail.start_time }} 至 {{ activityDetail.end_time }}</span>
          </div>
          <div class="meta-item" v-if="activityDetail.location">
            <span class="label">活动地点:</span>
            <span class="value">{{ activityDetail.location }}</span>
          </div>
          <div class="meta-item" v-if="activityDetail.organizer">
            <span class="label">主办方:</span>
            <span class="value">{{ activityDetail.organizer }}</span>
          </div>
          <div class="meta-item" v-if="activityDetail.contact">
            <span class="label">联系方式:</span>
            <span class="value">{{ activityDetail.contact }}</span>
          </div>
          <div class="meta-item">
            <span class="label">报名人数:</span>
            <span class="value">{{ activityDetail.current_participants }}/{{ activityDetail.max_participants || '不限' }}</span>
          </div>
          <div class="meta-item" v-if="activityDetail.registration_deadline">
            <span class="label">报名截止:</span>
            <span class="value">{{ activityDetail.registration_deadline }}</span>
          </div>
        </div>

        <div class="action-section">
          <el-button
            v-if="!isCreator && !activityDetail.is_registered && !activityDetail.is_full"
            type="primary"
            size="large"
            @click="showRegisterDialog"
          >
            立即报名
          </el-button>
          <el-tag v-else-if="activityDetail.is_registered" type="success" size="large">已报名成功</el-tag>
          <el-tag v-else-if="activityDetail.is_full" type="danger" size="large">活动已满</el-tag>
          <el-button v-if="isCreator" @click="showRegistrations">查看报名列表</el-button>
        </div>
      </el-card>

      <!-- 报名列表（仅创建者可见） -->
      <el-card v-if="isCreator && registrations.length > 0" class="registrations-card" shadow="hover">
        <template #header>
          <h3>报名列表 ({{ registrations.length }}人)</h3>
        </template>

        <el-table :data="registrations" style="width: 100%">
          <el-table-column prop="username" label="用户" width="120" />
          <el-table-column prop="real_name" label="真实姓名" width="120" />
          <el-table-column prop="phone" label="联系电话" width="140" />
          <el-table-column prop="email" label="邮箱" width="180" />
          <el-table-column prop="message" label="留言" show-overflow-tooltip />
          <el-table-column prop="create_time" label="报名时间" width="160" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
                {{ scope.row.status === 1 ? '已报名' : '已取消' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
    <UserFooter />
  </div>

  <!-- 报名对话框 -->
    <el-dialog v-model="registerDialogVisible" title="活动报名" width="600px" :close-on-click-modal="false">
      <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" label-width="90px">
        <el-form-item label="真实姓名" prop="real_name">
          <el-input v-model="registerForm.real_name" placeholder="请输入真实姓名" maxlength="50" />
        </el-form-item>

        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入联系电话" maxlength="20" />
        </el-form-item>

        <el-form-item label="邮箱">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱（选填）" maxlength="100" />
        </el-form-item>

        <el-form-item label="报名留言">
          <el-input
            v-model="registerForm.message"
            type="textarea"
            :rows="4"
            placeholder="请说明参加活动的原因或特殊需求"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="registerDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRegister" :loading="submitting">提交报名</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import UserHeader from '../components/UserHeader.vue'
import UserFooter from '../components/UserFooter.vue'
import request from '../utils/request'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const submitting = ref(false)
const registerDialogVisible = ref(false)
const registerFormRef = ref(null)
const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const activityDetail = ref({})
const registrations = ref([])
const currentUserId = ref(null)

const getCoverUrl = (coverPath) => {
  if (!coverPath) return ''
  if (coverPath.startsWith('http')) return coverPath
  return `${API_BASE}/${coverPath}`
}

const registerForm = reactive({
  activity_id: null,
  real_name: '',
  phone: '',
  email: '',
  message: ''
})

const registerRules = {
  real_name: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

const isCreator = computed(() => {
  return currentUserId.value && activityDetail.value.create_by === currentUserId.value
})

const getStatusColor = (status) => {
  const colors = { 0: 'warning', 1: 'success', 2: 'danger', 3: 'info', 4: 'primary', 5: 'info' }
  return colors[status] || ''
}

const getStatusText = (status) => {
  const texts = { 0: '待审核', 1: '已发布', 2: '已拒绝', 3: '已取消', 4: '进行中', 5: '已结束' }
  return texts[status] || '未知'
}

const loadActivityDetail = async () => {
  loading.value = true
  try {
    const response = await request.get(`/activity/${route.params.id}`, {
      headers: {
        'Authorization': localStorage.getItem('token')
      }
    })
    if (response.code === 200) {
      activityDetail.value = response.data
    } else {
      ElMessage.error(response.msg || '加载失败')
      router.back()
    }
  } catch (error) {
    console.error('加载活动详情失败:', error)
    ElMessage.error('加载失败')
    router.back()
  } finally {
    loading.value = false
  }
}

const loadRegistrations = async () => {
  try {
    const response = await request.get(`/activity/registrations/${route.params.id}`, {
      headers: {
        'Authorization': localStorage.getItem('token')
      }
    })
    if (response.code === 200) {
      registrations.value = response.data
    }
  } catch (error) {
    console.error('加载报名列表失败:', error)
  }
}

const showRegisterDialog = () => {
  registerForm.activity_id = route.params.id
  registerDialogVisible.value = true
}

const showRegistrations = () => {
  loadRegistrations()
}

const submitRegister = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      const response = await request.post('/activity/register', registerForm, {
        headers: {
          'Authorization': localStorage.getItem('token')
        }
      })
      if (response.code === 200) {
        ElMessage.success('报名成功')
        registerDialogVisible.value = false
        loadActivityDetail()
      } else {
        ElMessage.error(response.msg || '报名失败')
      }
    } catch (error) {
      console.error('报名失败:', error)
      ElMessage.error('报名失败')
    } finally {
      submitting.value = false
    }
  })
}

const goToEdit = () => {
  router.push(`/activity/edit/${route.params.id}`)
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这个活动吗？删除后将无法恢复！', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await request.delete(`/activity/${route.params.id}`, {
      headers: {
        'Authorization': localStorage.getItem('token')
      }
    })

    if (response.code === 200) {
      ElMessage.success('删除成功')
      router.push('/activities')
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
  loadActivityDetail()
  if (isCreator.value) {
    loadRegistrations()
  }
})
</script>

<style scoped>
.activity-detail-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f6fa;
}

.activity-detail-container {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 30px auto;
  padding: 0 20px;
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.activity-info-card {
  border-radius: 16px;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.header-tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.activity-title {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin: 0 0 20px 0;
}

.cover-image {
  width: 100%;
  height: 400px;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 24px;
  background: #f5f5f5;
}

.activity-content {
  font-size: 16px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 24px;
  white-space: pre-wrap;
}

.activity-meta {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  margin-bottom: 24px;
}

.meta-item {
  display: flex;
  gap: 8px;
}

.meta-item .label {
  color: #999;
  min-width: 90px;
  flex-shrink: 0;
}

.meta-item .value {
  color: #333;
  font-weight: 500;
}

.action-section {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

.registrations-card {
  border-radius: 16px;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #6f4bb0 0%, #4b2e83 100%);
  border: none;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #5a3d91 0%, #3d2569 100%);
}

@media (max-width: 768px) {
  .activity-meta {
    grid-template-columns: 1fr;
  }

  .cover-image {
    height: 250px;
  }
}
</style>
