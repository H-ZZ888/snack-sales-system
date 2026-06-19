<template>
  <div class="order-confirm-page">
    <div class="container">
      <div class="page-header">
        <h1>确认订单</h1>
        <el-button :icon="ArrowLeft" @click="goBack" class="back-button">
          返回
        </el-button>
      </div>
      
      <div v-loading="loading" class="order-content">
        <!-- 收货地址 -->
        <div class="section address-section">
          <h2>收货地址</h2>
          
          <div v-if="addressList.length > 0" class="address-list">
            <div
              v-for="address in addressList"
              :key="address.id"
              class="address-item"
              :class="{ active: selectedAddressId === address.id }"
              @click="selectedAddressId = address.id"
            >
              <div class="address-info">
                <div class="receiver">
                  <span class="name">{{ address.receiverName }}</span>
                  <span class="phone">{{ address.receiverPhone }}</span>
                  <el-tag v-if="address.isDefault === 1" size="small" type="success">
                    默认
                  </el-tag>
                </div>
                <div class="address">
                  {{ address.province }} {{ address.city }} {{ address.district }} {{ address.detail }}
                </div>
              </div>
              <el-icon v-if="selectedAddressId === address.id" class="check-icon" color="#52c41a">
                <CircleCheck />
              </el-icon>
            </div>
          </div>
          
          <el-button @click="$router.push('/user/addresses')">
            管理收货地址
          </el-button>
        </div>
        
        <!-- 商品信息 -->
        <div class="section goods-section">
          <h2>商品信息</h2>
          
          <div class="goods-list">
            <div
              v-for="item in orderItems"
              :key="item.productId"
              class="goods-item"
            >
              <img :src="getImageUrl(item.productImage)" :alt="item.productName" />
              <div class="goods-info">
                <div class="name">{{ item.productName }}</div>
                <div class="spec">{{ item.productPrice }}/{{ item.productUnit }}</div>
              </div>
              <div class="quantity">x{{ item.quantity }}</div>
              <div class="amount">¥{{ (item.productPrice * item.quantity).toFixed(2) }}</div>
            </div>
          </div>
        </div>
        
        <!-- 备注 -->
        <div class="section remark-section">
          <h2>订单备注</h2>
          <el-input
            v-model="remark"
            type="textarea"
            :rows="3"
            placeholder="请输入订单备注（选填）"
            maxlength="200"
            show-word-limit
          />
        </div>
        
        <!-- 结算 -->
        <div class="section settlement-section">
          <div class="settlement-content">
            <div class="price-detail">
              <div class="price-item">
                <span>商品金额：</span>
                <span>¥{{ totalAmount }}</span>
              </div>
              <div class="price-item total">
                <span>应付金额：</span>
                <span class="amount">¥{{ totalAmount }}</span>
              </div>
            </div>
            
            <el-button
              type="primary"
              size="large"
              :disabled="!selectedAddressId || orderItems.length === 0"
              :loading="submitting"
              @click="handleSubmit"
            >
              提交订单
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { CircleCheck, ArrowLeft } from '@element-plus/icons-vue'
import { getAddressList } from '@/api/address'
import { createOrder, payOrder } from '@/api/order'
import { getProductDetail } from '@/api/product'
import { getCartList } from '@/api/cart'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const submitting = ref(false)
const addressList = ref([])
const selectedAddressId = ref(null)
const orderItems = ref([])
const remark = ref('')

const totalAmount = computed(() => {
  return orderItems.value
    .reduce((sum, item) => sum + item.productPrice * item.quantity, 0)
    .toFixed(2)
})

const getImageUrl = (path) => {
  if (!path) return '/placeholder.png'
  if (path.startsWith('http')) return path
  return `http://localhost:8080/api${path}`
}

const fetchAddresses = async () => {
  try {
    const res = await getAddressList()
    addressList.value = res.data || []
    
    // 默认选中默认地址
    const defaultAddress = addressList.value.find(item => item.isDefault === 1)
    if (defaultAddress) {
      selectedAddressId.value = defaultAddress.id
    } else if (addressList.value.length > 0) {
      selectedAddressId.value = addressList.value[0].id
    }
  } catch (error) {
    console.error('获取收货地址失败:', error)
  }
}

const fetchOrderItems = async () => {
  loading.value = true
  try {
    if (route.query.from === 'cart') {
      // 从购物车结算
      const items = JSON.parse(route.query.items || '[]')
      const cartRes = await getCartList()
      const cartList = cartRes.data || []
      
      orderItems.value = items.map(item => {
        const cartItem = cartList.find(c => c.id === item.cartId)
        return {
          productId: item.productId,
          productName: cartItem.productName,
          productImage: cartItem.productImage,
          productPrice: cartItem.productPrice,
          productUnit: cartItem.productUnit,
          quantity: item.quantity
        }
      })
    } else {
      // 直接购买
      const productId = route.query.productId
      const quantity = parseInt(route.query.quantity || 1)
      
      const res = await getProductDetail(productId)
      const product = res.data
      
      orderItems.value = [{
        productId: product.id,
        productName: product.name,
        productImage: product.image,
        productPrice: product.price,
        productUnit: product.unit,
        quantity: quantity
      }]
    }
  } catch (error) {
    console.error('获取订单信息失败:', error)
    ElMessage.error('获取订单信息失败')
    router.back()
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!selectedAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  
  const address = addressList.value.find(item => item.id === selectedAddressId.value)
  if (!address) {
    ElMessage.warning('收货地址不存在')
    return
  }
  
  submitting.value = true
  try {
    // 先创建订单
    const orderData = {
      receiverName: address.receiverName,
      receiverPhone: address.receiverPhone,
      receiverAddress: `${address.province}${address.city}${address.district}${address.detail}`,
      remark: remark.value,
      items: orderItems.value.map(item => ({
        productId: item.productId,
        quantity: item.quantity
      }))
    }
    
    const res = await createOrder(orderData)
    const orderId = res.data
    
    // 订单创建成功后，询问是否支付
    try {
      await ElMessageBox.confirm(
        `确认支付 ¥${totalAmount.value} 吗？`,
        '确认支付',
        {
          confirmButtonText: '确定支付',
          cancelButtonText: '取消',
          type: 'warning',
          center: true
        }
      )
      
      // 用户确认支付，调用支付接口
      try {
        await payOrder(orderId)
        ElMessage.success('支付成功')
        router.push('/user/orders')
      } catch (payError) {
        console.error('支付失败:', payError)
        ElMessage.error('支付失败，订单已创建，您可以在"我的订单"中继续支付')
        router.push('/user/orders')
      }
    } catch (error) {
      // 用户点击取消，订单已创建但未支付，跳转到订单页面
      ElMessage.info('订单已创建，您可以稍后在"我的订单"中完成支付')
      router.push('/user/orders')
    }
  } catch (error) {
    console.error('提交订单失败:', error)
    ElMessage.error('提交订单失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  fetchAddresses()
  fetchOrderItems()
})
</script>

<style scoped>
.order-confirm-page {
  min-height: calc(100vh - 200px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  margin: 0;
}

.back-button {
  border-radius: 8px;
  transition: all 0.3s;
}

.back-button:hover {
  color: #ff6b6b;
  border-color: #ff6b6b;
  background: rgba(255, 107, 107, 0.1);
  transform: translateX(-5px);
}

.order-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.section {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}

.section h2 {
  font-size: 18px;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e8e8e8;
}

/* 收货地址 */
.address-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 15px;
  margin-bottom: 15px;
}

.address-item {
  position: relative;
  padding: 15px;
  border: 2px solid #e8e8e8;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.address-item:hover,
.address-item.active {
  border-color: #52c41a;
}

.address-info .receiver {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.receiver .name {
  font-weight: bold;
  font-size: 16px;
}

.receiver .phone {
  color: #666;
}

.address {
  color: #666;
  line-height: 1.6;
}

.check-icon {
  position: absolute;
  top: 15px;
  right: 15px;
  font-size: 24px;
}

/* 商品信息 */
.goods-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.goods-item {
  display: grid;
  grid-template-columns: 80px 1fr auto auto;
  gap: 15px;
  align-items: center;
  padding: 15px;
  background: #f5f5f5;
  border-radius: 8px;
}

.goods-item img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.goods-info .name {
  font-size: 16px;
  margin-bottom: 8px;
}

.goods-info .spec {
  font-size: 14px;
  color: #666;
}

.quantity {
  font-size: 14px;
  color: #666;
}

.amount {
  font-size: 18px;
  color: #ff4d4f;
  font-weight: bold;
}

/* 结算 */
.settlement-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-detail {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.price-item {
  display: flex;
  gap: 20px;
  font-size: 16px;
}

.price-item.total {
  font-size: 18px;
  font-weight: bold;
}

.price-item.total .amount {
  color: #ff4d4f;
  font-size: 24px;
}
</style>

