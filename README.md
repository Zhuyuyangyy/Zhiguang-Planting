# Zhiguang Planting

智光种植项目源码，包含前端 Vue/Vite 应用和后端 Spring Boot 服务。

## 目录

- `前端源码/`: 前端应用
- `后端源码/`: 后端服务

## 前端

```bash
cd 前端源码
npm install
npm run dev
```

## 后端

```bash
cd 后端源码
mvn spring-boot:run
```

后端运行所需数据库、Dify、讯飞、MinIO 等敏感配置通过环境变量注入，详见 `后端源码/src/main/resources/application.yml`。
