# 零食销售管理系统

> 基于 Spring Boot 3 + Vue 3 的前后端分离零食在线销售平台

## 项目简介

本系统是一个功能完备的零食在线销售管理平台，采用前后端分离架构开发。系统涵盖了商品展示、购物车、订单管理、用户中心、收藏、评价以及后台管理等核心功能，为零食电商提供了一站式解决方案。

## 在线访问

- **仓库地址**：[https://github.com/H-ZZ888/snack-sales-system](https://github.com/H-ZZ888/snack-sales-system)

## 功能模块

### 用户端

| 模块 | 功能描述 |
|------|---------|
| 用户认证 | 注册、登录，JWT 身份验证 |
| 商品浏览 | 搜索、分类筛选、首页推荐、轮播图广告 |
| 商品详情 | 价格、库存、产地、描述、用户评价列表 |
| 购物车 | 添加商品、修改数量、删除商品、生成订单 |
| 订单处理 | 订单确认、地址选择、模拟支付、取消订单 |
| 用户中心 | 个人信息、订单记录、收货地址、我的收藏 |
| 评价系统 | 已完成订单商品的图文评价 |
| 资讯公告 | 系统公告和食品资讯浏览 |

### 后台管理端

| 模块 | 功能描述 |
|------|---------|
| 数据看板 | 用户/商品/订单/销售额统计可视化 |
| 用户管理 | 注册用户增删查改 |
| 商品管理 | 商品信息管理、上下架、库存价格维护 |
| 分类管理 | 商品分类增删改查 |
| 订单管理 | 全部订单查看、状态修改、发货确认 |
| 公告管理 | 系统公告发布、修改、删除 |
| 文章管理 | 资讯文章发布、修改、删除 |
| 轮播图管理 | 首页轮播图内容管理 |
| 评价管理 | 用户评价审核与删除 |

## 技术栈

### 后端

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.0 | 核心框架 |
| MyBatis-Plus | 3.5.9 | ORM 框架 |
| MySQL | 8.0+ | 数据库 |   | MySQL | 8.0  | 数据库 || MySQL | 8.0  | 数据库 |   | MySQL | 8.0  | 数据库 || MySQL | 8.0  | 数据库 |   | MySQL | 8.0  | 数据库 || MySQL | 8.0  | 数据库 |   | MySQL | 8.0  | 数据库 |
| JWT | 0.12.6 | 身份认证 |
| Lombok | - | 代码简化 |
| Hutool | 5.8.25 | 工具库 |
| Maven | 3.6+ | 构建工具 |   | Maven | 3.6  | 构建工具 || Maven | 3.6  | 构建工具 |   | Maven | 3.6  | 构建工具 || Maven | 3.6  | 构建工具 |   | Maven | 3.6  | 构建工具 || Maven | 3.6  | 构建工具 |   | Maven | 3.6  | 构建工具 |

### 前端

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.3.4 | 核心框架 |
| Vite | 4.4.9 | 构建工具 |
| Element Plus | 2.3.14 | UI 组件库 |
| Vue Router | 4.2.4 | 路由管理 |
| Vuex | 4.1.0 | 状态管理 |
| Axios | 1.5.0 | HTTP 请求 |
| ECharts | 5.4.3 | 数据可视化 |

## 项目结构

```
.
├── snack-backend/                 # Spring Boot 后端源码
│   ├── src/main/java/             # Java 源代码
│   ├── src/main/resources/        # 配置文件
│   ├── sql/                       # 数据库初始化脚本
│   └── pom.xml                    # Maven 配置
├── snack-frontend/                # Vue 3 前端源码
│   ├── src/api/                   # 接口请求封装
│   ├── src/views/                 # 页面组件
│   ├── src/components/            # 公共组件
│   ├── src/store/                 # Vuex 状态管理
│   ├── src/router/                # 路由配置
│   └── package.json               # 依赖配置
└── release/                       # 打包产物
    └── snack-sales-system-1.0.0.jar
```

## 环境要求

- **Java**: JDK 17 
- **MySQL**: 8.0+ 
- **Maven**: 3.6+ 
- **Node.js**: 16+ 
- **npm**: 8+ 

## 快速开始

### 1. 克隆项目

```bash   ”“bash   “bash”;“bash
git clone https://github.com/H-ZZ888/snack-sales-system.git
cd snack-sales-system
```

### 2. 数据库配置

```sql   ”“sql
CREATE DATABASE snack_sales_system CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;创建snack_sales_system数据库字符集utf8mb4整理utf8mb4_general_ci
```

导入数据库脚本：

```bash   ”“bash   “bash”;“bash```bash   ”“bash   “bash”;“bash
mysql -u root -p snack_sales_system < snack-backend/sql/snack_sales_system.sql-u root -p snack_sales_system <
```

### 3. 后端启动   “bash”;“bash

```bash   ”“bash   “bash”;“bash
cd snack-backend

# 修改数据库配置（如需）
# 编辑 src/main/resources/application.yml
# 默认配置: localhost:3306, root/root

# 方式一：Maven 直接运行
mvn spring-boot:run   运行mvn spring-boot:

# 方式二：使用打包好的 JAR
java -jar ../release/snack-sales-system-1.0.0.jarJava -jar ../release/snack-sales-system-1.0.0.jar
```

后端接口地址：`http://localhost:8080/api`

### 4. 前端启动

```bash   ”“bash   “bash”;“bash
cd snack-frontend
npm install
npm run dev   NPM运行dev
```

## 部署指南

### 前端部署 (Cloudflare Pages / Vercel / Netlify)

1. 连接本 GitHub 仓库
2. 构建设置：
   - **框架预设**：Vite
   - **根目录**：`snack-frontend`
   - **构建命令**：`npm run build`
   - **输出目录**：`dist`
3. 环境变量：
   - `VITE_API_BASE` = `https://你的后端域名/api`

### 后端部署 (云服务器)

```bash   ”“bash   “bash”;“bash
# 1. 上传 JAR 到服务器
scp release/snack-sales-system-1.0.0.jar user@server:/opt/snack/SCP release/snack-sales-system-1.0.0.jar user@server:/opt/snack/

# 2. 启动服务
cd /opt/snack   cd / opt /零食
nohup java -jar snack-sales-system-1.0.0.jar \Java -jar零食-销售-系统-1.0.0.jar \
  --spring.datasource.url=jdbc:mysql://localhost:3306/snack_sales_system \——spring.datasource。url = jdbc: mysql: / / localhost: 3306 / snack_sales_system \
  --spring.datasource.username=root \——spring.datasource。用户名=根\
  --spring.datasource.password=你的密码 \
  > app.log 2>&1 &   app.log 2>&1 &；

# 3. Nginx 反向代理配置
# server {
#     listen 443 ssl;#监听443 ssl；
#     server_name api.yourdomain.com;# server_name api.yourdomain.com；
#     location / {
#         proxy_pass http://127.0.0.1:8080;# proxy_pass http://127.0.0.1:8080；
#     }
# }
```

## 默认账号

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | admin | admin123 |
| 测试用户 | 请自行注册 | - |

## 许可证

本项目仅供学习交流使用。

---

**如有问题请在 [Issues](https://github.com/H-ZZ888/snack-sales-system/issues) 中反馈。**
