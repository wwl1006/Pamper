<template>
  <div class="service-square-container">
    <div class="header-section">
      <h2>宠物医疗与服务</h2>
      <el-button type="primary" @click="goToCreate">
        <el-icon><Plus /></el-icon>
        发布服务/需求
      </el-button>
    </div>

    <div class="filter-section">
      <el-radio-group v-model="publishType" @change="loadServices">
        <el-radio-button :value="null">全部</el-radio-button>
        <el-radio-button :value="0">需求发布</el-radio-button>
        <el-radio-button :value="1">服务提供</el-radio-button>
      </el-radio-group>

      <el-radio-group v-model="serviceType" @change="loadServices" class="type-filter">
        <el-radio-button :value="''">全部类型</el-radio-button>
        <el-radio-button value="医疗">医疗</el-radio-button>
        <el-radio-button value="美容">美容</el-radio-button>
        <el-radio-button value="寄养">寄养</el-radio-button>
        <el-radio-button value="训练">训练</el-radio-button>
        <el-radio-button value="其他">其他</el-radio-button>
      </el-radio-group>
    </div>

    <div class="services-grid" v-loading="loading">
      <el-empty v-if="!loading && serviceList.length === 0" description="暂无服务信息" />

      <div v-for="service in serviceList" :key="service.id" class="service-card" @click="goToDetail(service.id)">
        <div class="card-header">
          <el-tag :type="service.publish_type === 0 ? 'warning' : 'success'" size="small">
            {{ service.publish_type === 0 ? '需求' : '服务' }}
          </el-tag>
          <el-tag size="small">{{ service.service_type }}</el-tag>
        </div>

        <h3 class="service-title">{{ service.title }}</h3>

        <div class="service-content">{{ service.content }}</div>

        <div class="service-info">
          <div class="info-item" v-if="service.pet_type">
            <el-icon><PriceTag /></el-icon>
            <span>{{ service.pet_type }}</span>
          </div>
          <div class="info-item" v-if="service.location">
            <el-icon><Location /></el-icon>
            <span>{{ service.location }}</span>
          </div>
          <div class="info-item" v-if="service.price">
            <el-icon><Coin /></el-icon>
            <span>¥{{ service.price }}</span>
          </div>
        </div>

        <div class="service-footer">
          <div class="publisher-info">
            <img :src="`${API_BASE}/profile/avatar/id/${service.avatar}`" class="avatar" alt="发布者头像">
            <span>{{ service.username }}</span>
          </div>
          <div class="stats">
            <span><el-icon><View /></el-icon> {{ service.view_count }}</span>
            <span><el-icon><Document /></el-icon> {{ service.application_count }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="pagination-section" v-if="total > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadServices"
        @current-change="loadServices"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, PriceTag, Location, Coin, View, Document } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()
const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const loading = ref(false)
const serviceList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const publishType = ref(null)
const serviceType = ref('')

const loadServices = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    }

    if (publishType.value !== null) {
      params.publishType = publishType.value
    }

    if (serviceType.value) {
      params.serviceType = serviceType.value
    }

    const response = await request.get('/service/list', { params })
    if (response.code === 200) {
      serviceList.value = response.data.list
      total.value = response.data.total
    } else {
      ElMessage.error(response.msg || '加载失败')
    }
  } catch (error) {
    console.error('加载服务列表失败:', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const goToCreate = () => {
  router.push('/service/create')
}

const goToDetail = (serviceId) => {
  router.push(`/service/${serviceId}`)
}

onMounted(() => {
  loadServices()
})
</script>

<style scoped>
.service-square-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-section h2 {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.filter-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.type-filter {
  flex-wrap: wrap;
}

.services-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  min-height: 300px;
  margin-bottom: 24px;
}

.service-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.service-card:hover {
  box-shadow: 0 4px 20px rgba(111, 75, 176, 0.15);
  transform: translateY(-4px);
}

.card-header {
  display: flex;
  gap: 8px;
}

.service-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.service-content {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.service-info {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #666;
}

.service-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.publisher-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #f0f0f0;
}

.stats {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #999;
}

.stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-section {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #6f4bb0 0%, #4b2e83 100%);
  border: none;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #5a3d91 0%, #3d2569 100%);
}

:deep(.el-radio-button__original-radio:checked+.el-radio-button__inner) {
  background-color: #6f4bb0;
  border-color: #6f4bb0;
}
</style>
