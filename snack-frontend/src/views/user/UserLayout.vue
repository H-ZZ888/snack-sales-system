<template>
  <div class="user-layout">
    <div class="user-container">
      <!-- 左侧菜单 -->
      <div class="user-sidebar">
        <div class="user-profile">
          <div class="avatar-upload" @click="handleAvatarClick">
            <el-avatar :size="80" :src="getUserAvatar()">
              {{ (userInfo.nickname || userInfo.username)?.[0] }}
            </el-avatar>
            <div class="avatar-overlay">
              <el-icon :size="30" class="camera-icon">
                <Camera />
              </el-icon>
              <span class="upload-text">更换头像</span>
            </div>
          </div>
          <h3 class="user-nickname">{{ userInfo.nickname || userInfo.username }}</h3>
          <input
            ref="avatarInput"
            type="file"
            accept="image/*"
            style="display: none"
            @change="handleAvatarChange"
          />
        </div>
        
        <el-menu
          :default-active="activeMenu"
          class="user-menu"
          router
        >
          <el-menu-item index="/user/center">
            <el-icon><User /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
          <el-menu-item index="/user/orders">
            <el-icon><Document /></el-icon>
            <span>我的订单</span>
          </el-menu-item>
          <el-menu-item index="/user/favorites">
            <el-icon><Star /></el-icon>
            <span>我的收藏</span>
          </el-menu-item>
          <el-menu-item index="/user/addresses">
            <el-icon><Location /></el-icon>
            <span>收货地址</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 右侧内容区 -->
      <div class="user-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage, ElLoading } from 'element-plus'
import { User, Document, Star, Location, Camera } from '@element-plus/icons-vue'
import { uploadFile } from '@/api/upload'
import { getUserInfo, updateUserInfo } from '@/api/user'

const route = useRoute()
const store = useStore()
const avatarInput = ref(null)

const userInfo = computed(() => store.getters.userInfo)
const activeMenu = computed(() => route.path)

const getUserAvatar = () => {
  if (!userInfo.value.avatar) return ''
  if (userInfo.value.avatar.startsWith('http')) return userInfo.value.avatar
  return getImageUrl(userInfo.value.avatar)
}

const handleAvatarClick = () => {
  avatarInput.value.click()
}

const handleAvatarChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }

  // 验证文件大小（限制5MB）
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过5MB')
    return
  }

  try {
    const loadingInstance = ElLoading.service({
      text: '上传中...',
      background: 'rgba(255, 255, 255, 0.7)'
    })
    
    // 上传文件
    const uploadRes = await uploadFile(file)
    
    // 更新用户信息
    await updateUserInfo({
      avatar: uploadRes.data
    })
    
    // 重新获取用户信息并更新store
    const res = await getUserInfo()
    store.commit('SET_USER_INFO', res.data)
    
    loadingInstance.close()
    ElMessage.success('头像更换成功')
  } catch (error) {
    console.error('上传头像失败:', error)
    ElMessage.error('上传头像失败')
  } finally {
    // 清空input，允许重复选择同一文件
    event.target.value = ''
  }
}
</script>

<style scoped>
.user-layout {
  min-height: calc(100vh - 60px);
  background: #f5f5f5;
  padding: 20px 0;
}

.user-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  gap: 20px;
  padding: 0 20px;
}

.user-sidebar {
  width: 240px;
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  height: fit-content;
  position: sticky;
  top: 80px;
}

.user-profile {
  text-align: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 20px;
}

.avatar-upload {
  position: relative;
  display: inline-block;
  cursor: pointer;
  border-radius: 50%;
  overflow: hidden;
}

.avatar-upload .el-avatar {
  transition: all 0.3s;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s;
  border-radius: 50%;
}

.avatar-upload:hover .avatar-overlay {
  opacity: 1;
}

.camera-icon {
  color: white;
  margin-bottom: 5px;
}

.upload-text {
  color: white;
  font-size: 14px;
  font-weight: 500;
}

.user-nickname {
  margin-top: 15px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.user-menu {
  border: none;
}

.user-menu :deep(.el-menu-item) {
  border-radius: 8px;
  margin-bottom: 8px;
  color: #666;
  transition: all 0.3s;
}

.user-menu :deep(.el-menu-item:hover) {
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.1) 0%, rgba(254, 202, 87, 0.1) 100%);
  color: #ff6b6b;
}

.user-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #ff6b6b 0%, #feca57 100%);
  color: white;
}

.user-content {
  flex: 1;
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  min-height: 600px;
}

@media (max-width: 768px) {
  .user-container {
    flex-direction: column;
  }
  
  .user-sidebar {
    width: 100%;
    position: static;
  }
}
</style>
