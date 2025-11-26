<template>
  <div class="post-square-layout">
    <UserHeader />
    <main class="post-square-main">
      <!-- 顶部分类筛选 -->
      <div class="filter-bar">
        <el-radio-group v-model="currentCategory" @change="handleCategoryChange">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="日常分享">日常分享</el-radio-button>
          <el-radio-button label="宠物医疗">宠物医疗</el-radio-button>
          <el-radio-button label="养护知识">养护知识</el-radio-button>
          <el-radio-button label="其他">其他</el-radio-button>
        </el-radio-group>

        <el-button type="primary" @click="goToCreate">发布新帖</el-button>
      </div>

      <!-- 帖子列表 -->
      <div v-if="loading" class="loading-state">加载中...</div>
      <div v-else-if="postList.length === 0" class="empty-state">
        暂无帖子，快来发布第一篇吧！
      </div>
      <div v-else class="post-grid">
        <el-card
          v-for="post in postList"
          :key="post.id"
          class="post-card"
          shadow="hover"
          @click="goToDetail(post.id)"
        >
          <!-- 帖子头部 -->
          <div class="post-header">
            <img :src="getAvatarUrl(post.user_id)" class="user-avatar" />
            <div class="user-info">
              <div class="username">{{ post.username }}</div>
              <div class="post-time">{{ post.create_time }}</div>
            </div>
            <el-tag size="small" type="info">{{ post.category }}</el-tag>
          </div>

          <!-- 帖子内容 -->
          <div class="post-content">
            <h3 v-if="post.title" class="post-title">{{ post.title }}</h3>
            <p class="post-text">{{ truncateText(post.content, 100) }}</p>

            <!-- 图片预览 -->
            <div v-if="post.images" class="post-images">
              <img
                v-for="(img, index) in getImageUrls(post.images).slice(0, 3)"
                :key="index"
                :src="img"
                class="post-image"
              />
            </div>

            <!-- 视频标记 -->
            <div v-if="post.video" class="video-tag">
              <el-icon><VideoPlay /></el-icon>
              视频
            </div>
          </div>

          <!-- 帖子底部 -->
          <div class="post-footer">
            <div class="stat-item">
              <el-icon><View /></el-icon>
              {{ post.view_count }}
            </div>
            <div class="stat-item">
              <el-icon :class="{ liked: post.is_liked }"><Star /></el-icon>
              {{ post.like_count }}
            </div>
            <div class="stat-item">
              <el-icon><ChatDotRound /></el-icon>
              {{ post.comment_count }}
            </div>
          </div>
        </el-card>
      </div>

      <!-- 分页器 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next, total"
          @current-change="handlePageChange"
        />
      </div>
    </main>
    <UserFooter />
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { View, Star, ChatDotRound, VideoPlay } from '@element-plus/icons-vue'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import UserHeader from '../components/UserHeader.vue'
import UserFooter from '../components/UserFooter.vue'
import request from '../utils/request'

const router = useRouter()
const loading = ref(false)
const postList = ref([])
const currentCategory = ref('')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const loadPosts = async () => {
  loading.value = true
  try {
    const res = await request.get('/post/list', {
      params: {
        category: currentCategory.value || undefined,
        page: currentPage.value,
        pageSize: pageSize.value
      }
    })

    if (res.code === 200) {
      postList.value = res.data.list || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '获取帖子列表失败')
    }
  } catch (error) {
    ElMessage.error('服务器错误')
  } finally {
    loading.value = false
  }
}

const handleCategoryChange = () => {
  currentPage.value = 1
  loadPosts()
}

const handlePageChange = () => {
  loadPosts()
}

const goToCreate = () => {
  router.push('/post/create')
}

const goToDetail = (id) => {
  router.push(`/post/${id}`)
}

const getAvatarUrl = (userId) => {
  return `${API_BASE}/profile/avatar/id/${userId}`
}

const getImageUrls = (imagesStr) => {
  if (!imagesStr) return []
  return imagesStr.split(',').map(img => `${API_BASE}/${img}`)
}

const truncateText = (text, maxLength) => {
  if (!text) return ''
  // CSS已经处理多行省略,这里只做基础截断
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '…'
}

onMounted(() => {
  loadPosts()
})
</script>

<style scoped>
.post-square-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.post-square-main {
  flex: 1;
  padding: 32px;
  background: #f5f6fa;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 16px;
  background: #fff;
  border-radius: 16px;
}

.post-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  margin-bottom: 24px;
}

.post-card {
  cursor: pointer;
  transition: all 0.3s;
}

.post-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.post-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.user-info {
  flex: 1;
}

.username {
  font-weight: 600;
  color: #333;
}

.post-time {
  font-size: 12px;
  color: #999;
}

.post-content {
  margin-bottom: 16px;
}

.post-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.post-text {
  margin: 0 0 12px 0;
  font-size: 14px;
  line-height: 1.6;
  color: #666;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  overflow: hidden;
  text-overflow: ellipsis;
  word-break: break-all;
}

.post-images {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

.post-image {
  width: 100%;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
}

.video-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  background: #a27bff;
  color: #fff;
  border-radius: 12px;
  font-size: 12px;
}

.post-footer {
  display: flex;
  gap: 24px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
  font-size: 14px;
}

.stat-item .el-icon.liked {
  color: #ff6b6b;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 24px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}
</style>
