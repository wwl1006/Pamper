<script setup>
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { reactive } from 'vue';
import { useRouter } from 'vue-router'

const router = useRouter()

const form = reactive({
  username: "",
  password: ""
})
const login = async () => {
  console.log('登录信息', form.username, ':', form.password);
  if (!form.username || !form.password) {
    ElMessage.error("用户名或密码不能为空！")
    return
  }
  await axios.post('http://localhost:8080/login', {
    username: form.username,
    password: form.password
  }).then(res => {
    if (res.code === 200) {
      console.log("登录成功", res.data);
      router.push('/')
    } else {
      ElMessage.error(res.msg)
    }
  }).catch(err => {
    console.log("请求失败", err);
    ElMessage.error("服务器错误！")
  })
}
const register = () => {
  console.log('跳转注册页面');
  router.push('/register')
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <img class="login-logo" src="../assets/Img/dog.webp" alt="logo">
      <h2>宠伴 - 登录</h2>
      <!-- model="form" 就是告诉表单“我的数据叫 form”
      ref="loginForm" 就是给表单取个名字，以后 JS 里能找到它 -->
      <el-form :model="form" ref="loginForm" class="login-form">
        <el-form-item prop="username">
          <el-input class="login-input" v-model="form.username" placeholder="用户名" prefix-icon="User"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input class="login-input" type="password" v-model="form.password" placeholder="密码" prefix-icon="Lock" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" @click="login">登录</el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <span>还没有账号？</span>
        <el-link type="primary" @click="register">注册</el-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import "../assets/css/login.css"
</style>