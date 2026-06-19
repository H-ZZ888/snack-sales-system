<template>
  <el-container class="admin-layout">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '240px'" class="admin-aside">
      <div class="logo-container">
        <img src="/logo/logo.png" alt="Logo" class="logo-img" v-show="!isCollapse" />
        <img src="/logo/logo.png" alt="Logo" class="logo-img-small" v-show="isCollapse" />
        <span class="logo-text" v-show="!isCollapse">零食销售管理系统</span>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :unique-opened="true"
        router
        class="admin-menu"
      >
        <el-menu-item index="/admin/banners">
          <el-icon><Picture /></el-icon>
          <template #title>轮播图管理</template>
        </el-menu-item>
        
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>数据统计</template>
        </el-menu-item>
        
        <el-menu-item index="/admin/products">
          <el-icon><Goods /></el-icon>
          <template #title>商品管理</template>
        </el-menu-item>
        
        <el-menu-item index="/admin/categories">
          <el-icon><Menu /></el-icon>
          <template #title>分类管理</template>
        </el-menu-item>
        
        <el-menu-item index="/admin/orders">
          <el-icon><ShoppingCart /></el-icon>
          <template #title>订单管理</template>
        </el-menu-item>
        
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        
        <el-menu-item index="/admin/reviews">
          <el-icon><ChatDotRound /></el-icon>
          <template #title>评价管理</template>
        </el-menu-item>
        
        <el-menu-item index="/admin/articles">
          <el-icon><Document /></el-icon>
          <template #title>资讯管理</template>
        </el-menu-item>
        
        <el-menu-item index="/admin/notices">
          <el-icon><Bell /></el-icon>
          <template #title>公告管理</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container class="main-container">
      <!-- 顶部导航栏 -->
      <el-header class="admin-header">
        <div class="header-left">
          <el-icon class="collapse-icon" @click="toggleCollapse">
            <Expand v-if="isCollapse" />
            <Fold v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="breadcrumbTitle">{{ breadcrumbTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-button text @click="goToUserSite">
            <el-icon><Monitor /></el-icon>
            <span>前台首页</span>
          </el-button>
          
          <el-dropdown @command="handleCommand">
            <div class="admin-info">
              <el-avatar :size="36" :src="getAdminAvatar()">
                {{ (adminInfo.username)?.[0] }}
              </el-avatar>
              <span class="admin-name">{{ adminInfo.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区域 -->
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  DataAnalysis, Goods, Menu, ShoppingCart, User, ChatDotRound,
  Document, Bell, Picture, Expand, Fold, Monitor, ArrowDown, SwitchButton
} from '@element-plus/icons-vue'
import { setLoggingOut } from '@/utils/request'

const router = useRouter()
const route = useRoute()
const store = useStore()

const isCollapse = ref(false)
const adminInfo = computed(() => store.state.admin.adminInfo)
const activeMenu = computed(() => route.path)

const breadcrumbTitle = computed(() => {
  return route.meta.title || ''
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const getAdminAvatar = () => {
  if (!adminInfo.value.avatar) return ''
  if (adminInfo.value.avatar.startsWith('http')) return adminInfo.value.avatar
  return `http://localhost:8080/api${adminInfo.value.avatar}`
}

const goToUserSite = () => {
  window.open('/', '_blank')
}

const handleCommand = async (command) => {
  if (command === 'profile') {
    router.push('/admin/profile')
  } else if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      
      // 设置退出登录标志，避免显示"登录已过期"提示
      setLoggingOut(true)
      store.dispatch('admin/logout')
      ElMessage.success('退出登录成功')
      router.push('/admin/login')
    } catch (error) {
      // 用户取消
    }
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  overflow: hidden;
}

.admin-aside {
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  transition: width 0.3s;
  overflow-x: hidden;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.logo-container {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
  background: rgba(255, 255, 255, 0.1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-img {
  width: 45px;
  height: 45px;
  object-fit: contain;
  margin-right: 10px;
}

.logo-img-small {
  width: 32px;
  height: 32px;
  object-fit: contain;
}

.logo-text {
  font-size: 16px;
  font-weight: 600;
  color: white;
  white-space: nowrap;
}

.admin-menu {
  border: none;
  background: transparent;
}

.admin-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.85);
  transition: all 0.3s;
}

.admin-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.15);
  color: white;
}

.admin-menu :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  font-weight: 600;
}

.main-container {
  background: #f5f7fa;
}

.admin-header {
  background: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.collapse-icon {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
  transition: all 0.3s;
}

.collapse-icon:hover {
  color: #667eea;
  transform: scale(1.1);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 8px;
  transition: all 0.3s;
}

.admin-info:hover {
  background: #f5f7fa;
}

.admin-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.admin-main {
  padding: 20px;
  overflow-y: auto;
}

@media (max-width: 768px) {
  .admin-aside {
    width: 64px !important;
  }
  
  .logo-text {
    display: none;
  }
}
</style>
