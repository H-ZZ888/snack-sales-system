<template>
  <div class="admin-profile">
    <el-card shadow="hover" class="profile-card">
      <div class="profile-header">
        <h2 class="profile-title">个人信息</h2>
      </div>
      
      <!-- 头像区域 -->
      <div class="avatar-section">
        <div class="avatar-container">
          <el-avatar :size="120" :src="getAdminAvatar()">
            {{ (adminInfo.username)?.[0] }}
          </el-avatar>
          <div class="avatar-overlay" @click="handleAvatarClick">
            <el-icon class="camera-icon"><Camera /></el-icon>
            <div class="avatar-text">更换头像</div>
          </div>
        </div>
        <input
          ref="avatarInput"
          type="file"
          accept="image/*"
          style="display: none"
          @change="handleAvatarChange"
        />
      </div>
      
      <!-- 标签页 -->
      <el-tabs v-model="activeTab" class="profile-tabs">
        <el-tab-pane label="基本信息" name="info">
          <el-form :model="form" label-width="120px" class="profile-form">
            <el-form-item label="用户名">
              <el-input v-model="form.username" disabled />
            </el-form-item>
            
            <el-form-item label="昵称">
              <el-input v-model="form.nickname" placeholder="请输入昵称" />
            </el-form-item>
            
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
            
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="success" @click="handleUpdateInfo" :loading="updateLoading">
                保存修改
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="修改密码" name="password">
          <el-form 
            ref="passwordFormRef" 
            :model="passwordForm" 
            :rules="passwordRules" 
            label-width="120px"
            class="profile-form"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input 
                v-model="passwordForm.oldPassword" 
                type="password" 
                placeholder="请输入原密码" 
                show-password 
              />
            </el-form-item>
            
            <el-form-item label="新密码" prop="newPassword">
              <el-input 
                v-model="passwordForm.newPassword" 
                type="password" 
                placeholder="请输入新密码" 
                show-password 
              />
            </el-form-item>
            
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input 
                v-model="passwordForm.confirmPassword" 
                type="password" 
                placeholder="请再次输入新密码" 
                show-password 
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="success" @click="handleUpdatePassword" :loading="passwordLoading">
                确认修改
              </el-button>
              <el-button @click="resetPasswordForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { Camera } from '@element-plus/icons-vue'
import { getAdminInfo, updateAdminInfo, updateAdminPassword } from '@/api/admin'
import axios from 'axios'

const store = useStore()
const adminInfo = computed(() => store.state.admin.adminInfo)

const activeTab = ref('info')
const avatarInput = ref(null)
const passwordFormRef = ref(null)
const updateLoading = ref(false)
const passwordLoading = ref(false)

const form = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  avatar: ''
})

const originalForm = reactive({})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const getAdminAvatar = () => {
  if (!form.avatar) return ''
  if (form.avatar.startsWith('http')) return form.avatar
  return getImageUrl(form.avatar)
}

const loadAdminInfo = async () => {
  try {
    const res = await getAdminInfo()
    Object.assign(form, res.data)
    Object.assign(originalForm, res.data)
  } catch (error) {
    console.error('获取管理员信息失败:', error)
  }
}

const handleAvatarClick = () => {
  avatarInput.value?.click()
}

const handleAvatarChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片文件')
    return
  }
  
  // 验证文件大小（5MB）
  if (file.size / 1024 / 1024 > 5) {
    ElMessage.error('图片大小不能超过5MB')
    return
  }
  
  const formData = new FormData()
  formData.append('file', file)
  
  try {
    const token = localStorage.getItem('adminToken')
    const response = await axios.post(getUploadUrl(), formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': `Bearer ${token}`
      }
    })
    
    if (response.data.code === 200) {
      form.avatar = response.data.data
      await handleUpdateInfo()
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error(response.data.message || '头像上传失败')
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败')
  }
  
  // 清空input
  event.target.value = ''
}

const handleUpdateInfo = async () => {
  updateLoading.value = true
  try {
    await updateAdminInfo(form)
    ElMessage.success('信息更新成功')
    // 更新store中的管理员信息
    store.commit('admin/SET_ADMIN_INFO', form)
    Object.assign(originalForm, form)
  } catch (error) {
    console.error('信息更新失败:', error)
  } finally {
    updateLoading.value = false
  }
}

const resetForm = () => {
  Object.assign(form, originalForm)
}

const handleUpdatePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      passwordLoading.value = true
      try {
        await updateAdminPassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        ElMessage.success('密码修改成功')
        resetPasswordForm()
      } catch (error) {
        console.error('密码修改失败:', error)
      } finally {
        passwordLoading.value = false
      }
    }
  })
}

const resetPasswordForm = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.clearValidate()
}

onMounted(() => {
  loadAdminInfo()
})
</script>

<style scoped>
.admin-profile {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.profile-card {
  border-radius: 8px;
}

.profile-header {
  text-align: center;
  padding: 20px 0 10px;
}

.profile-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.avatar-section {
  display: flex;
  justify-content: center;
  padding: 30px 0;
}

.avatar-container {
  position: relative;
  width: 120px;
  height: 120px;
  cursor: pointer;
  border-radius: 50%;
  overflow: hidden;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-container:hover .avatar-overlay {
  opacity: 1;
}

.camera-icon {
  font-size: 32px;
  color: white;
  margin-bottom: 5px;
}

.avatar-text {
  color: white;
  font-size: 14px;
}

.profile-tabs {
  margin-top: 20px;
}

.profile-tabs :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
}

.profile-tabs :deep(.el-tabs__active-bar) {
  background-color: #52c41a;
}

.profile-tabs :deep(.el-tabs__item) {
  font-size: 16px;
  color: #909399;
}

.profile-tabs :deep(.el-tabs__item.is-active) {
  color: #52c41a;
  font-weight: 600;
}

.profile-tabs :deep(.el-tabs__item:hover) {
  color: #52c41a;
}

.profile-form {
  max-width: 600px;
  margin: 40px auto;
  padding: 0 20px;
}

.profile-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

.profile-form :deep(.el-input__inner) {
  border-radius: 4px;
}

.profile-form :deep(.el-button) {
  min-width: 100px;
  border-radius: 4px;
}

.profile-form :deep(.el-button--success) {
  background: #52c41a;
  border-color: #52c41a;
}

.profile-form :deep(.el-button--success:hover) {
  background: #73d13d;
  border-color: #73d13d;
}

@media (max-width: 768px) {
  .admin-profile {
    padding: 10px;
  }
  
  .profile-form {
    padding: 0 10px;
  }
}
</style>
