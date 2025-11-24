import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

// 创建一个 axios 实例
const request = axios.create({
  baseURL: BASE_URL,
  timeout: 5000,
});

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 保证 headers 存在
    config.headers = config.headers || {};
    // 在请求发送之前做一些处理，比如添加 Token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.token = token;
    }
    return config;
  },
  error => {
    // 处理请求错误
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  response => {
    const payload = response.data;
    if (payload?.code === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
    }
    return payload;
  },
  error => {
    // 处理响应错误
    if (error.response) {
      // 服务器返回的状态码不在2xx范围内
      console.error('Error:', error.response.status, error.response.data);
    } else {
      console.error('Error:', error.message);
    }
    return Promise.reject(error);
  }
);

export default request;

// example usage:
  // await request.post('/account/register', {
  //   username: form.username,
  //   password: form.password,
  //   // repassword: form.repassword,
  //   user_type: form.user_type
  // }).then(res => {
  //   if (res.code === 200) {
  //     ElMessage.success('注册成功，前往登录中...')
  //     setTimeout(login, 1000)
  //   } else {
  //     ElMessage.error(res.msg)
  //   }
  // }).catch(err => {
  //   console.log("请求失败", err);
  //   ElMessage.error("服务器错误！")
  // })
