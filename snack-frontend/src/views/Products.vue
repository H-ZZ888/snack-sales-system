<template>
  <div class="products-page">
    <div class="container">
      <div class="page-header">
        <h1>商品列表</h1>
        <el-select
          v-model="filters.categoryId"
          placeholder="全部分类"
          clearable
          size="large"
          @change="handleSearch"
          @clear="filters.categoryId = null"
          class="category-select-header"
        >
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
        </el-select>
      </div>
      
      <!-- 搜索和筛选区 -->
      <div class="search-section">
        <div class="search-container">
          <el-input
            v-model="filters.keyword"
            placeholder="搜索商品名称、品牌、口味..."
            size="large"
            clearable
            @keyup.enter="handleSearch"
            @clear="handleSearch"
            class="search-input"
          >
            <template #prefix>
              <el-icon class="search-icon"><Search /></el-icon>
            </template>
          </el-input>
          
          <el-button
            type="primary"
            size="large"
            :icon="Search"
            @click="handleSearch"
            class="search-button"
          >
            搜索
          </el-button>
        </div>
        
        <!-- 热门商品推荐 -->
        <div class="hot-products">
          <span class="hot-label">热门商品：</span>
          <el-tag
            v-for="tag in hotProducts"
            :key="tag"
            @click="searchByTag(tag)"
            class="hot-tag"
            round
          >
            {{ tag }}
          </el-tag>
        </div>
      </div>
      
      <div class="product-list" v-loading="loading">
        <div
          v-for="product in products"
          :key="product.id"
          class="product-card"
          @click.stop="goToDetail(product.id)"
        >
          <div class="product-image">
            <img :src="getImageUrl(product.image)" :alt="product.name" />
            <div v-if="product.stock === 0" class="out-of-stock">已售罄</div>
            <div v-else class="cart-overlay">
              <el-icon class="cart-icon"><ShoppingCart /></el-icon>
            </div>
          </div>
          <div class="product-info">
            <h3 class="product-name" :title="product.name">{{ product.name }}</h3>
            <p v-if="product.description" class="product-desc" :title="product.description">
              {{ product.description }}
            </p>
            <p v-if="product.origin" class="product-origin">
              <el-icon><Location /></el-icon>
              {{ product.origin }}
            </p>
            <div class="product-footer">
              <div class="price-info">
                <span class="price">¥{{ product.price }}</span>
                <span v-if="product.unit" class="unit">/{{ product.unit }}</span>
              </div>
              <span v-if="product.sales !== undefined && product.sales !== null" class="sales">
                已售{{ product.sales }}件
              </span>
            </div>
          </div>
        </div>
      </div>
      
      <div v-if="products.length === 0 && !loading" class="empty">
        <el-empty description="暂无商品" />
      </div>
      
      <div v-if="total > 0" class="pagination">
        <el-pagination
          :current-page="filters.pageNum"
          :page-size="filters.pageSize"
          :total="total"
          :page-sizes="[12, 24, 36, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search, Location, ShoppingCart } from '@element-plus/icons-vue'
import { getProductList } from '@/api/product'
import { getCategoryList } from '@/api/category'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const products = ref([])
const categories = ref([])
const total = ref(0)
const hotProducts = ref(['薯片', '巧克力', '坚果', '糖果', '饼干', '果冻'])

const filters = reactive({
  pageNum: 1,
  pageSize: 12,
  categoryId: route.query.categoryId ? Number(route.query.categoryId) : null,
  keyword: route.query.keyword || ''
})

const getImageUrl = (path) => {
  if (!path) return '/placeholder.png'
  if (path.startsWith('http')) return path
  return `http://localhost:8080/api${path}`
}

const goToDetail = (id) => {
  console.log('跳转到商品详情:', id)
  router.push(`/product/${id}`)
}

const handleSearch = () => {
  filters.pageNum = 1
  fetchProducts()
}

const searchByTag = (tag) => {
  filters.keyword = tag
  handleSearch()
}

const handlePageChange = (page) => {
  filters.pageNum = page
  fetchProducts()
}

const handleSizeChange = (size) => {
  filters.pageSize = size
  filters.pageNum = 1
  fetchProducts()
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: filters.pageNum,
      pageSize: filters.pageSize
    }
    
    // 只有当categoryId有值时才添加到参数中
    if (filters.categoryId !== null && filters.categoryId !== undefined) {
      params.categoryId = filters.categoryId
    }
    
    // 只有当keyword有值时才添加到参数中
    if (filters.keyword && filters.keyword.trim()) {
      params.keyword = filters.keyword.trim()
    }
    
    const res = await getProductList(params)
    products.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取商品列表失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const res = await getCategoryList()
    categories.value = res.data || []
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

watch(() => route.query, (newQuery) => {
  filters.categoryId = newQuery.categoryId ? Number(newQuery.categoryId) : null
  filters.keyword = newQuery.keyword || ''
  filters.pageNum = 1
  fetchProducts()
}, { immediate: false })

onMounted(() => {
  fetchCategories()
  fetchProducts()
})
</script>

<style scoped>
.products-page {
  min-height: calc(100vh - 200px);
  padding: 90px 0 30px 0;
}

.page-header {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
}

.page-header h1 {
  font-size: 32px;
  margin: 0;
  color: #2d5016;
  font-weight: bold;
  position: relative;
  padding-bottom: 10px;
}

.page-header h1::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 60px;
  height: 4px;
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  border-radius: 2px;
}

.category-select-header {
  width: 220px;
  flex-shrink: 0;
}

.category-select-header :deep(.el-input__wrapper) {
  border-radius: 50px;
  box-shadow: 0 4px 16px rgba(255, 107, 107, 0.15);
  transition: all 0.3s;
  padding: 8px 20px;
  border: 2px solid transparent;
  background: #fff;
}

.category-select-header :deep(.el-input__wrapper:hover) {
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.25);
  border-color: rgba(255, 107, 107, 0.2);
  transform: translateY(-2px);
}

.category-select-header :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.2);
  border-color: #ff6b6b;
}

/* 搜索区域样式 */
.search-section {
  margin: 20px 0 40px;
}

.search-container {
  display: flex;
  gap: 15px;
  max-width: 800px;
  margin: 0 auto 20px;
  align-items: stretch;
}

.search-input {
  flex: 1;
  min-width: 0;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 50px;
  box-shadow: 0 4px 16px rgba(255, 107, 107, 0.15);
  transition: all 0.3s;
  padding: 5px 20px;
  border: 2px solid transparent;
  height: 100%;
}

.search-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.25);
  border-color: rgba(255, 107, 107, 0.2);
}

.search-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.2);
  border-color: #ff6b6b;
}

.search-input :deep(.el-input__prefix) {
  margin-left: 5px;
}

.search-icon {
  color: #ff6b6b;
  font-size: 20px;
}

.search-button {
  min-width: 120px;
  flex-shrink: 0;
  border-radius: 50px;
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  border: none;
  font-weight: 600;
  font-size: 16px;
  box-shadow: 0 4px 16px rgba(255, 107, 107, 0.3);
  transition: all 0.3s;
}

.search-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 24px rgba(255, 107, 107, 0.4);
}

.search-button:active {
  transform: translateY(-1px);
}

/* 热门商品推荐样式 */
.hot-products {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
  padding: 15px 20px;
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.05) 0%, rgba(254, 202, 87, 0.05) 100%);
  border-radius: 50px;
  max-width: 800px;
  margin: 0 auto;
}

.hot-label {
  font-size: 14px;
  color: #666;
  font-weight: 600;
  white-space: nowrap;
}

.hot-tag {
  cursor: pointer;
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.1) 0%, rgba(254, 202, 87, 0.1) 100%);
  border: 1px solid rgba(255, 107, 107, 0.3);
  color: #ff6b6b;
  font-weight: 500;
  transition: all 0.3s;
  font-size: 13px;
}

.hot-tag:hover {
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  color: #fff;
  border-color: transparent;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
}

.filters {
  display: flex;
  gap: 15px;
  align-items: center;
}

.filters :deep(.el-select .el-input__wrapper) {
  border-radius: 8px;
  transition: all 0.3s;
}

.filters :deep(.el-select .el-input__wrapper:hover) {
  border-color: #ff6b6b;
}

.filters :deep(.el-input__wrapper) {
  border-radius: 8px;
  transition: all 0.3s;
}

.filters :deep(.el-input__wrapper:hover) {
  border-color: #ff6b6b;
}

.filters :deep(.el-button) {
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  border: none;
  color: #fff;
  transition: all 0.3s;
}

.filters :deep(.el-button:hover) {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(247, 93, 55, 0.3);
}

.product-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.product-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.1);
  border: 2px solid transparent;
  user-select: none;
}

.product-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 6px 24px rgba(255, 107, 107, 0.25);
  border-color: rgba(255, 107, 107, 0.3);
}

.product-image {
  position: relative;
  width: 100%;
  height: 220px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
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

.out-of-stock {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  padding: 10px 20px;
  border-radius: 4px;
  font-size: 18px;
  font-weight: bold;
}

.product-info {
  padding: 15px;
}

.product-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-origin {
  font-size: 13px;
  color: #ff6b6b;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 500;
}

.product-origin .el-icon {
  font-size: 14px;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-info {
  display: flex;
  align-items: center;
  gap: 5px;
}

.price {
  font-size: 22px;
  color: #ff4d4f;
  font-weight: bold;
}

.unit {
  color: #999;
  font-size: 14px;
}

.sales {
  font-size: 14px;
  color: #999;
}

.empty {
  padding: 60px 0;
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 20px 0;
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
</style>

