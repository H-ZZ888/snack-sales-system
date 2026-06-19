<template>
  <div class="user-favorites">
    <div class="page-header">
      <div class="header-content">
        <el-icon :size="32" class="header-icon"><Star /></el-icon>
        <div class="header-text">
          <h2>我的收藏</h2>
          <p class="header-subtitle">收藏的美味零食都在这里</p>
        </div>
      </div>
      <div v-if="total > 0" class="header-stats">
        <el-tag type="danger" size="large" effect="plain">
          <el-icon><Star /></el-icon>
          <span>共 {{ total }} 件商品</span>
        </el-tag>
      </div>
    </div>
    
    <div v-loading="loading" class="favorites-content">
      <div v-if="favorites.length > 0" class="favorites-list">
        <div
          v-for="item in favorites"
          :key="item.favoriteId"
          class="favorite-card"
        >
          <div class="product-image" @click="goToDetail(item.id)">
            <img :src="getImageUrl(item.image)" :alt="item.name" />
            <div class="image-overlay">
              <el-icon :size="40"><View /></el-icon>
              <span>查看详情</span>
            </div>
          </div>
          <div class="product-info">
            <h3 class="product-name" @click="goToDetail(item.id)">
              {{ item.name }}
            </h3>
            <p class="product-origin">
              <el-icon><Location /></el-icon>
              <span>{{ item.origin }}</span>
            </p>
            <div class="product-footer">
              <div class="price-info">
                <span class="price">¥{{ item.price }}</span>
                <span class="unit">/{{ item.unit }}</span>
              </div>
              <el-button
                type="danger"
                size="small"
                text
                @click="handleDelete(item.favoriteId)"
                class="delete-btn"
              >
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </div>
      
      <el-empty v-else-if="!loading" description="还没有收藏任何商品" :image-size="160">
        <template #image>
          <el-icon :size="160" color="#d3d3d3"><Star /></el-icon>
        </template>
        <el-button type="primary" size="large" @click="$router.push('/products')">
          <el-icon><ShoppingBag /></el-icon>
          <span>去逛逛</span>
        </el-button>
      </el-empty>
    </div>
    
    <div v-if="total > 0" class="pagination">
      <el-pagination
        :current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { getImageUrl } from '@/utils/url'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Star, View, Location, Delete, ShoppingBag } from '@element-plus/icons-vue'
import { getFavoriteList, deleteFavorite } from '@/api/favorite'

const router = useRouter()

const loading = ref(false)
const favorites = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(6)


const goToDetail = (productId) => {
  router.push(`/product/${productId}`)
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteFavorite(id)
    ElMessage.success('已取消收藏')
    fetchFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      ElMessage.error('取消收藏失败')
    }
  }
}

const handlePageChange = (page) => {
  pageNum.value = page
  fetchFavorites()
}

const fetchFavorites = async () => {
  // 检查用户是否登录
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  loading.value = true
  try {
    const res = await getFavoriteList({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    
    console.log('收藏列表完整响应:', res)
    console.log('收藏列表数据:', res.data)
    console.log('收藏列表records:', res.data?.records)
    
    // 后端返回的是 Page<ProductVO>
    const records = res.data?.records || []
    
    if (records.length === 0) {
      console.log('没有收藏记录')
      favorites.value = []
      total.value = 0
      return
    }
    
    // 直接使用后端返回的数据，确保所有字段都存在
    favorites.value = records.map(item => {
      console.log('处理收藏项:', item)
      return {
        id: item.id,  // 商品ID
        favoriteId: item.favoriteId,  // 收藏记录ID
        name: item.name || '未知商品',
        image: item.image || '',
        price: item.price || 0,
        unit: item.unit || '袋',
        origin: item.origin || '未知'
      }
    })
    
    total.value = res.data?.total || 0
    console.log('处理后的收藏列表:', favorites.value)
    console.log('总数:', total.value)
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    console.error('错误详情:', error.response)
    
    // 如果是401错误，说明未登录或token过期
    if (error.response?.status === 401) {
      ElMessage.warning('登录已过期，请重新登录')
      router.push('/login')
    } else {
      ElMessage.error('获取收藏列表失败：' + (error.response?.data?.message || error.message || '请稍后重试'))
    }
    
    favorites.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchFavorites()
})
</script>

<style scoped>
.user-favorites {
  min-height: 400px;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 25px 30px;
  background: linear-gradient(135deg, #fff5f5 0%, #ffe6e6 100%);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(255, 107, 107, 0.15);
}

.header-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-icon {
  color: #ff6b6b;
  filter: drop-shadow(0 2px 4px rgba(255, 107, 107, 0.3));
}

.header-text h2 {
  font-size: 26px;
  color: #2d3436;
  margin: 0 0 5px 0;
  font-weight: bold;
}

.header-subtitle {
  font-size: 14px;
  color: #636e72;
  margin: 0;
}

.header-stats {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-stats :deep(.el-tag) {
  padding: 10px 20px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.favorites-content {
  min-height: 300px;
}

.favorites-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(334px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
  justify-content: start;
}

@media (max-width: 1024px) {
  .favorites-list {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .favorites-list {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
}

.favorite-card {
  width: 334px;
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  overflow: visible;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
}

.favorite-card:hover {
  border-color: rgba(255, 107, 107, 0.3);
  box-shadow: 0 12px 30px rgba(255, 107, 107, 0.2);
  transform: translateY(-8px);
}

.product-image {
  width: 286px;
  height: 286px;
  margin: 24px auto 0;
  overflow: hidden;
  cursor: pointer;
  position: relative;
  background: #fff;
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

.favorite-card:hover .product-image img {
  transform: scale(1.08);
}

.image-overlay {
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
  gap: 8px;
  color: #fff;
  opacity: 0;
  transition: all 0.3s ease;
  pointer-events: none;
}

.image-overlay .el-icon {
  font-size: 56px;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.2));
  animation: viewBounce 0.6s ease;
}

@keyframes viewBounce {
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

.image-overlay span {
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 2px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.product-image:hover .image-overlay {
  opacity: 1;
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
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
  transition: all 0.3s;
  line-height: 1.4;
}

.product-name:hover {
  color: #ff6b6b;
}

.product-origin {
  display: none;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-info {
  display: flex;
  align-items: baseline;
  gap: 5px;
}

.price {
  font-size: 20px;
  color: #ff4d4f;
  font-weight: 700;
  font-family: Arial, sans-serif;
}

.unit {
  color: #999;
  font-size: 12px;
}

.delete-btn {
  padding: 6px;
  border-radius: 50%;
  transition: all 0.3s;
  font-size: 14px;
}

.delete-btn:hover {
  background: rgba(255, 77, 79, 0.1);
  transform: scale(1.15) rotate(10deg);
}

.delete-btn :deep(.el-icon) {
  font-size: 16px;
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 30px 0;
}

.pagination :deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  color: #fff;
}

.pagination :deep(.el-pagination.is-background .el-pager li:not(.is-disabled):hover) {
  color: #ff6b6b;
}

.pagination :deep(.el-pagination button:not(.is-disabled):hover) {
  color: #ff6b6b;
}

/* 空状态样式 */
:deep(.el-empty) {
  padding: 80px 20px;
}

:deep(.el-empty__description) {
  font-size: 18px;
  color: #999;
  margin-top: 20px;
}

:deep(.el-empty .el-button) {
  margin-top: 30px;
  padding: 15px 40px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 30px;
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  border: none;
  box-shadow: 0 4px 16px rgba(255, 107, 107, 0.4);
  transition: all 0.3s;
}

:deep(.el-empty .el-button:hover) {
  transform: translateY(-3px);
  box-shadow: 0 6px 24px rgba(255, 107, 107, 0.5);
}

:deep(.el-empty .el-button span) {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 响应式 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .favorites-list {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 16px;
  }
  
  .product-image {
    height: 180px;
  }
}
</style>

