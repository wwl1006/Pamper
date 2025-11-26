<template>
  <div class="admin-users">
    <el-card class="header-card">
      <h2>用户管理</h2>
      <p>管理平台所有用户账号</p>
    </el-card>

    <el-card class="table-card">
      <el-table :data="users" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="头像" width="80">
          <template #default="scope">
            <el-avatar :src="scope.row.avatar || '/default-avatar.png'" />
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column label="用户类型" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.user_type === 0" type="danger">管理员</el-tag>
            <el-tag v-else-if="scope.row.user_type === 2" type="warning">商户</el-tag>
            <el-tag v-else type="success">普通用户</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="create_time" label="注册时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 1 && scope.row.user_type !== 0"
              type="warning"
              size="small"
              @click="handleUpdateStatus(scope.row.id, 0)"
            >
              禁用
            </el-button>
            <el-button
              v-if="scope.row.status === 0"
              type="success"
              size="small"
              @click="handleUpdateStatus(scope.row.id, 1)"
            >
              启用
            </el-button>
            <el-tag v-if="scope.row.user_type === 0" type="info">管理员</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="loadUsers"
        @size-change="loadUsers"
        style="margin-top: 20px; justify-content: center;"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const users = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/admin/users', {
      params: { page: page.value, pageSize: pageSize.value }
    })
    if (res.code === 200) {
      users.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleUpdateStatus = async (userId, status) => {
  try {
    await ElMessageBox.confirm(
      `确定要${status === 1 ? '启用' : '禁用'}该用户吗？`,
      '提示',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )

    const res = await request.put(`/api/admin/users/${userId}/status`, null, {
      params: { status }
    })

    if (res.code === 200) {
      ElMessage.success('操作成功')
      loadUsers()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.admin-users {
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

.table-card {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
</style>
