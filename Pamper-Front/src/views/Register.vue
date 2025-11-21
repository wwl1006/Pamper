<script setup>
import axios from 'axios';
import { ElMessage } from 'element-plus';
// import { ca } from 'element-plus/es/locales.mjs';
import { reactive } from 'vue';
import { useRouter } from 'vue-router'

const router = useRouter()
const form = reactive({
  username: "",
  password: "",
  repassword: ""
})
const doRegister = async () => {
  if (!form.username || !form.password) {
    ElMessage.error("用户名或密码不能为空！")
    return
  }
  if (form.password != form.repassword) {
    ElMessage.error("两次密码不一致，请重试！")
    return
  }
  await axios.post('http://localhost:8080/register', {
    username: form.username,
    password: form.password,
    repassword: form.repassword
  }).then(res => {
    if (res.code === 200) {
      ElMessage.success('注册成功，前往登录中...')
      setTimeout(login, 1000)
    } else (ElMessage.error(res.msg))
  }).catch(err => {
    console.log("请求失败", err);
    ElMessage.error("服务器错误！")
  })
}
const login = () => {
  router.push('/login')
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <img class="login-logo" src="../assets/Img/dog.webp" alt="logo">
      <h2>宠伴 - 登录</h2>
      <el-form :model="form" ref="loginForm" class="login-form">
        <el-form-item prop="username">
          <el-input class="login-input" v-model="form.username" placeholder="用户名" prefix-icon="User"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input class="login-input" v-model="form.password" placeholder="密码" prefix-icon="User"></el-input>
        </el-form-item>
        <el-form-item prop="repassword">
          <el-input class="login-input" v-model="form.repassword" placeholder="确认密码" prefix-icon="User"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" @click="doRegister">注册</el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <el-link type="primary" @click="login">返回登录</el-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import "../assets/css/login.css"
</style>