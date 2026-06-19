<template>
  <div class="user-addresses">
    <div class="page-header">
      <div class="header-left">
        <el-icon :size="28" class="header-icon"><Location /></el-icon>
        <h2>收货地址</h2>
      </div>
      <el-button type="success" @click="handleAdd" class="add-btn">
        <el-icon><Plus /></el-icon>
        <span>添加地址</span>
      </el-button>
    </div>
    
    <div v-loading="loading" class="address-list">
      <div
        v-for="address in addressList"
        :key="address.id"
        class="address-card"
      >
        <div class="address-info">
          <div class="receiver-info">
            <span class="name">{{ address.receiverName }}</span>
            <span class="phone">{{ address.receiverPhone }}</span>
            <el-tag v-if="address.isDefault === 1" size="small" type="success">
              默认
            </el-tag>
          </div>
          <div class="address-detail">
            {{ address.province }} {{ address.city }} {{ address.district }} {{ address.detail }}
          </div>
        </div>
        
        <div class="address-actions">
          <el-button
            v-if="address.isDefault !== 1"
            type="primary"
            text
            @click="handleSetDefault(address.id)"
          >
            <el-icon><CircleCheck /></el-icon>
            <span>设为默认</span>
          </el-button>
          <el-button
            type="primary"
            text
            @click="handleEdit(address)"
          >
            <el-icon><Edit /></el-icon>
            <span>编辑</span>
          </el-button>
          <el-button
            type="danger"
            text
            @click="handleDelete(address.id)"
          >
            <el-icon><Delete /></el-icon>
            <span>删除</span>
          </el-button>
        </div>
      </div>
    </div>
    
    <el-empty v-if="addressList.length === 0 && !loading" description="暂无收货地址" />
    
    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="addressForm"
        :rules="rules"
        label-width="100px"
        class="address-form"
      >
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="addressForm.receiverName" placeholder="请输入收货人姓名">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="手机号" prop="receiverPhone">
          <el-input v-model="addressForm.receiverPhone" placeholder="请输入手机号">
            <template #prefix>
              <el-icon><Iphone /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="省份" prop="province">
          <el-input v-model="addressForm.province" placeholder="请输入省份">
            <template #prefix>
              <el-icon><MapLocation /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="城市" prop="city">
          <el-input v-model="addressForm.city" placeholder="请输入城市">
            <template #prefix>
              <el-icon><OfficeBuilding /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="区县" prop="district">
          <el-input v-model="addressForm.district" placeholder="请输入区县">
            <template #prefix>
              <el-icon><Place /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="详细地址" prop="detail">
          <el-input
            v-model="addressForm.detail"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址"
          >
            <template #prefix>
              <el-icon><Position /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="默认地址">
          <el-switch 
            v-model="addressForm.isDefault" 
            :active-value="1" 
            :inactive-value="0"
            active-color="#ff6b6b"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false" class="cancel-btn">取消</el-button>
        <el-button type="success" :loading="submitting" @click="handleSubmit" class="submit-btn">
          <el-icon><Check /></el-icon>
          <span>确定</span>
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Location, 
  Plus, 
  User, 
  Iphone, 
  MapLocation, 
  OfficeBuilding, 
  Place, 
  Position,
  Check,
  Edit,
  Delete,
  CircleCheck
} from '@element-plus/icons-vue'
import { getAddressList, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api/address'

const loading = ref(false)
const submitting = ref(false)
const addressList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加地址')
const isEdit = ref(false)

const formRef = ref(null)

const addressForm = reactive({
  id: null,
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: 0
})

const rules = {
  receiverName: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' }
  ],
  receiverPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  province: [
    { required: true, message: '请输入省份', trigger: 'blur' }
  ],
  city: [
    { required: true, message: '请输入城市', trigger: 'blur' }
  ],
  district: [
    { required: true, message: '请输入区县', trigger: 'blur' }
  ],
  detail: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ]
}

const resetForm = () => {
  addressForm.id = null
  addressForm.receiverName = ''
  addressForm.receiverPhone = ''
  addressForm.province = ''
  addressForm.city = ''
  addressForm.district = ''
  addressForm.detail = ''
  addressForm.isDefault = 0
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '添加地址'
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (address) => {
  Object.assign(addressForm, address)
  dialogTitle.value = '编辑地址'
  isEdit.value = true
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (isEdit.value) {
          await updateAddress(addressForm)
          ElMessage.success('修改成功')
        } else {
          await addAddress(addressForm)
          ElMessage.success('添加成功')
        }
        
        dialogVisible.value = false
        fetchAddressList()
      } catch (error) {
        console.error('操作失败:', error)
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleSetDefault = async (id) => {
  try {
    await setDefaultAddress(id)
    ElMessage.success('已设为默认地址')
    fetchAddressList()
  } catch (error) {
    console.error('设置默认地址失败:', error)
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个地址吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteAddress(id)
    ElMessage.success('删除成功')
    fetchAddressList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const fetchAddressList = async () => {
  loading.value = true
  try {
    const res = await getAddressList()
    addressList.value = res.data || []
  } catch (error) {
    console.error('获取收货地址失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchAddressList()
})
</script>

<style scoped>
/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 20px;
  border-bottom: 3px solid rgba(82, 196, 26, 0.2);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  color: #ff6b6b;
}

.page-header h2 {
  font-size: 24px;
  color: #2d3436;
  margin: 0;
  font-weight: bold;
}

.add-btn {
  border-radius: 25px;
  padding: 12px 30px;
  font-size: 15px;
  font-weight: 500;
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  border: none;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
  transition: all 0.3s;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(255, 107, 107, 0.4);
}

.add-btn span {
  display: flex;
  align-items: center;
  gap: 6px;
}

.address-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.address-card {
  border: 1px solid rgba(255, 107, 107, 0.15);
  border-radius: 12px;
  padding: 25px;
  transition: all 0.3s;
  box-shadow: 0 2px 12px rgba(255, 107, 107, 0.08);
  background: linear-gradient(135deg, #ffffff 0%, #fff5f0 100%);
}

.address-card:hover {
  border-color: rgba(255, 107, 107, 0.3);
  box-shadow: 0 4px 20px rgba(255, 107, 107, 0.15);
  transform: translateY(-2px);
}

.receiver-info {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.receiver-info .name {
  font-size: 17px;
  font-weight: 600;
  color: #2d3436;
}

.receiver-info .phone {
  color: #666;
  font-size: 15px;
}

.address-detail {
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
}

.address-actions {
  display: flex;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid rgba(82, 196, 26, 0.1);
}

.address-actions :deep(.el-button) {
  border-radius: 20px;
  padding: 8px 20px;
  font-weight: 500;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.address-actions :deep(.el-button .el-icon) {
  font-size: 16px;
}

.address-actions :deep(.el-button--primary) {
  color: #52c41a;
}

.address-actions :deep(.el-button--primary:hover) {
  color: #fff;
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  transform: translateY(-2px);
}

.address-actions :deep(.el-button--danger) {
  color: #ff4d4f;
}

.address-actions :deep(.el-button--danger:hover) {
  color: #fff;
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  transform: translateY(-2px);
}

/* 标签样式 */
:deep(.el-tag--success) {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  color: #fff;
  border: none;
  border-radius: 15px;
  padding: 3px 12px;
  font-weight: 500;
}

/* 对话框样式 */
:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  padding: 20px;
}

:deep(.el-dialog__title) {
  font-size: 20px;
  color: #2d5016;
  font-weight: bold;
}

/* 表单样式 */
.address-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #2d5016;
}

.address-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  transition: all 0.3s;
}

.address-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #52c41a inset;
}

.address-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #52c41a inset;
}

.address-form :deep(.el-input__prefix) {
  color: #52c41a;
}

.address-form :deep(.el-textarea__inner) {
  border-radius: 8px;
  transition: all 0.3s;
}

.address-form :deep(.el-textarea__inner:hover) {
  border-color: #52c41a;
}

.address-form :deep(.el-textarea__inner:focus) {
  border-color: #52c41a;
}

/* 按钮样式 */
.submit-btn {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  border: none;
  border-radius: 20px;
  padding: 10px 30px;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(82, 196, 26, 0.4);
}

.submit-btn span {
  display: flex;
  align-items: center;
  gap: 6px;
}

.cancel-btn {
  border-radius: 20px;
  padding: 10px 30px;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s;
}

.cancel-btn:hover {
  transform: translateY(-2px);
}

/* 空状态 */
:deep(.el-empty) {
  padding: 60px 0;
}
</style>

