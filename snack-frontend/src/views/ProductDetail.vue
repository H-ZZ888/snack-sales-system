<template>
  <div class="product-detail">
    <div class="container">
      <!-- 返回按钮 -->
      <div class="back-button">
        <el-button :icon="ArrowLeft" @click="goBack" plain>
          返回商品列表
        </el-button>
      </div>

      <el-card v-loading="loading" shadow="hover" class="detail-card">
        <!-- 商品主要信息区 -->
        <div class="product-main">
          <!-- 左侧：商品图片 -->
          <div class="product-images">
            <div class="main-image">
              <el-image
                :src="getImageUrl(currentImage)"
                fit="contain"
                :preview-src-list="imageList"
                class="image-preview"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                    <span>暂无图片</span>
                  </div>
                </template>
              </el-image>
            </div>
          </div>

          <!-- 右侧：商品信息和操作 -->
          <div class="product-info">
            <h1 class="product-title">{{ product.name }}</h1>
            
            <div class="product-subtitle">{{ product.description }}</div>
            
            <!-- 价格区域 -->
            <div class="price-section">
              <div class="price-row">
                <span class="price-label">价格</span>
                <div class="price-value">
                  <span class="current-price">¥{{ product.price }}<span class="price-unit" v-if="product.unit">/{{ product.unit }}</span></span>
                  <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
                </div>
              </div>
              <div class="stock-row">
                <span class="stock-label">库存</span>
                <span class="stock-value">{{ product.stock }} {{ product.unit || '件' }}</span>
              </div>
              <div class="origin-row" v-if="product.origin">
                <span class="origin-label">产地</span>
                <span class="origin-value">{{ product.origin }}</span>
              </div>
            </div>

            <!-- 数量选择 -->
            <div class="quantity-section">
              <span class="quantity-label">数量</span>
              <el-input-number
                v-model="quantity"
                :min="1"
                :max="product.stock"
                size="large"
                class="quantity-input"
              />
            </div>

            <!-- 操作按钮 -->
            <div class="action-buttons">
              <el-button
                type="danger"
                size="large"
                :icon="ShoppingCart"
                @click="handleBuyNow"
                :disabled="product.stock === 0"
                class="buy-btn"
              >
                立即购买
              </el-button>
              <el-button
                type="warning"
                size="large"
                :icon="ShoppingCartFull"
                @click="handleAddToCart"
                :disabled="product.stock === 0"
                class="cart-btn"
              >
                加入购物车
              </el-button>
              <el-button
                :type="isFavorite ? 'danger' : 'default'"
                size="large"
                :icon="isFavorite ? StarFilled : Star"
                @click="handleToggleFavorite"
                class="favorite-btn"
                plain
              >
                {{ isFavorite ? '已收藏' : '收藏' }}
              </el-button>
            </div>

            <!-- 商品标签 -->
            <div class="product-tags" v-if="product.categoryName">
              <el-tag type="warning" effect="plain">{{ product.categoryName }}</el-tag>
              <el-tag v-if="product.stock > 0" type="success" effect="plain">有货</el-tag>
              <el-tag v-else type="info" effect="plain">缺货</el-tag>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 商品详情和评价 -->
      <el-card shadow="hover" class="detail-tabs-card">
        <el-tabs v-model="activeTab" class="detail-tabs">
          <!-- 商品详情 -->
          <el-tab-pane label="商品详情" name="detail">
            <div class="detail-content">
              <div class="detail-item">
                <span class="detail-label">商品名称：</span>
                <span class="detail-text">{{ product.name }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">商品分类：</span>
                <span class="detail-text">{{ product.categoryName }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">商品描述：</span>
                <span class="detail-text">{{ product.description || '暂无描述' }}</span>
              </div>
              <div class="detail-item" v-if="product.origin">
                <span class="detail-label">商品产地：</span>
                <span class="detail-text origin-text">{{ product.origin }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">商品库存：</span>
                <span class="detail-text">{{ product.stock }} {{ product.unit || '件' }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">销售数量：</span>
                <span class="detail-text">{{ product.sales || 0 }} {{ product.unit || '件' }}</span>
              </div>
            </div>
          </el-tab-pane>

          <!-- 用户评价 -->
          <el-tab-pane label="用户评价" name="reviews">
            <div class="reviews-section">
              <!-- 评价统计 -->
              <div class="review-stats">
                <div class="stats-score">
                  <div class="score-number">{{ averageRating }}</div>
                  <div class="stats-count">{{ reviewTotal }} 条评价</div>
                </div>
              </div>

              <!-- 评价列表 -->
              <div class="review-list">
                <el-empty v-if="reviews.length === 0" description="暂无评价" :image-size="120" />
                
                <div
                  v-for="review in reviews"
                  :key="review.id"
                  class="review-item"
                >
                  <div class="review-header">
                    <div class="review-user">
                      <el-avatar :size="40" :src="getUserAvatar(review.userAvatar)">
                        {{ review.username?.[0] || 'U' }}
                      </el-avatar>
                      <div class="user-info">
                        <div class="user-name">{{ review.username || '匿名用户' }}</div>
                        <div class="review-date">{{ review.createTime }}</div>
                      </div>
                    </div>
                    <!-- 删除按钮 -->
                    <el-button
                      v-if="isCurrentUserReview(review)"
                      type="danger"
                      size="small"
                      text
                      @click="handleDeleteReview(review.id)"
                      class="delete-btn"
                    >
                      删除
                    </el-button>
                  </div>
                  
                  <div class="review-content">
                    <div class="review-text">{{ review.content }}</div>
                    
                    <div v-if="review.images" class="review-images">
                      <el-image
                        v-for="(img, index) in getReviewImages(review.images)"
                        :key="index"
                        :src="getImageUrl(img)"
                        :preview-src-list="getReviewImages(review.images).map(i => getImageUrl(i))"
                        :initial-index="index"
                        :z-index="9999"
                        :hide-on-click-modal="true"
                        fit="cover"
                        class="review-img"
                        lazy
                        preview-teleported
                      >
                        <template #error>
                          <div class="img-error">
                            <el-icon><Picture /></el-icon>
                          </div>
                        </template>
                      </el-image>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 评价分页 -->
              <div v-if="reviewTotal > reviewPageSize" class="review-pagination">
                <el-pagination
                  v-model:current-page="reviewPageNum"
                  :page-size="reviewPageSize"
                  :total="reviewTotal"
                  layout="total, prev, pager, next"
                  @current-change="handleReviewPageChange"
                />
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ShoppingCart, ShoppingCartFull, Star, StarFilled, Picture, ArrowLeft
} from '@element-plus/icons-vue'
import { getProductDetail } from '@/api/product'
import { getCartList, addToCart } from '@/api/cart'
import { addFavorite, removeFavorite, checkFavorite } from '@/api/favorite'
import { getProductReviews, deleteUserReview } from '@/api/review'

const route = useRoute()
const router = useRouter()
const store = useStore()

const loading = ref(false)
const product = ref({})
const quantity = ref(1)
const currentImage = ref('')
const activeTab = ref('detail')
const isFavorite = ref(false)
const reviews = ref([])
const reviewPageNum = ref(1)
const reviewPageSize = ref(6)
const reviewTotal = ref(0)

const imageList = computed(() => {
  if (!product.value.image) return []
  return [getImageUrl(product.value.image)]
})

const averageRating = computed(() => {
  if (reviews.value.length === 0) return 0
  const sum = reviews.value.reduce((acc, review) => acc + review.rating, 0)
  return (sum / reviews.value.length).toFixed(1)
})

const getImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  if (path.startsWith('/api')) {
    return 'http://localhost:8080' + path
  }
  return 'http://localhost:8080/api' + path
}

const getUserAvatar = (avatar) => {
  if (!avatar) return ''
  return getImageUrl(avatar)
}

const getReviewImages = (images) => {
  if (!images) return []
  return images.split(',').filter(img => img)
}

// 加载商品详情
const loadProductDetail = async () => {
  loading.value = true
  try {
    const res = await getProductDetail(route.params.id)
    product.value = res.data
    currentImage.value = product.value.image
    
    // 检查是否已收藏
    if (store.state.user.token) {
      try {
        isFavorite.value = await checkIsFavorite()
      } catch (error) {
        console.error('检查收藏状态失败:', error)
        ElMessage.error('加载收藏状态失败，请刷新重试')
      }
    }
    
    // 加载评价
    await loadReviews()
  } catch (error) {
    console.error('加载商品详情失败:', error)
    ElMessage.error('加载商品详情失败')
  } finally {
    loading.value = false
  }
}

// 检查是否已收藏
const checkIsFavorite = async () => {
  const res = await checkFavorite(route.params.id)
  return res.data || false
}

// 加载评价
const loadReviews = async () => {
  try {
    const res = await getProductReviews(route.params.id, {
      pageNum: reviewPageNum.value,
      pageSize: reviewPageSize.value
    })
    console.log('评价数据:', res.data)
    reviews.value = res.data?.records || []
    reviewTotal.value = res.data?.total || 0
  } catch (error) {
    console.error('加载评价失败:', error)
    // 静默失败，显示空列表
    reviews.value = []
    reviewTotal.value = 0
  }
}

// 处理评价分页变化
const handleReviewPageChange = (page) => {
  reviewPageNum.value = page
  loadReviews()
}

// 立即购买
const handleBuyNow = () => {
  // 检查是否登录
  if (!store.state.user.token) {
    ElMessage.warning('请先登录后再购买')
    router.push({
      path: '/login',
      query: { redirect: route.fullPath }
    })
    return
  }
  
  // 检查库存
  if (product.value.stock === 0) {
    ElMessage.error('商品已售罄')
    return
  }
  
  if (quantity.value > product.value.stock) {
    ElMessage.error('购买数量超过库存')
    return
  }
  
  // 跳转到订单确认页
  router.push({
    path: '/order/confirm',
    query: {
      type: 'buy',
      productId: product.value.id,
      quantity: quantity.value
    }
  })
}

// 加入购物车
const handleAddToCart = async () => {
  // 检查是否登录
  if (!store.state.user.token) {
    ElMessage.warning('请先登录后再加入购物车')
    router.push({
      path: '/login',
      query: { redirect: route.fullPath }
    })
    return
  }
  
  // 检查库存
  if (product.value.stock === 0) {
    ElMessage.error('商品已售罄')
    return
  }
  
  if (quantity.value > product.value.stock) {
    ElMessage.error('添加数量超过库存')
    return
  }
  
  try {
    await addToCart({
      productId: product.value.id,
      quantity: quantity.value
    })
    
    // 更新购物车数量 - 重新获取购物车列表以确保数量准确（种类数）
    const res = await getCartList()
    const cartData = Array.isArray(res.data) ? res.data : []
    store.dispatch('updateCartCount', cartData.length)
    
    ElMessage.success('已加入购物车')
  } catch (error) {
    console.error('加入购物车失败:', error)
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('加入购物车失败，请稍后重试')
    }
  }
}

// 切换收藏
const handleToggleFavorite = async () => {
  // 检查是否登录
  if (!store.state.user.token) {
    ElMessage.warning('请先登录后再收藏')
    router.push({
      path: '/login',
      query: { redirect: route.fullPath }
    })
    return
  }
  
  try {
    if (isFavorite.value) {
      await removeFavorite(product.value.id)
      ElMessage.success('已取消收藏')
      isFavorite.value = false
    } else {
      await addFavorite(product.value.id)
      ElMessage.success('收藏成功')
      isFavorite.value = true
    }
  } catch (error) {
    console.error('操作失败:', error)
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('操作失败，请稍后重试')
    }
  }
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 判断是否是当前用户的评价
const isCurrentUserReview = (review) => {
  try {
    const userInfo = store.state.user.userInfo
    if (!userInfo || Object.keys(userInfo).length === 0) {
      return false
    }
    const currentUserId = userInfo.id
    if (!currentUserId) {
      return false
    }
    const reviewUserId = review.userId
    if (!reviewUserId) {
      return false
    }
    return reviewUserId == currentUserId
  } catch (error) {
    console.error('isCurrentUserReview error:', error)
    return false
  }
}

// 删除评价
const handleDeleteReview = async (reviewId) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条评价吗？删除后无法恢复。',
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteUserReview(reviewId)
    ElMessage.success('删除成功')
    
    await loadReviews()
  } catch (error) {
    if (error === 'cancel') {
      return
    }
    console.error('删除评价失败:', error)
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('删除失败，请稍后重试')
    }
  }
}

onMounted(() => {
  loadProductDetail()
})
</script>

<style scoped>
.product-detail {
  min-height: calc(100vh - 120px);
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.back-button {
  margin-bottom: 20px;
}

.back-button .el-button {
  border-radius: 8px;
  transition: all 0.3s;
}

.back-button .el-button:hover {
  color: #ff6b6b;
  border-color: #ff6b6b;
  background: rgba(255, 107, 107, 0.1);
  transform: translateX(-5px);
}

.detail-card {
  margin-bottom: 20px;
  border-radius: 12px;
  overflow: hidden;
}

.product-main {
  display: flex;
  gap: 40px;
  padding: 20px;
}

.product-images {
  flex: 0 0 450px;
}

.main-image {
  width: 100%;
  height: 450px;
  background: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-preview {
  width: 100%;
  height: 100%;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 14px;
}

.image-error .el-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-title {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin: 0 0 10px 0;
  line-height: 1.4;
}

.product-subtitle {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
  line-height: 1.6;
}

.price-section {
  background: linear-gradient(135deg, #fff5f5 0%, #ffe6e6 100%);
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.price-row {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.price-label {
  font-size: 14px;
  color: #666;
  margin-right: 20px;
}

.price-value {
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.current-price {
  font-size: 32px;
  font-weight: 700;
  color: #ff6b6b;
}

.price-unit {
  font-size: 16px;
  font-weight: 400;
  color: #ff6b6b;
  margin-left: 2px;
}

.original-price {
  font-size: 16px;
  color: #999;
  text-decoration: line-through;
}

.stock-row {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.stock-label {
  font-size: 14px;
  color: #666;
  margin-right: 20px;
}

.stock-value {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.origin-row {
  display: flex;
  align-items: center;
}

.origin-label {
  font-size: 14px;
  color: #666;
  margin-right: 20px;
}

.origin-value {
  font-size: 16px;
  color: #ff6b6b;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 5px;
}

.origin-value::before {
  content: '📍';
  font-size: 16px;
}

.quantity-section {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
}

.quantity-label {
  font-size: 16px;
  color: #333;
  margin-right: 20px;
  font-weight: 500;
}

.quantity-input {
  width: 150px;
}

.action-buttons {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.buy-btn {
  flex: 1;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8787 100%);
  border: none;
}

.buy-btn:hover {
  background: linear-gradient(135deg, #ff5252 0%, #ff6b6b 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
}

.cart-btn {
  flex: 1;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #feca57 0%, #ffd93d 100%);
  border: none;
  color: #333;
}

.cart-btn:hover {
  background: linear-gradient(135deg, #ffc107 0%, #feca57 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(254, 202, 87, 0.4);
}

.favorite-btn {
  width: 120px;
  height: 50px;
  font-size: 16px;
}

.product-tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.detail-tabs-card {
  border-radius: 12px;
  overflow: hidden;
}

.detail-tabs {
  min-height: 400px;
}

.detail-content {
  padding: 20px;
}

.detail-item {
  display: flex;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-label {
  width: 120px;
  font-weight: 600;
  color: #333;
  flex-shrink: 0;
}

.detail-text {
  flex: 1;
  color: #666;
  line-height: 1.6;
}

.origin-text {
  color: #ff6b6b;
  font-weight: 500;
}

.origin-text::before {
  content: '📍 ';
}

.reviews-section {
  padding: 20px;
}

.review-stats {
  background: linear-gradient(135deg, #fff5f5 0%, #ffe6e6 100%);
  padding: 40px;
  border-radius: 12px;
  margin-bottom: 30px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(255, 107, 107, 0.1);
}

.stats-score {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.score-number {
  font-size: 56px;
  font-weight: 700;
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1;
}

.stats-count {
  font-size: 16px;
  color: #666;
  font-weight: 500;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.review-item {
  background: transparent;
  padding: 20px 0;
  border-radius: 0;
  border: none;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.review-user {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.delete-btn {
  font-size: 13px;
  padding: 4px 12px;
}

.delete-btn:hover {
  background-color: rgba(255, 107, 107, 0.1);
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.review-date {
  font-size: 12px;
  color: #999;
}

.review-content {
  padding-left: 52px;
}

.review-text {
  font-size: 14px;
  color: #333;
  line-height: 1.8;
  margin-bottom: 12px;
  word-break: break-word;
}

.review-text {
  font-size: 15px;
  color: #555;
  line-height: 1.8;
  margin-bottom: 16px;
  word-break: break-word;
}

.review-images {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.review-img {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
}

.review-img:hover {
  transform: scale(1.05);
  border-color: #ff6b6b;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
}

.img-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  color: #ccc;
}

.img-error .el-icon {
  font-size: 32px;
}

/* 评价分页样式 */
.review-pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.review-pagination :deep(.el-pagination) {
  display: flex;
  justify-content: center;
}

.review-pagination :deep(.el-pagination__total) {
  font-weight: 500;
  color: #666;
}

.review-pagination :deep(.el-pager li) {
  min-width: 35px;
  height: 35px;
  line-height: 35px;
  border-radius: 6px;
  margin: 0 3px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.review-pagination :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
}

.review-pagination :deep(.el-pager li:hover:not(.is-active)) {
  color: #ff6b6b;
}

.review-pagination :deep(.btn-prev),
.review-pagination :deep(.btn-next) {
  min-width: 35px;
  height: 35px;
  line-height: 35px;
  border-radius: 6px;
  margin: 0 3px;
  transition: all 0.3s ease;
}

.review-pagination :deep(.btn-prev:hover),
.review-pagination :deep(.btn-next:hover) {
  color: #ff6b6b;
}

@media (max-width: 768px) {
  .product-main {
    flex-direction: column;
  }
  
  .product-images {
    flex: none;
    width: 100%;
  }
  
  .main-image {
    height: 300px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .favorite-btn {
    width: 100%;
  }
  
  .review-stats {
    padding: 30px 20px;
  }
  
  .score-number {
    font-size: 48px;
  }
  
  .review-item {
    padding: 16px;
  }
  
  .review-img {
    width: 80px;
    height: 80px;
  }
}

/* 图片预览样式优化 */
:deep(.el-image-viewer__wrapper) {
  z-index: 9999 !important;
}

:deep(.el-image-viewer__close) {
  top: 60px !important;
  right: 40px !important;
  z-index: 10000 !important;
  background: rgba(0, 0, 0, 0.5) !important;
  border-radius: 50% !important;
  width: 44px !important;
  height: 44px !important;
  font-size: 24px !important;
}

:deep(.el-image-viewer__close:hover) {
  background: rgba(255, 107, 107, 0.8) !important;
}

:deep(.el-image-viewer__mask) {
  background-color: rgba(0, 0, 0, 0.8) !important;
}
</style>
