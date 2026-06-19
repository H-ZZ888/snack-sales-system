<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-header">
        <div class="logo-image">
          <img src="/logo/logo.png" alt="零食商城">
        </div>
        <h2 class="title">零食销售管理系统</h2>
        <p class="subtitle">🍭 欢迎登录，开启美味之旅</p>
      </div>
      
      <el-form
        ref="formRef"
        :model="loginForm"
        :rules="rules"
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            clearable
          >
            <template #prefix>
              <el-icon><User /></el-icon>
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
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="captcha">
          <div class="captcha-wrapper">
            <el-input
              v-model="loginForm.captcha"
              placeholder="请输入验证码"
              size="large"
              clearable
              maxlength="4"
              @keyup.enter="handleLogin"
            >
              <template #prefix>
                <el-icon><CircleCheck /></el-icon>
              </template>
            </el-input>
            <div class="captcha-code" @click="refreshCaptcha">
              <canvas ref="captchaCanvas" width="120" height="40"></canvas>
              <div class="refresh-hint">
                <el-icon><RefreshRight /></el-icon>
                <span>点击刷新</span>
              </div>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-button"
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
        <span>还没有账号？</span>
        <router-link to="/register">立即注册</router-link>
      </div>
      
      <div class="admin-entry">
        <router-link to="/admin/login">
          <el-icon><Setting /></el-icon>
          <span>管理员登录</span>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { User, Lock, ShoppingCart, MagicStick, Setting, CircleCheck, RefreshRight } from '@element-plus/icons-vue'
import { login } from '@/api/user'

const router = useRouter()
const route = useRoute()
const store = useStore()

const formRef = ref(null)
const captchaCanvas = ref(null)
const loading = ref(false)
const captchaText = ref('')

const loginForm = reactive({
  username: '',
  password: '',
  captcha: ''
})

const validateCaptcha = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入验证码'))
  } else if (value.toLowerCase() !== captchaText.value.toLowerCase()) {
    callback(new Error('验证码错误'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  captcha: [
    { required: true, validator: validateCaptcha, trigger: 'blur' }
  ]
}

// 生成随机验证码
const generateCaptcha = () => {
  const chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678'
  let code = ''
  for (let i = 0; i < 4; i++) {
    code += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  return code
}

// 绘制验证码
const drawCaptcha = () => {
  if (!captchaCanvas.value) return
  
  const canvas = captchaCanvas.value
  const ctx = canvas.getContext('2d')
  const width = canvas.width
  const height = canvas.height
  
  // 生成验证码文本
  captchaText.value = generateCaptcha()
  
  // 清空画布
  ctx.clearRect(0, 0, width, height)
  
  // 绘制渐变背景
  const gradient = ctx.createLinearGradient(0, 0, width, height)
  gradient.addColorStop(0, '#fff5f0')
  gradient.addColorStop(0.5, '#ffe7e0')
  gradient.addColorStop(1, '#ffd4cc')
  ctx.fillStyle = gradient
  ctx.fillRect(0, 0, width, height)
  
  // 绘制干扰线
  for (let i = 0; i < 3; i++) {
    ctx.strokeStyle = `rgba(${Math.random() * 255}, ${Math.random() * 255}, ${Math.random() * 255}, 0.3)`
    ctx.beginPath()
    ctx.moveTo(Math.random() * width, Math.random() * height)
    ctx.lineTo(Math.random() * width, Math.random() * height)
    ctx.stroke()
  }
  
  // 绘制干扰点
  for (let i = 0; i < 30; i++) {
    ctx.fillStyle = `rgba(${Math.random() * 255}, ${Math.random() * 255}, ${Math.random() * 255}, 0.4)`
    ctx.beginPath()
    ctx.arc(Math.random() * width, Math.random() * height, 1, 0, 2 * Math.PI)
    ctx.fill()
  }
  
  // 绘制验证码文字
  const colors = ['#ff6b6b', '#feca57', '#ff9ff3', '#54a0ff', '#48dbfb']
  for (let i = 0; i < captchaText.value.length; i++) {
    const char = captchaText.value[i]
    const x = 20 + i * 22
    const y = 28
    const angle = (Math.random() - 0.5) * 0.4
    
    ctx.save()
    ctx.translate(x, y)
    ctx.rotate(angle)
    ctx.font = 'bold 24px Arial'
    ctx.fillStyle = colors[Math.floor(Math.random() * colors.length)]
    ctx.fillText(char, 0, 0)
    ctx.restore()
  }
}

// 刷新验证码
const refreshCaptcha = () => {
  drawCaptcha()
  loginForm.captcha = ''
}

// 快速填充测试账号
const quickFill = () => {
  loginForm.username = 'user001'
  loginForm.password = '123456'
}

const handleLogin = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login({
          username: loginForm.username,
          password: loginForm.password
        })
        store.dispatch('login', {
          token: res.data.token,
          userInfo: res.data
        })
        
        // 显示成功消息
        ElMessage.success('登录成功')
        
        // 延迟跳转，确保消息能够显示
        setTimeout(() => {
          const redirect = route.query.redirect || '/'
          router.push(redirect)
        }, 500)
      } catch (error) {
        console.error('登录失败:', error)
        refreshCaptcha()
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  drawCaptcha()
})
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 25%, #ffeaa7 50%, #dfe6e9 75%, #74b9ff 100%);
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
  position: relative;
  padding: 20px;
}

@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.login-page::before {
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

.login-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 460px;
  padding: 50px 45px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  box-shadow: 0 15px 50px rgba(255, 107, 107, 0.25);
  border: 2px solid rgba(255, 107, 107, 0.1);
  backdrop-filter: blur(20px);
}

.login-header {
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
  filter: drop-shadow(0 4px 12px rgba(255, 107, 107, 0.3));
  transition: transform 0.3s ease;
}

.logo-image img:hover {
  transform: scale(1.05);
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
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-15px);
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
  font-size: 16px;
  color: #636e72;
  margin: 0;
  font-weight: 500;
}

.login-form {
  margin-bottom: 25px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 22px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(255, 107, 107, 0.08);
  transition: all 0.3s;
  padding: 12px 15px;
  background: rgba(255, 255, 255, 0.9);
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 16px rgba(255, 107, 107, 0.15);
  transform: translateY(-2px);
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(255, 107, 107, 0.3);
  background: #fff;
}

.login-form :deep(.el-input__prefix) {
  color: #ff6b6b;
  font-size: 18px;
}

.captcha-wrapper {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.captcha-wrapper .el-input {
  flex: 1;
}

.captcha-code {
  position: relative;
  cursor: pointer;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(255, 107, 107, 0.15);
  transition: all 0.3s;
  background: #fff;
}

.captcha-code:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 20px rgba(255, 107, 107, 0.25);
}

.captcha-code canvas {
  display: block;
}

.refresh-hint {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(255, 107, 107, 0.9), transparent);
  color: #fff;
  font-size: 10px;
  padding: 3px 0;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 3px;
  opacity: 0;
  transition: opacity 0.3s;
}

.captcha-code:hover .refresh-hint {
  opacity: 1;
}

.login-button {
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
}

.login-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(255, 107, 107, 0.5);
  background: linear-gradient(135deg, #feca57 0%, #ff6b6b 100%);
}

.login-button:active {
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
  border-color: #ff6b6b;
  color: #ff6b6b;
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

.admin-entry {
  text-align: center;
  margin-top: 18px;
}

.admin-entry a {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #636e72;
  font-size: 14px;
  padding: 8px 16px;
  border-radius: 20px;
  transition: all 0.3s;
  background: rgba(255, 107, 107, 0.05);
}

.admin-entry a:hover {
  color: #ff6b6b;
  background: rgba(255, 107, 107, 0.1);
  transform: translateY(-2px);
}

@media (max-width: 768px) {
  .login-container {
    padding: 40px 30px;
  }
  
  .title {
    font-size: 24px;
  }
  
  .captcha-wrapper {
    flex-direction: column;
  }
  
  .captcha-code {
    width: 100%;
  }
  
  .captcha-code canvas {
    width: 100%;
  }
}
</style>

