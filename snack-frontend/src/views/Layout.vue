<template>
  <div class="layout">
    <Header />
    <main class="main-content">
      <router-view />
    </main>
    <Footer />
  </div>
</template>

<script setup>
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { onMounted } from 'vue'
import { useStore } from 'vuex'
import { getCartList } from '@/api/cart'

const store = useStore()

// 如果已登录，获取购物车数量
onMounted(async () => {
  if (store.getters.isLogin) {
    try {
      const res = await getCartList()
      if (res.data && Array.isArray(res.data)) {
        store.dispatch('updateCartCount', res.data.length)
      }
    } catch (error) {
      console.error('获取购物车失败:', error)
    }
  }
})
</script>

<style scoped>
.layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: linear-gradient(to bottom, #f0f9ff 0%, #fff 100%);
}

.main-content {
  flex: 1;
  margin-top: 60px;
  padding-top: 0;
}
</style>

