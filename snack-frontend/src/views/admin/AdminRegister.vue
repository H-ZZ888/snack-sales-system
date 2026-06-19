<template>
  <div class="admin-register-page">
    <div class="admin-register-container">
      <div class="admin-register-header">
        <div class="logo-image">
          <img src="/logo/logo.png" alt="管理员">
        </div>
        <h2 class="title">零食销售管理系统</h2>
        <p class="subtitle">🎯 管理员注册</p>
      </div>
      
      <el-form
        ref="formRef"
        :model="registerForm"
        :rules="rules"
        class="admin-register-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="管理员账号"
                size="large"
                clearable
              >
                <template #prefix>
                  <el-icon><UserFilled /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="nickname">
              <el-input
                v-model="registerForm.nickname"
                placeholder="昵称"
                size="large"
                clearable
              >
                <template #prefix>
                  <el-icon><Avatar /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="密码"
                size="large"
                show-password
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="确认密码"
                size="large"
                show-password
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="手机号"
            size="large"
            clearable
          >
            <template #prefix>
              <el-icon><Iphone /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="邮箱"
            size="large"
            clearable
          >
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="admin-register-button"
            :loading="loading"
            @click="handleRegister"
          >
            <span v-if="!loading">立即注册</span>
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="links">
        <span>已有管理员账号？</span>
        <router-link to="/admin/login">立即登录</router-link>
      </div>
      
      <div class="back-to-user">
        <router-link to="/login">
          <el-icon><Back /></el-icon>
          <span>返回用户端</span>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UserFilled, Lock, Iphone, Message, Avatar, Management, Back } from '@element-plus/icons-vue'
import { adminRegister } from '@/api/admin'

const router = useRouter()

const formRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  nickname: '',
  password: '',
  confirmPassword: '',
  phone: '',
  email: ''
})

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const validatePhone = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入手机号'))
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

const validateEmail = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入邮箱'))
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
    callback(new Error('请输入正确的邮箱'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入管理员账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度在3到20个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePassword, trigger: 'blur' }
  ],
  phone: [
    { required: true, validator: validatePhone, trigger: 'blur' }
  ],
  email: [
    { required: true, validator: validateEmail, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const { confirmPassword, ...data } = registerForm
        await adminRegister(data)
        ElMessage.success('注册成功，请登录')
        router.push('/admin/login')
      } catch (error) {
        console.error('注册失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.admin-register-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
  padding: 40px 20px;
  position: relative;
}

@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.admin-register-page::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 50%, rgba(102, 126, 234, 0.2) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(240, 147, 251, 0.2) 0%, transparent 50%);
  pointer-events: none;
}

.admin-register-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 580px;
  padding: 50px 45px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  box-shadow: 0 15px 50px rgba(102, 126, 234, 0.3);
  border: 2px solid rgba(102, 126, 234, 0.1);
  backdrop-filter: blur(20px);
}

.admin-register-header {
  text-align: center;
  margin-bottom: 35px;
}

.logo-image {
  display: inline-block;
  margin-bottom: 25px;
}

.logo-image img {
  width: 100px;
  height: 100px;
  object-fit: contain;
  filter: drop-shadow(0 4px 12px rgba(102, 126, 234, 0.4));
  transition: transform 0.3s ease;
}

.logo-image img:hover {
  transform: scale(1.05);
}

.icon-wrapper {
  display: inline-block;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  margin-bottom: 20px;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
  animation: rotate 4s linear infinite;
}

@keyframes rotate {
  0% {
    transform: rotateY(0deg);
  }
  100% {
    transform: rotateY(360deg);
  }
}

.header-icon {
  color: #fff;
  display: block;
}

.title {
  font-size: 28px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 10px;
  letter-spacing: 1px;
}

.subtitle {
  font-size: 15px;
  color: #636e72;
  margin: 0;
  font-weight: 500;
}

.admin-register-form {
  margin-bottom: 25px;
}

.admin-register-form :deep(.el-form-item) {
  margin-bottom: 18px;
}

.admin-register-form :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(102, 126, 234, 0.08);
  transition: all 0.3s;
  padding: 10px 15px;
  background: rgba(255, 255, 255, 0.9);
}

.admin-register-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
  transform: translateY(-2px);
}

.admin-register-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.3);
  background: #fff;
}

.admin-register-form :deep(.el-input__prefix) {
  color: #667eea;
  font-size: 18px;
}

.admin-register-button {
  width: 100%;
  height: 48px;
  font-size: 17px;
  font-weight: bold;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  letter-spacing: 3px;
  transition: all 0.4s;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.3);
  margin-top: 10px;
}

.admin-register-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.5);
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}

.admin-register-button:active {
  transform: translateY(-1px);
}

.links {
  text-align: center;
  font-size: 14px;
  color: #636e72;
  padding-top: 25px;
  border-top: 2px solid rgba(102, 126, 234, 0.1);
}

.links a {
  color: #667eea;
  font-weight: bold;
  margin-left: 8px;
  transition: all 0.3s;
}

.links a:hover {
  color: #764ba2;
  text-decoration: underline;
}

.back-to-user {
  text-align: center;
  margin-top: 18px;
}

.back-to-user a {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #636e72;
  font-size: 14px;
  padding: 8px 16px;
  border-radius: 20px;
  transition: all 0.3s;
  background: rgba(102, 126, 234, 0.05);
}

.back-to-user a:hover {
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
  transform: translateY(-2px);
}

@media (max-width: 768px) {
  .admin-register-container {
    padding: 40px 30px;
  }
  
  .title {
    font-size: 24px;
  }
  
  .admin-register-form :deep(.el-col) {
    width: 100%;
  }
}
</style>

