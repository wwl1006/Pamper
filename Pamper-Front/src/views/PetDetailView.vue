<template>
  <div class="pet-detail-container">
    <div v-loading="loading" class="content-wrapper">
      <!-- 宠物基本信息卡片 -->
      <el-card class="pet-info-card" shadow="hover">
        <div class="pet-header">
          <div class="pet-avatar-large">
            <el-image
              :src="petDetail.avatar ? `http://localhost:8080/${petDetail.avatar}` : defaultAvatar"
              fit="cover"
              style="width: 100%; height: 100%"
            />
          </div>
          <div class="pet-basic-info">
            <h2>{{ petDetail.pet_name }}</h2>
            <div class="info-tags">
              <el-tag size="large">{{ petDetail.pet_type }}</el-tag>
              <el-tag size="large" type="info" v-if="petDetail.breed">{{ petDetail.breed }}</el-tag>
              <el-tag size="large" :type="petDetail.gender === '公' ? 'primary' : 'danger'">
                {{ petDetail.gender }}
              </el-tag>
            </div>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">生日</span>
                <span class="value">{{ petDetail.birthday || '未设置' }}</span>
              </div>
              <div class="info-item">
                <span class="label">毛色</span>
                <span class="value">{{ petDetail.color || '未设置' }}</span>
              </div>
              <div class="info-item">
                <span class="label">体重</span>
                <span class="value">{{ petDetail.weight ? `${petDetail.weight} kg` : '未设置' }}</span>
              </div>
              <div class="info-item">
                <span class="label">健康记录</span>
                <span class="value highlight">{{ petDetail.health_record_count || 0 }} 条</span>
              </div>
            </div>
            <div class="description" v-if="petDetail.description">
              <span class="label">简介</span>
              <p>{{ petDetail.description }}</p>
            </div>
          </div>
        </div>
        <div class="action-buttons">
          <el-button type="primary" @click="goToEdit">编辑档案</el-button>
          <el-button @click="goBack">返回列表</el-button>
        </div>
      </el-card>

      <!-- 健康记录部分 -->
      <el-card class="health-records-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <h3>健康记录</h3>
            <el-button type="primary" size="small" @click="showAddRecordDialog">
              <el-icon><Plus /></el-icon>
              添加记录
            </el-button>
          </div>
        </template>

        <el-empty v-if="healthRecords.length === 0" description="暂无健康记录">
          <el-button type="primary" @click="showAddRecordDialog">添加第一条记录</el-button>
        </el-empty>

        <el-timeline v-else>
          <el-timeline-item
            v-for="record in healthRecords"
            :key="record.id"
            :timestamp="record.record_date"
            placement="top"
          >
            <el-card class="record-card">
              <div class="record-header">
                <div class="record-title">
                  <el-tag :type="getRecordTypeColor(record.record_type)" size="small">
                    {{ record.record_type }}
                  </el-tag>
                  <span class="title-text">{{ record.title }}</span>
                </div>
                <div class="record-actions">
                  <el-button type="primary" size="small" link @click="editRecord(record)">编辑</el-button>
                  <el-button type="danger" size="small" link @click="deleteRecord(record)">删除</el-button>
                </div>
              </div>
              <div class="record-content">{{ record.content }}</div>
              <div class="record-footer" v-if="record.doctor || record.hospital">
                <span v-if="record.doctor">医生: {{ record.doctor }}</span>
                <span v-if="record.hospital">医院: {{ record.hospital }}</span>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </div>

    <!-- 添加/编辑健康记录对话框 -->
    <el-dialog
      v-model="recordDialogVisible"
      :title="isEditRecord ? '编辑健康记录' : '添加健康记录'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="recordForm" :rules="recordRules" ref="recordFormRef" label-width="90px">
        <el-form-item label="记录类型" prop="record_type">
          <el-select v-model="recordForm.record_type" placeholder="请选择记录类型" style="width: 100%">
            <el-option label="疫苗" value="疫苗" />
            <el-option label="驱虫" value="驱虫" />
            <el-option label="体检" value="体检" />
            <el-option label="诊疗" value="诊疗" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item label="记录标题" prop="title">
          <el-input v-model="recordForm.title" placeholder="请输入记录标题" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="记录日期" prop="record_date">
          <el-date-picker
            v-model="recordForm.record_date"
            type="date"
            placeholder="请选择记录日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="记录内容" prop="content">
          <el-input
            v-model="recordForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入详细内容"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="医生">
          <el-input v-model="recordForm.doctor" placeholder="请输入医生姓名" maxlength="50" />
        </el-form-item>

        <el-form-item label="医院">
          <el-input v-model="recordForm.hospital" placeholder="请输入医院名称" maxlength="100" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="recordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRecord" :loading="submitting">
          {{ isEditRecord ? '保存' : '添加' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const submitting = ref(false)
const recordDialogVisible = ref(false)
const isEditRecord = ref(false)
const recordFormRef = ref(null)

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const petDetail = ref({
  id: null,
  pet_name: '',
  pet_type: '',
  breed: '',
  gender: '',
  birthday: '',
  color: '',
  weight: null,
  description: '',
  avatar: '',
  health_record_count: 0
})

const healthRecords = ref([])

const recordForm = reactive({
  id: null,
  pet_id: null,
  record_type: '',
  title: '',
  content: '',
  record_date: '',
  doctor: '',
  hospital: ''
})

const recordRules = {
  record_type: [{ required: true, message: '请选择记录类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入记录标题', trigger: 'blur' }],
  record_date: [{ required: true, message: '请选择记录日期', trigger: 'change' }],
  content: [{ required: true, message: '请输入记录内容', trigger: 'blur' }]
}

const getRecordTypeColor = (type) => {
  const colorMap = {
    '疫苗': 'success',
    '驱虫': 'warning',
    '体检': 'info',
    '诊疗': 'danger',
    '其他': ''
  }
  return colorMap[type] || ''
}

const loadPetDetail = async () => {
  loading.value = true
  try {
    const response = await request.get(`/pet/profile/${route.params.id}`)
    if (response.code === 200) {
      petDetail.value = response.data
      loadHealthRecords()
    } else {
      ElMessage.error(response.msg || '加载失败')
      goBack()
    }
  } catch (error) {
    console.error('加载宠物信息失败:', error)
    ElMessage.error('加载失败')
    goBack()
  } finally {
    loading.value = false
  }
}

const loadHealthRecords = async () => {
  try {
    const response = await request.get(`/pet/health/${route.params.id}`)
    if (response.code === 200) {
      healthRecords.value = response.data
    }
  } catch (error) {
    console.error('加载健康记录失败:', error)
  }
}

const showAddRecordDialog = () => {
  isEditRecord.value = false
  Object.assign(recordForm, {
    id: null,
    pet_id: route.params.id,
    record_type: '',
    title: '',
    content: '',
    record_date: '',
    doctor: '',
    hospital: ''
  })
  recordDialogVisible.value = true
}

const editRecord = (record) => {
  isEditRecord.value = true
  Object.assign(recordForm, {
    id: record.id,
    pet_id: record.pet_id,
    record_type: record.record_type,
    title: record.title,
    content: record.content,
    record_date: record.record_date,
    doctor: record.doctor || '',
    hospital: record.hospital || ''
  })
  recordDialogVisible.value = true
}

const submitRecord = async () => {
  if (!recordFormRef.value) return

  await recordFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      let response
      if (isEditRecord.value) {
        response = await request.put('/pet/health/update', recordForm)
      } else {
        response = await request.post('/pet/health/add', recordForm)
      }

      if (response.code === 200) {
        ElMessage.success(isEditRecord.value ? '保存成功' : '添加成功')
        recordDialogVisible.value = false
        loadHealthRecords()
        loadPetDetail()
      } else {
        ElMessage.error(response.msg || '操作失败')
      }
    } catch (error) {
      console.error('提交失败:', error)
      ElMessage.error('操作失败')
    } finally {
      submitting.value = false
    }
  })
}

const deleteRecord = async (record) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条健康记录吗？删除后将无法恢复！',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await request.delete(`/pet/health/${record.id}`)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadHealthRecords()
      loadPetDetail()
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

const goToEdit = () => {
  router.push(`/pet/edit/${route.params.id}`)
}

const goBack = () => {
  router.push('/mypets')
}

onMounted(() => {
  loadPetDetail()
})
</script>

<style scoped>
.pet-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.pet-info-card {
  border-radius: 16px;
}

.pet-header {
  display: flex;
  gap: 30px;
  margin-bottom: 24px;
}

.pet-avatar-large {
  width: 200px;
  height: 200px;
  border-radius: 16px;
  overflow: hidden;
  flex-shrink: 0;
  background: #f5f5f5;
}

.pet-basic-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.pet-basic-info h2 {
  font-size: 32px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.info-tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item .label {
  font-size: 13px;
  color: #999;
}

.info-item .value {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.info-item .value.highlight {
  color: #6f4bb0;
}

.description {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.description .label {
  font-size: 13px;
  color: #999;
}

.description p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.action-buttons {
  display: flex;
  gap: 12px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

.health-records-card {
  border-radius: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.record-card {
  margin-top: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.record-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-text {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.record-actions {
  display: flex;
  gap: 8px;
}

.record-content {
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
}

.record-footer {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #999;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #6f4bb0 0%, #4b2e83 100%);
  border: none;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #5a3d91 0%, #3d2569 100%);
}

:deep(.el-timeline-item__timestamp) {
  color: #6f4bb0;
  font-weight: 500;
}

:deep(.el-tag--large) {
  border-radius: 8px;
  padding: 8px 16px;
}

@media (max-width: 768px) {
  .pet-header {
    flex-direction: column;
    align-items: center;
  }

  .pet-avatar-large {
    width: 150px;
    height: 150px;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
