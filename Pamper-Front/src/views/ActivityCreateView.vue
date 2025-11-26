<template>
  <div class="create-activity-container">
    <UserHeader />
    <div class="create-activity-content">
      <h2>{{ isEditMode ? '编辑活动' : '发布活动' }}</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px" class="activity-form">
        <el-form-item label="活动标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入活动标题" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="活动类型" prop="activity_type">
          <el-select v-model="form.activity_type" placeholder="请选择活动类型" style="width: 100%">
            <el-option label="线上活动" value="线上"></el-option>
            <el-option label="线下活动" value="线下"></el-option>
            <el-option label="混合活动" value="混合"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="开始时间" prop="start_time">
          <el-date-picker
            v-model="form.start_time"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="结束时间" prop="end_time">
          <el-date-picker
            v-model="form.end_time"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="活动地点" prop="location" v-if="form.activity_type !== '线上'">
          <el-input v-model="form.location" placeholder="请输入活动地点" />
        </el-form-item>

        <el-form-item label="最大参与人数" prop="max_participants">
          <el-input-number
            v-model="form.max_participants"
            :min="1"
            :max="9999"
            placeholder="请输入最大参与人数"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="主办方">
          <el-input v-model="form.organizer" placeholder="请输入主办方名称（选填）" maxlength="100" />
        </el-form-item>

        <el-form-item label="联系方式">
          <el-input v-model="form.contact" placeholder="请输入联系电话或邮箱（选填）" maxlength="100" />
        </el-form-item>

        <el-form-item label="报名截止时间">
          <el-date-picker
            v-model="form.registration_deadline"
            type="datetime"
            placeholder="选择报名截止时间（选填）"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="活动封面" prop="cover_image">
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :before-upload="beforeCoverUpload"
            :http-request="uploadCover"
          >
            <img v-if="coverPreviewUrl" :src="coverPreviewUrl" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon">
              <Plus />
            </el-icon>
          </el-upload>
          <div class="upload-hint">点击上传活动封面，建议尺寸 800x400</div>
        </el-form-item>

        <el-form-item label="活动内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="6"
            placeholder="请输入活动详细内容"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitting">
            {{ submitting ? (isEditMode ? '保存中...' : '发布中...') : (isEditMode ? '保存修改' : '发布活动') }}
          </el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
    <UserFooter />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import UserHeader from '../components/UserHeader.vue'
import UserFooter from '../components/UserFooter.vue'
import request from '../utils/request'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const submitting = ref(false)
const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const isEditMode = computed(() => !!route.params.id)

const form = reactive({
  id: null,
  title: '',
  activity_type: '',
  start_time: '',
  end_time: '',
  location: '',
  max_participants: 50,
  cover_image: '',
  content: '',
  organizer: '',
  contact: '',
  registration_deadline: ''
})

const coverPreviewUrl = computed(() => {
  if (!form.cover_image) return ''
  if (form.cover_image.startsWith('http')) return form.cover_image
  return `${API_BASE}/${form.cover_image}`
})

const rules = {
  title: [
    { required: true, message: '请输入活动标题', trigger: 'blur' },
    { min: 1, max: 100, message: '标题长度在1到100个字符之间', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入活动内容', trigger: 'blur' },
    { min: 1, max: 1000, message: '内容长度在1到1000个字符之间', trigger: 'blur' }
  ],
  activity_type: [
    { required: true, message: '请选择活动类型', trigger: 'change' }
  ],
  start_time: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  end_time: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请输入活动地点', trigger: 'blur' }
  ]
}

// 验证上传文件
const beforeCoverUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('上传头像图片只能是图片格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
    return false
  }
  return isImage && isLt2M
}

// 自定义上传方法
const uploadCover = async (options) => {
  const formData = new FormData()
  formData.append('file', options.file)

  try {
    const response = await request.post('/activity/upload/cover', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': localStorage.getItem('token')
      }
    })
    if (response.code === 200) {
      form.cover_image = response.data
      ElMessage.success('封面上传成功')
    } else {
      ElMessage.error(response.msg || '封面上传失败')
    }
  } catch (error) {
    ElMessage.error('上传失败')
    console.error('Upload error:', error)
  }
}

const submitForm = async () => {
  try {
    await formRef.value.validate()

    // 验证时间
    if (new Date(form.start_time) >= new Date(form.end_time)) {
      ElMessage.error('结束时间必须晚于开始时间')
      return
    }

    // 验证线下活动必须有地点
    if (form.activity_type !== '线上' && !form.location.trim()) {
      ElMessage.error('线下活动和混合活动必须填写地点')
      return
    }

    submitting.value = true

    const activityData = { ...form }
    if (activityData.activity_type === '线上') {
      activityData.location = null
    }

    let response
    if (isEditMode.value) {
      // 编辑模式：调用更新API
      response = await request.put('/activity/update', activityData, {
        headers: {
          'Authorization': localStorage.getItem('token')
        }
      })
    } else {
      // 创建模式：调用创建API
      response = await request.post('/activity/create', activityData, {
        headers: {
          'Authorization': localStorage.getItem('token')
        }
      })
    }

    if (response.code === 200) {
      ElMessage.success(response.msg || (isEditMode.value ? '保存成功' : '活动发布成功'))
      if (isEditMode.value) {
        router.push(`/activity/${route.params.id}`)
      } else {
        router.push('/activities')
      }
    } else {
      ElMessage.error(response.msg || (isEditMode.value ? '保存失败' : '发布失败'))
    }
  } catch (error) {
    if (error) {
      console.error('提交错误:', error)
    }
  } finally {
    submitting.value = false
  }
}

const loadActivityData = async () => {
  try {
    const response = await request.get(`/activity/${route.params.id}`, {
      headers: {
        'Authorization': localStorage.getItem('token')
      }
    })
    if (response.code === 200) {
      const data = response.data
      form.id = data.id
      form.title = data.title
      form.activity_type = data.activity_type
      form.start_time = data.start_time
      form.end_time = data.end_time
      form.location = data.location || ''
      form.max_participants = data.max_participants
      form.cover_image = data.cover_image || ''
      form.content = data.content || ''
      form.organizer = data.organizer || ''
      form.contact = data.contact || ''
      form.registration_deadline = data.registration_deadline || ''
    } else {
      ElMessage.error('加载活动数据失败')
      router.back()
    }
  } catch (error) {
    console.error('加载活动数据失败:', error)
    ElMessage.error('加载失败')
    router.back()
  }
}

const handleCancel = () => {
  if (isEditMode.value) {
    router.push(`/activity/${route.params.id}`)
  } else {
    router.push('/activities')
  }
}

onMounted(() => {
  if (isEditMode.value) {
    loadActivityData()
  }
})

</script>

<style scoped>
.create-activity-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.create-activity-content {
  flex: 1;
  max-width: 800px;
  margin: 30px auto;
  padding: 30px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.create-activity-content h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 24px;
}

.activity-form {
  max-width: 600px;
  margin: 0 auto;
}

.avatar-uploader {
  width: 200px;
  height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 200px;
  height: 100px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar {
  width: 200px;
  height: 100px;
  display: block;
}

.upload-hint {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}
</style>