# 零食销售管理系统

> 基于 Spring Boot 3 + Vue 3 的前后端分离零食在线销售平台

- **前端在线地址**：[https://h-zz888.github.io/snack-sales-system/](https://h-zz888.github.io/snack-sales-system/)
- **GitHub 仓库**：[https://github.com/H-ZZ888/snack-sales-system](https://github.com/H-ZZ888/snack-sales-system)
- **联系方式**：foxth818@gmail.com（如需获取完整项目源码或部署帮助，请通过邮箱联系）

## 在线访问

| 项目 | 状态 | 说明 |
|------|------|------|
| GitHub 仓库 | ✅ 已完成 | https://github.com/H-ZZ888/snack-sales-system |
| 源代码访问 | ✅ 已完成 | 其他人可以克隆仓库查看和运行源码 |
| JAR 下载包 | ✅ 已完成 | 可直接从仓库 `release/` 目录下载 `snack-sales-system-1.0.0.jar` |
| 前端在线站点 | ✅ 已完成 | https://h-zz888.github.io/snack-sales-system/ （界面可浏览） |
| 后端在线服务 | 🚧 待部署 | 需要云服务器、数据库和域名/API 地址 |

> 当前别人已经可以访问 GitHub 仓库、下载源码和 JAR 包、浏览前端在线站点；完整后端服务还需要继续部署。

## 项目简介

本系统是一个功能完备的零食在线销售管理平台，采用前后端分离架构开发。系统涵盖商品展示、购物车、订单管理、用户中心、收藏、评价以及后台管理等核心功能，为零食电商提供一站式解决方案。

## 功能模块

### 用户端

| 模块 | 功能描述 |
|------|---------|
| 用户认证 | 注册、登录，JWT 身份验证 |
| 商品浏览 | 搜索、分类筛选、首页推荐、轮播图广告 |
| 商品详情 | 查看价格、库存、产地、描述、用户评价 |
| 购物车 | 添加商品、修改数量、删除商品、生成订单 |
| 订单处理 | 订单确认、地址选择、模拟支付、取消订单 |
| 用户中心 | 个人信息、订单记录、收货地址、我的收藏 |
| 评价系统 | 对已完成订单商品进行图文评价 |
| 资讯公告 | 浏览系统公告和食品资讯 |

### 后台管理端

| 模块 | 功能描述 |
|------|---------|
| 数据看板 | 用户、商品、订单、销售额统计可视化 |
| 用户管理 | 注册用户增删查改 |
| 商品管理 | 商品信息维护、上下架、库存价格管理 |
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
| MySQL | 8.0+ | 数据库 |
| JWT | 0.12.6 | 身份认证 |
| Lombok | - | 代码简化 |
| Hutool | 5.8.25 | 工具库 |
| Maven | 3.6+ | 构建工具 |

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

```text
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
└── release/                       # JAR 可执行包
```

## 环境要求

- Java: JDK 17+
- MySQL: 8.0+
- Maven: 3.6+
- Node.js: 16+
- npm: 8+

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/H-ZZ888/snack-sales-system.git
cd snack-sales-system
```

### 2. 数据库配置

创建数据库：

```sql
CREATE DATABASE snack_sales_system CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

导入数据库脚本：

```bash
mysql -u root -p snack_sales_system < snack-backend/sql/snack_sales_system.sql
```

### 3. 启动后端

```bash
cd snack-backend
mvn spring-boot:run
```

默认后端接口地址：`http://localhost:8080/api`

如需修改数据库连接，请编辑 `snack-backend/src/main/resources/application.yml`。

### 4. 启动前端

```bash
cd snack-frontend
npm install
npm run dev
```

默认前端访问地址：`http://localhost:5172`

## 部署指南

### 前端部署（Cloudflare Pages / Vercel / Netlify）

1. 连接 GitHub 仓库：`https://github.com/H-ZZ888/snack-sales-system`
2. 设置构建参数：
   - 框架预设：Vite
   - 根目录：`snack-frontend`
   - 构建命令：`npm run build`
   - 输出目录：`dist`
3. 设置环境变量：
   - `VITE_API_BASE=https://你的后端域名/api`

### 后端部署（云服务器）

```bash
cd /opt/snack
java -jar snack-sales-system-1.0.0.jar \
  --spring.datasource.url=jdbc:mysql://localhost:3306/snack_sales_system \
  --spring.datasource.username=root \
  --spring.datasource.password=你的数据库密码
```

建议使用 Nginx 将后端代理到 HTTPS 域名，例如 `https://api.yourdomain.com`。

## 默认账号

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | admin | admin123 |
| 测试用户 | 请自行注册 | - |

## 说明

- 仓库不直接提交 JAR 文件，避免 Git 仓库体积过大。
- JAR 包建议通过 GitHub Releases 或 GitHub Actions 自动构建发布。
- 本项目仅供学习交流使用。

## 反馈

如有问题请在 Issues 中反馈： https://github.com/H-ZZ888/snack-sales-system/issues
