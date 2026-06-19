<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-header">
        <div class="logo-image">
          <img src="/logo/logo.png" alt="零食商城">
        </div>
        <h2 class="title">零食销售管理系统</h2>
        <p class="subtitle">🎉 注册新账号，开启购物之旅</p>
      </div>
      
      <el-form
        ref="formRef"
        :model="registerForm"
        :rules="rules"
        class="register-form"
      >
        <el-row :gutter="15">
          <el-col :span="12">
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="用户名"
                size="large"
                clearable
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
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
        
        <el-row :gutter="15">
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
            maxlength="11"
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
            class="register-button"
            :loading="loading"
            @click="handleRegister"
          >
            <span v-if="!loading">立即注册</span>
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="links">
        <span>已有账号？</span>
        <router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Iphone, Message, Avatar, ShoppingBag } from '@element-plus/icons-vue'
import { register } from '@/api/user'

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
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3到20个字符', trigger: 'blur' }
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
        await register(data)
        ElMessage.success('注册成功，请登录')
        router.push('/login')
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
.register-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 25%, #ffeaa7 50%, #dfe6e9 75%, #74b9ff 100%);
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

.register-page::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 50%, rgba(255, 107, 107, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(254, 202, 87, 0.1) 0%, transparent 50%);
  pointer-events: none;
}

.register-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 580px;
  padding: 50px 45px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  box-shadow: 0 15px 50px rgba(255, 107, 107, 0.25);
  border: 2px solid rgba(255, 107, 107, 0.1);
  backdrop-filter: blur(20px);
}

.register-header {
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
  filter: drop-shadow(0 4px 12px rgba(255, 107, 107, 0.3));
  transition: transform 0.3s ease;
}

.logo-image img:hover {
  transform: scale(1.05) rotate(5deg);
}

.icon-wrapper {
  display: inline-block;
  padding: 20px;
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  border-radius: 50%;
  margin-bottom: 20px;
  box-shadow: 0 8px 25px rgba(255, 107, 107, 0.4);
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-15px) rotate(5deg);
  }
}

.header-icon {
  color: #fff;
  display: block;
}

.title {
  font-size: 28px;
  font-weight: bold;
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
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

.register-form {
  margin-bottom: 25px;
}

.register-form :deep(.el-form-item) {
  margin-bottom: 18px;
}

.register-form :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(255, 107, 107, 0.08);
  transition: all 0.3s;
  padding: 10px 15px;
  background: rgba(255, 255, 255, 0.9);
}

.register-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 16px rgba(255, 107, 107, 0.15);
  transform: translateY(-2px);
}

.register-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(255, 107, 107, 0.3);
  background: #fff;
}

.register-form :deep(.el-input__prefix) {
  color: #ff6b6b;
  font-size: 18px;
}

.register-button {
  width: 100%;
  height: 48px;
  font-size: 17px;
  font-weight: bold;
  border-radius: 12px;
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  border: none;
  letter-spacing: 3px;
  transition: all 0.4s;
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.3);
  margin-top: 10px;
}

.register-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(255, 107, 107, 0.5);
  background: linear-gradient(135deg, #feca57 0%, #ff6b6b 100%);
}

.register-button:active {
  transform: translateY(-1px);
}

.links {
  text-align: center;
  font-size: 14px;
  color: #636e72;
  padding-top: 25px;
  border-top: 2px solid rgba(255, 107, 107, 0.1);
}

.links a {
  color: #ff6b6b;
  font-weight: bold;
  margin-left: 8px;
  transition: all 0.3s;
}

.links a:hover {
  color: #feca57;
  text-decoration: underline;
}

@media (max-width: 768px) {
  .register-container {
    padding: 40px 30px;
  }
  
  .title {
    font-size: 24px;
  }
  
  .register-form :deep(.el-col) {
    width: 100%;
  }
}
</style>

