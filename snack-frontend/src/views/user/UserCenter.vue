<template>
  <div class="user-center">
    <div class="section-card">
      <div class="section-header">
        <h2>个人信息</h2>
        <el-icon class="header-icon"><User /></el-icon>
      </div>
      
      <el-form
        ref="formRef"
        :model="userForm"
        :rules="rules"
        label-width="100px"
        class="user-form"
      >
      <el-form-item label="用户名">
        <el-input v-model="userForm.username" disabled>
          <template #prefix>
            <el-icon><User /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="userForm.nickname" placeholder="请输入昵称">
          <template #prefix>
            <el-icon><Avatar /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="userForm.phone" placeholder="请输入手机号">
          <template #prefix>
            <el-icon><Iphone /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="userForm.email" placeholder="请输入邮箱">
          <template #prefix>
            <el-icon><Message /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      
      <el-form-item>
        <el-button type="success" size="large" :loading="loading" @click="handleUpdate">
          <el-icon><Check /></el-icon>
          保存修改
        </el-button>
      </el-form-item>
    </el-form>
    </div>
    
    <div class="section-card">
      <div class="section-header">
        <h2>修改密码</h2>
        <el-icon class="header-icon"><Lock /></el-icon>
      </div>
      
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
        class="user-form"
      >
      <el-form-item label="原密码" prop="oldPassword">
        <el-input
          v-model="passwordForm.oldPassword"
          type="password"
          placeholder="请输入原密码"
          show-password
        >
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      
      <el-form-item label="新密码" prop="newPassword">
        <el-input
          v-model="passwordForm.newPassword"
          type="password"
          placeholder="请输入新密码"
          show-password
        >
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input
          v-model="passwordForm.confirmPassword"
          type="password"
          placeholder="请再次输入新密码"
          show-password
        >
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      
      <el-form-item>
        <el-button type="success" size="large" :loading="passwordLoading" @click="handleUpdatePassword">
          <el-icon><Check /></el-icon>
          修改密码
        </el-button>
      </el-form-item>
    </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Check, Avatar, Iphone, Message } from '@element-plus/icons-vue'
import { getUserInfo, updateUserInfo, updatePassword } from '@/api/user'

const store = useStore()
const router = useRouter()

const formRef = ref(null)
const passwordFormRef = ref(null)
const loading = ref(false)
const passwordLoading = ref(false)

const userForm = reactive({
  username: '',
  nickname: '',
  phone: '',
  email: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }
  ]
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
    { required: true, validator: validatePassword, trigger: 'blur' }
  ]
}

const handleUpdate = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 确保头像路径也一起保存
        console.log('保存的用户信息:', userForm)
        await updateUserInfo(userForm)
        ElMessage.success('修改成功')
        
        // 更新store中的用户信息
        const res = await getUserInfo()
        console.log('更新后的用户信息:', res.data)
        store.commit('SET_USER_INFO', res.data)
      } catch (error) {
        console.error('修改失败:', error)
        ElMessage.error('修改失败，请重试')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleUpdatePassword = async () => {
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      passwordLoading.value = true
      try {
        await updatePassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        ElMessage.success('密码已修改请重新登录')
        
        // 清除登录状态并跳转登录页
        store.dispatch('logout')
        router.push('/login')
      } catch (error) {
        console.error('修改密码失败:', error)
      } finally {
        passwordLoading.value = false
      }
    }
  })
}

const fetchUserInfo = async () => {
  try {
    const res = await getUserInfo()
    console.log('获取到的用户信息:', res.data)
    console.log('用户头像路径:', res.data.avatar)
    Object.assign(userForm, res.data)
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.user-center {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.section-card {
  background: #fff;
  border-radius: 12px;
  padding: 35px;
  box-shadow: 0 2px 12px rgba(82, 196, 26, 0.1);
  border: 1px solid rgba(82, 196, 26, 0.1);
  transition: all 0.3s;
}

.section-card:hover {
  box-shadow: 0 4px 20px rgba(82, 196, 26, 0.15);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid rgba(255, 107, 107, 0.2);
}

.section-header h2 {
  font-size: 24px;
  color: #2d3436;
  font-weight: bold;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-icon {
  font-size: 24px;
  color: #ff6b6b;
}

.user-form {
  max-width: 600px;
}

.user-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #2d3436;
}

.user-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  transition: all 0.3s;
}

.user-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #ff6b6b inset;
}

.user-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #ff6b6b inset;
}

.user-form :deep(.el-input__prefix) {
  color: #ff6b6b;
}

.user-form :deep(.el-input__prefix .el-icon) {
  font-size: 16px;
}

.user-form :deep(.el-button) {
  border-radius: 25px;
  padding: 12px 40px;
  font-size: 16px;
  font-weight: 500;
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  border: none;
  transition: all 0.3s;
}

.user-form :deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4);
}
</style>

