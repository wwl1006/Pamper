<template>
  <div class="admin-content">
    <el-card class="header-card">
      <h2>内容管理</h2>
      <p>管理已发布的帖子、领养信息和活动</p>
    </el-card>

    <el-card class="content-card">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <!-- 帖子管理 -->
        <el-tab-pane label="帖子管理" name="posts">
          <el-table :data="posts" v-loading="loading" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="title" label="标题" width="200" />
            <el-table-column prop="category" label="分类" width="120" />
            <el-table-column prop="username" label="发布者" width="120" />
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 1" type="success">已发布</el-tag>
                <el-tag v-else-if="scope.row.status === 3" type="info">已删除</el-tag>
                <el-tag v-else type="warning">其他</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="like_count" label="点赞" width="80" />
            <el-table-column prop="comment_count" label="评论" width="80" />
            <el-table-column prop="create_time" label="发布时间" width="180" />
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="scope">
                <el-button
                  v-if="scope.row.status === 1"
                  type="danger"
                  size="small"
                  @click="deletePost(scope.row.id)"
                >
                  删除
                </el-button>
                <el-button
                  v-if="scope.row.status === 3"
                  type="success"
                  size="small"
                  @click="restorePost(scope.row.id)"
                >
                  恢复
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

        <!-- 领养管理 -->
        <el-tab-pane label="领养管理" name="adoptions">
          <el-table :data="adoptions" v-loading="loading" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="pet_name" label="宠物名称" width="120" />
            <el-table-column prop="pet_type" label="类型" width="100" />
            <el-table-column prop="pet_breed" label="品种" width="120" />
            <el-table-column prop="username" label="发布者" width="120" />
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 1" type="success">已发布</el-tag>
                <el-tag v-else-if="scope.row.status === 3" type="warning">已领养</el-tag>
                <el-tag v-else-if="scope.row.status === 4" type="info">已下架</el-tag>
                <el-tag v-else>其他</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="create_time" label="发布时间" width="180" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <el-button
                  v-if="scope.row.status === 1"
                  type="warning"
                  size="small"
                  @click="updateAdoptionStatus(scope.row.id, 4)"
                >
                  下架
                </el-button>
                <el-button
                  v-if="scope.row.status === 4"
                  type="success"
                  size="small"
                  @click="updateAdoptionStatus(scope.row.id, 1)"
                >
                  上架
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

        <!-- 活动管理 -->
        <el-tab-pane label="活动管理" name="activities">
          <el-table :data="activities" v-loading="loading" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="title" label="标题" width="200" />
            <el-table-column prop="activity_type" label="类型" width="120" />
            <el-table-column prop="location" label="地点" width="150" />
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 1" type="success">已发布</el-tag>
                <el-tag v-else-if="scope.row.status === 3" type="info">已取消</el-tag>
                <el-tag v-else-if="scope.row.status === 4" type="warning">进行中</el-tag>
                <el-tag v-else-if="scope.row.status === 5" type="info">已结束</el-tag>
                <el-tag v-else>其他</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="start_time" label="开始时间" width="180" />
            <el-table-column prop="creator_name" label="创建者" width="120" />
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="scope">
                <el-button
                  v-if="scope.row.status === 1"
                  type="danger"
                  size="small"
                  @click="updateActivityStatus(scope.row.id, 3)"
                >
                  取消
                </el-button>
                <el-button
                  v-if="scope.row.status === 3"
                  type="success"
                  size="small"
                  @click="updateActivityStatus(scope.row.id, 1)"
                >
                  恢复
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
    const res = await request.get('/api/admin/posts/all', {
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

const deletePost = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该帖子吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await request.put(`/api/admin/posts/${id}/review`, null, {
      params: { status: 3 }
    })

    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadPosts()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const restorePost = async (id) => {
  try {
    await ElMessageBox.confirm('确定要恢复该帖子吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await request.put(`/api/admin/posts/${id}/review`, null, {
      params: { status: 1 }
    })

    if (res.code === 200) {
      ElMessage.success('恢复成功')
      loadPosts()
    } else {
      ElMessage.error(res.msg || '恢复失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const loadAdoptions = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/admin/adoptions/all', {
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

const updateAdoptionStatus = async (id, status) => {
  try {
    const action = status === 4 ? '下架' : '上架'
    await ElMessageBox.confirm(`确定要${action}该领养信息吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await request.put(`/api/admin/adoptions/${id}/review`, null, {
      params: { status }
    })

    if (res.code === 200) {
      ElMessage.success(`${action}成功`)
      loadAdoptions()
    } else {
      ElMessage.error(res.msg || `${action}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const loadActivities = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/admin/activities/all', {
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

const updateActivityStatus = async (id, status) => {
  try {
    const action = status === 3 ? '取消' : '恢复'
    await ElMessageBox.confirm(`确定要${action}该活动吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await request.put(`/api/admin/activities/${id}/review`, null, {
      params: { status }
    })

    if (res.code === 200) {
      ElMessage.success(`${action}成功`)
      loadActivities()
    } else {
      ElMessage.error(res.msg || `${action}失败`)
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
.admin-content {
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
