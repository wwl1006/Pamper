<template>
  <div class="post-create-layout">
    <UserHeader />
    <main class="post-create-main">
      <el-card class="create-card">
        <template #header>
          <div class="card-header">
            <span>发布新帖子</span>
          </div>
        </template>

        <el-form :model="postForm" :rules="postRules" ref="postFormRef" label-width="100px">
          <el-form-item label="帖子类型" prop="post_type">
            <el-radio-group v-model="postForm.post_type">
              <el-radio :value="0">纯文字</el-radio>
              <el-radio :value="1">图文</el-radio>
              <el-radio :value="2">视频</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="分类" prop="category">
            <el-select v-model="postForm.category" placeholder="请选择分类">
              <el-option label="日常分享" value="日常分享" />
              <el-option label="宠物医疗" value="宠物医疗" />
              <el-option label="养护知识" value="养护知识" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>

          <el-form-item label="标题" prop="title">
            <el-input
              v-model="postForm.title"
              placeholder="请输入帖子标题（选填）"
              clearable
              maxlength="100"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="内容" prop="content">
            <el-input
              v-model="postForm.content"
              type="textarea"
              :rows="8"
              placeholder="分享你和宠物的故事..."
              maxlength="2000"
              show-word-limit
            />
          </el-form-item>

          <!-- 图片上传 -->
          <el-form-item v-if="postForm.post_type === 1" label="上传图片">
            <el-upload
              action="#"
              list-type="picture-card"
              :auto-upload="false"
              :on-change="handleImageChange"
              :on-remove="handleImageRemove"
              :file-list="imageList"
              accept="image/*"
              :limit="9"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
            <div class="upload-tip">最多上传9张图片，每张不超过10MB</div>
          </el-form-item>

          <!-- 视频上传 -->
          <el-form-item v-if="postForm.post_type === 2" label="上传视频">
            <el-upload
              action="#"
              :auto-upload="false"
              :on-change="handleVideoChange"
              :file-list="videoList"
              accept="video/*"
              :limit="1"
            >
              <el-button type="primary">选择视频</el-button>
            </el-upload>
            <div class="upload-tip">只能上传1个视频，不超过100MB</div>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="publishing" @click="submitPost">
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
const postFormRef = ref()
const publishing = ref(false)
const imageList = ref([])
const videoList = ref([])

const postForm = reactive({
  post_type: 0,
  category: '其他',
  title: '',
  content: ''
})

const postRules = {
  content: [
    { required: true, message: '帖子内容不能为空', trigger: 'blur' },
    { max: 2000, message: '内容不能超过2000字', trigger: 'blur' }
  ]
}

const handleImageChange = (file, fileList) => {
  imageList.value = fileList
}

const handleImageRemove = (file, fileList) => {
  imageList.value = fileList
}

const handleVideoChange = (file, fileList) => {
  videoList.value = fileList
}

const submitPost = async () => {
  try {
    await postFormRef.value?.validate()
    publishing.value = true

    let imageUrls = []
    let videoUrl = ''

    // 上传图片
    if (postForm.post_type === 1 && imageList.value.length > 0) {
      const formData = new FormData()
      imageList.value.forEach(item => {
        formData.append('files', item.raw)
      })

      const imgRes = await request.post('/post/upload/images', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })

      if (imgRes.code === 200) {
        imageUrls = imgRes.data
      } else {
        ElMessage.error('图片上传失败：' + imgRes.msg)
        return
      }
    }

    // 上传视频
    if (postForm.post_type === 2 && videoList.value.length > 0) {
      const formData = new FormData()
      formData.append('file', videoList.value[0].raw)

      const videoRes = await request.post('/post/upload/video', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })

      if (videoRes.code === 200) {
        videoUrl = videoRes.data
      } else {
        ElMessage.error('视频上传失败：' + videoRes.msg)
        return
      }
    }

    // 创建帖子
    const postData = {
      post_type: postForm.post_type,
      category: postForm.category,
      title: postForm.title,
      content: postForm.content,
      images: imageUrls.join(','),
      video: videoUrl
    }

    const res = await request.post('/post/create', postData)
    if (res.code === 200) {
      ElMessage.success('发布成功')
      router.push('/community')
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
  postFormRef.value?.resetFields()
  imageList.value = []
  videoList.value = []
}
</script>

<style scoped>
.post-create-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.post-create-main {
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
