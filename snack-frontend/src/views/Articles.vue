<template>
  <div class="articles-page">
    <div class="container">
      <h1>资讯中心</h1>
      
      <!-- 搜索框 -->
      <div class="search-section">
        <div class="search-container">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索零食资讯、美食故事、健康知识..."
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
      </div>
      
      <div v-loading="loading" class="articles-list">
        <div
          v-for="article in articles"
          :key="article.id"
          class="article-card"
          @click="goToDetail(article.id)"
        >
          <div class="article-image">
            <img :src="getImageUrl(article.image)" :alt="article.title" />
            <div class="view-overlay">
              <el-icon class="view-icon"><View /></el-icon>
              <span class="view-text">点击查看</span>
            </div>
          </div>
          <div class="article-content">
            <h3 class="article-title">{{ article.title }}</h3>
            <p class="article-excerpt">{{ article.content }}</p>
            <div class="article-meta">
              <el-tag size="small">{{ article.category }}</el-tag>
              <span class="article-time">{{ article.createTime }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <el-empty v-if="articles.length === 0 && !loading" description="暂无资讯" />
      
      <div v-if="total > 0" class="pagination">
        <el-pagination
          :current-page="pageNum"
          :page-size="pageSize"
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
import { getImageUrl } from '@/utils/url'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, View } from '@element-plus/icons-vue'
import { getArticleList } from '@/api/article'

const router = useRouter()

const loading = ref(false)
const articles = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)
const searchKeyword = ref('')


const goToDetail = (id) => {
  router.push(`/article/${id}`)
}

const handleSearch = () => {
  pageNum.value = 1
  fetchArticles()
}

const handlePageChange = (page) => {
  pageNum.value = page
  fetchArticles()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  pageNum.value = 1
  fetchArticles()
}

const fetchArticles = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    
    const res = await getArticleList(params)
    articles.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取资讯列表失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchArticles()
})
</script>

<style scoped>
.articles-page {
  min-height: calc(100vh - 200px);
  padding: 90px 0 30px 0;
}

.articles-page h1 {
  font-size: 32px;
  margin-bottom: 30px;
  color: #2d5016;
  font-weight: bold;
  position: relative;
  padding-bottom: 15px;
}

.articles-page h1::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 60px;
  height: 4px;
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  border-radius: 2px;
}

.search-section {
  margin: 30px 0 40px;
}

.search-container {
  display: flex;
  gap: 15px;
  max-width: 800px;
  margin: 0 auto 20px;
}

.search-input {
  flex: 1;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 50px;
  box-shadow: 0 4px 16px rgba(255, 107, 107, 0.15);
  transition: all 0.3s;
  padding: 5px 20px;
  border: 2px solid transparent;
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

.articles-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.article-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.1);
  border: 2px solid transparent;
}

.article-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 6px 24px rgba(255, 107, 107, 0.25);
  border-color: rgba(255, 107, 107, 0.3);
}

.article-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  position: relative;
}

.article-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.article-card:hover .article-image img {
  transform: scale(1.1);
}

.view-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s ease;
  gap: 12px;
}

.article-card:hover .view-overlay {
  opacity: 1;
}

.view-icon {
  font-size: 48px;
  color: white;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
  animation: viewPulse 0.6s ease;
}

.view-text {
  color: white;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 2px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

@keyframes viewPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.15);
  }
}

.article-content {
  padding: 20px;
}

.article-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.article-excerpt {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.article-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.article-meta :deep(.el-tag) {
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  color: #fff;
  border: none;
  border-radius: 15px;
  padding: 4px 12px;
  font-weight: 500;
}

.article-time {
  font-size: 12px;
  color: #999;
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 40px 0 20px;
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

