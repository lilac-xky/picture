# 郄汐云图库 (Qiexi Cloud Gallery)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Tencent Cloud](https://img.shields.io/badge/Storage-Tencent%20COS-orange.svg)](https://cloud.tencent.com/product/cos)

**郄汐云图库** 是一款基于现代化技术栈构建的私有化/团队级图片管理平台。支持高效的图片上传、分类管理、细粒度的权限控制以及自动化的 API 文档集成，旨在为用户提供流畅的云端图片存储与管理体验。

---

## 项目特性

### 已实现功能
- **完备的鉴权系统**：支持用户注册、登录。内置权限校验，确保数据安全。
- **管理驾驶舱**：管理员可全局管理用户状态、审核及清理图片资源。
- **多媒介上传**：集成腾讯云 COS 对象存储，支持图片快速上传与稳定访问。
- **细粒度权限控制**：实现“谁上传谁管理”原则，仅允许原作者及管理员对图片进行编辑或删除。
- **自动化 API 集成**：深度集成 Apifox/OpenAPI，支持接口定义一键同步。

### 核心亮点
- **前后端解耦**：基于 RESTful API 设计。
- **配置驱动**：灵活的环境变量与 YAML 配置。
- **代码生成**：前端 API 层通过 Swagger/OpenAPI 规范自动生成，减少手动编写成本。

---

## 技术栈

### 后端
- **核心框架**：Spring Boot 3.x
- **对象存储**：腾讯云 COS (Cloud Object Storage)
- **数据库**：MySQL 8.0+
- **持久层**：MyBatis Plus
- **API 文档**：apifox

### 前端
- **核心框架**：vue3
- **语言**：TypeScript
- **网络请求**：Axios / OpenAPI Request
- **UI 框架**：Ant Design Vue
- **构建工具**：Vite
- **状态管理**：Pinia
- **路由管理**：Vue Router
- **工具链**：Apifox

---

## 快速开始

### 前置要求
- Java 17+
- Node.js 18+
- MySQL 8.0
- 腾讯云 COS 账号及存储桶配置

### 后端配置
1. 进入后端目录，在 `src/main/resources` 下创建 `application-dev.yml`。
2. 填充以下核心配置：
   ```yaml
   picture:
     datasource:
       url: jdbc:mysql://localhost:3306/your_data
       username: root
       password: your_password
    
       # 腾讯云 COS 配置
       cos:
         client:
         	host: YOUR_HOST
           secretId: YOUR_SECRET_ID
           secretKey: YOUR_SECRET_KEY
           region: YOUR_REGION
           bucket: YOUR_BUCKET_NAME
   ```

> 确保 `.gitignore` 中明确加入 `application-dev.yml`，防止秘钥泄露到公共仓库。

### 前端 API 同步

项目使用自动生成机制处理接口调用：
1. 修改根目录下的 `openapi.config.ts`：
   ```typescript
   // 示例配置
   export default {
     schemaPath: 'http://localhost:8080/v3/api-docs', // 你的 Swagger/OpenAPI 地址
     serversPath: './src/api', // 生成代码的存放路径
   };
   ```
   
   **不要手动修改 `src/api` 下的文件**，因为下次自动生成会覆盖。如果有自定义需求，应该在 Service 层做封装。
2. 执行生成命令：
   ```bash
   npm run openapi
   ```

---

## 目录结构预览

```text
.
├── backend   # 后端项目根目录
│   ├── src/main/java       # 业务逻辑
│   └── src/main/resources  # 配置文件
├── frontend  # 前端项目根目录
│   ├── src/api             # 自动生成的接口代码 (Generated)
│   ├── src/views           # 页面组件
│   └── openapi.config.ts   # API 自动生成配置
└── README.md
```

---

## 路线图 (Roadmap)

项目正在持续迭代中，以下是后续开发计划：

- [ ] 协同开发**：支持多人协作管理同一图库。
- [ ] **空间模块**：引入个人空间与公共空间逻辑隔离。
- [ ] **图库分析**：图片热度、存储占比、流量统计可视化。
- [ ] 团队空间**：针对企业级场景的团队成员权限管理。
- [ ] **DDD 重构**：采用领域驱动设计优化核心业务架构，提升系统扩展性。

