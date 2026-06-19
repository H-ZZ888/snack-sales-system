<template>
  <div class="admin-login-page">
    <div class="admin-login-container">
      <div class="admin-login-header">
        <div class="logo-image">
          <img src="/logo/logo.png" alt="管理员">
        </div>
        <h2 class="title">零食销售管理系统</h2>
        <p class="subtitle">🔐 管理员登录</p>
      </div>
      
      <el-form
        ref="formRef"
        :model="loginForm"
        :rules="rules"
        class="admin-login-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入管理员账号"
            size="large"
            clearable
          >
            <template #prefix>
              <el-icon><UserFilled /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="admin-login-button"
            :loading="loading"
            @click="handleLogin"
          >
            <span v-if="!loading">立即登录</span>
          </el-button>
        </el-form-item>
        
        <div class="quick-fill">
          <el-button
            type="warning"
            size="small"
            plain
            round
            @click="quickFill"
          >
            <el-icon><MagicStick /></el-icon>
            <span>快速填充测试账号</span>
          </el-button>
        </div>
      </el-form>
      
      <div class="links">
        <span>还没有管理员账号？</span>
        <router-link to="/admin/register">立即注册</router-link>
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
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { UserFilled, Lock, Management, MagicStick, Back } from '@element-plus/icons-vue'
import { adminLogin } from '@/api/admin'

const router = useRouter()
const route = useRoute()
const store = useStore()

const formRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入管理员账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

// 快速填充测试账号
const quickFill = () => {
  loginForm.username = 'Admin'
  loginForm.password = '123456'
}

const handleLogin = async () => {
  if (!formRef.value) return
  
  try {
    const valid = await formRef.value.validate()
    if (!valid) return
    
    loading.value = true
    
    const res = await adminLogin(loginForm)
    console.log('登录响应:', res)
    
    if (!res.data || !res.data.token) {
      ElMessage.error('登录失败：未获取到token')
      return
    }
    
    // 保存管理员登录信息
    await store.dispatch('admin/login', {
      token: res.data.token,
      adminInfo: res.data
    })
    
    console.log('Token已保存:', store.state.admin.token)
    
    // 先显示成功消息
    ElMessage.success('登录成功')
    
    // 延迟跳转，确保消息能够显示
    setTimeout(() => {
      const redirect = route.query.redirect || '/admin/dashboard'
      console.log('准备跳转到:', redirect)
      router.push(redirect)
    }, 500)
    
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error(error.message || '登录失败，请检查账号密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.admin-login-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
  position: relative;
}

@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.admin-login-page::before {
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
  z-index: 0;
}

.admin-login-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 460px;
  padding: 50px 45px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  box-shadow: 0 15px 50px rgba(102, 126, 234, 0.3);
  border: 2px solid rgba(102, 126, 234, 0.1);
  backdrop-filter: blur(20px);
}

.admin-login-header {
  text-align: center;
  margin-bottom: 40px;
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
  font-size: 16px;
  color: #636e72;
  margin: 0;
  font-weight: 500;
}

.admin-login-form {
  margin-bottom: 25px;
}

.admin-login-form :deep(.el-form-item) {
  margin-bottom: 22px;
}

.admin-login-form :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(102, 126, 234, 0.08);
  transition: all 0.3s;
  padding: 12px 15px;
  background: rgba(255, 255, 255, 0.9);
}

.admin-login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
  transform: translateY(-2px);
}

.admin-login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.3);
  background: #fff;
}

.admin-login-form :deep(.el-input__prefix) {
  color: #667eea;
  font-size: 18px;
}

.admin-login-button {
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
}

.admin-login-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.5);
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}

.admin-login-button:active {
  transform: translateY(-1px);
}

.quick-fill {
  text-align: center;
  margin-top: 18px;
}

.quick-fill .el-button {
  font-size: 13px;
  border-radius: 20px;
  transition: all 0.3s;
  border-color: #feca57;
  color: #feca57;
}

.quick-fill .el-button:hover {
  transform: scale(1.05);
  background: rgba(254, 202, 87, 0.1);
  border-color: #667eea;
  color: #667eea;
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
  .admin-login-container {
    padding: 40px 30px;
  }
  
  .title {
    font-size: 24px;
  }
}
</style>

