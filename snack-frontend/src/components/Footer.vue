<template>
  <footer class="footer">
    <div class="footer-wave">
      <svg viewBox="0 0 1200 120" preserveAspectRatio="none">
        <path d="M0,0 C150,80 350,0 600,50 C850,100 1050,20 1200,80 L1200,120 L0,120 Z" fill="url(#gradient)"></path>
        <defs>
          <linearGradient id="gradient" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" style="stop-color:#ff6b6b;stop-opacity:0.3" />
            <stop offset="50%" style="stop-color:#feca57;stop-opacity:0.3" />
            <stop offset="100%" style="stop-color:#48dbfb;stop-opacity:0.3" />
          </linearGradient>
        </defs>
      </svg>
    </div>
    
    <div class="container">
      <div class="footer-content">
        <div class="footer-section about">
          <div class="footer-logo">
            <img src="/logo/logo.png" alt="零食商城" class="logo-img">
            <h3>零食销售管理系统</h3>
          </div>
          <p class="about-text">精选全球美味零食，为您带来舌尖上的享受。新鲜直达，品质保证，让每一口都是幸福的味道。</p>
          <div class="social-links">
            <a href="#" class="social-icon"><el-icon><ChatDotRound /></el-icon></a>
            <a href="#" class="social-icon"><el-icon><Share /></el-icon></a>
            <a href="#" class="social-icon"><el-icon><Star /></el-icon></a>
          </div>
        </div>
        
        <div class="footer-section">
          <h3><el-icon><Grid /></el-icon> 快捷导航</h3>
          <ul class="footer-links">
            <li><router-link to="/products"><el-icon><ArrowRight /></el-icon>商品列表</router-link></li>
            <li><router-link to="/articles"><el-icon><ArrowRight /></el-icon>资讯中心</router-link></li>
            <li><router-link to="/notices"><el-icon><ArrowRight /></el-icon>系统公告</router-link></li>
            <li><router-link to="/user/center"><el-icon><ArrowRight /></el-icon>个人中心</router-link></li>
          </ul>
        </div>
        
        <div class="footer-section contact">
          <h3><el-icon><Phone /></el-icon> 联系我们</h3>
          <div class="contact-item">
            <el-icon color="#feca57"><Phone /></el-icon>
            <span>400-888-8888</span>
          </div>
          <div class="contact-item">
            <el-icon color="#feca57"><Message /></el-icon>
            <span>service@snacks.com</span>
          </div>
          <div class="contact-item">
            <el-icon color="#feca57"><Clock /></el-icon>
            <span>周一至周日 9:00-22:00</span>
          </div>
        </div>
      </div>
      
      <div class="footer-bottom">
        <div class="footer-divider"></div>
        <div class="copyright">
          <p>
            <el-icon><CopyDocument /></el-icon>
            &copy; 2026 零食销售管理系统 | 让美味触手可及
          </p>
          <p class="icp">桂ICP备2026年—黄先生版权所有</p>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { ShoppingBag, ChatDotRound, Share, Star, Grid, ArrowRight, Phone, Message, Clock, CopyDocument } from '@element-plus/icons-vue'
</script>

<style scoped>
.footer {
  background: linear-gradient(135deg, #2d3436 0%, #000000 100%);
  color: #fff;
  padding: 0;
  margin-top: 80px;
  position: relative;
  overflow: hidden;
}

.footer::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 50%, rgba(255, 107, 107, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(254, 202, 87, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 40% 20%, rgba(72, 219, 251, 0.1) 0%, transparent 50%);
  pointer-events: none;
}

.footer-wave {
  position: absolute;
  top: -1px;
  left: 0;
  width: 100%;
  height: 120px;
  overflow: hidden;
  line-height: 0;
}

.footer-wave svg {
  position: relative;
  display: block;
  width: calc(100% + 1.3px);
  height: 120px;
}

.container {
  position: relative;
  z-index: 1;
  padding-top: 80px;
}

.footer-content {
  display: grid;
  grid-template-columns: 2fr 1.2fr 1.5fr;
  gap: 60px;
  margin-bottom: 50px;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
  padding: 0 20px;
}

.footer-section {
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}

.logo-img {
  width: 50px;
  height: 50px;
  object-fit: contain;
  flex-shrink: 0;
}

.footer-logo h3 {
  font-size: 24px;
  font-weight: bold;
  background: linear-gradient(135deg, #feca57 0%, #ff6b6b 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.about-text {
  font-size: 14px;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 25px;
}

.social-links {
  display: flex;
  gap: 15px;
}

.social-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #feca57;
  font-size: 20px;
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  backdrop-filter: blur(10px);
}

.social-icon:hover {
  background: linear-gradient(135deg, #feca57 0%, #ff6b6b 100%);
  color: #fff;
  transform: translateY(-5px) rotate(360deg);
  box-shadow: 0 8px 20px rgba(254, 202, 87, 0.4);
}

.footer-section h3 {
  font-size: 18px;
  margin-bottom: 25px;
  color: #fff;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
  position: relative;
  padding-bottom: 15px;
}

.footer-section h3::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 50px;
  height: 3px;
  background: linear-gradient(90deg, #feca57 0%, #ff6b6b 100%);
  border-radius: 2px;
}

.footer-links {
  list-style: none;
  padding: 0;
}

.footer-links li {
  margin-bottom: 12px;
}

.footer-links a {
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.footer-links a:hover {
  color: #feca57;
  transform: translateX(8px);
}

.footer-links a .el-icon {
  font-size: 12px;
  transition: transform 0.3s;
}

.footer-links a:hover .el-icon {
  transform: translateX(3px);
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 15px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  padding: 10px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.05);
  transition: all 0.3s;
}

.contact-item:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateX(5px);
}

.footer-divider {
  height: 1px;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(254, 202, 87, 0.3) 20%,
    rgba(255, 107, 107, 0.3) 50%,
    rgba(72, 219, 251, 0.3) 80%,
    transparent 100%
  );
  margin-bottom: 30px;
}

.footer-bottom {
  padding-bottom: 30px;
}

.copyright {
  text-align: center;
}

.copyright p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin: 8px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.icp {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

@media (max-width: 1024px) {
  .footer-content {
    grid-template-columns: repeat(2, 1fr);
    gap: 40px;
  }
}

@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: 1fr;
    gap: 30px;
  }
  
  .footer-wave {
    height: 80px;
  }
  
  .footer-wave svg {
    height: 80px;
  }
  
  .container {
    padding-top: 50px;
  }
}
</style>

