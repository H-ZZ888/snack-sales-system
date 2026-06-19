<template>
  <div class="cart-page">
    <div class="container">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-content">
          <el-icon :size="32" class="header-icon"><ShoppingCart /></el-icon>
          <div class="header-text">
            <h1>购物车</h1>
            <p class="header-subtitle">精选美味零食，一键加入购物车</p>
          </div>
        </div>
        <div v-if="cartList.length > 0" class="header-stats">
          <el-tag type="success" size="large" effect="plain">
            <el-icon><Goods /></el-icon>
            <span>共 {{ cartList.length }} 种商品</span>
          </el-tag>
        </div>
      </div>
      
      <div v-loading="loading" class="cart-content">
        <!-- 购物车有商品 -->
        <div v-if="cartList.length > 0" class="cart-main">
          <!-- 商品列表 -->
          <div class="cart-items">
            <div
              v-for="item in cartList"
              :key="item.id"
              class="cart-item"
            >
              <el-checkbox
                v-model="item.checked"
                @change="handleCheck"
                size="large"
                class="item-checkbox"
              />
              
              <div class="item-image" @click="goToDetail(item.productId)">
                <img :src="getImageUrl(item.productImage)" :alt="item.productName" />
              </div>
              
              <div class="item-info">
                <h3 class="item-name" @click="goToDetail(item.productId)">
                  {{ item.productName }}
                </h3>
                <p class="item-stock">
                  <el-icon><Box /></el-icon>
                  <span>库存: {{ item.productStock }}{{ item.productUnit }}</span>
                </p>
              </div>
              
              <div class="item-price">
                <span class="price-value">¥{{ item.productPrice }}</span>
                <span class="price-unit">/{{ item.productUnit }}</span>
              </div>
              
              <div class="item-quantity">
                <el-input-number
                  v-model="item.quantity"
                  :min="1"
                  :max="item.productStock"
                  @change="handleUpdateQuantity(item)"
                  size="default"
                />
              </div>
              
              <div class="item-total">
                <span class="total-label">小计</span>
                <span class="total-value">¥{{ (item.productPrice * item.quantity).toFixed(2) }}</span>
              </div>
              
              <div class="item-actions">
                <el-button
                  type="danger"
                  :icon="Delete"
                  circle
                  @click="handleDelete(item.id)"
                  title="删除商品"
                />
              </div>
            </div>
          </div>
          
          <!-- 结算栏 -->
          <div class="cart-footer">
            <div class="footer-left">
              <el-checkbox
                v-model="checkAll"
                @change="handleCheckAll"
                size="large"
              >
                <span class="checkbox-label">全选</span>
              </el-checkbox>
              
              <el-button
                type="danger"
                text
                :icon="Delete"
                @click="handleClearCart"
                class="clear-btn"
              >
                清空购物车
              </el-button>
            </div>
            
            <div class="footer-right">
              <div class="total-info">
                <div class="info-row">
                  <span class="info-label">已选商品:</span>
                  <span class="info-value">{{ checkedCount }} 件</span>
                </div>
                <div class="info-row total-row">
                  <span class="total-label">合计:</span>
                  <span class="total-price">¥{{ totalAmount }}</span>
                </div>
              </div>
              
              <el-button
                type="success"
                size="large"
                :disabled="checkedCount === 0"
                @click="handleCheckout"
                class="checkout-btn"
              >
                <el-icon><ShoppingBag /></el-icon>
                <span>去结算 ({{ checkedCount }})</span>
              </el-button>
            </div>
          </div>
        </div>
        
        <!-- 购物车为空 -->
        <div v-else class="empty-cart">
          <el-empty description="购物车空空如也～" :image-size="180">
            <template #image>
              <el-icon :size="180" color="#d3d3d3"><ShoppingCart /></el-icon>
            </template>
            <el-button type="success" size="large" @click="$router.push('/products')">
              <el-icon><ShoppingBag /></el-icon>
              <span>去逛逛</span>
            </el-button>
          </el-empty>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ShoppingCart, 
  ShoppingBag, 
  Delete, 
  Box,
  Goods
} from '@element-plus/icons-vue'
import { getCartList, updateCart, deleteCart, clearCart } from '@/api/cart'

const router = useRouter()
const store = useStore()

const loading = ref(false)
const cartList = ref([])
const checkAll = ref(false)

const getImageUrl = (path) => {
  if (!path) return '/placeholder.png'
  if (path.startsWith('http')) return path
  return `http://localhost:8080/api${path}`
}

const goToDetail = (productId) => {
  router.push(`/product/${productId}`)
}

const checkedCount = computed(() => {
  return cartList.value.filter(item => item.checked).length
})

const totalAmount = computed(() => {
  return cartList.value
    .filter(item => item.checked)
    .reduce((sum, item) => sum + item.productPrice * item.quantity, 0)
    .toFixed(2)
})

const handleCheckAll = () => {
  cartList.value.forEach(item => {
    item.checked = checkAll.value
  })
}

const handleCheck = () => {
  checkAll.value = cartList.value.every(item => item.checked)
}

const handleUpdateQuantity = async (item) => {
  try {
    await updateCart({
      cartId: item.id,
      quantity: item.quantity
    })
    
    // 更新购物车总数量（种类数）
    const totalCount = cartList.value.length
    store.dispatch('updateCartCount', totalCount)
  } catch (error) {
    console.error('更新数量失败:', error)
    ElMessage.error('更新数量失败')
    fetchCartList()
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteCart(id)
    ElMessage.success('删除成功')
    
    // 重新加载购物车列表
    await fetchCartList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleClearCart = async () => {
  try {
    await ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await clearCart()
    ElMessage.success('已清空购物车')
    
    // 清空购物车列表和数量
    cartList.value = []
    store.dispatch('updateCartCount', 0)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空购物车失败:', error)
      ElMessage.error('清空购物车失败')
    }
  }
}

const handleCheckout = () => {
  const selectedItems = cartList.value
    .filter(item => item.checked)
    .map(item => ({
      productId: item.productId,
      quantity: item.quantity,
      cartId: item.id
    }))
  
  router.push({
    path: '/order/confirm',
    query: {
      from: 'cart',
      items: JSON.stringify(selectedItems)
    }
  })
}

const fetchCartList = async () => {
  // 检查用户是否登录
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  loading.value = true
  try {
    const res = await getCartList()
    console.log('购物车原始数据:', res.data)
    
    // 确保数据是数组
    const data = Array.isArray(res.data) ? res.data : []
    cartList.value = data.map(item => ({
      ...item,
      checked: false
    }))
    
    // 计算购物车总数量（种类数）
    const totalCount = cartList.value.length
    store.dispatch('updateCartCount', totalCount)
    
    console.log('处理后的购物车数据:', cartList.value)
    console.log('购物车总数量:', totalCount)
  } catch (error) {
    console.error('获取购物车失败:', error)
    
    // 如果是401错误，说明未登录或token过期
    if (error.response?.status === 401) {
      ElMessage.warning('登录已过期，请重新登录')
      router.push('/login')
    } else {
      ElMessage.error('获取购物车数据失败，请稍后重试')
    }
    
    cartList.value = []
    store.dispatch('updateCartCount', 0)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchCartList()
})
</script>

<style scoped>
.cart-page {
  min-height: calc(100vh - 200px);
  padding: 30px 0;
  background: #f5f5f5;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 25px 30px;
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(82, 196, 26, 0.3);
}

.header-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-icon {
  color: #fff;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.header-text h1 {
  font-size: 28px;
  color: #fff;
  margin: 0 0 5px 0;
  font-weight: bold;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
}

.header-stats :deep(.el-tag) {
  padding: 10px 20px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
  color: #fff;
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 内容区域 */
.cart-content {
  background: #fff;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 2px 20px rgba(82, 196, 26, 0.1);
  border: 1px solid rgba(82, 196, 26, 0.1);
  min-height: 400px;
}

/* 购物车商品列表 */
.cart-items {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 30px;
}

.cart-item {
  display: grid;
  grid-template-columns: 50px 120px 2fr 140px 180px 140px 60px;
  gap: 20px;
  align-items: center;
  padding: 20px;
  background: #fff;
  border: 2px solid rgba(82, 196, 26, 0.1);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.cart-item:hover {
  border-color: rgba(82, 196, 26, 0.3);
  box-shadow: 0 4px 16px rgba(82, 196, 26, 0.15);
  transform: translateX(5px);
}

.item-checkbox {
  display: flex;
  justify-content: center;
}

.item-image {
  width: 120px;
  height: 120px;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.item-image:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 16px rgba(82, 196, 26, 0.3);
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.item-name {
  font-size: 17px;
  font-weight: 600;
  color: #2d5016;
  margin: 0;
  cursor: pointer;
  transition: all 0.3s;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-name:hover {
  color: #52c41a;
}

.item-stock {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #52c41a;
  font-weight: 500;
  margin: 0;
}

.item-stock .el-icon {
  font-size: 16px;
}

.item-price {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.price-value {
  font-size: 22px;
  color: #ff4d4f;
  font-weight: bold;
  font-family: Arial, sans-serif;
}

.price-unit {
  font-size: 13px;
  color: #999;
}

.item-quantity :deep(.el-input-number) {
  width: 140px;
}

.item-quantity :deep(.el-input-number .el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px rgba(82, 196, 26, 0.2) inset;
  transition: all 0.3s;
}

.item-quantity :deep(.el-input-number .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #52c41a inset;
}

.item-total {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.total-label {
  font-size: 13px;
  color: #999;
}

.total-value {
  font-size: 24px;
  color: #ff4d4f;
  font-weight: bold;
  font-family: Arial, sans-serif;
}

.item-actions {
  display: flex;
  justify-content: center;
}

.item-actions :deep(.el-button.is-circle) {
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(255, 77, 79, 0.2);
}

.item-actions :deep(.el-button.is-circle:hover) {
  transform: scale(1.1) rotate(10deg);
  box-shadow: 0 4px 12px rgba(255, 77, 79, 0.4);
}

/* 底部结算区 */
.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 25px 30px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e6f7ff 100%);
  border-radius: 12px;
  border: 2px solid rgba(82, 196, 26, 0.2);
  box-shadow: 0 4px 16px rgba(82, 196, 26, 0.1);
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.footer-left :deep(.el-checkbox) {
  font-size: 16px;
  font-weight: 500;
}

.footer-left :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #52c41a;
  border-color: #52c41a;
}

.checkbox-label {
  color: #2d5016;
  font-size: 16px;
  font-weight: 600;
}

.clear-btn {
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s;
}

.clear-btn:hover {
  transform: translateY(-2px);
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 30px;
}

.total-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 15px;
}

.info-label {
  color: #666;
}

.info-value {
  color: #52c41a;
  font-weight: 600;
  font-size: 16px;
}

.total-row {
  padding-top: 10px;
  border-top: 1px solid rgba(82, 196, 26, 0.2);
}

.total-label {
  font-size: 18px;
  color: #2d5016;
  font-weight: 600;
}

.total-price {
  color: #ff4d4f;
  font-size: 32px;
  font-weight: bold;
  text-shadow: 0 2px 4px rgba(255, 77, 79, 0.2);
  font-family: Arial, sans-serif;
}

.checkout-btn {
  padding: 15px 40px;
  font-size: 18px;
  font-weight: 600;
  border-radius: 30px;
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  border: none;
  box-shadow: 0 4px 16px rgba(82, 196, 26, 0.4);
  transition: all 0.3s;
}

.checkout-btn:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 6px 24px rgba(82, 196, 26, 0.5);
}

.checkout-btn:active:not(:disabled) {
  transform: translateY(-1px);
}

.checkout-btn:disabled {
  background: #d9d9d9;
  box-shadow: none;
}

.checkout-btn span {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 空购物车 */
.empty-cart {
  padding: 80px 20px;
}

.empty-cart :deep(.el-empty__description) {
  font-size: 18px;
  color: #999;
  margin-top: 20px;
}

.empty-cart :deep(.el-button) {
  margin-top: 30px;
  padding: 15px 40px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 30px;
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  border: none;
  box-shadow: 0 4px 16px rgba(82, 196, 26, 0.4);
  transition: all 0.3s;
}

.empty-cart :deep(.el-button:hover) {
  transform: translateY(-3px);
  box-shadow: 0 6px 24px rgba(82, 196, 26, 0.5);
}

.empty-cart :deep(.el-button span) {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 响应式 */
@media (max-width: 1200px) {
  .cart-item {
    grid-template-columns: 50px 100px 2fr 120px 150px 120px 60px;
    gap: 15px;
    padding: 15px;
  }
  
  .item-image {
    width: 100px;
    height: 100px;
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .cart-item {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .item-image {
    width: 100%;
    height: 200px;
  }
  
  .cart-footer {
    flex-direction: column;
    gap: 20px;
  }
  
  .footer-left,
  .footer-right {
    width: 100%;
    justify-content: center;
  }
  
  .total-info {
    align-items: center;
  }
}
</style>
