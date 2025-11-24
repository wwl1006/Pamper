<template>
  <div class="profile-layout">
    <UserHeader />
    <main class="profile-main">
      <section class="profile-summary">
        <img :src="avatarSrc" alt="用户头像" class="summary-avatar" />
        <h2>{{ form.username || '宠友' }}</h2>
        <p class="role">{{ roleLabel }}</p>
        <p class="meta">注册时间：{{ form.create_time || '-' }}</p>
        <el-upload
          class="avatar-upload"
          :show-file-list="false"
          :auto-upload="false"
          accept=".png,.jpg,.jpeg,.gif,.webp"
          :before-upload="beforeSelect"
          :on-change="handleFileChange"
        >
          <el-button type="primary" plain :loading="uploading">
            {{ uploading ? '上传中...' : '更换头像' }}
          </el-button>
        </el-upload>
        <el-button text type="primary" :disabled="!avatarFile" @click="submitAvatar">
          提交头像
        </el-button>
      </section>

      <section class="profile-form">
        <h3>个人信息</h3>
        <el-form :model="form" :rules="rules" ref="profileForm" label-width="90px" class="info-form">
          <el-form-item label="用户名">
            <el-input v-model="form.username" disabled />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" placeholder="请输入邮箱" clearable />
          </el-form-item>
          <el-form-item label="个人简介" prop="description">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="4"
              placeholder="简单介绍一下自己吧~"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="saving" @click="saveProfile">保存修改</el-button>
            <el-button @click="loadProfile">恢复</el-button>
          </el-form-item>
        </el-form>
      </section>
    </main>
    <UserFooter />
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import UserHeader from '../components/UserHeader.vue'
import UserFooter from '../components/UserFooter.vue'
import request from '../utils/request'
import DefaultAvatar from '../assets/Img/dog.webp'

const profileForm = ref()
const form = reactive({
  id: '',
  username: '',
  email: '',
  description: '',
  create_time: '',
  user_type: 1
})
const rules = {
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  description: [
    { max: 200, message: '个人简介不能超过200字', trigger: ['blur', 'change'] }
  ]
}
const saving = ref(false)
const uploading = ref(false)
const avatarFile = ref(null)
const avatarPreview = ref('')
const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const avatarSrc = computed(() => {
  if (avatarPreview.value) {
    return avatarPreview.value
  }
  if (form.id) {
    return `${API_BASE}/profile/avatar/id/${form.id}`
  }
  return DefaultAvatar
})

const roleLabel = computed(() => {
  if (form.user_type === 0) return '管理员'
  if (form.user_type === 2) return '商户用户'
  return '普通用户'
})

const syncLocalProfile = (payload) => {
  localStorage.setItem('userInfo', JSON.stringify(payload))
  window.dispatchEvent(new CustomEvent('pamper-profile-updated', { detail: payload }))
}

const assignProfile = (data = {}) => {
  form.id = data.id
  form.username = data.username
  form.email = data.email
  form.description = data.description
  form.create_time = data.create_time
  form.user_type = data.user_type
  avatarPreview.value = ''
  avatarFile.value = null
}

const loadProfile = async () => {
  try {
    const res = await request.get('/profile/me')
    if (res.code === 200) {
      assignProfile(res.data || {})
      syncLocalProfile(res.data || {})
    } else {
      ElMessage.error(res.msg || '查询资料失败')
    }
  } catch (error) {
    ElMessage.error('服务器错误，暂时无法获取资料')
  }
}

const saveProfile = async () => {
  saving.value = true
  try {
    await profileForm.value?.validate()
    const payload = {
      email: form.email,
      description: form.description
    }
    const res = await request.put('/profile/me', payload)
    if (res.code === 200) {
      assignProfile(res.data || {})
      syncLocalProfile(res.data || {})
      ElMessage.success(res.msg || '资料更新成功')
    } else {
      ElMessage.error(res.msg || '资料更新失败')
    }
  } catch (error) {
    if (error) {
      console.warn(error)
    }
  } finally {
    saving.value = false
  }
}

const beforeSelect = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
  }
  return isImage && isLt2M
}

const handleFileChange = (uploadFile) => {
  avatarFile.value = uploadFile.raw
  if (uploadFile.raw) {
    avatarPreview.value = URL.createObjectURL(uploadFile.raw)
  }
}

const submitAvatar = async () => {
  if (!avatarFile.value) {
    ElMessage.warning('请先选择图片')
    return
  }
  const formData = new FormData()
  formData.append('file', avatarFile.value)
  uploading.value = true
  try {
    const res = await request.post('/profile/uploadAvatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    if (res.code === 200) {
      ElMessage.success(res.msg || '头像更新成功')
      avatarPreview.value = ''
      await loadProfile()
    } else {
      ElMessage.error(res.msg || '头像更新失败')
    }
  } catch (error) {
    ElMessage.error('服务器错误，上传失败')
  } finally {
    uploading.value = false
  }
}

const handleProfileEvent = (event) => {
  if (event?.detail) {
    assignProfile(event.detail)
  }
}

onMounted(() => {
  loadProfile()
  window.addEventListener('pamper-profile-updated', handleProfileEvent)
})

onBeforeUnmount(() => {
  window.removeEventListener('pamper-profile-updated', handleProfileEvent)
})
</script>

<style scoped>
.profile-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.profile-main {
  flex: 1;
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 24px;
  padding: 32px 48px;
  background: #f5f6fa;
}

.profile-summary,
.profile-form {
  background: #fff;
  border-radius: 18px;
  padding: 24px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.05);
}

.profile-summary {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.summary-avatar {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #f5f6fa;
}

.avatar-upload {
  margin-top: 12px;
}

.role {
  color: #a27bff;
  font-weight: 600;
}

.meta {
  color: #888;
}

.profile-form h3 {
  margin-bottom: 16px;
}

.info-form {
  max-width: 560px;
}
</style>

