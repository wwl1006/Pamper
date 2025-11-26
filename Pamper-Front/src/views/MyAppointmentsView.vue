<template>
  <div class="appointments-container">
    <h2>我的预约</h2>

    <div class="appointments-list" v-loading="loading">
      <el-empty v-if="!loading && appointments.length === 0" description="暂无预约记录" />

      <el-card v-for="appointment in appointments" :key="appointment.id" class="appointment-card" shadow="hover">
        <div class="card-header">
          <div class="service-info">
            <el-tag :type="getStatusColor(appointment.status)">{{ getStatusText(appointment.status) }}</el-tag>
            <el-tag>{{ appointment.service_type }}</el-tag>
          </div>
          <span class="appointment-time">{{ appointment.appointment_time }}</span>
        </div>

        <h3 class="service-title">{{ appointment.service_title }}</h3>

        <div class="appointment-details">
          <div class="detail-item">
            <el-icon><Location /></el-icon>
            <span>{{ appointment.location }}</span>
          </div>
          <div class="detail-item" v-if="appointment.notes">
            <el-icon><Document /></el-icon>
            <span>{{ appointment.notes }}</span>
          </div>
        </div>

        <div class="card-footer">
          <span class="create-time">创建于 {{ appointment.create_time }}</span>
          <div class="actions">
            <el-button
              v-if="appointment.status === 0"
              size="small"
              type="success"
              @click="updateStatus(appointment.id, 1)"
            >
              确认预约
            </el-button>
            <el-button
              v-if="appointment.status === 1"
              size="small"
              type="primary"
              @click="updateStatus(appointment.id, 2)"
            >
              完成服务
            </el-button>
            <el-button
              v-if="appointment.status < 2"
              size="small"
              type="danger"
              @click="updateStatus(appointment.id, 3)"
            >
              取消预约
            </el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Location, Document } from '@element-plus/icons-vue'
import request from '../utils/request'

const loading = ref(false)
const appointments = ref([])

const getStatusColor = (status) => {
  const colors = {
    0: 'warning',
    1: 'success',
    2: 'info',
    3: 'danger'
  }
  return colors[status] || ''
}

const getStatusText = (status) => {
  const texts = {
    0: '待确认',
    1: '已确认',
    2: '已完成',
    3: '已取消'
  }
  return texts[status] || '未知'
}

const loadAppointments = async () => {
  loading.value = true
  try {
    const response = await request.get('/service/appointments/my', {
      headers: {
        'Authorization': localStorage.getItem('token')
      }
    })
    if (response.code === 200) {
      appointments.value = response.data
    } else {
      ElMessage.error(response.msg || '加载失败')
    }
  } catch (error) {
    console.error('加载预约列表失败:', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const updateStatus = async (appointmentId, status) => {
  const messages = {
    1: '确认这个预约吗？',
    2: '确认服务已完成吗？',
    3: '确定要取消这个预约吗？'
  }

  try {
    await ElMessageBox.confirm(messages[status], '确认操作', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: status === 3 ? 'warning' : 'info'
    })

    const response = await request.put('/service/appointment/status', null, {
      params: {
        appointmentId,
        status
      },
      headers: {
        'Authorization': localStorage.getItem('token')
      }
    })

    if (response.code === 200) {
      ElMessage.success('操作成功')
      loadAppointments()
    } else {
      ElMessage.error(response.msg || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新状态失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  loadAppointments()
})
</script>

<style scoped>
.appointments-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 30px 20px;
}

.appointments-container h2 {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin: 0 0 24px 0;
}

.appointments-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 300px;
}

.appointment-card {
  border-radius: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.service-info {
  display: flex;
  gap: 8px;
}

.appointment-time {
  font-size: 16px;
  font-weight: 600;
  color: #6f4bb0;
}

.service-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.appointment-details {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
  margin-bottom: 16px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.create-time {
  font-size: 13px;
  color: #999;
}

.actions {
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
  .card-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .actions {
    width: 100%;
  }

  .actions .el-button {
    flex: 1;
  }
}
</style>
