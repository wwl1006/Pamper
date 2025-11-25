<template>
  <div class="notice-layout">
    <UserHeader />
    <main class="notice-main">
      <section class="notice-sidebar">
        <div class="tab-group">
          <div
            class="tab-item"
            :class="{ active: activeTab === 'received' }"
            @click="switchTab('received')"
          >
            收到的通知
          </div>
          <div
            class="tab-item"
            :class="{ active: activeTab === 'announcements' }"
            @click="switchTab('announcements')"
          >
            系统公告
          </div>
          <div
            class="tab-item"
            :class="{ active: activeTab === 'sent' }"
            @click="switchTab('sent')"
          >
            发送的通知
          </div>
          <div
            class="tab-item"
            :class="{ active: activeTab === 'send' }"
            @click="switchTab('send')"
          >
            发送私信
          </div>
        </div>
      </section>

      <section class="notice-content">
        <div v-if="activeTab === 'received'" class="notice-list-wrapper">
          <h3>收到的通知</h3>
          <div v-if="loading" class="loading-state">加载中...</div>
          <div v-else-if="receivedNotices.length === 0" class="empty-state">
            暂无收到的通知
          </div>
          <div v-else class="notice-list">
            <div
              v-for="notice in receivedNotices"
              :key="notice.id"
              class="notice-item"
            >
              <div class="notice-header">
                <span class="notice-type">{{ getNoticeTypeLabel(notice.notice_type) }}</span>
                <span class="notice-time">{{ notice.create_time }}</span>
              </div>
              <div class="notice-message">{{ notice.message }}</div>
              <div class="notice-footer">
                <span class="notice-sender">来自用户ID: {{ notice.sender }}</span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'announcements'" class="notice-list-wrapper">
          <h3>系统公告</h3>
          <div v-if="loading" class="loading-state">加载中...</div>
          <div v-else-if="announcements.length === 0" class="empty-state">
            暂无系统公告
          </div>
          <div v-else class="notice-list">
            <div
              v-for="notice in announcements"
              :key="notice.id"
              class="notice-item announcement"
            >
              <div class="notice-header">
                <span class="notice-type">公告</span>
                <span class="notice-time">{{ notice.create_time }}</span>
              </div>
              <div class="notice-message">{{ notice.message }}</div>
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'sent'" class="notice-list-wrapper">
          <h3>发送的通知</h3>
          <div v-if="loading" class="loading-state">加载中...</div>
          <div v-else-if="sentNotices.length === 0" class="empty-state">
            暂无发送的通知
          </div>
          <div v-else class="notice-list">
            <div
              v-for="notice in sentNotices"
              :key="notice.id"
              class="notice-item"
            >
              <div class="notice-header">
                <span class="notice-type">{{ getNoticeTypeLabel(notice.notice_type) }}</span>
                <span class="notice-time">{{ notice.create_time }}</span>
              </div>
              <div class="notice-message">{{ notice.message }}</div>
              <div class="notice-footer" v-if="notice.accepter">
                <span class="notice-receiver">发送给用户ID: {{ notice.accepter }}</span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'send'" class="send-notice-wrapper">
          <h3>发送私信</h3>
          <el-form :model="sendForm" :rules="sendRules" ref="sendFormRef" label-width="120px" class="send-form">
            <el-form-item label="接收者用户名" prop="accepterUsername">
              <el-input
                v-model="sendForm.accepterUsername"
                placeholder="请输入接收者的用户名"
                clearable
              />
            </el-form-item>
            <el-form-item label="消息内容" prop="message">
              <el-input
                v-model="sendForm.message"
                type="textarea"
                :rows="6"
                placeholder="请输入消息内容"
                maxlength="255"
                show-word-limit
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="sending" @click="submitSendForm">
                发送
              </el-button>
              <el-button @click="resetSendForm">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </section>
    </main>
    <UserFooter />
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import UserHeader from '../components/UserHeader.vue'
import UserFooter from '../components/UserFooter.vue'
import request from '../utils/request'

const activeTab = ref('received')
const loading = ref(false)
const sending = ref(false)
const receivedNotices = ref([])
const announcements = ref([])
const sentNotices = ref([])
const sendFormRef = ref()

const sendForm = reactive({
  notice_type: 0,
  accepterUsername: '',
  message: ''
})

const sendRules = {
  accepterUsername: [
    { required: true, message: '请输入接收者用户名', trigger: 'blur' }
  ],
  message: [
    { required: true, message: '消息内容不能为空', trigger: 'blur' },
    { max: 255, message: '消息内容不能超过255字', trigger: 'blur' }
  ]
}

const getNoticeTypeLabel = (type) => {
  return type === 0 ? '私信' : '公告'
}

const switchTab = async (tab) => {
  activeTab.value = tab
  if (tab === 'received') {
    await loadReceivedNotices()
  } else if (tab === 'announcements') {
    await loadAnnouncements()
  } else if (tab === 'sent') {
    await loadSentNotices()
  }
}

const loadReceivedNotices = async () => {
  loading.value = true
  try {
    const res = await request.get('/notice/myNotices')
    if (res.code === 200) {
      receivedNotices.value = res.data || []
    } else {
      ElMessage.error(res.msg || '获取通知列表失败')
    }
  } catch (error) {
    ElMessage.error('服务器错误，暂时无法获取通知列表')
  } finally {
    loading.value = false
  }
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

const loadSentNotices = async () => {
  loading.value = true
  try {
    const res = await request.get('/notice/mySent')
    if (res.code === 200) {
      sentNotices.value = res.data || []
    } else {
      ElMessage.error(res.msg || '获取发送列表失败')
    }
  } catch (error) {
    ElMessage.error('服务器错误，暂时无法获取发送列表')
  } finally {
    loading.value = false
  }
}

const submitSendForm = async () => {
  try {
    await sendFormRef.value?.validate()
    sending.value = true

    const payload = {
      notice_type: 0,
      accepterUsername: sendForm.accepterUsername,
      message: sendForm.message
    }

    const res = await request.post('/notice/send', payload)
    if (res.code === 200) {
      ElMessage.success(res.msg || '发送成功')
      resetSendForm()
      await loadSentNotices()
      activeTab.value = 'sent'
    } else {
      ElMessage.error(res.msg || '发送失败')
    }
  } catch (error) {
    if (error) {
      console.warn(error)
    }
  } finally {
    sending.value = false
  }
}

const resetSendForm = () => {
  sendForm.accepterUsername = ''
  sendForm.message = ''
  sendFormRef.value?.clearValidate()
}

onMounted(() => {
  loadReceivedNotices()
})
</script>

<style scoped>
.notice-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.notice-main {
  flex: 1;
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 24px;
  padding: 32px;
  background: #f5f6fa;
}

.notice-sidebar {
  background: #fff;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  height: fit-content;
}

.tab-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.tab-item {
  padding: 12px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  color: #666;
  font-weight: 500;
}

.tab-item:hover {
  background: #f5f6fa;
  color: #a27bff;
}

.tab-item.active {
  background: linear-gradient(135deg, #6f4bb0, #4b2e83);
  color: white;
}

.notice-content {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.notice-list-wrapper h3,
.send-notice-wrapper h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 20px;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 16px;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.notice-item {
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  transition: all 0.3s;
}

.notice-item:hover {
  border-color: #a27bff;
  box-shadow: 0 2px 8px rgba(162, 123, 255, 0.1);
}

.notice-item.announcement {
  background: linear-gradient(135deg, #fff5f5, #fff);
  border-left: 4px solid #a27bff;
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.notice-type {
  display: inline-block;
  padding: 4px 12px;
  background: #a27bff;
  color: white;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.notice-time {
  color: #999;
  font-size: 13px;
}

.notice-message {
  color: #333;
  font-size: 15px;
  line-height: 1.6;
  margin-bottom: 8px;
  white-space: pre-wrap;
  word-break: break-word;
}

.notice-footer {
  color: #666;
  font-size: 13px;
  margin-top: 8px;
}

.notice-sender,
.notice-receiver {
  color: #888;
}

.send-form {
  max-width: 600px;
}
</style>
