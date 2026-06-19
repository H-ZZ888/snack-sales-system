<template>
  <div class="admin-notices">
    <el-card shadow="hover">
      <!-- 操作栏 -->
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button type="success" :icon="Plus" @click="handleAdd">
            添加公告
          </el-button>
          <el-button 
            type="danger" 
            :icon="Delete" 
            @click="handleBatchDelete"
            :disabled="selectedIds.length === 0"
          >
            批量删除 {{ selectedIds.length > 0 ? `(${selectedIds.length})` : '' }}
          </el-button>
        </div>
        <div class="toolbar-right">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索公告标题"
            :prefix-icon="Search"
            clearable
            style="width: 250px; margin-right: 10px;"
            @keyup.enter="loadNotices"
          />
          <el-button type="primary" :icon="Search" @click="loadNotices">
            搜索
          </el-button>
        </div>
      </div>
      
      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="notices"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="title" label="公告标题" min-width="250" show-overflow-tooltip />
        <el-table-column prop="content" label="公告内容" min-width="400" show-overflow-tooltip />
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button class="edit-btn" size="small" :icon="Edit" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button class="delete-btn" size="small" :icon="Delete" @click="handleDelete(row)">
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
    
    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        
        <el-form-item label="公告内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="8"
            placeholder="请输入公告内容"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="success" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Edit, Delete } from '@element-plus/icons-vue'
import {
  getNoticeList,
  addNotice,
  updateNotice,
  deleteNotice,
  batchDeleteNotices
} from '@/api/admin'

const loading = ref(false)
const notices = ref([])
const searchKeyword = ref('')
const selectedIds = ref([])
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const dialogVisible = ref(false)
const dialogTitle = ref('添加公告')
const formRef = ref(null)
const submitting = ref(false)

const form = reactive({
  id: null,
  title: '',
  content: ''
})

const rules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' }
  ]
}

// 加载公告列表
const loadNotices = async () => {
  loading.value = true
  try {
    const res = await getNoticeList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchKeyword.value
    })
    notices.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('加载公告列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 页码改变
const handlePageChange = (page) => {
  pagination.pageNum = page
  loadNotices()
}

// 页大小改变
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  loadNotices()
}

// 添加公告
const handleAdd = () => {
  dialogTitle.value = '添加公告'
  Object.assign(form, {
    id: null,
    title: '',
    content: ''
  })
  dialogVisible.value = true
}

// 编辑公告
const handleEdit = (row) => {
  dialogTitle.value = '编辑公告'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (form.id) {
          await updateNotice(form)
          ElMessage.success('更新成功')
        } else {
          await addNotice(form)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadNotices()
      } catch (error) {
        console.error('操作失败:', error)
      } finally {
        submitting.value = false
      }
    }
  })
}

// 删除公告
const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该公告吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  
  try {
    await deleteNotice(row.id)
    ElMessage.success('删除成功')
    loadNotices()
  } catch (error) {
    console.error('删除失败:', error)
  }
}

// 表格选择改变
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先选择要删除的公告')
    return
  }
  
  await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 条公告吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  
  try {
    await batchDeleteNotices(selectedIds.value)
    ElMessage.success('批量删除成功')
    selectedIds.value = []
    loadNotices()
  } catch (error) {
    console.error('批量删除失败:', error)
  }
}

onMounted(() => {
  loadNotices()
})
</script>

<style scoped>
.admin-notices {
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

.toolbar-right {
  display: flex;
  align-items: center;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 美化操作按钮 */
.edit-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.edit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.delete-btn {
  background: linear-gradient(135deg, #f5576c 0%, #f093fb 100%);
  border: none;
  color: white;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.delete-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 87, 108, 0.4);
}
</style>

