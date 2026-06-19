<template>
  <div class="admin-orders">
    <el-card shadow="hover">
      <!-- 操作栏 -->
      <div class="toolbar">
        <div class="toolbar-left">
          <el-radio-group v-model="orderStatus" @change="handleStatusFilter">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="未支付">未支付</el-radio-button>
            <el-radio-button label="已支付">已支付</el-radio-button>
            <el-radio-button label="已发货">已发货</el-radio-button>
            <el-radio-button label="已完成">已完成</el-radio-button>
            <el-radio-button label="已取消">已取消</el-radio-button>
          </el-radio-group>
          <el-button
            type="danger"
            :icon="Delete"
            :disabled="selectedIds.length === 0"
            @click="handleBatchDelete"
            style="margin-left: 10px;"
          >
            批量删除 {{ selectedIds.length > 0 ? `(${selectedIds.length})` : '' }}
          </el-button>
        </div>
        <div class="toolbar-right">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索订单号/收货人"
            :prefix-icon="Search"
            clearable
            style="width: 250px; margin-right: 10px;"
            @keyup.enter="loadOrders"
          />
          <el-button type="primary" :icon="Search" @click="loadOrders">
            搜索
          </el-button>
        </div>
      </div>
      
      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="orders"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="receiverName" label="收货人" width="100" />
        <el-table-column prop="receiverPhone" label="联系电话" width="120" />
        <el-table-column prop="receiverAddress" label="收货地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="totalAmount" label="订单金额" width="120" align="right">
          <template #default="{ row }">
            <span style="color: #f5222d; font-weight: bold;">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="payStatus" label="支付状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.payStatus === '已支付'" type="success">{{ row.payStatus }}</el-tag>
            <el-tag v-else type="info">{{ row.payStatus }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="订单状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.orderStatus === '已完成'" type="success">{{ row.orderStatus }}</el-tag>
            <el-tag v-else-if="row.orderStatus === '已发货'" type="primary">{{ row.orderStatus }}</el-tag>
            <el-tag v-else-if="row.orderStatus === '已取消'" type="danger">{{ row.orderStatus }}</el-tag>
            <el-tag v-else type="warning">{{ row.orderStatus }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="160" />
        <el-table-column label="操作" width="340" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              type="info"
              size="small"
              class="action-btn view-btn"
              @click="handleViewDetail(row)"
            >
              <el-icon><View /></el-icon>
              详情
            </el-button>
            <el-button
              v-if="row.orderStatus === '未发货' && row.payStatus === '已支付'"
              type="primary"
              size="small"
              class="action-btn ship-btn"
              @click="handleShip(row)"
            >
              <el-icon><Van /></el-icon>
              发货
            </el-button>
            <el-button
              type="warning"
              size="small"
              class="action-btn status-btn"
              @click="handleChangeStatus(row)"
            >
              修改状态
            </el-button>
            <el-button
              type="danger"
              size="small"
              class="action-btn delete-btn"
              @click="handleDelete(row)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          :current-page="pagination.pageNum"
          :page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
    
    <!-- 订单详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="800px"
      top="5vh"
      :close-on-click-modal="false"
    >
      <div v-if="currentOrder" class="order-detail-dialog">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ currentOrder.createTime }}</el-descriptions-item>
          <el-descriptions-item label="收货人">{{ currentOrder.receiverName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentOrder.receiverPhone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">{{ currentOrder.receiverAddress }}</el-descriptions-item>
          <el-descriptions-item label="订单金额">
            <span style="color: #f5222d; font-weight: bold; font-size: 16px;">¥{{ currentOrder.totalAmount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="支付状态">
            <el-tag v-if="currentOrder.payStatus === '已支付'" type="success">{{ currentOrder.payStatus }}</el-tag>
            <el-tag v-else type="info">{{ currentOrder.payStatus }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="订单状态" :span="2">
            <el-tag v-if="currentOrder.orderStatus === '已完成'" type="success">{{ currentOrder.orderStatus }}</el-tag>
            <el-tag v-else-if="currentOrder.orderStatus === '已发货'" type="primary">{{ currentOrder.orderStatus }}</el-tag>
            <el-tag v-else-if="currentOrder.orderStatus === '已取消'" type="danger">{{ currentOrder.orderStatus }}</el-tag>
            <el-tag v-else type="warning">{{ currentOrder.orderStatus }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item v-if="currentOrder.remark" label="订单备注" :span="2">{{ currentOrder.remark }}</el-descriptions-item>
        </el-descriptions>
        
        <el-divider content-position="left">订单商品</el-divider>
        
        <el-table :data="currentOrder.items" border style="width: 100%">
          <el-table-column label="商品图片" width="100" align="center">
            <template #default="{ row: item }">
              <el-image
                :src="getImageUrl(item.productImage)"
                fit="cover"
                style="width: 60px; height: 60px; border-radius: 4px;"
                :preview-src-list="[getImageUrl(item.productImage)]"
              />
            </template>
          </el-table-column>
          <el-table-column prop="productName" label="商品名称" min-width="150" />
          <el-table-column prop="price" label="单价" width="100" align="right">
            <template #default="{ row: item }">
              ¥{{ item.price }}
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" align="center" />
          <el-table-column prop="totalAmount" label="小计" width="120" align="right">
            <template #default="{ row: item }">
              <span style="color: #f5222d; font-weight: bold;">¥{{ item.totalAmount }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
    
    <!-- 修改状态对话框 -->
    <el-dialog
      v-model="statusDialogVisible"
      title="修改订单状态"
      width="450px"
      :close-on-click-modal="false"
    >
      <el-form label-width="100px">
        <el-form-item label="订单状态">
          <el-select v-model="statusForm.orderStatus" placeholder="请选择订单状态" style="width: 100%">
            <el-option
              v-for="option in orderStatusOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitStatus">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Van, Delete, View } from '@element-plus/icons-vue'
import {
  getOrderList,
  shipOrder,
  deleteOrder,
  updateOrderStatus
} from '@/api/admin'

const loading = ref(false)
const orders = ref([])
const searchKeyword = ref('')
const orderStatus = ref('')
const selectedIds = ref([])
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const detailDialogVisible = ref(false)
const currentOrder = ref(null)

const statusDialogVisible = ref(false)
const statusForm = reactive({
  orderId: null,
  orderStatus: ''
})

const orderStatusOptions = [
  { label: '未发货', value: '未发货' },
  { label: '已发货', value: '已发货' },
  { label: '已完成', value: '已完成' },
  { label: '已取消', value: '已取消' }
]

// 获取图片URL
const getImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  // 如果路径已经包含/api，直接拼接
  if (path.startsWith('/api')) {
    return 'http://localhost:8080' + path
  }
  // 否则添加/api前缀
  return 'http://localhost:8080/api' + path
}

// 加载订单列表
const loadOrders = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchKeyword.value
    }
    // 只有选择了具体状态时才传递orderStatus参数
    if (orderStatus.value) {
      params.orderStatus = orderStatus.value
    }
    const res = await getOrderList(params)
    orders.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('加载订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 状态筛选
const handleStatusFilter = () => {
  pagination.pageNum = 1
  loadOrders()
}

// 页码改变
const handlePageChange = (page) => {
  pagination.pageNum = page
  loadOrders()
}

// 页大小改变
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  loadOrders()
}

// 查看订单详情
const handleViewDetail = (row) => {
  currentOrder.value = row
  detailDialogVisible.value = true
}

// 发货
const handleShip = async (row) => {
  await ElMessageBox.confirm('确定要发货吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  })
  
  try {
    await shipOrder(row.id)
    ElMessage.success('发货成功')
    loadOrders()
  } catch (error) {
    console.error('发货失败:', error)
  }
}

// 删除订单
const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  
  try {
    await deleteOrder(row.id)
    ElMessage.success('删除成功')
    loadOrders()
  } catch (error) {
    console.error('删除失败:', error)
  }
}

// 修改订单状态
const handleChangeStatus = (row) => {
  statusForm.orderId = row.id
  statusForm.orderStatus = row.orderStatus
  statusDialogVisible.value = true
}

// 提交状态修改
const handleSubmitStatus = async () => {
  if (!statusForm.orderStatus) {
    ElMessage.warning('请选择订单状态')
    return
  }
  
  try {
    await updateOrderStatus(statusForm.orderId, statusForm.orderStatus)
    ElMessage.success('订单状态修改成功')
    statusDialogVisible.value = false
    loadOrders()
  } catch (error) {
    console.error('修改状态失败:', error)
  }
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) return
  
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个订单吗？`, '批量删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await Promise.all(selectedIds.value.map(id => deleteOrder(id)))
    ElMessage.success('批量删除成功')
    selectedIds.value = []
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.admin-orders {
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.toolbar-right {
  display: flex;
  align-items: center;
}

.action-btn {
  margin-right: 8px;
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s;
}

.action-btn:last-child {
  margin-right: 0;
}

.view-btn {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border: none;
  color: white;
}

.view-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(79, 172, 254, 0.4);
}

.ship-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
}

.ship-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.status-btn {
  border: none;
  color: white;
}

.status-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.delete-btn {
  background: linear-gradient(135deg, #f5576c 0%, #f093fb 100%);
  border: none;
  color: white;
}

.delete-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 87, 108, 0.4);
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.order-detail-dialog {
  padding: 10px 0;
}

.order-detail-dialog :deep(.el-descriptions) {
  margin-bottom: 20px;
}

.order-detail-dialog :deep(.el-divider__text) {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}
</style>

