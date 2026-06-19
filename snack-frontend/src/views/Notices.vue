<template>
  <div class="notices-page">
    <div class="container">
      <div class="page-header">
        <h1>系统公告</h1>
        
        <div class="filter-bar">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索公告"
            clearable
            style="width: 250px; margin-right: 10px;"
            @keyup.enter="handleFilterChange"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          
          <el-button type="primary" @click="handleFilterChange">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </div>
      </div>
      
      <div v-loading="loading" class="notices-list">
        <div
          v-for="notice in notices"
          :key="notice.id"
          class="notice-card"
          @click="handleViewDetail(notice)"
        >
          <div class="notice-header">
            <h3 class="notice-title">{{ notice.title }}</h3>
          </div>
          <div class="notice-content">
            {{ notice.content }}
          </div>
          <div class="notice-footer">
            <span class="notice-time">{{ notice.createTime }}</span>
          </div>
        </div>
      </div>
      
      <el-empty v-if="notices.length === 0 && !loading" description="暂无公告" />
      
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
    
    <!-- 公告详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="currentNotice.title"
      width="700px"
    >
      <div class="notice-detail">
        <div class="detail-meta">
          <span class="detail-time">{{ currentNotice.createTime }}</span>
        </div>
        <div class="detail-content">
          {{ currentNotice.content }}
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { getNoticeList } from '@/api/notice'

const loading = ref(false)
const notices = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')

const detailDialogVisible = ref(false)
const currentNotice = reactive({
  title: '',
  content: '',
  createTime: ''
})

const handleViewDetail = (notice) => {
  Object.assign(currentNotice, notice)
  detailDialogVisible.value = true
}

const handleFilterChange = () => {
  pageNum.value = 1
  fetchNotices()
}

const handlePageChange = (page) => {
  pageNum.value = page
  fetchNotices()
}

const fetchNotices = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    
    const res = await getNoticeList(params)
    notices.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取公告列表失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchNotices()
})
</script>

<style scoped>
.notices-page {
  min-height: calc(100vh - 200px);
  padding: 90px 0 30px 0;
  background: #f5f5f5;
}

.container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  margin-bottom: 30px;
  position: relative;
  z-index: 1;
}

.page-header h1 {
  font-size: 32px;
  margin-bottom: 20px;
  font-weight: bold;
  position: relative;
  padding-bottom: 15px;
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-header h1::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 80px;
  height: 4px;
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  border-radius: 2px;
}

.filter-bar {
  display: flex;
  align-items: center;
  padding: 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(255, 107, 107, 0.15);
  border: 2px solid rgba(255, 107, 107, 0.1);
  transition: all 0.3s;
}

.filter-bar:hover {
  box-shadow: 0 6px 28px rgba(255, 107, 107, 0.25);
  border-color: rgba(255, 107, 107, 0.2);
}

.filter-bar :deep(.el-input__wrapper) {
  border-radius: 50px;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.1);
  transition: all 0.3s;
  border: 1px solid rgba(255, 107, 107, 0.15);
}

.filter-bar :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.2);
  border-color: rgba(255, 107, 107, 0.3);
}

.filter-bar :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.15);
  border-color: #ff6b6b;
}

.filter-bar :deep(.el-button--primary) {
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  border: none;
  border-radius: 50px;
  padding: 12px 32px;
  font-weight: 600;
  box-shadow: 0 4px 16px rgba(255, 107, 107, 0.3);
  transition: all 0.3s;
}

.filter-bar :deep(.el-button--primary:hover) {
  transform: translateY(-3px);
  box-shadow: 0 6px 24px rgba(255, 107, 107, 0.4);
}

.notices-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 30px;
}

.notice-card {
  background: white;
  border-radius: 16px;
  padding: 30px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(255, 107, 107, 0.1);
  border: 2px solid transparent;
  position: relative;
  overflow: hidden;
}

.notice-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 5px;
  height: 100%;
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  transform: scaleY(0);
  transition: transform 0.3s ease;
}

.notice-card:hover::before {
  transform: scaleY(1);
}

.notice-card:hover {
  transform: translateX(8px);
  box-shadow: 0 8px 32px rgba(255, 107, 107, 0.25);
  border-color: rgba(255, 107, 107, 0.2);
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.notice-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  display: flex;
  align-items: center;
  gap: 12px;
}

.notice-title::before {
  content: '📢';
  font-size: 24px;
}

.notice-content {
  font-size: 15px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 15px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.notice-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 8px;
}

.notice-time {
  font-size: 13px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 6px;
}

.notice-time::before {
  content: '🕐';
  font-size: 14px;
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 40px 0 20px;
}

.pagination :deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  color: #fff;
  border: none;
}

.pagination :deep(.el-pagination.is-background .el-pager li:not(.is-disabled):hover) {
  color: #ff6b6b;
}

.pagination :deep(.el-pagination button:not(.is-disabled):hover) {
  color: #ff6b6b;
}

/* 详情对话框 */
.notice-detail {
  padding: 10px 0;
}

.detail-meta {
  display: flex;
  gap: 15px;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid rgba(255, 107, 107, 0.1);
}

.detail-time {
  font-size: 14px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 6px;
}

.detail-time::before {
  content: '🕐';
  font-size: 16px;
}

.detail-content {
  font-size: 16px;
  line-height: 2;
  color: #333;
  white-space: pre-wrap;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 12px;
  border-left: 4px solid #ff6b6b;
}

:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  padding: 20px;
  margin: 0;
}

:deep(.el-dialog__title) {
  color: white;
  font-weight: 600;
  font-size: 20px;
}

:deep(.el-dialog__headerbtn .el-dialog__close) {
  color: white;
  font-size: 20px;
}

:deep(.el-dialog__headerbtn .el-dialog__close:hover) {
  color: rgba(255, 255, 255, 0.8);
}

:deep(.el-empty__description p) {
  color: #999;
  font-size: 16px;
}
</style>

