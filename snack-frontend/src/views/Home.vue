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

      <!-- 精选商品 -->
      <div class="product-section" v-if="products.length > 0">
        <div class="section-header">
          <h2 class="section-title">精选商品</h2>
          <router-link to="/products" class="more-link">查看更多 →</router-link>
        </div>
        <div class="product-grid">
          <div v-for="product in products" :key="product.id" class="product-card" @click="goToDetail(product.id)">
            <div class="product-image">
              <img :src="getImageUrl(product.image)" :alt="product.name" />
              <div class="cart-overlay">
                <el-icon class="cart-icon"><ShoppingCart /></el-icon>
              </div>
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ product.name }}</h3>
              <div class="product-footer">
                <span class="product-price">¥{{ product.price }}</span>
                <span class="product-sales" v-if="product.sales">已售{{ product.sales }}件</span>
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
          <div v-for="product in hotProducts" :key="product.id" class="product-card hot-card" @click="goToDetail(product.id)">
            <div class="hot-badge">热销</div>
            <div class="product-image">
              <img :src="getImageUrl(product.image)" :alt="product.name" />
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ product.name }}</h3>
              <div class="product-footer">
                <span class="product-price">¥{{ product.price }}</span>
                <span class="product-sales hot-sales" v-if="product.sales">
                  <el-icon><TrendCharts /></el-icon>已售{{ product.sales }}件
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
          <div v-for="article in articles" :key="article.id" class="article-card" @click="goToArticle(article.id)">
            <div class="article-image">
              <img :src="getImageUrl(article.image)" :alt="article.title" />
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
import { ShoppingCart, Fries, Sugar, Burger, Chicken, IceDrink, ForkSpoon, TrendCharts } from '@element-plus/icons-vue'
import { getProductList } from '@/api/product'
import { getCategoryList } from '@/api/category'
import { getArticleList } from '@/api/article'
import { getBannerList } from '@/api/banner'
import { getImageUrl } from '@/utils/url'

const router = useRouter()
const loading = ref(false)
const products = ref([])
const hotProducts = ref([])
const categories = ref([])
const articles = ref([])
const carouselImages = ref([])

// 演示数据 - 后端不可用时自动显示
const demoData = {
  carousel: [
    { id: 1, image: 'images/home/1.png', title: '零食狂欢季', subtitle: '全场优惠进行中' },
    { id: 2, image: 'images/home/2.png', title: '新品上市', subtitle: '尝鲜价低至5折' },
    { id: 3, image: 'images/home/3.png', title: '限时特惠', subtitle: '每日精选好货' },
    { id: 4, image: 'images/home/4.png', title: '品质保证', subtitle: '好吃不贵' }
  ],
  categories: [
    { id: 1, name: '膨化食品' },
    { id: 2, name: '糖果巧克力' },
    { id: 3, name: '饼干糕点' },
    { id: 4, name: '肉类零食' },
    { id: 5, name: '饮料冲调' },
    { id: 6, name: '坚果炒货' }
  ],
  products: [
    { id: 1, name: '薯片大礼包', price: 29.90, image: '/uploads/2026-03-04/9e370d3267b74644acf3a049463e1e40.jpeg', sales: 1280 },
    { id: 2, name: '坚果混合装', price: 49.90, image: '/uploads/2026-03-04/d0b57b457e6d4082b4452582f58ce67a.jpg', sales: 960 },
    { id: 3, name: '巧克力礼盒', price: 39.90, image: '/uploads/2026-03-03/fe0e30faf7334c69ab50facd507859dc.jpg', sales: 850 },
    { id: 4, name: '牛肉干', price: 35.00, image: '/uploads/2026-03-05/f203a23adf134081b0c5e3d594adc8e3.jpg', sales: 720 },
    { id: 5, name: '夹心饼干', price: 19.90, image: '/uploads/2026-03-04/9e370d3267b74644acf3a049463e1e40.jpeg', sales: 680 },
    { id: 6, name: '果冻布丁', price: 15.90, image: '/uploads/2025-11-24/dd8e14f6f82a42fa9b126b70e0bbe598.jpg', sales: 550 },
    { id: 7, name: '鱿鱼丝', price: 25.00, image: '/uploads/2025-10-29/f675486222414cdab8b3c65c6550c0a5.jpg', sales: 490 },
    { id: 8, name: '话梅蜜饯', price: 12.90, image: '/uploads/2025-10-29/d8de8a172677453fb1230df3305dc6c7.jpg', sales: 430 },
    { id: 9, name: '海苔脆片', price: 22.00, image: '/uploads/2026-03-04/cdcbb7c7132a43959f0989a803ffe08d.png', sales: 380 }
  ]
}

const getCategoryIcon = (categoryName) => {
  const iconMap = {
    '膨化食品': Fries,
    '糖果巧克力': Sugar,
    '饼干糕点': Burger,
    '肉类零食': Chicken,
    '饮料冲调': IceDrink,
    '坚果炒货': ForkSpoon
  }
  return iconMap[categoryName] || ShoppingCart
}

const goToProducts = (categoryId) => {
  if (categoryId) { router.push({ path: '/products', query: { categoryId } }) }
  else { router.push('/products') }
}

const goToDetail = (id) => { router.push(`/product/${id}`) }

const goToArticle = (id) => { router.push(`/article/${id}`) }

const fetchData = async () => {
  loading.value = true
  try { const r = await getBannerList(); if (r.code === 200 && r.data) carouselImages.value = r.data } catch(e) {}
  try { const r = await getCategoryList(); if (r.code === 200 && r.data) categories.value = r.data } catch(e) {}
  try { const r = await getProductList({ pageNum: 1, pageSize: 9 }); if (r.code === 200 && r.data) products.value = r.data.records || r.data || [] } catch(e) {}
  try { const r = await getProductList({ pageNum: 1, pageSize: 9, sortBy: 'sales' }); if (r.code === 200 && r.data) hotProducts.value = r.data.records || r.data || [] } catch(e) {}
  try { const r = await getArticleList({ pageNum: 1, pageSize: 3 }); if (r.code === 200 && r.data) articles.value = r.data.records || r.data || [] } catch(e) {}
  useDemoIfEmpty()
  loading.value = false
}

const useDemoIfEmpty = () => {
  if (!carouselImages.value.length) carouselImages.value = demoData.carousel
  if (!categories.value.length) categories.value = demoData.categories
  if (!products.value.length) products.value = demoData.products
  if (!hotProducts.value.length) hotProducts.value = demoData.products
}

onMounted(() => { fetchData() })
</script>

<style scoped>
.banner-section { background: #f5f5f5; padding: 25px 0 50px; margin-bottom: 30px; }
.banner-container { max-width: 1200px; margin: 0 auto; padding: 0 20px; overflow: hidden; height: 420px; }
.banner-container :deep(.el-carousel),
.banner-container :deep(.el-carousel__container) { height: 420px !important; }
.banner-item { width: 100%; height: 420px; overflow: hidden; position: relative; background: #f5f5f5; display: flex; align-items: center; justify-content: center; }
.banner-item img { width: 100%; height: 100%; object-fit: contain; }
.banner-content { position: absolute; bottom: 0; left: 0; right: 0; padding: 30px 40px; background: linear-gradient(to top, rgba(0,0,0,0.7) 0%, rgba(0,0,0,0.4) 50%, transparent 100%); color: white; text-align: left; }
.banner-title { font-size: 28px; font-weight: 700; margin-bottom: 8px; }
.banner-desc { font-size: 16px; opacity: 0.9; }
.content-wrapper { max-width: 1200px; margin: 0 auto; padding: 0 20px 60px; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.section-title { font-size: 24px; font-weight: 700; color: #333; margin-bottom: 20px; position: relative; padding-left: 14px; }
.section-title::before { content: ''; position: absolute; left: 0; top: 4px; bottom: 4px; width: 4px; background: #e4393c; border-radius: 2px; }
.more-link { font-size: 14px; color: #999; text-decoration: none; }
.more-link:hover { color: #e4393c; }
.category-section { margin-bottom: 40px; }
.category-grid { display: grid; grid-template-columns: repeat(6, 1fr); gap: 16px; }
.category-card { background: #fff; border-radius: 12px; padding: 24px 12px; text-align: center; cursor: pointer; transition: all 0.3s; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.category-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(0,0,0,0.12); }
.category-icon { width: 56px; height: 56px; margin: 0 auto 12px; background: linear-gradient(135deg, #fce4ec, #f8bbd0); border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #e4393c; }
.category-name { font-size: 14px; font-weight: 500; color: #333; }
.product-section, .hot-product-section { margin-bottom: 40px; }
.product-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
.product-card { background: #fff; border-radius: 12px; overflow: hidden; cursor: pointer; transition: all 0.3s; box-shadow: 0 2px 8px rgba(0,0,0,0.06); position: relative; }
.product-card:hover { transform: translateY(-4px); box-shadow: 0 12px 32px rgba(0,0,0,0.12); }
.hot-card { border: 1px solid #ffe0e0; }
.hot-badge { position: absolute; top: 12px; left: 12px; background: linear-gradient(135deg, #ff6b6b, #e4393c); color: white; padding: 4px 12px; border-radius: 4px; font-size: 12px; font-weight: 600; z-index: 2; }
.product-image { width: 100%; height: 240px; overflow: hidden; position: relative; background: #f9f9f9; }
.product-image img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.3s; }
.product-card:hover .product-image img { transform: scale(1.05); }
.cart-overlay { position: absolute; bottom: 12px; right: 12px; width: 40px; height: 40px; background: #e4393c; border-radius: 50%; display: flex; align-items: center; justify-content: center; opacity: 0; transform: translateY(10px); transition: all 0.3s; }
.product-card:hover .cart-overlay { opacity: 1; transform: translateY(0); }
.cart-icon { color: white; font-size: 18px; }
.product-info { padding: 16px; }
.product-name { font-size: 16px; font-weight: 500; color: #333; margin-bottom: 8px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.product-footer { display: flex; justify-content: space-between; align-items: center; }
.product-price { font-size: 20px; font-weight: 700; color: #e4393c; }
.product-sales { font-size: 12px; color: #999; display: flex; align-items: center; gap: 4px; }
.hot-sales { color: #ff6b6b; font-weight: 500; }
.article-section { margin-bottom: 40px; }
.article-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
.article-card { background: #fff; border-radius: 12px; overflow: hidden; cursor: pointer; transition: all 0.3s; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.article-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(0,0,0,0.12); }
.article-image { width: 100%; height: 180px; overflow: hidden; position: relative; background: #f5f5f5; }
.article-image img { width: 100%; height: 100%; object-fit: cover; }
.article-content { padding: 16px; }
.article-title { font-size: 16px; font-weight: 500; color: #333; margin-bottom: 8px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.article-time { font-size: 13px; color: #999; }
</style>
