<script setup>
import "../assets/Js/Date.js"
import { onMounted, ref } from 'vue'
import { getCurrentTime, getGreet } from "../assets/Js/Date.js"
const activeMenu = ref("1")
const time = ref("")
const greet = ref("")
const user = ref('wwl')
const keyword = ref('')
const handleSelect = (index) => {
  console.log('选中的菜单：', index);
  activeMenu.value = index
}
onMounted(() => {
  time.value = getCurrentTime();
  greet.value = getGreet();
  setInterval(() => {
    time.value = getCurrentTime()
  }, 1000)
})
</script>

<template>
  <div class="header">
    <!-- 左侧 -->
    <div class="left">
      <img src="../assets/Img/dog.webp" class="logo" />
      <span class="title">宠伴 - 宠物交流与服务平台</span>
    </div>

    <!-- 中间菜单 -->
    <el-menu :default-active="activeMenu" class="menu" mode="horizontal" @select="handleSelect">
      <el-menu-item index="1">首页</el-menu-item>
      <el-menu-item index="2">社区</el-menu-item>
      <el-menu-item index="3">领养中心</el-menu-item>
      <el-menu-item index="4">商城</el-menu-item>
      <el-menu-item index="5">服务</el-menu-item>
    </el-menu>

    <!-- 右侧 -->
    <div class="right">
      <el-input v-model="keyword" placeholder="搜索帖子 / 宠物 / 用户" class="search" clearable prefix-icon="Search" />
      <span class="time">{{ time }}</span>
      <span class="greet">
        <el-icon>
          <User />
        </el-icon>
        {{ greet }}, {{ user }}！
      </span>
      <button class="logout">退出</button>
    </div>
  </div>

</template>


<style scoped>
@import "../assets/css/header.css";

.header {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  background: #fff;
  border-bottom: 1px solid #eee;
}

.search {
  width: 200px;
}

/* 左侧固定 */
.left {
  gap: 10px;
  margin-right: 30px;
}

/* logo */
.logo {
  width: 36px;
  height: 36px;
}

/* 标题 */
.title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

/* 中间菜单自动撑开并居中 */
.menu {
  flex: 1;
  display: flex;
  justify-content: center;
  /* ⭐ 让菜单居中 */
  border-bottom: none;
  /* 去掉底线 */
}

/* 右侧内容 */
.right {
  gap: 15px;
  margin-left: 40px;
  /* ⭐ 与菜单保持适当间距 */
}

/* 退出按钮 */
.logout {
  padding: 6px 12px;
  background: #4b2e83;
  color: white;
  border-radius: 6px;
}
</style>