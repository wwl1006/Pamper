<script setup>
import { RouterLink, RouterView } from 'vue-router'
import Header from './components/Header.vue';
import Footer from './components/Footer.vue';
import Slidebar from './components/Slidebar.vue';
</script>

<template>
  <router-view></router-view>
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