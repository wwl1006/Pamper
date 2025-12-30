# 基于nginx镜像构建
FROM nginx:alpine

# 将构建好的前端文件复制到nginx的html目录
COPY dist/ /usr/share/nginx/html/

# 复制nginx配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露端口
EXPOSE 80

# 启动nginx
CMD ["nginx", "-g", "daemon off;"]