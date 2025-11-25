<template>
  <div class="post-detail-layout">
    <UserHeader />
    <main class="post-detail-main">
      <div v-if="loading" class="loading-state">加载中...</div>
      <div v-else-if="!post" class="empty-state">帖子不存在</div>
      <div v-else class="detail-container">
        <!-- 帖子详情卡片 -->
        <el-card class="post-detail-card">
          <!-- 帖子头部 -->
          <div class="post-header">
            <img :src="getAvatarUrl(post.user_id)" class="user-avatar" />
            <div class="user-info">
              <div class="username">{{ post.username }}</div>
              <div class="post-meta">
                <span>{{ post.create_time }}</span>
                <el-tag size="small" type="info">{{ post.category }}</el-tag>
              </div>
            </div>
          </div>

          <!-- 帖子内容 -->
          <div class="post-content">
            <h2 v-if="post.title" class="post-title">{{ post.title }}</h2>
            <div class="post-text">{{ post.content }}</div>

            <!-- 图片展示 -->
            <div v-if="post.images" class="post-images">
              <el-image
                v-for="(img, index) in getImageUrls(post.images)"
                :key="index"
                :src="img"
                :preview-src-list="getImageUrls(post.images)"
                :initial-index="index"
                fit="cover"
                class="post-image"
              />
            </div>

            <!-- 视频展示 -->
            <div v-if="post.video" class="post-video">
              <video :src="getVideoUrl(post.video)" controls class="video-player"></video>
            </div>
          </div>

          <!-- 帖子统计和操作 -->
          <div class="post-actions">
            <div class="stats">
              <span><el-icon><View /></el-icon> {{ post.view_count }}</span>
              <span><el-icon><ChatDotRound /></el-icon> {{ post.comment_count }}</span>
            </div>
            <el-button
              :type="post.is_liked ? 'danger' : 'default'"
              :icon="Star"
              @click="handleLike"
            >
              {{ post.is_liked ? '已点赞' : '点赞' }} ({{ post.like_count }})
            </el-button>
          </div>
        </el-card>

        <!-- 评论区域 -->
        <el-card class="comment-section">
          <template #header>
            <div class="section-header">评论 ({{ comments.length }})</div>
          </template>

          <!-- 发表评论 -->
          <div class="comment-form">
            <el-input
              v-model="commentText"
              type="textarea"
              :rows="3"
              placeholder="说说你的看法..."
              maxlength="500"
              show-word-limit
            />
            <el-button
              type="primary"
              :loading="commenting"
              @click="submitComment"
              style="margin-top: 12px"
            >
              发表评论
            </el-button>
          </div>

          <!-- 评论列表 -->
          <div v-if="commentsLoading" class="loading-state">加载评论中...</div>
          <div v-else-if="comments.length === 0" class="empty-comments">暂无评论</div>
          <div v-else class="comment-list">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <img :src="getAvatarUrl(comment.user_id)" class="comment-avatar" />
              <div class="comment-content">
                <div class="comment-header">
                  <span class="comment-username">{{ comment.username }}</span>
                  <span class="comment-time">{{ comment.create_time }}</span>
                </div>
                <div class="comment-text">{{ comment.content }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </main>
    <UserFooter />
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { View, Star, ChatDotRound } from '@element-plus/icons-vue'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import UserHeader from '../components/UserHeader.vue'
import UserFooter from '../components/UserFooter.vue'
import request from '../utils/request'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const commentsLoading = ref(false)
const commenting = ref(false)
const post = ref(null)
const comments = ref([])
const commentText = ref('')

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const loadPost = async () => {
  loading.value = true
  try {
    const res = await request.get(`/post/${route.params.id}`)
    if (res.code === 200) {
      post.value = res.data
      loadComments()
    } else {
      ElMessage.error(res.msg || '获取帖子详情失败')
    }
  } catch (error) {
    ElMessage.error('服务器错误')
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  commentsLoading.value = true
  try {
    const res = await request.get(`/post/${route.params.id}/comments`)
    if (res.code === 200) {
      comments.value = res.data || []
    }
  } catch (error) {
    console.error('加载评论失败', error)
  } finally {
    commentsLoading.value = false
  }
}

const handleLike = async () => {
  try {
    const res = await request.post(`/post/like/${route.params.id}`)
    if (res.code === 200) {
      ElMessage.success(res.msg)
      // 重新加载帖子更新点赞状态
      loadPost()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    ElMessage.error('服务器错误')
  }
}

const submitComment = async () => {
  if (!commentText.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  commenting.value = true
  try {
    const res = await request.post('/post/comment', {
      post_id: route.params.id,
      content: commentText.value
    })

    if (res.code === 200) {
      ElMessage.success('评论成功')
      commentText.value = ''
      loadComments()
      loadPost() // 重新加载帖子更新评论数
    } else {
      ElMessage.error(res.msg || '评论失败')
    }
  } catch (error) {
    ElMessage.error('服务器错误')
  } finally {
    commenting.value = false
  }
}

const getAvatarUrl = (userId) => {
  return `${API_BASE}/profile/avatar/id/${userId}`
}

const getImageUrls = (imagesStr) => {
  if (!imagesStr) return []
  return imagesStr.split(',').map(img => `${API_BASE}/${img}`)
}

const getVideoUrl = (videoPath) => {
  return `${API_BASE}/${videoPath}`
}

onMounted(() => {
  loadPost()
})
</script>

<style scoped>
.post-detail-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.post-detail-main {
  flex: 1;
  padding: 32px;
  background: #f5f6fa;
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

.detail-container {
  max-width: 900px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.post-detail-card,
.comment-section {
  border-radius: 16px;
}

.post-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.user-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.user-info {
  flex: 1;
}

.username {
  font-weight: 600;
  font-size: 16px;
  color: #333;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 4px;
  font-size: 12px;
  color: #999;
}

.post-content {
  padding: 24px 0;
}

.post-title {
  margin: 0 0 16px 0;
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.post-text {
  font-size: 16px;
  line-height: 1.8;
  color: #666;
  white-space: pre-wrap;
  word-break: break-word;
}

.post-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.post-image {
  width: 100%;
  height: 250px;
  border-radius: 8px;
  cursor: pointer;
}

.post-video {
  margin-top: 16px;
}

.video-player {
  width: 100%;
  max-height: 500px;
  border-radius: 8px;
}

.post-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.stats {
  display: flex;
  gap: 24px;
  color: #666;
  font-size: 14px;
}

.stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.section-header {
  font-weight: 600;
  font-size: 16px;
}

.comment-form {
  margin-bottom: 24px;
}

.empty-comments {
  text-align: center;
  padding: 40px 20px;
  color: #999;
  font-size: 14px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #f5f6fa;
  border-radius: 8px;
}

.comment-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.comment-username {
  font-weight: 600;
  color: #333;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-text {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
