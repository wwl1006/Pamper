<script setup>
import { RouterLink, RouterView } from 'vue-router'
import Header from '../components/Header.vue';
import Footer from '../components/Footer.vue';
import Slidebar from '../components/Slidebar.vue';
</script>

<template>
  <div class="layout">
    <!--头部-->
    <Header />
    <div class="main">
      <!--侧边栏-->
      <Slidebar />
      <!--内容区-->
      <div class="content">
        <router-view></router-view>
      </div>
    </div>
    <div>
      <!--底部-->
      <Footer />
    </div>
  </div>

</template>

<style>
.layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.main {
  flex: 1;
  display: flex;
}

.content {
  flex: 1;
  padding: 20px;
  background: #f5f6fa;
  overflow-y: auto;
  /*纵向内容超出高度的时候出现滚动条*/
}
</style>