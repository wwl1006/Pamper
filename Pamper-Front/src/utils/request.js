import axios from 'axios';

// 创建一个 axios 实例
const request = axios.create({
  baseURL: 'http://localhost:8080', // 你的 API 基础 URL
  timeout: 5000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json', // 默认 Content-Type
  },
});

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 在请求发送之前做一些处理，比如添加 Token
    const token = localStorage.getItem('token'); // 从本地存储获取Token
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
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
    // 可以在这里对响应数据做一些处理
    return response.data;
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
