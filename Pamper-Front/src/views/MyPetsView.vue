<template>
  <div class="my-pets-container">
    <div class="header-section">
      <h2>我的宠物档案</h2>
      <el-button type="primary" @click="goToAddPet">
        <el-icon><Plus /></el-icon>
        添加宠物档案
      </el-button>
    </div>

    <div class="pets-grid" v-loading="loading">
      <el-empty v-if="!loading && petList.length === 0" description="还没有添加宠物档案哦">
        <el-button type="primary" @click="goToAddPet">立即添加</el-button>
      </el-empty>

      <div v-for="pet in petList" :key="pet.id" class="pet-card">
        <div class="pet-avatar" @click="goToPetDetail(pet.id)">
          <el-image
            :src="getAvatarUrl(pet.avatar)"
            fit="cover"
            style="width: 100%; height: 100%"
          />
        </div>
        <div class="pet-info">
          <h3 class="pet-name">{{ pet.pet_name }}</h3>
          <div class="pet-detail-item">
            <el-tag size="small">{{ pet.pet_type }}</el-tag>
            <el-tag size="small" type="info" v-if="pet.breed">{{ pet.breed }}</el-tag>
          </div>
          <div class="pet-detail-item">
            <span class="label">性别:</span>
            <span>{{ pet.gender || '未知' }}</span>
          </div>
          <div class="pet-detail-item">
            <span class="label">生日:</span>
            <span>{{ pet.birthday || '未设置' }}</span>
          </div>
          <div class="pet-detail-item">
            <span class="label">健康记录:</span>
            <span class="health-count">{{ pet.health_record_count || 0 }} 条</span>
          </div>
        </div>
        <div class="pet-actions">
          <el-button size="small" @click="goToPetDetail(pet.id)">查看详情</el-button>
          <el-button size="small" @click="goToEditPet(pet.id)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(pet)">删除</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()
const loading = ref(false)
const petList = ref([])
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://47.97.44.200:8080'

const getAvatarUrl = (avatar) => {
  return avatar ? `${API_BASE}/${avatar}` : defaultAvatar
}

const loadMyPets = async () => {
  loading.value = true
  try {
    const response = await request.get('/pet/profile/my')
    if (response.code === 200) {
      petList.value = response.data
    } else {
      ElMessage.error(response.msg || '加载失败')
    }
  } catch (error) {
    console.error('加载宠物列表失败:', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const goToAddPet = () => {
  router.push('/pet/add')
}

const goToPetDetail = (petId) => {
  router.push(`/pet/${petId}`)
}

const goToEditPet = (petId) => {
  router.push(`/pet/edit/${petId}`)
}

const handleDelete = async (pet) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除宠物"${pet.pet_name}"的档案吗？删除后将无法恢复！`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await request.delete(`/pet/profile/${pet.id}`)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadMyPets()
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
  loadMyPets()
})
</script>

<style scoped>
.my-pets-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header-section h2 {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.pets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  min-height: 300px;
}

.pet-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.pet-card:hover {
  box-shadow: 0 4px 20px rgba(111, 75, 176, 0.15);
  transform: translateY(-4px);
}

.pet-avatar {
  width: 100%;
  height: 200px;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  background: #f5f5f5;
}

.pet-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.pet-name {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.pet-detail-item {
  font-size: 14px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 8px;
}

.pet-detail-item .label {
  color: #999;
  min-width: 60px;
}

.health-count {
  color: #6f4bb0;
  font-weight: 500;
}

.pet-actions {
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.pet-actions .el-button {
  flex: 1;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #6f4bb0 0%, #4b2e83 100%);
  border: none;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #5a3d91 0%, #3d2569 100%);
}

:deep(.el-tag) {
  border-radius: 8px;
}
</style>
