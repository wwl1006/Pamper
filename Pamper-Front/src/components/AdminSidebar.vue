<template>
  <el-menu
    :default-active="activeMenu"
    class="admin-sidebar"
    :router="true"
    background-color="#FFEFD5"
    text-color="#393838"
    active-text-color="#409eff"
  >
    <div class="admin-title">
      <h3>管理后台</h3>
    </div>

    <el-menu-item index="/admin/users">
      <el-icon><User /></el-icon>
      <span>用户管理</span>
    </el-menu-item>

    <el-menu-item index="/admin/review">
      <el-icon><Select /></el-icon>
      <span>内容审核</span>
    </el-menu-item>

    <el-menu-item index="/admin/content">
      <el-icon><Document /></el-icon>
      <span>内容管理</span>
    </el-menu-item>

    <el-menu-item index="/admin/notice">
      <el-icon><Bell /></el-icon>
      <span>发送公告</span>
    </el-menu-item>

    <el-menu-item index="/">
      <el-icon><HomeFilled /></el-icon>
      <span>返回首页</span>
    </el-menu-item>
  </el-menu>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  User,
  Select,
  Document,
  Bell,
  HomeFilled
} from '@element-plus/icons-vue'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.admin-sidebar {
  width: 200px;
  height: 100vh;
  border-right: none;
}

.admin-title {
  padding: 20px;
  text-align: center;
  background: linear-gradient(#FFEFD5, #FFFAF0);
}

.admin-title h3 {
  margin: 0;
  color: #393838;
  font-size: 18px;
  font-weight: bold;
}

.el-menu-item {
  font-size: 16px;
  font-weight: bold;
}

.el-menu-item:hover {
  background: #f6dcb3fd !important;
}

.el-menu-item.is-active {
  background: #f6dcb3fd !important;
}
</style>
