<template>
  <!-- 头部导航栏 -->
  <div class="header-wrapper">
    <div class="container">
      <!-- logo区域 -->
      <router-link to="/" class="logo-section">
        <img src="/logo/logo.png" alt="零食销售管理系统" class="logo-img">
        <span class="site-title">零食销售管理系统</span>
      </router-link>

      <!-- 导航菜单 -->
      <el-menu
          mode="horizontal"
          :default-active="activeMenu"
          router
          class="nav-menu"
      >
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/products">零食商品</el-menu-item>
        <el-menu-item index="/articles">商品资讯</el-menu-item>
        <el-menu-item index="/notices">系统公告</el-menu-item>
      </el-menu>

      <!-- 右侧功能区 -->
      <div class="header-right">
        <el-input
            v-model="searchKeyword"
            placeholder="搜索商品"
            class="search-input"
            @keyup.enter="handleSearch"
        >
          <template #suffix>
            <el-icon class="search-icon" @click="handleSearch">
              <Search />
            </el-icon>
          </template>
        </el-input>

        <router-link to="/cart" class="cart-link" v-if="isLogin">
          <el-badge :value="cartCount" :hidden="cartCount === 0">
            <el-icon :size="24"><ShoppingCart /></el-icon>
          </el-badge>
        </router-link>

        <div v-if="isLogin" class="user-info">
          <el-dropdown @command="handleCommand">
            <div class="user-dropdown">
              <el-avatar
                  :size="36"
                  :src="getUserAvatar()"
                  class="user-avatar"
              >
                {{ (userInfo.nickname || userInfo.username)?.[0] }}
              </el-avatar>
              <span class="user-name">
                {{ userInfo.nickname || userInfo.username }}
                <el-icon><ArrowDown /></el-icon>
              </span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="center">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="orders">
                  <el-icon><Document /></el-icon>
                  我的订单
                </el-dropdown-item>
                <el-dropdown-item command="favorites">
                  <el-icon><Star /></el-icon>
                  我的收藏
                </el-dropdown-item>
                <el-dropdown-item command="addresses">
                  <el-icon><Location /></el-icon>
                  收货地址
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>

        <div v-else class="auth-links">
          <router-link to="/login">登录</router-link>
          <span class="divider">|</span>
          <router-link to="/register">注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { Search, ShoppingCart, ArrowDown, User, Document, Star, Location, SwitchButton } from '@element-plus/icons-vue'
import { setLoggingOut } from '@/utils/request'
import { getCartList } from '@/api/cart'

const router = useRouter()
const route = useRoute()
const store = useStore()

const searchKeyword = ref('')

const isLogin = computed(() => store.getters.isLogin)
const userInfo = computed(() => store.getters.userInfo)
const cartCount = computed(() => store.getters.cartCount)

const activeMenu = computed(() => route.path)

const getUserAvatar = () => {
  if (!userInfo.value.avatar) return ''
  if (userInfo.value.avatar.startsWith('http')) return userInfo.value.avatar
  return getImageUrl(userInfo.value.avatar)
}

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      path: '/products',
      query: { keyword: searchKeyword.value }
    })
  }
}

const handleCommand = (command) => {
  if (command === 'logout') {
    // 设置退出登录标志，避免显示"登录已过期"提示
    setLoggingOut(true)
    store.dispatch('logout')
    ElMessage.success('退出登录成功')
    router.push('/')
  } else {
    router.push(`/user/${command}`)
  }
}

// 加载购物车数量
const loadCartCount = async () => {
  if (!isLogin.value) {
    store.dispatch('updateCartCount', 0)
    return
  }
  
  try {
    const res = await getCartList()
    const cartData = Array.isArray(res.data) ? res.data : []
    // 修改为计算商品种类数量（即数组长度），而不是商品总件数
    const totalCount = cartData.length
    store.dispatch('updateCartCount', totalCount)
  } catch (error) {
    console.error('加载购物车数量失败:', error)
    // 不显示错误消息，静默失败
  }
}

onMounted(() => {
  // 页面加载时如果已登录，加载购物车数量
  loadCartCount()
})
</script>

<style scoped>
/* 头部样式 */
.header-wrapper {
  height: 60px;
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 50%, #ff9ff3 100%);
  box-shadow: 0 2px 12px rgba(255, 107, 107, 0.2);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.container {
  display: flex;
  align-items: center;
  width: 100%;
  max-width: 1400px;
  padding: 0 20px;
  box-sizing: border-box;
}

/* logo区域样式 - 移除所有特效 */
.logo-section {
  display: flex;
  align-items: center;
  margin-right: 40px;
  flex-shrink: 0;
  min-width: 320px;
  text-decoration: none;
  cursor: pointer;
}

.logo-img {
  width: auto;
  height: 60px;
}

.site-title {
  font-size: 20px;
  font-family: 微软雅黑, sans-serif;
  color: #fff;
  font-weight: bolder;
  padding-left: 6px;
}

/* 导航菜单样式 */
.nav-menu {
  flex: 1;
  border: none;
  background: transparent;
  min-width: 0;
  display: flex;
  justify-content: center;
}

.nav-menu :deep(.el-menu-item) {
  color: #fff;
  font-size: 15px;
  font-weight: 500;
  border-bottom: none;
  padding: 0 18px;
  transition: all 0.3s ease;
  transform-origin: center;
  position: relative;
}

.nav-menu :deep(.el-menu-item:hover) {
  color: #fff;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 6px;
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
}

.nav-menu :deep(.el-menu-item.is-active) {
  color: #fff;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 6px;
  border-bottom: none;
  transform: translateY(-2px);
  font-weight: 600;
}

/* 右侧功能区样式 */
.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-shrink: 0;
}

.search-input {
  width: 200px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.85);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}

.search-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.12);
}

.search-icon {
  cursor: pointer;
  color: #2c3e50;
  transition: transform 0.3s;
}

.search-icon:hover {
  transform: scale(1.1);
}

.cart-link {
  display: flex;
  align-items: center;
  color: #fff;
  padding: 6px 10px;
  border-radius: 6px;
  transition: all 0.3s;
}

.cart-link:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: scale(1.1);
}

.user-info {
  cursor: pointer;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px;
  border-radius: 20px;
  transition: all 0.3s;
}

.user-dropdown:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.user-avatar {
  border: 2px solid rgba(255, 255, 255, 0.5);
  transition: transform 0.3s;
}

.user-dropdown:hover .user-avatar {
  transform: scale(1.1);
}

.user-name {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #fff;
  font-size: 14px;
  font-weight: 500;
}

.auth-links {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
}

.auth-links a {
  color: #fff;
  padding: 6px 12px;
  border-radius: 6px;
  transition: all 0.3s;
}

.auth-links a:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-2px);
}

.divider {
  color: rgba(255, 255, 255, 0.5);
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .logo-section {
    min-width: 280px;
  }
  .search-input {
    width: 160px;
  }
}

@media (max-width: 992px) {
  .site-title {
    font-size: 18px;
  }
  .nav-menu :deep(.el-menu-item) {
    padding: 0 12px;
    font-size: 14px;
  }
}
</style>
