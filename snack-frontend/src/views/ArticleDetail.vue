<template>
  <div class="article-detail">
    <div class="container">
      <!-- 返回按钮 -->
      <div class="back-button">
        <el-button :icon="ArrowLeft" @click="goBack" plain>
          返回资讯列表
        </el-button>
      </div>

      <el-card v-loading="loading" shadow="hover" class="detail-card">
        <!-- 文章头部 -->
        <div class="article-header">
          <h1 class="article-title">{{ article.title }}</h1>
          
          <div class="article-meta">
            <div class="meta-item">
              <el-icon><Clock /></el-icon>
              <span>{{ article.createTime }}</span>
            </div>
            <div class="meta-item">
              <el-icon><View /></el-icon>
              <span>{{ article.viewCount || 0 }} 次浏览</span>
            </div>
          </div>
        </div>

        <!-- 文章封面图 -->
        <div class="article-cover" v-if="article.image">
          <el-image
            :src="getImageUrl(article.image)"
            fit="cover"
            class="cover-image"
            :preview-src-list="[getImageUrl(article.image)]"
            :z-index="99999"
            preview-teleported
          >
            <template #error>
              <div class="image-error">
                <el-icon><Picture /></el-icon>
                <span>图片加载失败</span>
              </div>
            </template>
          </el-image>
        </div>

        <!-- 文章内容 -->
        <div class="article-content">
          <div class="content-text" v-html="formatContent(article.content)"></div>
        </div>
      </el-card>

      <!-- 相关推荐 -->
      <el-card shadow="hover" class="related-card" v-if="relatedArticles.length > 0">
        <template #header>
          <div class="card-header">
            <h3>相关推荐</h3>
          </div>
        </template>
        
        <div class="related-list">
          <div
            v-for="item in paginatedArticles"
            :key="item.id"
            class="related-item"
            @click="goToArticle(item.id)"
          >
            <div class="related-image">
              <el-image
                :src="getImageUrl(item.image)"
                fit="cover"
              >
                <template #error>
                  <div class="image-placeholder">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </div>
            <div class="related-info">
              <h4 class="related-title">{{ item.title }}</h4>
              <div class="related-meta">
                <span class="related-time">{{ item.createTime }}</span>
                <span class="related-views">
                  <el-icon><View /></el-icon>
                  {{ item.viewCount || 0 }}
                </span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 分页 -->
        <div class="pagination-wrapper" v-if="relatedArticles.length > pageSize">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="relatedArticles.length"
            layout="prev, pager, next"
            :hide-on-single-page="false"
            small
            background
          />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft, Clock, View, Picture
} from '@element-plus/icons-vue'
import { getArticleDetail, getArticleList } from '@/api/article'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const article = ref({})
const relatedArticles = ref([])
const currentPage = ref(1)
const pageSize = ref(3)

// 计算当前页显示的文章
const paginatedArticles = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return relatedArticles.value.slice(start, end)
})

const getImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  if (path.startsWith('/api')) {
    return getApiUrl(path)
  }
  return getApiUrl(path)
}

const formatContent = (content) => {
  if (!content) return ''
  // 将换行符转换为<br>标签
  return content.replace(/\n/g, '<br>')
}

const goBack = () => {
  router.back()
}

const goToArticle = (id) => {
  router.push(`/article/${id}`)
  // 重新加载文章详情
  loadArticleDetail()
}

// 加载文章详情
const loadArticleDetail = async () => {
  loading.value = true
  try {
    const res = await getArticleDetail(route.params.id)
    article.value = res.data
    
    // 加载相关推荐
    await loadRelatedArticles()
  } catch (error) {
    console.error('加载文章详情失败:', error)
    ElMessage.error('加载文章详情失败')
  } finally {
    loading.value = false
  }
}

// 加载相关推荐
const loadRelatedArticles = async () => {
  try {
    const res = await getArticleList({ pageNum: 1, pageSize: 10 })
    // 过滤掉当前文章
    relatedArticles.value = (res.data.records || []).filter(
      item => item.id !== article.value.id
    )
    // 重置分页到第一页
    currentPage.value = 1
  } catch (error) {
    console.error('加载相关推荐失败:', error)
  }
}

onMounted(() => {
  loadArticleDetail()
})
</script>

<style scoped>
.article-detail {
  min-height: calc(100vh - 120px);
  padding: 20px 0;
}

.container {
  max-width: 900px;
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

.article-header {
  padding: 30px 0 20px;
  border-bottom: 2px solid rgba(255, 107, 107, 0.1);
  margin-bottom: 30px;
}

.article-title {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 20px 0;
  line-height: 1.4;
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.article-meta {
  display: flex;
  gap: 30px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
}

.meta-item .el-icon {
  color: #feca57;
  font-size: 18px;
}

.article-cover {
  margin-bottom: 30px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(255, 107, 107, 0.15);
}

.cover-image {
  width: 100%;
  height: 450px;
  display: block;
}

.image-error,
.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  color: #999;
}

.image-error .el-icon,
.image-placeholder .el-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.article-content {
  padding: 20px 0;
  margin-bottom: 30px;
}

.content-text {
  font-size: 16px;
  line-height: 2;
  color: #333;
  text-align: justify;
}

.content-text :deep(p) {
  margin-bottom: 20px;
}

.content-text :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 20px 0;
}

.related-card {
  border-radius: 12px;
  overflow: hidden;
}

.card-header h3 {
  margin: 0;
  font-size: 20px;
  color: #2c3e50;
  font-weight: 600;
}

.related-list {
  display: grid;
  gap: 15px;
}

.related-item {
  display: flex;
  gap: 15px;
  padding: 15px;
  border-radius: 8px;
  background: #fafafa;
  cursor: pointer;
  transition: all 0.3s;
}

.related-item:hover {
  background: #fff;
  transform: translateX(5px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.15);
}

.related-image {
  flex-shrink: 0;
  width: 120px;
  height: 80px;
  border-radius: 6px;
  overflow: hidden;
}

.related-image .el-image {
  width: 100%;
  height: 100%;
}

.related-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.related-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.related-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  font-size: 13px;
  color: #999;
}

.related-views {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.pagination-wrapper :deep(.el-pagination) {
  gap: 8px;
  justify-content: center;
}

.pagination-wrapper :deep(.el-pagination.is-background .el-pager li) {
  background: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  transition: all 0.3s;
}

.pagination-wrapper :deep(.el-pagination.is-background .el-pager li:hover) {
  color: #48dbfb;
  border-color: #48dbfb;
}

.pagination-wrapper :deep(.el-pagination.is-background .el-pager li.is-active) {
  background: linear-gradient(135deg, #48dbfb 0%, #58b1f9 100%);
  border-color: transparent;
  color: white;
}

.pagination-wrapper :deep(.el-pagination.is-background .btn-prev),
.pagination-wrapper :deep(.el-pagination.is-background .btn-next) {
  background: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  transition: all 0.3s;
}

.pagination-wrapper :deep(.el-pagination.is-background .btn-prev:hover),
.pagination-wrapper :deep(.el-pagination.is-background .btn-next:hover) {
  color: #48dbfb;
  border-color: #48dbfb;
}

@media (max-width: 768px) {
  .article-title {
    font-size: 24px;
  }
  
  .cover-image {
    height: 250px;
  }
  
  .content-text {
    font-size: 15px;
  }
  
  .related-item {
    flex-direction: column;
  }
  
  .related-image {
    width: 100%;
    height: 150px;
  }
}
</style>
