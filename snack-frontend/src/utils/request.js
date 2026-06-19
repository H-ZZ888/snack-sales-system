import axios from 'axios'
import { ElMessage } from 'element-plus'
import store from '@/store'
import router from '@/router'

// 创建axios实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/api',
  timeout: 10000
})

// 标记是否正在退出登录
let isLoggingOut = false

// 设置退出登录标志
export const setLoggingOut = (value) => {
  isLoggingOut = value
}

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 根据请求路径判断使用哪个token
    let token = ''
    if (config.url.startsWith('/admin')) {
      // 管理员端接口使用管理员token
      token = store.state.admin.token
    } else {
      // 用户端接口使用用户token
      token = store.state.user.token
    }
    
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的状态码不是200，说明接口请求失败
    if (res.code !== 200) {
      // 401: 未登录或token过期 - 只在需要登录的页面才显示错误
      if (res.code === 401) {
        // 判断是管理员还是用户
        const isAdminRoute = router.currentRoute.value.path.startsWith('/admin')
        const currentRoute = router.currentRoute.value
        
        // 如果不是正在退出登录，且当前页面需要认证，才显示错误提示
        if (!isLoggingOut && (currentRoute.meta.requireAuth || currentRoute.meta.requireAdminAuth)) {
          ElMessage.error(res.message || '登录已过期，请重新登录')
        }
        
        if (isAdminRoute) {
          store.dispatch('admin/logout')
          // 只有在需要认证的页面才跳转到登录页
          if (currentRoute.meta.requireAdminAuth) {
            router.push('/admin/login')
          }
        } else {
          store.dispatch('logout')
          // 只有在需要认证的页面才跳转到登录页
          if (currentRoute.meta.requireAuth) {
            router.push('/login')
          }
        }
        
        // 重置标志
        isLoggingOut = false
      } else {
        ElMessage.error(res.message || '请求失败')
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      return res
    }
  },
  error => {
    console.error('响应错误:', error)
    
    if (error.response) {
      if (error.response.status === 401) {
        // 判断是管理员还是用户
        const isAdminRoute = router.currentRoute.value.path.startsWith('/admin')
        const currentRoute = router.currentRoute.value
        
        // 如果不是正在退出登录，且当前页面需要认证，才显示错误提示
        if (!isLoggingOut && (currentRoute.meta.requireAuth || currentRoute.meta.requireAdminAuth)) {
          ElMessage.error('登录已过期，请重新登录')
        }
        
        if (isAdminRoute) {
          store.dispatch('admin/logout')
          // 只有在需要认证的页面才跳转到登录页
          if (currentRoute.meta.requireAdminAuth) {
            router.push('/admin/login')
          }
        } else {
          store.dispatch('logout')
          // 只有在需要认证的页面才跳转到登录页
          if (currentRoute.meta.requireAuth) {
            router.push('/login')
          }
        }
        
        // 重置标志
        isLoggingOut = false
      } else if (error.response.status === 404) {
        ElMessage.error('请求的资源不存在')
      } else if (error.response.status === 500) {
        ElMessage.error('服务器错误')
      } else {
        ElMessage.error(error.response.data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    
    return Promise.reject(error)
  }
)

export default request

