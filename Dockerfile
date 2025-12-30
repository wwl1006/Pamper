# 使用多阶段构建
# 第一阶段：构建Vue.js前端
FROM node:18-alpine AS frontend-build

WORKDIR /app

# 复制前端文件
COPY Pamper-Front/ ./frontend/

# 安装前端依赖
WORKDIR /app/frontend
RUN npm install --production

# 构建前端应用
RUN npm run build


# 第二阶段：构建后端应用
FROM maven:3.8.6-openjdk:8-slim AS backend-build

WORKDIR /app

# 复制pom.xml并下载依赖
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src/ ./src/

# 构建项目
RUN mvn clean package -DskipTests


# 最终阶段：运行应用程序
FROM openjdk:8-jre-slim

# 安装Nginx来提供前端静态文件
RUN apt-get update && apt-get install -y nginx && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# 从构建阶段复制打包好的jar文件
COPY --from=backend-build /app/target/Pamper-0.0.1-SNAPSHOT.jar app.jar

# 从前端构建阶段复制构建好的前端文件
COPY --from=frontend-build /app/frontend/dist/ /var/www/html/

# 复制Nginx配置
RUN rm -rf /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/nginx.conf

# 创建上传目录并设置权限
RUN mkdir -p /app/activities/covers \
    && mkdir -p /app/adoption/images \
    && mkdir -p /app/avatars \
    && mkdir -p /app/pets/avatars \
    && mkdir -p /app/posts/images \
    && mkdir -p /app/posts/videos \
    && chmod -R 755 /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]