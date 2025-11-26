<template>
  <div class="adoption-create-layout">
    <UserHeader />
    <main class="adoption-create-main">
      <el-card class="create-card">
        <template #header>
          <div class="card-header">
            <span>发布领养信息</span>
          </div>
        </template>

        <el-form :model="adoptionForm" :rules="adoptionRules" ref="adoptionFormRef" label-width="100px">
          <el-form-item label="宠物名称" prop="pet_name">
            <el-input
              v-model="adoptionForm.pet_name"
              placeholder="请输入宠物名称"
              clearable
              maxlength="50"
            />
          </el-form-item>

          <el-form-item label="宠物类型" prop="pet_type">
            <el-select v-model="adoptionForm.pet_type" placeholder="请选择宠物类型">
              <el-option label="猫" value="猫" />
              <el-option label="狗" value="狗" />
              <el-option label="兔子" value="兔子" />
              <el-option label="仓鼠" value="仓鼠" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>

          <el-form-item label="宠物品种" prop="pet_breed">
            <el-input
              v-model="adoptionForm.pet_breed"
              placeholder="请输入宠物品种（选填）"
              clearable
              maxlength="50"
            />
          </el-form-item>

          <el-form-item label="年龄" prop="pet_age">
            <el-input
              v-model="adoptionForm.pet_age"
              placeholder="例如：2岁、3个月"
              clearable
              maxlength="20"
            />
          </el-form-item>

          <el-form-item label="性别" prop="pet_gender">
            <el-radio-group v-model="adoptionForm.pet_gender">
              <el-radio value="公">公</el-radio>
              <el-radio value="母">母</el-radio>
              <el-radio value="未知">未知</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="所在地区" prop="location">
            <el-input
              v-model="adoptionForm.location"
              placeholder="例如：北京市朝阳区"
              clearable
              maxlength="100"
            />
          </el-form-item>

          <el-form-item label="联系方式" prop="contact">
            <el-input
              v-model="adoptionForm.contact"
              placeholder="请输入联系方式（手机号/微信等）"
              clearable
              maxlength="100"
            />
          </el-form-item>

          <el-form-item label="详细描述" prop="description">
            <el-input
              v-model="adoptionForm.description"
              type="textarea"
              :rows="6"
              placeholder="请描述宠物的性格、习惯、健康状况等..."
              maxlength="1000"
              show-word-limit
            />
          </el-form-item>

          <!-- 图片上传 -->
          <el-form-item label="宠物照片">
            <el-upload
              action="#"
              list-type="picture-card"
              :auto-upload="false"
              :on-change="handleImageChange"
              :on-remove="handleImageRemove"
              :file-list="imageList"
              accept="image/*"
              :limit="6"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
            <div class="upload-tip">最多上传6张图片，每张不超过10MB</div>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="publishing" @click="submitAdoption">
              发布
            </el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </main>
    <UserFooter />
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import UserHeader from '../components/UserHeader.vue'
import UserFooter from '../components/UserFooter.vue'
import request from '../utils/request'

const router = useRouter()
const adoptionFormRef = ref()
const publishing = ref(false)
const imageList = ref([])

const adoptionForm = reactive({
  pet_name: '',
  pet_type: '',
  pet_breed: '',
  pet_age: '',
  pet_gender: '',
  location: '',
  contact: '',
  description: ''
})

const adoptionRules = {
  pet_name: [
    { required: true, message: '宠物名称不能为空', trigger: 'blur' }
  ],
  pet_type: [
    { required: true, message: '请选择宠物类型', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请填写详细描述', trigger: 'blur' }
  ]
}

const handleImageChange = (file, fileList) => {
  imageList.value = fileList
}

const handleImageRemove = (file, fileList) => {
  imageList.value = fileList
}

const submitAdoption = async () => {
  try {
    await adoptionFormRef.value?.validate()
    publishing.value = true

    let imageUrls = []

    // 上传图片
    if (imageList.value.length > 0) {
      const formData = new FormData()
      imageList.value.forEach(item => {
        formData.append('files', item.raw)
      })

      const imgRes = await request.post('/adoption/upload/images', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })

      if (imgRes.code === 200) {
        imageUrls = imgRes.data
      } else {
        ElMessage.error('图片上传失败：' + imgRes.msg)
        return
      }
    }

    // 创建领养信息
    const adoptionData = {
      pet_name: adoptionForm.pet_name,
      pet_type: adoptionForm.pet_type,
      pet_breed: adoptionForm.pet_breed,
      pet_age: adoptionForm.pet_age,
      pet_gender: adoptionForm.pet_gender,
      location: adoptionForm.location,
      contact: adoptionForm.contact,
      description: adoptionForm.description,
      images: imageUrls.join(',')
    }

    const res = await request.post('/adoption/create', adoptionData)
    if (res.code === 200) {
      ElMessage.success('发布成功')
      router.push('/adoption')
    } else {
      ElMessage.error(res.msg || '发布失败')
    }
  } catch (error) {
    console.error(error)
  } finally {
    publishing.value = false
  }
}

const resetForm = () => {
  adoptionFormRef.value?.resetFields()
  imageList.value = []
}
</script>

<style scoped>
.adoption-create-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.adoption-create-main {
  flex: 1;
  padding: 32px;
  background: #f5f6fa;
}

.create-card {
  max-width: 800px;
  margin: 0 auto;
  border-radius: 16px;
}

.card-header {
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.upload-tip {
  margin-top: 8px;
  color: #999;
  font-size: 12px;
}
</style>
