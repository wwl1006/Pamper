<template>
  <div class="admin-notice-container">
    <h2 class="page-title">通知管理</h2>

    <!-- 功能卡片 -->
    <div class="cards-wrapper">
      <!-- 发送公告卡片 -->
      <el-card class="notice-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>发送系统公告</span>
          </div>
        </template>

        <el-form :model="announcementForm" :rules="announcementRules" ref="announcementFormRef" label-width="100px">
          <el-form-item label="公告内容" prop="message">
            <el-input
              v-model="announcementForm.message"
              type="textarea"
              :rows="6"
              placeholder="请输入公告内容，全体用户可见"
              maxlength="255"
              show-word-limit
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="sending" @click="sendAnnouncement">
              发送公告
            </el-button>
            <el-button @click="resetAnnouncementForm">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 公告历史卡片 -->
      <el-card class="notice-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>公告历史</span>
            <el-button type="primary" size="small" @click="loadAnnouncements">刷新</el-button>
          </div>
        </template>

        <div v-if="loading" class="loading-state">加载中...</div>
        <div v-else-if="announcements.length === 0" class="empty-state">
          暂无公告记录
        </div>
        <div v-else class="announcement-list">
          <div
            v-for="notice in announcements"
            :key="notice.id"
            class="announcement-item"
          >
            <div class="announcement-header">
              <el-tag type="warning" size="small">公告</el-tag>
              <span class="announcement-time">{{ notice.create_time }}</span>
            </div>
            <div class="announcement-content">{{ notice.message }}</div>
            <div class="announcement-footer">
              <span class="sender-info">发送者ID: {{ notice.sender }}</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import request from '../utils/request'

const loading = ref(false)
const sending = ref(false)
const announcements = ref([])
const announcementFormRef = ref()

const announcementForm = reactive({
  message: ''
})

const announcementRules = {
  message: [
    { required: true, message: '公告内容不能为空', trigger: 'blur' },
    { max: 255, message: '公告内容不能超过255字', trigger: 'blur' }
  ]
}

const sendAnnouncement = async () => {
  try {
    await announcementFormRef.value?.validate()
    sending.value = true

    const payload = {
      notice_type: 1,
      message: announcementForm.message
    }

    const res = await request.post('/notice/send', payload)
    if (res.code === 200) {
      ElMessage.success(res.msg || '公告发送成功')
      resetAnnouncementForm()
      await loadAnnouncements()
    } else {
      ElMessage.error(res.msg || '公告发送失败')
    }
  } catch (error) {
    if (error) console.warn(error)
  } finally {
    sending.value = false
  }
}

const resetAnnouncementForm = () => {
  announcementForm.message = ''
  announcementFormRef.value?.clearValidate()
}

const loadAnnouncements = async () => {
  loading.value = true
  try {
    const res = await request.get('/notice/announcements')
    if (res.code === 200) {
      announcements.value = res.data || []
    } else {
      ElMessage.error(res.msg || '获取公告列表失败')
    }
  } catch (error) {
    ElMessage.error('服务器错误，暂时无法获取公告列表')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadAnnouncements()
})
</script>

<style scoped>
.admin-notice-container {
  padding: 20px;
}

.page-title {
  margin: 0 0 24px 0;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.cards-wrapper {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.notice-card {
  background: #fff;
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #999;
  font-size: 14px;
}

.announcement-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 400px;
  overflow-y: auto;
}

.announcement-item {
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  transition: all 0.3s;
}

.announcement-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.announcement-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.announcement-time {
  color: #999;
  font-size: 12px;
}

.announcement-content {
  color: #333;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 8px;
  white-space: pre-wrap;
  word-break: break-word;
}

.announcement-footer {
  color: #888;
  font-size: 12px;
}
</style>
