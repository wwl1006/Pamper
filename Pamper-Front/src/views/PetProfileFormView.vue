<template>
  <div class="pet-form-container">
    <div class="form-card">
      <h2>{{ isEdit ? '编辑宠物档案' : '添加宠物档案' }}</h2>

      <el-form :model="petForm" :rules="rules" ref="formRef" label-width="100px" v-loading="loading">
        <el-form-item label="宠物头像">
          <div class="avatar-upload-section">
            <el-upload
              class="avatar-uploader"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
              accept="image/*"
            >
              <el-image
                v-if="petForm.avatar"
                :src="`http://localhost:8080/${petForm.avatar}`"
                fit="cover"
                class="avatar-preview"
              />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div class="upload-tip">点击上传宠物头像，支持 jpg/png 格式，大小不超过 5MB</div>
          </div>
        </el-form-item>

        <el-form-item label="宠物名称" prop="pet_name">
          <el-input v-model="petForm.pet_name" placeholder="请输入宠物名称" maxlength="50" show-word-limit />
        </el-form-item>

        <el-form-item label="宠物类型" prop="pet_type">
          <el-select v-model="petForm.pet_type" placeholder="请选择宠物类型" style="width: 100%">
            <el-option label="猫" value="猫" />
            <el-option label="狗" value="狗" />
            <el-option label="兔子" value="兔子" />
            <el-option label="仓鼠" value="仓鼠" />
            <el-option label="鸟类" value="鸟类" />
            <el-option label="爬行动物" value="爬行动物" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item label="品种">
          <el-input v-model="petForm.breed" placeholder="请输入宠物品种" maxlength="50" show-word-limit />
        </el-form-item>

        <el-form-item label="性别">
          <el-radio-group v-model="petForm.gender">
            <el-radio value="公">公</el-radio>
            <el-radio value="母">母</el-radio>
            <el-radio value="未知">未知</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="生日">
          <el-date-picker
            v-model="petForm.birthday"
            type="date"
            placeholder="请选择宠物生日"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="毛色">
          <el-input v-model="petForm.color" placeholder="请输入宠物毛色" maxlength="50" show-word-limit />
        </el-form-item>

        <el-form-item label="体重(kg)">
          <el-input-number
            v-model="petForm.weight"
            :min="0"
            :max="200"
            :precision="2"
            :step="0.1"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="描述">
          <el-input
            v-model="petForm.description"
            type="textarea"
            :rows="4"
            placeholder="介绍一下你的宠物吧~"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <div class="form-actions">
            <el-button @click="goBack">取消</el-button>
            <el-button type="primary" @click="handleSubmit">{{ isEdit ? '保存' : '添加' }}</el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const loading = ref(false)

const isEdit = ref(false)
const petId = ref(null)

const uploadUrl = 'http://localhost:8080/pet/upload/avatar'
const uploadHeaders = {
  Authorization: localStorage.getItem('token')
}

const petForm = reactive({
  id: null,
  pet_name: '',
  pet_type: '',
  breed: '',
  gender: '未知',
  birthday: '',
  color: '',
  weight: null,
  description: '',
  avatar: ''
})

const rules = {
  pet_name: [
    { required: true, message: '请输入宠物名称', trigger: 'blur' },
    { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  pet_type: [
    { required: true, message: '请选择宠物类型', trigger: 'change' }
  ]
}

const handleAvatarSuccess = (response) => {
  if (response.code === 200) {
    petForm.avatar = response.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

const loadPetDetail = async () => {
  loading.value = true
  try {
    const response = await request.get(`/pet/profile/${petId.value}`)
    if (response.code === 200) {
      Object.assign(petForm, response.data)
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

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      let response
      if (isEdit.value) {
        response = await request.put('/pet/profile/update', petForm, {
          headers: {
            'Authorization': localStorage.getItem('token')
          }
        })
      } else {
        response = await request.post('/pet/profile/create', petForm, {
          headers: {
            'Authorization': localStorage.getItem('token')
          }
        })
      }

      if (response.code === 200) {
        ElMessage.success(isEdit.value ? '保存成功' : '添加成功')
        router.push('/mypets')
      } else {
        ElMessage.error(response.msg || '操作失败')
      }
    } catch (error) {
      console.error('提交失败:', error)
      ElMessage.error('操作失败')
    } finally {
      loading.value = false
    }
  })
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  if (route.params.id) {
    isEdit.value = true
    petId.value = route.params.id
    loadPetDetail()
  }
})
</script>

<style scoped>
.pet-form-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 30px 20px;
}

.form-card {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.form-card h2 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0 0 30px 0;
  text-align: center;
}

.avatar-upload-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.avatar-uploader {
  width: 150px;
  height: 150px;
}

:deep(.avatar-uploader .el-upload) {
  width: 150px;
  height: 150px;
  border: 2px dashed #d9d9d9;
  border-radius: 12px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

:deep(.avatar-uploader .el-upload:hover) {
  border-color: #6f4bb0;
}

.avatar-preview {
  width: 150px;
  height: 150px;
  display: block;
}

.avatar-uploader-icon {
  font-size: 40px;
  color: #8c939d;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  line-height: 1.5;
}

.form-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  width: 100%;
  margin-top: 20px;
}

.form-actions .el-button {
  min-width: 120px;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #6f4bb0 0%, #4b2e83 100%);
  border: none;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #5a3d91 0%, #3d2569 100%);
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #333;
}

:deep(.el-input__inner),
:deep(.el-textarea__inner) {
  border-radius: 8px;
}

:deep(.el-select) {
  width: 100%;
}
</style>
