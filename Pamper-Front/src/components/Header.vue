<script setup>
import { onMounted, ref } from 'vue';
import { User } from '@element-plus/icons-vue'

const time = ref("");
const user = ref("admin");
const greet = ref("");
const hour = new Date().getHours();

if (hour >= 5 && hour < 9) {
  greet.value = '早上好';
} else if (hour >= 9 && hour < 11) {
  greet.value = '上午好';
} else if (hour >= 11 && hour < 13) {
  greet.value = '中午好';
} else if (hour >= 13 && hour < 18) {
  greet.value = '下午好';
} else if (hour >= 18 && hour <= 22) {
  greet.value = '晚上好';
} else {
  greet.value = '夜深了';
}

const UpdateTime = () => {
  time.value = new Date().toLocaleString();
}

onMounted(() => {
  UpdateTime();
  setInterval(UpdateTime, 1000);
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

.left {
  display: flex;
  align-items: center;
}

.logo {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 15px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
}

.title {
  font-size: 22px;
  font-weight: 700;
  color: #4b2e83;
  letter-spacing: 1px;
}

.right {
  display: flex;
  align-items: center;
  gap: 20px;
  font-size: 14px;
  color: #4b2e83;
}

.time {
  background: rgba(255, 255, 255, 0.3);
  padding: 6px 12px;
  border-radius: 8px;
  font-weight: 500;
}

.greet {
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 255, 255, 0.3);
  padding: 6px 12px;
  border-radius: 8px;
  font-weight: 500;
}

.logout {
  padding: 6px 14px;
  background: #4b2e83;
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s;
}

.logout:hover {
  background: #6f4bb0;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}
</style>
