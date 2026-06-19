<template>
  <div class="home">
    <!-- 轮播图区域 -->
    <div class="banner-section">
      <div class="banner-container">
        <el-carousel 
          :interval="4000" 
          arrow="hover"
          height="420px"
          v-if="carouselImages.length > 0"
        >
          <el-carousel-item v-for="item in carouselImages" :key="item.id">
            <div class="banner-item">
              <img :src="getImageUrl(item.image)" :alt="item.title" />
              <div class="banner-content" v-if="item.title">
                <h2 class="banner-title">{{ item.title }}</h2>
                <p class="banner-desc" v-if="item.subtitle">{{ item.subtitle }}</p>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="content-wrapper">
      <!-- 分类导航 -->
      <div class="category-section" v-if="categories.length > 0">
        <h2 class="section-title">商品分类</h2>
        <div class="category-grid">
          <div
            v-for="category in categories"
            :key="category.id"
            class="category-card"
            @click="goToProducts(category.id)"
          >
            <div class="category-icon">
              <el-icon :size="32">
                <component :is="getCategoryIcon(category.name)" />
              </el-icon>
            </div>
            
            <div class="category-name">{{ category.name }}</div>
          </div>
        </div>
      </div>

      <!-- 热门商品 -->
      <div class="product-section" v-if="products.length > 0">
        <div class="section-header">
          <h2 class="section-title">精选商品</h2>
          <router-link to="/products" class="more-link">查看更多 →</router-link>
        </div>
        <div class="product-grid">
          <div
            v-for="product in products"
            :key="product.id"
            class="product-card"
            @click="goToDetail(product.id)"
          >
            <div class="product-image">
              <img :src="getImageUrl(product.image)" :alt="product.name" />
              <div class="cart-overlay">
                <el-icon class="cart-icon"><ShoppingCart /></el-icon>
              </div>
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ product.name }}</h3>
              <div class="product-footer">
                <span class="product-price">¥{{ product.price }}<span class="price-unit" v-if="product.unit">/{{ product.unit }}</span></span>
                <span class="product-sales" v-if="product.sales">已售{{ product.sales }}{{ product.unit || '件' }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 热销商品 -->
      <div class="hot-product-section" v-if="hotProducts.length > 0">
        <div class="section-header">
          <h2 class="section-title">热销商品</h2>
          <router-link to="/products" class="more-link">查看更多 →</router-link>
        </div>
        <div class="product-grid">
          <div
            v-for="product in hotProducts"
            :key="product.id"
            class="product-card hot-card"
            @click="goToDetail(product.id)"
          >
            <div class="hot-badge">热销</div>
            <div class="product-image">
              <img :src="getImageUrl(product.image)" :alt="product.name" />
              <div class="cart-overlay">
                <el-icon class="cart-icon"><ShoppingCart /></el-icon>
              </div>
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ product.name }}</h3>
              <div class="product-footer">
                <span class="product-price">¥{{ product.price }}<span class="price-unit" v-if="product.unit">/{{ product.unit }}</span></span>
                <span class="product-sales hot-sales" v-if="product.sales">
                  <el-icon><TrendCharts /></el-icon>
                  已售{{ product.sales }}{{ product.unit || '件' }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 最新资讯 -->
      <div class="article-section" v-if="articles.length > 0">
        <div class="section-header">
          <h2 class="section-title">最新资讯</h2>
          <router-link to="/articles" class="more-link">查看更多 →</router-link>
        </div>
        <div class="article-grid">
          <div
            v-for="article in articles"
            :key="article.id"
            class="article-card"
            @click="goToArticle(article.id)"
          >
            <div class="article-image">
              <img :src="getImageUrl(article.image)" :alt="article.title" />
              <div class="view-overlay">
                <el-icon class="view-icon"><View /></el-icon>
                <span class="view-text">点击查看</span>
              </div>
            </div>
            <div class="article-content">
              <h3 class="article-title">{{ article.title }}</h3>
              <p class="article-time">{{ article.createTime }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ShoppingCart, Fries, Sugar, Burger, Chicken, IceDrink, ForkSpoon, TrendCharts, View } from '@element-plus/icons-vue'
import { getProductList } from '@/api/product'
import { getCategoryList } from '@/api/category'
import { getArticleList } from '@/api/article'
import { getBannerList } from '@/api/banner'

const router = useRouter()

const loading = ref(false)
const products = ref([])
const hotProducts = ref([])
const categories = ref([])
const articles = ref([])
const carouselImages = ref([])

// 根据分类名称获取对应的图标组件
const getCategoryIcon = (categoryName) => {
  const iconMap = {
    '膨化食品': Fries,
    '糖果巧克力': Sugar,
    '饼干糕点': Burger,
    '肉类零食': Chicken,
    '饮料冲调': IceDrink,
    '坚果炒货': ForkSpoon
  }
  return iconMap[categoryName] || ShoppingBag
}

const getImageUrl = (path) => {
  if (!path) return '/placeholder.png'
  if (path.startsWith('http')) return path
  return `http://localhost:8080/api${path}`
}

const goToProducts = (categoryId) => {
  if (categoryId) {
    router.push({ path: '/products', query: { categoryId } })
  } else {
    router.push('/products')
  }
}

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

const goToArticle = (id) => {
  router.push(`/article/${id}`)
}

const fetchData = async () => {
  loading.value = true
  try {
    const bannerRes = await getBannerList()
    carouselImages.value = bannerRes.data || []
    
    const categoryRes = await getCategoryList()
    categories.value = categoryRes.data || []
    
    // 精选商品：只显示9个
    const productRes = await getProductList({ pageNum: 1, pageSize: 9 })
    products.value = productRes.data?.records || []
    
    // 获取热销商品（按销量排序）：只显示9个
    const hotProductRes = await getProductList({ pageNum: 1, pageSize: 9, sortBy: 'sales' })
    hotProducts.value = hotProductRes.data?.records || []
    
    const articleRes = await getArticleList({ pageNum: 1, pageSize: 3 })
    articles.value = articleRes.data?.records || []
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>

.banner-section {
  background: #f5f5f5;
  padding: 25px 0 50px;
  margin-bottom: 30px;
}

.banner-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  border-radius: 0;
  overflow: hidden;
  box-shadow: none;
  height: 420px;
}

.banner-container :deep(.el-carousel),
.banner-container :deep(.el-carousel__container) {
  height: 420px !important;
}

.banner-item {
  width: 100%;
  height: 420px;
  overflow: hidden;
  position: relative;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-item img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  transition: transform 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.banner-item:hover img {
  transform: scale(1.08);
}

.banner-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 30px 40px;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7) 0%, rgba(0, 0, 0, 0.4) 50%, transparent 100%);
  color: white;
  transform: translateY(20px);
  opacity: 0;
  transition: all 0.4s ease;
}

.banner-item:hover .banner-content {
  transform: translateY(0);
  opacity: 1;
}

.banner-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 10px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.banner-desc {
  font-size: 16px;
  opacity: 0.95;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
}

.banner-container :deep(.el-carousel__arrow) {
  width: 55px;
  height: 55px;
  background: rgba(255, 255, 255, 0.9);
  color: #ff6b6b;
  font-size: 22px;
  border-radius: 50%;
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(10px);
}

.banner-container :deep(.el-carousel__arrow:hover) {
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  color: white;
  transform: scale(1.2);
  box-shadow: 0 6px 25px rgba(255, 107, 107, 0.6);
}

.banner-container :deep(.el-carousel__arrow--left) {
  left: 25px;
}

.banner-container :deep(.el-carousel__arrow--right) {
  right: 25px;
}

.banner-container :deep(.el-carousel__indicators) {
  bottom: 20px;
}

.banner-container :deep(.el-carousel__indicator) {
  padding: 6px 5px;
}

.banner-container :deep(.el-carousel__button) {
  width: 40px;
  height: 6px;
  border-radius: 3px;
  background: rgba(255, 255, 255, 0.5);
  transition: all 0.3s ease;
  backdrop-filter: blur(5px);
}

.banner-container :deep(.el-carousel__indicator:hover .el-carousel__button) {
  background: rgba(255, 255, 255, 0.8);
  width: 50px;
  transform: translateY(-2px);
}

.banner-container :deep(.is-active .el-carousel__button) {
  width: 60px;
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.6);
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px 40px;
}

.section-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 25px;
  position: relative;
  padding-left: 15px;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 5px;
  height: 24px;
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  border-radius: 3px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.more-link {
  color: #ff6b6b;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s;
}

.more-link:hover {
  color: #feca57;
  transform: translateX(5px);
}

.category-section {
  margin-bottom: 60px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 20px;
}

.category-card {
  background: white;
  padding: 30px 20px;
  border-radius: 16px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 2px solid transparent;
  position: relative;
  overflow: hidden;
}

.category-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.category-card:hover::before {
  transform: scaleX(1);
}

.category-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 28px rgba(255, 107, 107, 0.25);
  border-color: rgba(255, 107, 107, 0.2);
}

.category-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #fff5f5 0%, #ffe6e6 100%);
  border-radius: 50%;
  transition: all 0.3s ease;
}

.category-card:hover .category-icon {
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  transform: scale(1.1) rotate(5deg);
}

.category-icon .el-icon {
  color: #ff6b6b;
  transition: all 0.3s ease;
}

.category-card:hover .category-icon .el-icon {
  color: white;
}

.category-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  transition: all 0.3s ease;
}

.category-card:hover .category-name {
  color: #ff6b6b;
  transform: scale(1.05);
}

.product-section {
  margin-bottom: 50px;
}

.hot-product-section {
  margin-bottom: 50px;
}

.hot-product-section .section-title::before {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8787 100%);
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(334px, 1fr));
  gap: 20px;
  justify-content: start;
}

@media (max-width: 1024px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
}

.product-card {
  width: 334px;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #f0f0f0;
}

.product-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 30px rgba(255, 107, 107, 0.2);
}

.product-image {
  width: 286px;
  height: 286px;
  margin: 24px auto 0;
  overflow: hidden;
  background: #fff;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  transition: transform 0.5s ease;
}

.product-card:hover .product-image img {
  transform: scale(1.08);
}

.cart-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.85) 0%, rgba(254, 202, 87, 0.85) 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s ease;
  gap: 8px;
  pointer-events: none;
}

.product-card:hover .cart-overlay {
  opacity: 1;
  pointer-events: none;
}

.cart-icon {
  font-size: 56px;
  color: white;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.2));
  animation: cartBounce 0.6s ease;
}

.cart-overlay::after {
  content: '点击购买';
  color: white;
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 2px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

@keyframes cartBounce {
  0%, 100% {
    transform: scale(1) rotate(0deg);
  }
  25% {
    transform: scale(1.1) rotate(-5deg);
  }
  50% {
    transform: scale(1.2) rotate(0deg);
  }
  75% {
    transform: scale(1.1) rotate(5deg);
  }
}

.hot-card {
  position: relative;
  border: 2px solid transparent;
}

.hot-card:hover {
  border-color: rgba(255, 107, 107, 0.3);
}

.hot-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8787 100%);
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  z-index: 1;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.4);
}

.hot-sales {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #ff6b6b;
  font-weight: 600;
}

.hot-sales .el-icon {
  font-size: 14px;
}

.product-info {
  padding: 12px 16px 16px;
  height: 80px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  font-size: 20px;
  font-weight: 700;
  color: #ff4d4f;
}

.price-unit {
  font-size: 12px;
  font-weight: 400;
  color: #ff4d4f;
  margin-left: 2px;
}

.product-sales {
  font-size: 12px;
  color: #999;
}

.article-section {
  margin-bottom: 50px;
}

.article-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.article-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.article-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 30px rgba(255, 107, 107, 0.2);
}

.article-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f8f8f8;
  position: relative;
}

.article-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.article-card:hover .article-image img {
  transform: scale(1.1);
}

.view-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s ease;
  gap: 12px;
}

.article-card:hover .view-overlay {
  opacity: 1;
}

.view-icon {
  font-size: 48px;
  color: white;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
  animation: viewPulse 0.6s ease;
}

.view-text {
  color: white;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 2px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

@keyframes viewPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.15);
  }
}

.article-content {
  padding: 15px;
}

.article-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.5;
}

.article-time {
  font-size: 13px;
  color: #999;
}

@media (max-width: 768px) {
  .banner-item {
    height: 280px;
  }
  
  .banner-content {
    padding: 20px 25px;
  }
  
  .banner-title {
    font-size: 22px;
  }
  
  .banner-desc {
    font-size: 14px;
  }
  
  .banner-container :deep(.el-carousel__arrow) {
    width: 45px;
    height: 45px;
    font-size: 18px;
  }
  
  .banner-container :deep(.el-carousel__arrow--left) {
    left: 15px;
  }
  
  .banner-container :deep(.el-carousel__arrow--right) {
    right: 15px;
  }
  
  .section-title {
    font-size: 22px;
  }
  
  .category-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
  }
  
  .category-card {
    padding: 20px 10px;
  }
  
  .category-icon {
    width: 50px;
    height: 50px;
    margin-bottom: 12px;
  }
  
  .category-icon .el-icon {
    font-size: 24px;
  }
  
  .category-name {
    font-size: 14px;
  }
  
  .article-grid {
    grid-template-columns: repeat(1, 1fr);
  }
}
</style>
