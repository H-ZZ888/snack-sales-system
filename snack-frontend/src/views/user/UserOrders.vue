<template>
  <div class="user-orders">
    <h2 class="page-title">我的订单</h2>

    <!-- 订单状态筛选 -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="order-tabs">
      <el-tab-pane label="全部订单" name="all"></el-tab-pane>
      <el-tab-pane label="待付款" name="pending"></el-tab-pane>
      <el-tab-pane label="待发货" name="paid"></el-tab-pane>
      <el-tab-pane label="待收货" name="shipped"></el-tab-pane>
      <el-tab-pane label="已完成" name="completed"></el-tab-pane>
      <el-tab-pane label="已取消" name="cancelled"></el-tab-pane>
    </el-tabs>

    <!-- 订单列表 -->
    <div v-loading="loading" class="orders-list">
      <div v-if="orders.length === 0" class="empty-state">
        <el-empty description="暂无订单" />
      </div>

      <div v-else>
        <div v-for="order in orders" :key="order.id" class="order-card">
          <!-- 订单头部 -->
          <div class="order-header">
            <div class="order-info">
              <span class="order-no">订单号：{{ order.orderNo }}</span>
              <span class="order-time">{{ order.createTime }}</span>
            </div>
            <el-tag :type="getStatusType(order)">
              {{ getStatusText(order) }}
            </el-tag>
          </div>

          <!-- 订单商品 -->
          <div class="order-items">
            <div
              v-for="item in order.items"
              :key="item.id"
              class="order-item"
              @click="handleProductClick(item.productId)"
            >
              <img :src="getImageUrl(item.productImage)" :alt="item.productName" class="item-image" />
              <div class="item-info">
                <div class="item-name">{{ item.productName }}</div>
                <div class="item-spec">数量：{{ item.quantity }}{{ item.unit || '件' }}</div>
              </div>
              <div class="item-price">¥{{ item.price }}</div>
            </div>
          </div>

          <!-- 订单底部 -->
          <div class="order-footer">
            <div class="order-total">
              <span>实付款：</span>
              <span class="total-amount">¥{{ order.totalAmount }}</span>
            </div>
            <div class="order-actions">
              <el-button
                v-if="order.payStatus === '未支付' && order.orderStatus !== '已取消'"
                type="primary"
                size="small"
                @click="handlePay(order)"
              >
                立即付款
              </el-button>
              <el-button
                v-if="order.orderStatus === '已发货'"
                type="success"
                size="small"
                @click="handleConfirm(order)"
              >
                确认收货
              </el-button>
              <el-button
                v-if="order.orderStatus === '已完成'"
                type="warning"
                size="small"
                @click="handleReview(order)"
              >
                评价商品
              </el-button>
              <el-button
                v-if="order.orderStatus !== '已取消'"
                size="small"
                @click="handleViewDetail(order)"
              >
                查看详情
              </el-button>
              <el-button
                v-if="order.payStatus === '未支付' && order.orderStatus !== '已取消'"
                type="danger"
                size="small"
                @click="handleCancel(order)"
              >
                取消订单
              </el-button>
              <el-button
                v-if="order.orderStatus === '已取消' || order.orderStatus === '已完成'"
                type="danger"
                size="small"
                plain
                @click="handleDelete(order)"
              >
                删除订单
              </el-button>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <el-pagination
          v-if="total > 0"
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[5, 10, 20, 30]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="fetchOrders"
          @size-change="handleSizeChange"
          class="pagination"
        />
      </div>
    </div>

    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="700px"
      :close-on-click-modal="false"
    >
      <div v-if="selectedOrder" class="order-detail">
        <!-- 订单信息 -->
        <div class="detail-section">
          <h3 class="section-title">订单信息</h3>
          <div class="detail-row">
            <span class="detail-label">订单号：</span>
            <span class="detail-value">{{ selectedOrder.orderNo }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">下单时间：</span>
            <span class="detail-value">{{ selectedOrder.createTime }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">订单状态：</span>
            <span class="detail-value">
              <el-tag :type="getStatusType(selectedOrder)">
                {{ getStatusText(selectedOrder) }}
              </el-tag>
            </span>
          </div>
        </div>

        <!-- 收货信息 -->
        <div class="detail-section">
          <h3 class="section-title">收货信息</h3>
          <div class="detail-row">
            <span class="detail-label">收货人：</span>
            <span class="detail-value">{{ selectedOrder.receiverName }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">联系电话：</span>
            <span class="detail-value">{{ selectedOrder.receiverPhone }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">收货地址：</span>
            <span class="detail-value">{{ selectedOrder.receiverAddress }}</span>
          </div>
        </div>

        <!-- 商品信息 -->
        <div class="detail-section">
          <h3 class="section-title">商品信息</h3>
          <div class="detail-items">
            <div
              v-for="item in selectedOrder.items"
              :key="item.id"
              class="detail-item"
              @click="handleProductClick(item.productId)"
            >
              <img :src="getImageUrl(item.productImage)" :alt="item.productName" class="detail-item-image" />
              <div class="detail-item-info">
                <div class="detail-item-name">{{ item.productName }}</div>
                <div class="detail-item-spec">数量：{{ item.quantity }}{{ item.unit || '件' }}</div>
              </div>
              <div class="detail-item-price">¥{{ item.price }}</div>
            </div>
          </div>
        </div>

        <!-- 费用信息 -->
        <div class="detail-section">
          <h3 class="section-title">费用信息</h3>
          <div class="detail-row">
            <span class="detail-label">商品总额：</span>
            <span class="detail-value">¥{{ selectedOrder.totalAmount }}</span>
          </div>
          <div class="detail-row total-row">
            <span class="detail-label">实付款：</span>
            <span class="detail-value total-value">¥{{ selectedOrder.totalAmount }}</span>
          </div>
        </div>

        <!-- 操作按钮区域 -->
        <div class="detail-actions">
          <!-- 申请退款按钮（已支付且未完成、未取消状态） -->
          <el-button
            v-if="selectedOrder.payStatus === '已支付' && !['已完成', '已取消'].includes(selectedOrder.orderStatus)"
            type="warning"
            size="large"
            @click="handleRefund"
            class="action-btn refund-btn"
          >
            申请退款
          </el-button>
          
          <!-- 删除订单按钮（已取消或已完成状态） -->
          <el-button
            v-if="selectedOrder.orderStatus === '已取消' || selectedOrder.orderStatus === '已完成'"
            type="danger"
            size="large"
            plain
            @click="handleDeleteFromDetail"
            class="action-btn delete-btn"
          >
            删除订单
          </el-button>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="detailDialogVisible = false" size="large">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 评价对话框 -->
    <el-dialog
      v-model="reviewDialogVisible"
      title="评价商品"
      width="650px"
      :close-on-click-modal="false"
      class="review-dialog"
    >
      <div class="review-form-container">
        <!-- 商品信息 -->
        <div class="review-product-info">
          <img :src="getImageUrl(reviewForm.productImage)" :alt="reviewForm.productName" class="review-product-img" />
          <div class="review-product-details">
            <div class="review-product-title">{{ reviewForm.productName }}</div>
            <div class="review-product-hint">分享您的真实使用感受</div>
          </div>
        </div>
        
        <!-- 评分区域 -->
        <div class="review-rating-section">
          <div class="rating-label">
            <span class="label-text">商品评分</span>
            <span class="required-mark">*</span>
          </div>
          <div class="rating-content">
            <el-rate
              v-model="reviewForm.rating"
              :colors="['#ff6b6b', '#feca57', '#52c41a']"
              size="large"
              show-text
              :texts="['非常差', '比较差', '一般般', '比较好', '非常好']"
            />
          </div>
        </div>
        
        <!-- 评价内容 -->
        <div class="review-content-section">
          <div class="content-label">
            <span class="label-text">评价内容</span>
            <span class="required-mark">*</span>
          </div>
          <el-input
            v-model="reviewForm.content"
            type="textarea"
            :rows="6"
            placeholder="请分享您的使用感受，例如：商品质量、口味、包装、物流等方面的体验..."
            maxlength="500"
            show-word-limit
            class="review-textarea"
          />
        </div>

        <!-- 评价图片 -->
        <div class="review-images-section">
          <div class="images-label">
            <span class="label-text">评价图片</span>
            <span class="optional-text">（选填，最多5张）</span>
          </div>
          <div class="images-upload-area">
            <div class="uploaded-images">
              <div
                v-for="(image, index) in reviewForm.images"
                :key="index"
                class="image-item"
              >
                <img :src="getImageUrl(image)" alt="评价图片" />
                <div class="image-mask">
                  <el-icon class="remove-icon" @click="handleRemoveImage(index)">
                    <Delete />
                  </el-icon>
                </div>
              </div>
              <el-upload
                v-if="reviewForm.images.length < 5"
                :action="imageUploadUrl"
                :headers="{ Authorization: $store.state.user.token }"
                :show-file-list="false"
                :before-upload="beforeImageUpload"
                :on-success="handleImageSuccess"
                :on-error="handleImageError"
                class="image-uploader"
              >
                <div class="upload-trigger">
                  <el-icon class="upload-icon"><Plus /></el-icon>
                  <div class="upload-text">上传图片</div>
                </div>
              </el-upload>
            </div>
            <div class="upload-tips">
              支持 JPG、PNG、GIF 格式，单张图片不超过 5MB
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <div class="review-dialog-footer">
          <el-button @click="reviewDialogVisible = false" size="large" class="cancel-btn">
            取消
          </el-button>
          <el-button 
            type="primary" 
            @click="submitReview" 
            size="large" 
            :loading="submittingReview"
            class="submit-btn"
          >
            {{ submittingReview ? '提交中...' : '提交评价' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import { getOrderList, cancelOrder, receiveOrder, payOrder, deleteOrder } from '@/api/order'
import { addReview } from '@/api/review'

const router = useRouter()
const loading = ref(false)
const activeTab = ref('all')
const orders = ref([])
const pageNum = ref(1)
const pageSize = ref(5)
const total = ref(0)

const getImageUrl = (path) => {
  if (!path) return '/placeholder.png'
  if (path.startsWith('http')) return path
  return `http://localhost:8080/api${path}`
}

const getStatusType = (order) => {
  // 优先检查订单状态（已取消、已完成等）
  if (order.orderStatus === '已取消') {
    return 'danger'
  }
  
  if (order.orderStatus === '已完成') {
    return 'success'
  }
  
  // 未支付显示警告色
  if (order.payStatus === '未支付') {
    return 'warning'
  }
  
  // 根据订单状态显示不同颜色
  const typeMap = {
    '未发货': 'primary',
    '已发货': 'info'
  }
  return typeMap[order.orderStatus] || 'info'
}

const getStatusText = (order) => {
  // 优先检查订单状态（已取消、已完成等）
  if (order.orderStatus === '已取消') {
    return '已取消'
  }
  
  if (order.orderStatus === '已完成') {
    return '已完成'
  }
  
  // 未支付显示"待付款"
  if (order.payStatus === '未支付') {
    return '待付款'
  }
  
  // 根据订单状态显示文本
  const textMap = {
    '未发货': '待发货',
    '已发货': '待收货'
  }
  return textMap[order.orderStatus] || order.orderStatus
}

const handleProductClick = (productId) => {
  router.push(`/product/${productId}`)
}

const handleTabChange = () => {
  pageNum.value = 1
  fetchOrders()
}

const handleSizeChange = () => {
  pageNum.value = 1
  fetchOrders()
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    
    // 根据tab设置筛选条件
    if (activeTab.value === 'pending') {
      // 待付款：未支付状态
      params.payStatus = '未支付'
    } else if (activeTab.value === 'paid') {
      // 待发货：已支付且未发货
      params.payStatus = '已支付'
      params.orderStatus = '未发货'
    } else if (activeTab.value === 'shipped') {
      // 待收货：已发货
      params.orderStatus = '已发货'
    } else if (activeTab.value === 'completed') {
      // 已完成
      params.orderStatus = '已完成'
    } else if (activeTab.value === 'cancelled') {
      // 已取消
      params.orderStatus = '已取消'
    }

    const res = await getOrderList(params)
    orders.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const handlePay = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确认支付 ¥${order.totalAmount} 吗？`,
      '确认支付',
      {
        confirmButtonText: '确定支付',
        cancelButtonText: '取消',
        type: 'warning',
        center: true
      }
    )
    
    // 调用支付接口
    await payOrder(order.id)
    ElMessage.success({
      message: '支付成功',
      offset: 100
    })
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('支付失败:', error)
      const errorMsg = error.response?.data?.message || error.message || '支付失败'
      ElMessage.error(errorMsg)
    }
  }
}

const handleConfirm = async (order) => {
  try {
    await ElMessageBox.confirm('确认收到商品了吗？', '确认收货', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
      center: true
    })
    
    await receiveOrder(order.id)
    ElMessage.success({
      message: '确认收货成功',
      offset: 100
    })
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认收货失败:', error)
      const errorMsg = error.response?.data?.message || error.message || '确认收货失败'
      ElMessage.error(errorMsg)
    }
  }
}

const handleCancel = async (order) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '取消订单', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      center: true
    })
    
    await cancelOrder(order.id)
    ElMessage.success({
      message: '订单已取消',
      offset: 100
    })
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      const errorMsg = error.response?.data?.message || error.message || '取消订单失败'
      ElMessage.error(errorMsg)
    }
  }
}

const handleViewDetail = (order) => {
  selectedOrder.value = order
  detailDialogVisible.value = true
}

const handleRefund = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要申请退款吗？退款后订单将被取消，款项将原路退回。',
      '申请退款',
      {
        confirmButtonText: '确定申请',
        cancelButtonText: '取消',
        type: 'warning',
        center: true
      }
    )
    
    await cancelOrder(selectedOrder.value.id)
    ElMessage.success({
      message: '退款申请已提交，订单已取消',
      offset: 100
    })
    detailDialogVisible.value = false
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('申请退款失败:', error)
      ElMessage.error('申请退款失败')
    }
  }
}

const handleDelete = async (order) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该订单吗？删除后将无法恢复。',
      '删除订单',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        center: true
      }
    )
    
    await deleteOrder(order.id)
    ElMessage.success({
      message: '删除成功',
      duration: 3000,
      offset: 100,
      showClose: true
    })
    console.log('订单删除成功，提示已弹出')
    // 延迟刷新列表
    setTimeout(() => {
      fetchOrders()
    }, 500)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除订单失败:', error)
      const errorMsg = error.response?.data?.message || error.message || '删除订单失败'
      ElMessage.error(errorMsg)
    }
  }
}

const handleDeleteFromDetail = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该订单吗？删除后将无法恢复。',
      '删除订单',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        center: true
      }
    )
    
    await deleteOrder(selectedOrder.value.id)
    ElMessage.success({
      message: '删除成功',
      duration: 3000,
      offset: 100,
      showClose: true
    })
    console.log('详情页订单删除成功，提示已弹出')
    detailDialogVisible.value = false
    setTimeout(() => {
      fetchOrders()
    }, 500)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除订单失败:', error)
      const errorMsg = error.response?.data?.message || error.message || '删除订单失败'
      ElMessage.error(errorMsg)
    }
  }
}

const handleReview = (order) => {
  // 只评价第一个商品，如果需要评价多个商品，可以改为循环
  if (order.items && order.items.length > 0) {
    const firstItem = order.items[0]
    reviewForm.value = {
      orderId: order.id,
      productId: firstItem.productId,
      productName: firstItem.productName,
      productImage: firstItem.productImage,
      rating: 5,
      content: '',
      images: []
    }
    reviewDialogVisible.value = true
  } else {
    ElMessage.warning('订单商品信息不完整')
  }
}

// 图片上传前的验证
const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB！')
    return false
  }
  return true
}

// 图片上传成功
const handleImageSuccess = (response) => {
  if (response.code === 200) {
    reviewForm.value.images.push(response.data)
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '图片上传失败')
  }
}

// 图片上传失败
const handleImageError = () => {
  ElMessage.error('图片上传失败，请重试')
}

// 删除图片
const handleRemoveImage = (index) => {
  reviewForm.value.images.splice(index, 1)
}

const submitReview = async () => {
  if (!reviewForm.value.rating) {
    ElMessage.warning('请选择评分')
    return
  }
  
  if (!reviewForm.value.content || reviewForm.value.content.trim() === '') {
    ElMessage.warning('请填写评价内容')
    return
  }
  
  submittingReview.value = true
  try {
    // 将图片数组转换为逗号分隔的字符串
    const imagesStr = reviewForm.value.images.join(',')
    
    await addReview({
      orderId: reviewForm.value.orderId,
      productId: reviewForm.value.productId,
      rating: reviewForm.value.rating,
      content: reviewForm.value.content.trim(),
      images: imagesStr
    })
    
    ElMessage.success({
      message: '评价成功',
      duration: 3000,
      offset: 100,
      showClose: true
    })
    reviewDialogVisible.value = false
    setTimeout(() => {
      fetchOrders()
    }, 500)
  } catch (error) {
    console.error('提交评价失败:', error)
    const errorMsg = error.response?.data?.message || error.message || '提交评价失败'
    ElMessage.error(errorMsg)
  } finally {
    submittingReview.value = false
  }
}

const selectedOrder = ref(null)
const detailDialogVisible = ref(false)

// 评价相关
const reviewDialogVisible = ref(false)
const submittingReview = ref(false)
const reviewForm = ref({
  orderId: null,
  productId: null,
  productName: '',
  productImage: '',
  rating: 5,
  content: '',
  images: [] // 评价图片列表
})
const uploadingImage = ref(false)
const imageUploadUrl = getUploadUrl()

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.user-orders {
  width: 100%;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f0f0;
}

.order-tabs {
  margin-bottom: 20px;
}

.orders-list {
  min-height: 400px;
}

.empty-state {
  padding: 60px 0;
}

.order-card {
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  margin-bottom: 20px;
  overflow: hidden;
  transition: all 0.3s;
}

.order-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #fafafa;
  border-bottom: 1px solid #e8e8e8;
}

.order-info {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #666;
}

.order-no {
  font-weight: 500;
  color: #333;
}

.order-items {
  padding: 20px;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 10px 0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.order-item:hover {
  background-color: #fafafa;
}

.order-item:not(:last-child) {
  border-bottom: 1px dashed #e8e8e8;
  margin-bottom: 10px;
}

.item-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 6px;
  flex-shrink: 0;
}

.item-info {
  flex: 1;
}

.item-name {
  font-size: 15px;
  color: #333;
  margin-bottom: 8px;
  font-weight: 500;
}

.item-spec {
  font-size: 13px;
  color: #999;
}

.item-price {
  font-size: 16px;
  color: #ff6b6b;
  font-weight: 600;
  flex-shrink: 0;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #fafafa;
  border-top: 1px solid #e8e8e8;
}

.order-total {
  font-size: 14px;
  color: #666;
}

.total-amount {
  font-size: 20px;
  color: #ff6b6b;
  font-weight: 700;
  margin-left: 5px;
}

.order-actions {
  display: flex;
  gap: 10px;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .order-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .order-actions {
    width: 100%;
    flex-wrap: wrap;
  }
  
  .order-actions .el-button {
    flex: 1;
  }
}

/* 订单详情对话框样式 */
.order-detail {
  max-height: 600px;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 25px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 15px 0;
  padding-left: 10px;
  border-left: 3px solid #ff6b6b;
}

.detail-row {
  display: flex;
  align-items: center;
  padding: 8px 0;
  font-size: 14px;
}

.detail-label {
  width: 100px;
  color: #666;
  flex-shrink: 0;
}

.detail-value {
  flex: 1;
  color: #333;
}

.total-row {
  margin-top: 10px;
  padding-top: 15px;
  border-top: 1px dashed #e8e8e8;
  font-size: 16px;
}

.total-value {
  font-size: 20px;
  color: #ff6b6b;
  font-weight: 700;
}

.detail-items {
  background: #fafafa;
  padding: 15px;
  border-radius: 6px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.detail-item:hover {
  background-color: #eaeaea;
}

.detail-item:not(:last-child) {
  border-bottom: 1px dashed #e0e0e0;
}

.detail-item-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0;
}

.detail-item-info {
  flex: 1;
}

.detail-item-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
  font-weight: 500;
}

.detail-item-spec {
  font-size: 12px;
  color: #999;
}

.detail-item-price {
  font-size: 15px;
  color: #ff6b6b;
  font-weight: 600;
  flex-shrink: 0;
}

.detail-actions {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 2px dashed #e8e8e8;
  display: flex;
  justify-content: center;
  gap: 15px;
}

.action-btn {
  min-width: 180px;
  height: 45px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  border: none;
  transition: all 0.3s;
}

.pay-btn {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
}

.pay-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(82, 196, 26, 0.4);
}

.cancel-btn {
  background: linear-gradient(135deg, #f5576c 0%, #f093fb 100%);
}

.cancel-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(245, 87, 108, 0.4);
}

.refund-btn {
  background: linear-gradient(135deg, #feca57 0%, #ff9ff3 100%);
  color: #fff;
}

.refund-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(254, 202, 87, 0.4);
}

.delete-btn {
  border: 2px solid #ff4d4f;
  color: #ff4d4f;
  background: white;
}

.delete-btn:hover {
  background: #ff4d4f;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 77, 79, 0.3);
}

/* 分页样式 */
.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.pagination :deep(.el-pagination__total) {
  font-weight: 500;
  color: #666;
}

.pagination :deep(.el-pagination__sizes) {
  margin-right: 10px;
}

.pagination :deep(.el-pager li) {
  min-width: 35px;
  height: 35px;
  line-height: 35px;
  border-radius: 6px;
  margin: 0 3px;
  font-weight: 500;
}

.pagination :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  color: white;
}

.pagination :deep(.btn-prev),
.pagination :deep(.btn-next) {
  min-width: 35px;
  height: 35px;
  line-height: 35px;
  border-radius: 6px;
  margin: 0 3px;
}

.pagination :deep(.el-pagination__jump) {
  margin-left: 10px;
  font-weight: 500;
  color: #666;
}

/* 评价对话框样式 */
.review-product {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 8px;
}

.review-product-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 6px;
}

.review-product-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

/* 评价图片上传样式 */
.review-images-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.images-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.optional-text {
  font-size: 13px;
  font-weight: 400;
  color: #999;
}

.images-upload-area {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.uploaded-images {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.image-item {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid #e8e8e8;
  transition: all 0.3s;
}

.image-item:hover {
  border-color: #ff6b6b;
  transform: scale(1.05);
}

.image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.image-item:hover .image-mask {
  opacity: 1;
}

.remove-icon {
  font-size: 24px;
  color: white;
  cursor: pointer;
  transition: all 0.3s;
}

.remove-icon:hover {
  color: #ff6b6b;
  transform: scale(1.2);
}

.image-uploader {
  width: 100px;
  height: 100px;
}

.upload-trigger {
  width: 100px;
  height: 100px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  background: #fafafa;
}

.upload-trigger:hover {
  border-color: #ff6b6b;
  background: rgba(255, 107, 107, 0.05);
}

.upload-icon {
  font-size: 28px;
  color: #999;
  margin-bottom: 4px;
  transition: all 0.3s;
}

.upload-trigger:hover .upload-icon {
  color: #ff6b6b;
  transform: scale(1.1);
}

.upload-text {
  font-size: 12px;
  color: #999;
  transition: all 0.3s;
}

.upload-trigger:hover .upload-text {
  color: #ff6b6b;
}

.upload-tips {
  font-size: 12px;
  color: #999;
  line-height: 1.5;
  padding: 8px 12px;
  background: #f5f5f5;
  border-radius: 6px;
}
</style>
