<template>
  <div class="admin-review">
    <el-card class="header-card">
      <h2>内容审核</h2>
      <p>审核平台待发布的帖子、领养信息和活动</p>
    </el-card>

    <el-card class="content-card">
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- 帖子审核 -->
      <el-tab-pane label="帖子审核" name="posts">
        <el-table :data="posts" v-loading="loading" style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="标题" width="200" />
          <el-table-column prop="category" label="分类" width="120" />
          <el-table-column prop="username" label="发布者" width="120" />
          <el-table-column prop="create_time" label="发布时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button type="success" size="small" @click="reviewPost(scope.row.id, 1)">
                通过
              </el-button>
              <el-button type="danger" size="small" @click="reviewPost(scope.row.id, 2)">
                拒绝
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          v-model:current-page="postPage"
          :total="postTotal"
          :page-size="20"
          layout="total, prev, pager, next"
          @current-change="loadPosts"
          style="margin-top: 20px; justify-content: center;"
        />
      </el-tab-pane>

      <!-- 领养审核 -->
      <el-tab-pane label="领养审核" name="adoptions">
        <el-table :data="adoptions" v-loading="loading" style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="pet_name" label="宠物名称" width="120" />
          <el-table-column prop="pet_type" label="类型" width="100" />
          <el-table-column prop="pet_breed" label="品种" width="120" />
          <el-table-column prop="username" label="发布者" width="120" />
          <el-table-column prop="create_time" label="发布时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button type="success" size="small" @click="reviewAdoption(scope.row.id, 1)">
                通过
              </el-button>
              <el-button type="danger" size="small" @click="reviewAdoption(scope.row.id, 2)">
                拒绝
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          v-model:current-page="adoptionPage"
          :total="adoptionTotal"
          :page-size="20"
          layout="total, prev, pager, next"
          @current-change="loadAdoptions"
          style="margin-top: 20px; justify-content: center;"
        />
      </el-tab-pane>

      <!-- 活动审核 -->
      <el-tab-pane label="活动审核" name="activities">
        <el-table :data="activities" v-loading="loading" style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="标题" width="200" />
          <el-table-column prop="activity_type" label="类型" width="120" />
          <el-table-column prop="location" label="地点" width="150" />
          <el-table-column prop="start_time" label="开始时间" width="180" />
          <el-table-column prop="creator_name" label="创建者" width="120" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button type="success" size="small" @click="reviewActivity(scope.row.id, 1)">
                通过
              </el-button>
              <el-button type="danger" size="small" @click="reviewActivity(scope.row.id, 2)">
                拒绝
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          v-model:current-page="activityPage"
          :total="activityTotal"
          :page-size="20"
          layout="total, prev, pager, next"
          @current-change="loadActivities"
          style="margin-top: 20px; justify-content: center;"
        />
      </el-tab-pane>
    </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const activeTab = ref('posts')
const loading = ref(false)

// 帖子
const posts = ref([])
const postPage = ref(1)
const postTotal = ref(0)

// 领养
const adoptions = ref([])
const adoptionPage = ref(1)
const adoptionTotal = ref(0)

// 活动
const activities = ref([])
const activityPage = ref(1)
const activityTotal = ref(0)

const handleTabChange = (tab) => {
  if (tab === 'posts') loadPosts()
  else if (tab === 'adoptions') loadAdoptions()
  else if (tab === 'activities') loadActivities()
}

const loadPosts = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/admin/posts/pending', {
      params: { page: postPage.value, pageSize: 20 }
    })
    if (res.code === 200) {
      posts.value = res.data.list
      postTotal.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('加载帖子列表失败')
  } finally {
    loading.value = false
  }
}

const loadAdoptions = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/admin/adoptions/pending', {
      params: { page: adoptionPage.value, pageSize: 20 }
    })
    if (res.code === 200) {
      adoptions.value = res.data.list
      adoptionTotal.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('加载领养列表失败')
  } finally {
    loading.value = false
  }
}

const loadActivities = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/admin/activities/pending', {
      params: { page: activityPage.value, pageSize: 20 }
    })
    if (res.code === 200) {
      activities.value = res.data.list
      activityTotal.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('加载活动列表失败')
  } finally {
    loading.value = false
  }
}

const reviewPost = async (id, status) => {
  try {
    await ElMessageBox.confirm(
      `确定要${status === 1 ? '通过' : '拒绝'}该帖子吗？`,
      '提示',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )

    const res = await request.put(`/api/admin/posts/${id}/review`, null, {
      params: { status }
    })

    if (res.code === 200) {
      ElMessage.success('审核成功')
      loadPosts()
    } else {
      ElMessage.error(res.msg || '审核失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const reviewAdoption = async (id, status) => {
  try {
    await ElMessageBox.confirm(
      `确定要${status === 1 ? '通过' : '拒绝'}该领养信息吗？`,
      '提示',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )

    const res = await request.put(`/api/admin/adoptions/${id}/review`, null, {
      params: { status }
    })

    if (res.code === 200) {
      ElMessage.success('审核成功')
      loadAdoptions()
    } else {
      ElMessage.error(res.msg || '审核失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const reviewActivity = async (id, status) => {
  try {
    await ElMessageBox.confirm(
      `确定要${status === 1 ? '通过' : '拒绝'}该活动吗？`,
      '提示',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )

    const res = await request.put(`/api/admin/activities/${id}/review`, null, {
      params: { status }
    })

    if (res.code === 200) {
      ElMessage.success('审核成功')
      loadActivities()
    } else {
      ElMessage.error(res.msg || '审核失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  loadPosts()
})
</script>

<style scoped>
.admin-review {
  padding: 20px;
}

.header-card {
  margin-bottom: 20px;
}

.header-card h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
}

.header-card p {
  margin: 0;
  color: #666;
}

.content-card {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
</style>
