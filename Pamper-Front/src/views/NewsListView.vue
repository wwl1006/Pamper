<template>
  <div class="news-list-container">
    <div class="header-section">
      <h2>宠物资讯</h2>
      <el-button v-if="isAdmin" type="primary" @click="goToCreate">
        <el-icon><Plus /></el-icon>
        发布资讯
      </el-button>
    </div>

    <div class="filter-section">
      <el-radio-group v-model="category" @change="loadNews">
        <el-radio-button value="">全部分类</el-radio-button>
        <el-radio-button value="养护知识">养护知识</el-radio-button>
        <el-radio-button value="医疗健康">医疗健康</el-radio-button>
        <el-radio-button value="训练技巧">训练技巧</el-radio-button>
        <el-radio-button value="行业动态">行业动态</el-radio-button>
        <el-radio-button value="其他">其他</el-radio-button>
      </el-radio-group>
    </div>

    <div class="news-list" v-loading="loading">
      <el-empty v-if="!loading && newsList.length === 0" description="暂无资讯" />

      <div v-for="news in newsList" :key="news.id" class="news-card" @click="goToDetail(news.id)">
        <div class="news-cover" v-if="news.cover_image">
          <el-image :src="news.cover_image" fit="cover" style="width: 100%; height: 100%" />
        </div>
        <div class="news-content">
          <div class="news-header">
            <el-tag v-if="news.is_top === 1" type="danger" size="small">置顶</el-tag>
            <el-tag size="small">{{ news.category }}</el-tag>
          </div>

          <h3 class="news-title">{{ news.title }}</h3>

          <p class="news-summary" v-if="news.summary">{{ news.summary }}</p>

          <div class="news-footer">
            <div class="news-meta">
              <span v-if="news.author">作者: {{ news.author }}</span>
              <span v-if="news.source">来源: {{ news.source }}</span>
              <span>{{ news.publish_time || news.create_time }}</span>
            </div>
            <div class="news-stats">
              <span><el-icon><View /></el-icon> {{ news.view_count }}</span>
              <span><el-icon><StarFilled /></el-icon> {{ news.like_count }}</span>
            </div>
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
        @size-change="loadNews"
        @current-change="loadNews"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, View, StarFilled } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()
const loading = ref(false)
const newsList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const category = ref('')

const userInfo = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('userInfo') || '{}')
  } catch {
    return {}
  }
})

const isAdmin = computed(() => userInfo.value.user_type === 0)

const loadNews = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    }

    if (category.value) {
      params.category = category.value
    }

    const response = await request.get('/activity/news/list', { params })

    if (response.code === 200) {
      newsList.value = response.data.list
      total.value = response.data.total
    } else {
      ElMessage.error(response.msg || '加载失败')
    }
  } catch (error) {
    console.error('加载资讯列表失败:', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const goToCreate = () => {
  router.push('/news/create')
}

const goToDetail = (newsId) => {
  router.push(`/news/${newsId}`)
}

onMounted(() => {
  loadNews()
})
</script>

<style scoped>
.news-list-container {
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
  margin-bottom: 24px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 300px;
  margin-bottom: 24px;
}

.news-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  gap: 20px;
}

.news-card:hover {
  box-shadow: 0 4px 20px rgba(111, 75, 176, 0.15);
  transform: translateY(-2px);
}

.news-cover {
  width: 220px;
  height: 150px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
  background: #f5f5f5;
}

.news-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.news-header {
  display: flex;
  gap: 8px;
}

.news-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.news-summary {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.news-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  margin-top: auto;
}

.news-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #999;
}

.news-stats {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #999;
}

.news-stats span {
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

@media (max-width: 768px) {
  .news-card {
    flex-direction: column;
  }

  .news-cover {
    width: 100%;
    height: 200px;
  }

  .news-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
