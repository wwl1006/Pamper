<script setup>
import { onMounted, ref } from 'vue';
import { User } from '@element-plus/icons-vue'
import { getCurrentTime, getGreet } from '../assets/Js/Date';

const time = ref("");
const user = ref("admin");
const greet = ref("");
onMounted(() => {
  time.value = getCurrentTime();
  greet.value = getGreet();
  setInterval(() => {
    time.value = getCurrentTime()
  }, 1000);
});
</script>

<template>
  <header class="header">
    <div class="left">
      <img src="../assets/Img/dog.webp" alt="logo" class="logo">
      <span class="title">宠伴 - 宠物交流与服务平台</span>
    </div>
    <div class="right">
      <span class="time">{{ time }}</span>
      <span class="greet"><el-icon>
          <User />
        </el-icon> {{ greet }}, {{ user }}！</span>
      <button class="logout">退出</button>
    </div>
  </header>
</template>

<style scoped>
@import "../assets/css/header.css";

.header {
  height: 70px;
  background: linear-gradient(90deg, #ffecd2, #fcb69f);
  /* 渐变背景 */
  color: #333;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  /* 阴影 */
  border-radius: 0 0 12px 12px;
  font-family: "Segoe UI", "PingFang SC", "Helvetica Neue", sans-serif;
}
</style>
