<script setup>
import { Bell, ChatDotRound, Sunny, Service } from '@element-plus/icons-vue'
</script>

<template>
  <aside class="sidebar">
    <ul>
      <li>
        <router-link to="/admin/notice">
          <el-icon><Bell /></el-icon> 通知管理
        </router-link>
      </li>
      <li><router-link to="/community"><el-icon>
            <ChatDotRound />
          </el-icon> 宠物社区</router-link></li>
      <li><router-link to="/"><el-icon>
            <Sunny />
          </el-icon> 领养宠物</router-link></li>
      <li><router-link to="/"> <el-icon>
            <Service />
          </el-icon> 宠物医疗与服务</router-link></li>
    </ul>
  </aside>
  <!-- <h3>侧边栏</h3> -->
</template>

<style scoped>
.sidebar {
  width: 200px;
  background: linear-gradient(#FFEFD5, #FFFAF0);
  color: #fff;
  padding-top: 20px;
}

.sidebar ul {
  list-style: none;
  padding: 0;
}

.sidebar ul li {
  font-size: 16px;
  font-weight: bold;
  padding: 12px 20px;
}

.sidebar li:hover {
  font-size: 17px;
  background: #f6dcb3fd;
}

.sidebar a {
  color: #393838;
  text-decoration: none;
}
</style>