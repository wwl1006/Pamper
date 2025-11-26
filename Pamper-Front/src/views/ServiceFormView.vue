<template>
  <div class="service-form-container">
    <div class="form-card">
      <h2>{{ isEdit ? '编辑服务' : '发布服务/需求' }}</h2>

      <el-form :model="serviceForm" :rules="rules" ref="formRef" label-width="100px" v-loading="loading">
        <el-form-item label="发布类型" prop="publish_type">
          <el-radio-group v-model="serviceForm.publish_type">
            <el-radio :value="0">需求发布</el-radio>
            <el-radio :value="1">服务提供</el-radio>
          </el-radio-group>
          <div class="form-tip">需求发布：寻找服务提供者；服务提供：提供相关服务</div>
        </el-form-item>

        <el-form-item label="服务类型" prop="service_type">
          <el-select v-model="serviceForm.service_type" placeholder="请选择服务类型" style="width: 100%">
            <el-option label="医疗" value="医疗" />
            <el-option label="美容" value="美容" />
            <el-option label="寄养" value="寄养" />
            <el-option label="训练" value="训练" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item label="标题" prop="title">
          <el-input v-model="serviceForm.title" placeholder="请输入标题" maxlength="200" show-word-limit />
        </el-form-item>

        <el-form-item label="详细描述" prop="content">
          <el-input
            v-model="serviceForm.content"
            type="textarea"
            :rows="6"
            placeholder="请详细描述您的需求或服务内容"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="适用宠物">
          <el-input v-model="serviceForm.pet_type" placeholder="例如：猫、狗、小型犬等" maxlength="50" />
        </el-form-item>

        <el-form-item label="地点">
          <el-input v-model="serviceForm.location" placeholder="请输入地点或地址" maxlength="200" />
        </el-form-item>

        <el-form-item label="联系方式" prop="contact">
          <el-input v-model="serviceForm.contact" placeholder="请输入联系方式" maxlength="100" />
        </el-form-item>

        <el-form-item label="价格">
          <el-input-number
            v-model="serviceForm.price"
            :min="0"
            :max="999999"
            :precision="2"
            :step="1"
            style="width: 100%"
            placeholder="请输入价格"
          />
          <div class="form-tip">可选填，留空表示面议</div>
        </el-form-item>

        <el-form-item>
          <div class="form-actions">
            <el-button @click="goBack">取消</el-button>
            <el-button type="primary" @click="handleSubmit">{{ isEdit ? '保存' : '发布' }}</el-button>
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
import request from '../utils/request'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const loading = ref(false)

const isEdit = ref(false)
const serviceId = ref(null)

const serviceForm = reactive({
  id: null,
  publish_type: 0,
  service_type: '',
  title: '',
  content: '',
  pet_type: '',
  location: '',
  contact: '',
  price: null
})

const rules = {
  publish_type: [{ required: true, message: '请选择发布类型', trigger: 'change' }],
  service_type: [{ required: true, message: '请选择服务类型', trigger: 'change' }],
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 1, max: 200, message: '长度在 1 到 200 个字符', trigger: 'blur' }
  ],
  content: [{ required: true, message: '请输入详细描述', trigger: 'blur' }],
  contact: [{ required: true, message: '请输入联系方式', trigger: 'blur' }]
}

const loadServiceDetail = async () => {
  loading.value = true
  try {
    const response = await request.get(`/service/${serviceId.value}`, {
      headers: {
        'Authorization': localStorage.getItem('token')
      }
    })
    if (response.code === 200) {
      Object.assign(serviceForm, response.data)
    } else {
      ElMessage.error(response.msg || '加载失败')
      goBack()
    }
  } catch (error) {
    console.error('加载服务信息失败:', error)
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
        response = await request.put('/service/update', serviceForm, {
          headers: {
            'Authorization': localStorage.getItem('token')
          }
        })
      } else {
        response = await request.post('/service/create', serviceForm, {
          headers: {
            'Authorization': localStorage.getItem('token')
          }
        })
      }

      if (response.code === 200) {
        ElMessage.success(isEdit.value ? '保存成功' : '发布成功')
        router.push('/services')
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
    serviceId.value = route.params.id
    loadServiceDetail()
  }
})
</script>

<style scoped>
.service-form-container {
  max-width: 900px;
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

.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
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
</style>
