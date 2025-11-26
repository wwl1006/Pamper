<template>
  <div class="news-detail-container">
    <div v-loading="loading" class="content-wrapper">
      <el-card class="news-card" shadow="hover">
        <div class="news-header">
          <el-tag v-if="newsDetail.is_top === 1" type="danger">置顶</el-tag>
          <el-tag>{{ newsDetail.category }}</el-tag>
        </div>

        <h1 class="news-title">{{ newsDetail.title }}</h1>

        <div class="news-meta">
          <span v-if="newsDetail.author">作者: {{ newsDetail.author }}</span>
          <span v-if="newsDetail.source">来源: {{ newsDetail.source }}</span>
          <span>发布时间: {{ newsDetail.publish_time || newsDetail.create_time }}</span>
        </div>

        <div class="cover-image" v-if="newsDetail.cover_image">
          <el-image :src="newsDetail.cover_image" fit="cover" style="width: 100%; height: 100%" />
        </div>

        <div class="news-content" v-html="newsDetail.content"></div>

        <div class="news-tags" v-if="newsDetail.tags">
          <span class="tags-label">标签:</span>
          <el-tag
            v-for="tag in newsDetail.tags.split(',')"
            :key="tag"
            size="small"
            style="margin-right: 8px"
          >
            {{ tag }}
          </el-tag>
        </div>

        <div class="news-footer">
          <div class="stats">
            <span><el-icon><View /></el-icon> 浏览 {{ newsDetail.view_count }}</span>
            <span><el-icon><StarFilled /></el-icon> 点赞 {{ newsDetail.like_count }}</span>
          </div>
          <el-button type="primary" @click="handleLike" :loading="liking">
            <el-icon><StarFilled /></el-icon>
            点赞
          </el-button>
        </div>
      </el-card>

      <el-button class="back-button" @click="goBack">返回列表</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { View, StarFilled } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const liking = ref(false)
const newsDetail = ref({})

const loadNewsDetail = async () => {
  loading.value = true
  try {
    const response = await request.get(`/activity/news/${route.params.id}`)
    if (response.code === 200) {
      newsDetail.value = response.data
    } else {
      ElMessage.error(response.msg || '加载失败')
      router.back()
    }
  } catch (error) {
    console.error('加载资讯详情失败:', error)
    ElMessage.error('加载失败')
    router.back()
  } finally {
    loading.value = false
  }
}

const handleLike = async () => {
  liking.value = true
  try {
    const response = await request.post(`/activity/news/like/${route.params.id}`)
    if (response.code === 200) {
      ElMessage.success('点赞成功')
      newsDetail.value.like_count++
    } else {
      ElMessage.error(response.msg || '点赞失败')
    }
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('点赞失败')
  } finally {
    liking.value = false
  }
}

const goBack = () => {
  router.push('/news')
}

onMounted(() => {
  loadNewsDetail()
})
</script>

<style scoped>
.news-detail-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 30px 20px;
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.news-card {
  border-radius: 16px;
  padding: 40px;
}

.news-header {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.news-title {
  font-size: 32px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
  line-height: 1.4;
}

.news-meta {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #999;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.cover-image {
  width: 100%;
  height: 400px;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 30px;
  background: #f5f5f5;
}

.news-content {
  font-size: 16px;
  color: #333;
  line-height: 1.8;
  margin-bottom: 30px;
  white-space: pre-wrap;
}

.news-content :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 16px 0;
}

.news-content :deep(p) {
  margin: 16px 0;
}

.news-tags {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 30px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
}

.tags-label {
  font-weight: 500;
  color: #666;
}

.news-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

.stats {
  display: flex;
  gap: 24px;
  font-size: 14px;
  color: #666;
}

.stats span {
  display: flex;
  align-items: center;
  gap: 6px;
}

.back-button {
  width: 100%;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #6f4bb0 0%, #4b2e83 100%);
  border: none;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #5a3d91 0%, #3d2569 100%);
}

@media (max-width: 768px) {
  .news-card {
    padding: 20px;
  }

  .news-title {
    font-size: 24px;
  }

  .cover-image {
    height: 250px;
  }

  .news-meta {
    flex-direction: column;
    gap: 8px;
  }

  .news-footer {
    flex-direction: column;
    gap: 16px;
  }

  .news-footer .el-button {
    width: 100%;
  }
}
</style>
