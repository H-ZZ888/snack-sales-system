<template>
  <div class="admin-reviews">
    <el-card shadow="hover">
      <!-- 操作栏 -->
      <div class="toolbar">
        <div class="toolbar-left">
          <el-radio-group v-model="ratingFilter" @change="handleRatingFilter">
            <el-radio-button label="">全部评价</el-radio-button>
            <el-radio-button label="5">5星</el-radio-button>
            <el-radio-button label="4">4星</el-radio-button>
            <el-radio-button label="3">3星</el-radio-button>
            <el-radio-button label="2">2星</el-radio-button>
            <el-radio-button label="1">1星</el-radio-button>
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
            placeholder="搜索商品名称/用户名"
            :prefix-icon="Search"
            clearable
            style="width: 250px; margin-right: 10px;"
            @keyup.enter="loadReviews"
          />
          <el-button type="primary" :icon="Search" @click="loadReviews">
            搜索
          </el-button>
        </div>
      </div>
      
      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="reviews"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="productName" label="商品名称" min-width="150" />
        <el-table-column prop="username" label="评价用户" width="120" />
        <el-table-column prop="rating" label="评分" width="150" align="center">
          <template #default="{ row }">
            <el-rate
              v-model="row.rating"
              disabled
              :allow-half="false"
              show-score
              text-color="#ff9900"
            />
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评价内容" min-width="250" show-overflow-tooltip />
        <el-table-column label="评价图片" width="200" align="center">
          <template #default="{ row }">
            <div v-if="row.images" class="review-images">
              <el-image
                v-for="(img, index) in getImages(row.images)"
                :key="index"
                :src="getImageUrl(img)"
                fit="cover"
                style="width: 50px; height: 50px; border-radius: 4px; margin-right: 5px;"
                :preview-src-list="getImages(row.images).map(i => getImageUrl(i))"
              />
            </div>
            <span v-else style="color: #999;">无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="评价时间" width="160" />
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Delete } from '@element-plus/icons-vue'
import {
  getReviewList,
  deleteReview
} from '@/api/admin'

const loading = ref(false)
const reviews = ref([])
const searchKeyword = ref('')
const ratingFilter = ref('')
const selectedIds = ref([])
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

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

// 处理图片字符串
const getImages = (images) => {
  if (!images) return []
  return images.split(',').filter(img => img)
}

// 加载评价列表
const loadReviews = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchKeyword.value
    }
    // 只有选择了具体评分时才传递rating参数
    if (ratingFilter.value) {
      params.rating = ratingFilter.value
    }
    const res = await getReviewList(params)
    reviews.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('加载评价列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 评分筛选
const handleRatingFilter = () => {
  pagination.pageNum = 1
  loadReviews()
}

// 页码改变
const handlePageChange = (page) => {
  pagination.pageNum = page
  loadReviews()
}

// 页大小改变
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  loadReviews()
}

// 删除评价
const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该评价吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  
  try {
    await deleteReview(row.id)
    ElMessage.success('删除成功')
    loadReviews()
  } catch (error) {
    console.error('删除失败:', error)
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
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 条评价吗？`, '批量删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await Promise.all(selectedIds.value.map(id => deleteReview(id)))
    ElMessage.success('批量删除成功')
    selectedIds.value = []
    loadReviews()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

onMounted(() => {
  loadReviews()
})
</script>

<style scoped>
.admin-reviews {
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
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s;
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

.review-images {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 5px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>

