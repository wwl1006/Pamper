<template>
  <div class="my-posts-container">
    <UserHeader />
    <div class="my-posts-content">
      <h2>我的发布</h2>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <!-- 我的帖子 -->
        <el-tab-pane label="我的帖子" name="posts">
          <div class="tab-header">
            <el-button type="primary" @click="goToCreatePost">发布新帖子</el-button>
          </div>

          <el-table :data="posts" v-loading="loading" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="title" label="标题" width="250" />
            <el-table-column prop="category" label="分类" width="120" />
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 0" type="warning">待审核</el-tag>
                <el-tag v-else-if="scope.row.status === 1" type="success">已发布</el-tag>
                <el-tag v-else-if="scope.row.status === 2" type="danger">已拒绝</el-tag>
                <el-tag v-else type="info">已删除</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="like_count" label="点赞" width="80" />
            <el-table-column prop="comment_count" label="评论" width="80" />
            <el-table-column prop="create_time" label="发布时间" width="180" />
            <el-table-column label="操作" fixed="right" width="200">
              <template #default="scope">
                <el-button size="small" @click="viewPost(scope.row.id)">查看</el-button>
                <el-button type="danger" size="small" @click="deletePost(scope.row.id)" v-if="scope.row.status !== 3">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="postPage"
            :total="postTotal"
            :page-size="10"
            layout="total, prev, pager, next"
            @current-change="loadPosts"
            style="margin-top: 20px; justify-content: center;"
          />
        </el-tab-pane>

        <!-- 我的领养 -->
        <el-tab-pane label="我的领养" name="adoptions">
          <div class="tab-header">
            <el-button type="primary" @click="goToCreateAdoption">发布领养信息</el-button>
          </div>

          <el-table :data="adoptions" v-loading="loading" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="pet_name" label="宠物名称" width="150" />
            <el-table-column prop="pet_type" label="类型" width="100" />
            <el-table-column prop="pet_breed" label="品种" width="120" />
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 0" type="warning">待审核</el-tag>
                <el-tag v-else-if="scope.row.status === 1" type="success">可领养</el-tag>
                <el-tag v-else-if="scope.row.status === 2" type="danger">已拒绝</el-tag>
                <el-tag v-else-if="scope.row.status === 3" type="info">已领养</el-tag>
                <el-tag v-else>已下架</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="create_time" label="发布时间" width="180" />
            <el-table-column label="操作" fixed="right" width="200">
              <template #default="scope">
                <el-button size="small" @click="viewAdoption(scope.row.id)">查看</el-button>
                <el-button
                  type="warning"
                  size="small"
                  @click="updateAdoptionStatus(scope.row.id, scope.row.status === 1 ? 4 : 1)"
                  v-if="scope.row.status === 1 || scope.row.status === 4"
                >
                  {{ scope.row.status === 1 ? '下架' : '上架' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="adoptionPage"
            :total="adoptionTotal"
            :page-size="10"
            layout="total, prev, pager, next"
            @current-change="loadAdoptions"
            style="margin-top: 20px; justify-content: center;"
          />
        </el-tab-pane>

        <!-- 我的活动 -->
        <el-tab-pane label="我的活动" name="activities">
          <div class="tab-header">
            <el-button type="primary" @click="goToCreateActivity">发布活动</el-button>
          </div>

          <el-table :data="activities" v-loading="loading" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="title" label="标题" width="200" />
            <el-table-column prop="activity_type" label="类型" width="100" />
            <el-table-column prop="location" label="地点" width="150" />
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 0" type="warning">待审核</el-tag>
                <el-tag v-else-if="scope.row.status === 1" type="success">已发布</el-tag>
                <el-tag v-else-if="scope.row.status === 2" type="danger">已拒绝</el-tag>
                <el-tag v-else-if="scope.row.status === 3" type="info">已取消</el-tag>
                <el-tag v-else-if="scope.row.status === 4" type="primary">进行中</el-tag>
                <el-tag v-else>已结束</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="start_time" label="开始时间" width="180" />
            <el-table-column label="操作" fixed="right" width="200">
              <template #default="scope">
                <el-button size="small" @click="viewActivity(scope.row.id)">查看</el-button>
                <el-button
                  type="danger"
                  size="small"
                  @click="cancelActivity(scope.row.id)"
                  v-if="scope.row.status === 1"
                >
                  取消
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="activityPage"
            :total="activityTotal"
            :page-size="10"
            layout="total, prev, pager, next"
            @current-change="loadActivities"
            style="margin-top: 20px; justify-content: center;"
          />
        </el-tab-pane>
      </el-tabs>
    </div>
    <UserFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import UserHeader from '../components/UserHeader.vue'
import UserFooter from '../components/UserFooter.vue'
import request from '../utils/request'

const router = useRouter()
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

// 加载我的帖子
const loadPosts = async () => {
  loading.value = true
  try {
    const response = await request.get('/post/my', {
      params: { page: postPage.value, pageSize: 10 }
    })
    if (response.code === 200) {
      posts.value = response.data.list
      postTotal.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 加载我的领养
const loadAdoptions = async () => {
  loading.value = true
  try {
    const response = await request.get('/adoption/my', {
      params: { page: adoptionPage.value, pageSize: 10 }
    })
    if (response.code === 200) {
      adoptions.value = response.data.list
      adoptionTotal.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 加载我的活动
const loadActivities = async () => {
  loading.value = true
  try {
    const response = await request.get('/activity/my')
    if (response.code === 200) {
      activities.value = response.data || []
      activityTotal.value = activities.value.length
    }
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 查看帖子
const viewPost = (id) => {
  router.push(`/post/${id}`)
}

// 删除帖子
const deletePost = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个帖子吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await request.delete(`/post/${id}`)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadPosts()
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 查看领养
const viewAdoption = (id) => {
  router.push(`/adoption/${id}`)
}

// 更新领养状态
const updateAdoptionStatus = async (id, status) => {
  const action = status === 4 ? '下架' : '上架'
  try {
    await ElMessageBox.confirm(`确定要${action}这个领养信息吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await request.put(`/adoption/status`, null, {
      params: { adoptionId: id, status }
    })
    if (response.code === 200) {
      ElMessage.success(`${action}成功`)
      loadAdoptions()
    } else {
      ElMessage.error(response.msg || `${action}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 查看活动
const viewActivity = (id) => {
  router.push(`/activity/${id}`)
}

// 取消活动
const cancelActivity = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消这个活动吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await request.put('/activity/status', null, {
      params: { activityId: id, status: 3 }
    })
    if (response.code === 200) {
      ElMessage.success('取消成功')
      loadActivities()
    } else {
      ElMessage.error(response.msg || '取消失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 跳转到创建页面
const goToCreatePost = () => {
  router.push('/post/create')
}

const goToCreateAdoption = () => {
  router.push('/adoption/create')
}

const goToCreateActivity = () => {
  router.push('/activity/create')
}

onMounted(() => {
  loadPosts()
})
</script>

<style scoped>
.my-posts-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f6fa;
}

.my-posts-content {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 30px auto;
  padding: 30px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.my-posts-content h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 24px;
}

.tab-header {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
