<script setup>
import { ref, onMounted } from 'vue';
import ModelViewer from './components/ModelViewer.vue';
import AvatarComponent from './components/AvatarComponent.vue';
import UserLogin from './components/UserLogin.vue';
import KnowledgeScience from './components/KnowledgeScience.vue';
import DashboardComponent from './components/DashboardComponent.vue';
/**
 * @author 刘宏伟
 * @version 1.0
 * @date 2026/4/8
 * */
//AI辅助生成：豆包,2026-04-07 主应用组件
// App.vue - 智光种植 Main Component
const showFeatures = ref(false);
const currentSection = ref('home');
const isPlayingVideo = ref(false);
// 本地视频路径（public 文件夹下的视频）
const videoUrl = '/1.mp4';
function scrollToFeatures() {
  const featuresSection = document.getElementById('features');
  if (featuresSection) {
    featuresSection.scrollIntoView({ behavior: 'smooth' });
  }
}

function scrollToDashboard() {
  const dashboardSection = document.querySelector('.dashboard-section');
  if (dashboardSection) {
    dashboardSection.scrollIntoView({ behavior: 'smooth' });
  }
}

function navigateTo(section) {
  currentSection.value = section;
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function navigateToShop() {
  // 跳转到商城页面
  window.location.href = '/shop.html';
}

function playVideo() {
  isPlayingVideo.value = true;
  // 滚动到视频位置
  setTimeout(() => {
    const videoSection = document.getElementById('video-section');
    if (videoSection) {
      videoSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }, 100);
}

function closeVideo() {
  isPlayingVideo.value = false;
}

onMounted(() => {
  // 监听滚动事件来控制 Features 和 Dashboard 的显示动画
  window.addEventListener('scroll', () => {
    const scrollPosition = window.scrollY;
    const windowHeight = window.innerHeight;
    
    // 控制 Features Section 显示
    if (scrollPosition > windowHeight * 0.5) {
      showFeatures.value = true;
    }
    
    // 控制 Dashboard Section 显示
    const dashboardSection = document.querySelector('.dashboard-section');
    if (dashboardSection) {
      const dashboardTop = dashboardSection.offsetTop;
      const triggerPoint = dashboardTop - windowHeight * 0.8;
      
      if (scrollPosition > triggerPoint) {
        dashboardSection.classList.add('visible');
      }
    }
    

  });

  // 初始化数据可视化大屏的功能
  initDashboard();
});

function initDashboard() {
  // 初始化时钟
  function updateClock() {
    const now = new Date();
    const timeStr = now.toLocaleTimeString('zh-CN', { hour12: false });
    document.title = `智能激光口腔种植机器人 | ${timeStr}`;
  }
  setInterval(updateClock, 1000);
  updateClock();

  // 初始化图表
  setTimeout(() => {
    const gaugeChart = echarts.init(document.getElementById('gaugeChart'));
    const trendChart = echarts.init(document.getElementById('trendChart'));

    // 仪表盘配置
    const gaugeOption = {
      series: [{
        type: 'gauge',
        startAngle: 180,
        endAngle: 0,
        min: 0,
        max: 100,
        splitNumber: 5,
        radius: '100%',
        center: ['50%', '70%'],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#00aa66' },
            { offset: 0.5, color: '#00bbff' },
            { offset: 1, color: '#ff6666' }
          ])
        },
        progress: {
          show: true,
          width: 10,
          itemStyle: {
            shadowBlur: 8,
            shadowColor: 'rgba(0, 150, 255, 0.4)'
          }
        },
        pointer: {
          show: false
        },
        axisLine: {
          lineStyle: {
            width: 10,
            color: [[1, 'rgba(0, 100, 200, 0.2)']]
          }
        },
        axisTick: { show: false },
        splitLine: { show: false },
        axisLabel: { show: false },
        title: {
          show: false
        },
        detail: {
          show: false
        },
        data: [{
          value: 95,
          name: '精度'
        }]
      }]
    };

    // 趋势图配置
    const trendOption = {
      xAxis: {
        type: 'category',
        data: ['2021', '2022', '2023'],
        axisLine: {
          lineStyle: {
            color: 'rgba(0, 150, 255, 0.3)'
          }
        },
        axisLabel: {
          color: 'rgba(100, 150, 200, 0.8)',
          fontSize: 10
        }
      },
      yAxis: {
        type: 'value',
        min: 80,
        max: 100,
        axisLine: {
          show: false
        },
        axisTick: {
          show: false
        },
        splitLine: {
          lineStyle: {
            color: 'rgba(0, 150, 255, 0.1)'
          }
        },
        axisLabel: {
          color: 'rgba(100, 150, 200, 0.8)',
          fontSize: 10
        }
      },
      series: [{
        data: [85, 92, 98],
        type: 'line',
        smooth: true,
        lineStyle: {
          color: '#00bbff',
          width: 2,
          shadowColor: 'rgba(0, 150, 255, 0.5)',
          shadowBlur: 10
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(0, 187, 255, 0.3)' },
            { offset: 1, color: 'rgba(0, 187, 255, 0.1)' }
          ])
        },
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: {
          color: '#00bbff',
          borderColor: '#fff',
          borderWidth: 2
        }
      }]
    };

    gaugeChart.setOption(gaugeOption);
    trendChart.setOption(trendOption);

    // 响应式调整
    window.addEventListener('resize', () => {
      gaugeChart.resize();
      trendChart.resize();
    });
  }, 100);
}
</script>

<template>
  <div id="app">
    <!-- Header Navigation -->
    <header class="header">
      <a href="#" class="logo" @click.prevent="navigateTo('home')">智光种植</a>
      <nav class="nav">
        <a href="#" class="nav-link" @click.prevent="navigateTo('home')">首页</a>
        <a href="#" class="nav-link" @click.prevent="navigateTo('3d')">3D 展示</a>
        <a href="#" class="nav-link" @click.prevent="navigateTo('virtual')">虚拟人交互</a>
        <a href="#" class="nav-link" @click.prevent="navigateTo('knowledge')">走进智光</a>
        <a href="#" class="nav-link" @click.prevent="navigateToShop">商城页面</a>
      </nav>
      <div class="user-icon" @click="navigateTo('login')" style="cursor: pointer;">
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"></path>
          <circle cx="12" cy="7" r="4"></circle>
        </svg>
      </div>
    </header>

    <!-- 首页内容 -->
    <div v-if="currentSection === 'home'">
      <!-- Hero Section with Video -->
      <section v-if="!isPlayingVideo" class="hero">
        <div class="hero-content">
          <div class="tagline">智光种植</div>
          <h1 class="hero-title">智能口腔种植机器人</h1>
          <p class="hero-description">Experience the future of dental implant surgery with our intelligent laser-guided robotic system. Combining AI-powered 3D reconstruction, real-time optical navigation, and precision laser technology to deliver sub-millimeter accuracy. Redefining oral healthcare with minimally invasive procedures, faster recovery, and standardized excellence.</p>
          <div class="hero-buttons">
            <button class="button primary" @click="scrollToDashboard">查看更多</button>
            <button class="button secondary" @click="playVideo">播放视频</button>
          </div>
        </div>
        
        <!-- 向下滚动图标指引 -->
        <div class="scroll-down-icon">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 5V19M12 19L5 12M12 19L19 12" stroke="url(#gradient)" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
            <defs>
              <linearGradient id="gradient" x1="5" y1="12" x2="19" y2="12" gradientUnits="userSpaceOnUse">
                <stop stop-color="#7ceaff"/>
                <stop offset="1" stop-color="#4d80ff"/>
              </linearGradient>
            </defs>
          </svg>
        </div>
      </section>

      <!-- Video Player Section -->
      <section v-else class="hero video-hero">
        <div class="video-background">
          <div class="gradient-overlay"></div>
        </div>
        <div class="video-content-wrapper">
          <div class="video-header">
            <h2 class="video-title">
              <i class="fas fa-play-circle"></i> 智光种植机器人
            </h2>
            <button class="video-close-btn" @click="closeVideo" title="关闭视频">
              <i class="fas fa-times"></i>
            </button>
          </div>
          <div class="video-player-container">
            <video :src="videoUrl" controls autoplay class="main-video" ref="videoPlayer"></video>
            <div class="video-glow-effect"></div>
          </div>
        </div>
      </section>

      <!-- Dashboard Section -->
      <DashboardComponent />

      <!-- Features Section -->
      <section id="features" class="features" :class="{ 'visible': showFeatures }">
        <div class="container">
          <h2 class="section-title">技术特性</h2>
          <div class="features-grid">
            <!-- 3D展示 -->
            <div class="feature-card feature-card-3d">
              <div class="feature-image" @click="navigateTo('3d')" style="cursor: pointer;">
                <img src="/1.png" alt="3D展示">
              </div>
              <div class="feature-content" @click="navigateTo('3d')" style="cursor: pointer;">
                <h3 class="feature-title">3D展示</h3>
                <p class="feature-description">通过先进的3D建模技术，直观展示智能激光口腔种植机器人的结构和工作原理，让用户全方位了解产品细节。</p>
              </div>
            </div>

            <!-- 虚拟人交互 -->
            <div class="feature-card feature-card-virtual">
              <div class="feature-image" @click="navigateTo('virtual')" style="cursor: pointer;">
                <img src="/2.png" alt="虚拟人交互">
              </div>
              <div class="feature-content" @click="navigateTo('virtual')" style="cursor: pointer;">
                <h3 class="feature-title">虚拟人交互</h3>
                <p class="feature-description">结合AI虚拟人技术，提供实时互动体验，用户可以通过语音或文字与虚拟助手交流，获取产品信息和使用指导。</p>
              </div>
            </div>

            <!-- 知识科普 -->
            <div class="feature-card feature-card-knowledge">
              <div class="feature-image" @click="navigateTo('knowledge')" style="cursor: pointer;">
                <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=dental%20health%20education%20knowledge%20infographic%20modern%20design&image_size=square" alt="知识科普">
              </div>
              <div class="feature-content" @click="navigateTo('knowledge')" style="cursor: pointer;">
                <h3 class="feature-title">走进智光</h3>
                <p class="feature-description">提供口腔种植相关的专业知识和科普内容，帮助用户了解口腔健康的重要性和智能种植技术的优势。</p>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>

    <!-- 3D展示内容 -->
    <div v-else-if="currentSection === '3d'">
      <ModelViewer />
    </div>

    <!-- 虚拟人交互内容 -->
    <div v-else-if="currentSection === 'virtual'">
      <section class="section virtual-section">
        <div class="container">
          <AvatarComponent />
        </div>
      </section>
    </div>

    <!-- 知识科普内容 -->
    <div v-else-if="currentSection === 'knowledge'">
      <KnowledgeScience />
    </div>

    <!-- 用户登录内容 -->
    <div v-else-if="currentSection === 'login'">
      <section class="login-section">
        <div class="container">
          <UserLogin />
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
/* Video Player Styles - Enhanced */
.video-hero {
  position: relative;
  overflow: hidden;
  min-height: 85vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
}

.video-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #0a1929 0%, #1a3a5c 35%, #0d4a6b 65%, #0a1929 100%);
  background-size: 200% 200%;
  animation: bgGradient 15s ease infinite;
  z-index: 1;
}

@keyframes bgGradient {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.gradient-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: 
    radial-gradient(circle at 15% 30%, rgba(0, 102, 204, 0.4) 0%, transparent 45%),
    radial-gradient(circle at 85% 70%, rgba(0, 204, 255, 0.3) 0%, transparent 45%),
    radial-gradient(circle at 50% 50%, rgba(0, 153, 255, 0.15) 0%, transparent 70%),
    radial-gradient(circle at 25% 80%, rgba(0, 255, 255, 0.2) 0%, transparent 40%),
    radial-gradient(circle at 75% 20%, rgba(0, 180, 255, 0.25) 0%, transparent 40%);
  animation: gradientFlow 10s ease-in-out infinite;
  z-index: 2;
  filter: blur(40px);
}

@keyframes gradientFlow {
  0%, 100% {
    opacity: 0.7;
    transform: scale(1) rotate(0deg);
  }
  50% {
    opacity: 1;
    transform: scale(1.15) rotate(5deg);
  }
}

.video-content-wrapper {
  position: relative;
  z-index: 10;
  width: 90%;
  max-width: 1280px;
  padding: 50px;
  background: linear-gradient(145deg, rgba(10, 25, 41, 0.85), rgba(13, 42, 71, 0.75));
  backdrop-filter: blur(30px) saturate(180%);
  border-radius: 32px;
  border: 2px solid rgba(0, 153, 255, 0.4);
  box-shadow: 
    0 25px 80px rgba(0, 102, 204, 0.5),
    0 0 120px rgba(0, 204, 255, 0.25),
    inset 0 0 60px rgba(0, 153, 255, 0.1);
  animation: slideIn 0.8s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  overflow: hidden;
}

.video-content-wrapper::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    45deg,
    transparent 30%,
    rgba(0, 153, 255, 0.05) 40%,
    rgba(0, 204, 255, 0.08) 50%,
    rgba(0, 153, 255, 0.05) 60%,
    transparent 70%
  );
  animation: shimmer 8s linear infinite;
  pointer-events: none;
}

@keyframes shimmer {
  0% { transform: translate(-50%, -50%) rotate(0deg); }
  100% { transform: translate(-50%, -50%) rotate(360deg); }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(50px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.video-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 35px;
  position: relative;
  z-index: 1;
}

.video-title {
  font-size: 2.5rem;
  font-weight: 800;
  background: linear-gradient(135deg, #0099ff 0%, #00ddff 50%, #0099ff 100%);
  background-size: 200% auto;
  background-clip: text;
  -webkit-background-clip: text;
  color: transparent;
  text-shadow: 0 0 40px rgba(0, 153, 255, 0.6);
  display: flex;
  align-items: center;
  gap: 15px;
  animation: textShine 3s ease-in-out infinite;
}

@keyframes textShine {
  0%, 100% { background-position: 0% center; }
  50% { background-position: 200% center; }
}

.video-title i {
  font-size: 3rem;
  background: linear-gradient(135deg, #00ccff, #00ffcc);
  background-clip: text;
  -webkit-background-clip: text;
  color: transparent;
  filter: drop-shadow(0 0 15px rgba(0, 255, 255, 0.8));
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
    filter: drop-shadow(0 0 15px rgba(0, 255, 255, 0.8));
  }
  50% {
    transform: scale(1.15);
    opacity: 0.9;
    filter: drop-shadow(0 0 25px rgba(0, 255, 255, 1));
  }
}

.video-close-btn {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(0, 153, 255, 0.25), rgba(0, 204, 255, 0.2));
  border: 2px solid rgba(0, 204, 255, 0.5);
  color: #00ffcc;
  font-size: 24px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.5s cubic-bezier(0.68, -0.55, 0.265, 1.55);
  box-shadow: 
    0 6px 20px rgba(0, 153, 255, 0.4),
    inset 0 0 20px rgba(0, 255, 255, 0.1);
  position: relative;
  overflow: hidden;
}

.video-close-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(0, 255, 255, 0.3), transparent);
  transition: left 0.5s ease;
}

.video-close-btn:hover::before {
  left: 100%;
}

.video-close-btn:hover {
  background: linear-gradient(135deg, rgba(0, 204, 255, 0.4), rgba(0, 255, 255, 0.3));
  transform: rotate(180deg) scale(1.15);
  box-shadow: 
    0 10px 35px rgba(0, 204, 255, 0.7),
    inset 0 0 30px rgba(0, 255, 255, 0.2);
  border-color: #00ffcc;
  color: #ffffff;
}

.video-player-container {
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 
    0 15px 50px rgba(0, 102, 204, 0.6),
    0 0 80px rgba(0, 204, 255, 0.4),
    inset 0 0 100px rgba(0, 153, 255, 0.15);
  border: 3px solid rgba(0, 204, 255, 0.3);
  background: linear-gradient(145deg, rgba(0, 0, 0, 0.9), rgba(10, 25, 41, 0.95));
}

.main-video {
  width: 100%;
  height: auto;
  max-height: 70vh;
  display: block;
  background: #000;
  border-radius: 20px;
  position: relative;
  z-index: 2;
}

.main-video::-webkit-media-controls-panel {
  background: linear-gradient(transparent, rgba(0, 102, 204, 0.9));
}

.main-video::-webkit-media-controls-play-button {
  background: rgba(0, 204, 255, 0.8);
  border-radius: 50%;
  box-shadow: 0 0 15px rgba(0, 255, 255, 0.6);
}

.video-glow-effect {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 180%;
  height: 180%;
  transform: translate(-50%, -50%);
  background: 
    radial-gradient(circle, rgba(0, 255, 255, 0.2) 0%, transparent 60%),
    conic-gradient(from 0deg at 50% 50%, rgba(0, 153, 255, 0.3), rgba(0, 255, 255, 0.2), rgba(0, 153, 255, 0.3));
  animation: glowPulse 5s ease-in-out infinite, rotateGlow 10s linear infinite;
  pointer-events: none;
  z-index: 1;
  filter: blur(30px);
}

@keyframes glowPulse {
  0%, 100% {
    opacity: 0.6;
    transform: translate(-50%, -50%) scale(0.85);
  }
  50% {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1.15);
  }
}

@keyframes rotateGlow {
  0% { transform: translate(-50%, -50%) rotate(0deg); }
  100% { transform: translate(-50%, -50%) rotate(360deg); }
}

/* Corner decorations */
.video-content-wrapper::after {
  content: '';
  position: absolute;
  top: 10px;
  left: 10px;
  right: 10px;
  bottom: 10px;
  border: 1px solid rgba(0, 204, 255, 0.15);
  border-radius: 28px;
  pointer-events: none;
  z-index: 0;
}

/* Floating particles */
.video-content-wrapper .particles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.video-content-wrapper .particle {
  position: absolute;
  width: 4px;
  height: 4px;
  background: rgba(0, 255, 255, 0.6);
  border-radius: 50%;
  box-shadow: 0 0 10px rgba(0, 255, 255, 0.8);
  animation: float 6s ease-in-out infinite;
}

.video-content-wrapper .particle:nth-child(1) {
  left: 10%;
  top: 20%;
  animation-delay: 0s;
}

.video-content-wrapper .particle:nth-child(2) {
  left: 30%;
  top: 60%;
  animation-delay: 1s;
}

.video-content-wrapper .particle:nth-child(3) {
  left: 70%;
  top: 40%;
  animation-delay: 2s;
}

.video-content-wrapper .particle:nth-child(4) {
  left: 90%;
  top: 80%;
  animation-delay: 3s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) translateX(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100px) translateX(20px);
    opacity: 0;
  }
}

/* Responsive */
@media (max-width: 768px) {
  .video-hero {
    padding: 30px 15px;
    min-height: 75vh;
  }
  
  .video-content-wrapper {
    padding: 30px 20px;
  }
  
  .video-title {
    font-size: 1.6rem;
  }
  
  .video-title i {
    font-size: 2rem;
  }
  
  .video-close-btn {
    width: 44px;
    height: 44px;
    font-size: 20px;
  }
}
</style>

<style scoped>
/* Features Section - 与 Dashboard 底部背景融合 */
.features {
  background:
    radial-gradient(circle at 56% 4%, rgba(96, 243, 255, 0.26), transparent 34%),
    radial-gradient(circle at 50% 42%, rgba(34, 172, 255, 0.2), transparent 52%),
    linear-gradient(180deg, #05224a 0%, #032047 24%, #021736 62%, #01102a 100%);
  padding: 52px 20px 60px;
  position: relative;
  overflow: hidden;
}

.features::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(circle at 54% -12%, rgba(121, 247, 255, 0.2), transparent 44%),
    linear-gradient(180deg, rgba(8, 61, 124, 0.2), rgba(2, 22, 66, 0.32));
  pointer-events: none;
  z-index: 0;
}

.features::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 68px;
  background:
    linear-gradient(180deg, rgba(10, 88, 158, 0.26), rgba(6, 52, 109, 0.14) 42%, transparent 100%),
    radial-gradient(circle at 50% 0, rgba(120, 246, 255, 0.14), transparent 62%);
  pointer-events: none;
  z-index: 1;
  box-shadow: none;
}

/* 向下滚动图标指引 */
.scroll-down-icon {
  position: absolute;
  left: 50%;
  bottom: 30px;
  transform: translateX(-50%);
  z-index: 50;
  animation: fadeInUp 1s ease-out 1.5s both, iconBounce 1.8s ease-in-out infinite;
}

.scroll-down-icon svg {
  width: 42px;
  height: 42px;
  filter: drop-shadow(0 0 12px rgba(124, 234, 255, 0.8));
}

.scroll-down-icon path {
  stroke-dasharray: 28;
  stroke-dashoffset: 0;
  animation: drawArrow 2s ease-in-out infinite;
}

@keyframes iconBounce {
  0%, 100% {
    transform: translateX(-50%) translateY(0);
    opacity: 0.6;
  }
  50% {
    transform: translateX(-50%) translateY(10px);
    opacity: 1;
  }
}

@keyframes drawArrow {
  0%, 100% {
    stroke-dashoffset: 0;
    opacity: 0.4;
  }
  50% {
    stroke-dashoffset: -14;
    opacity: 1;
  }
}

@keyframes fadeInUp {
  0% {
    opacity: 0;
    transform: translateX(-50%) translateY(20px);
  }
  100% {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}

.features .container {
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.features .section-title {
  text-align: center;
  font-size: 2.2rem;
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 50px;
  text-shadow: 0 0 20px rgba(124, 234, 255, 0.6);
  position: relative;
}

.features .section-title::after {
  content: '';
  position: absolute;
  bottom: -15px;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 3px;
  background: linear-gradient(90deg, transparent, #7ceaff, transparent);
  box-shadow: 0 0 15px rgba(124, 234, 255, 0.8);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 30px;
  position: relative;
  z-index: 1;
}

.feature-card {
  background: linear-gradient(180deg, rgba(8, 31, 103, 0.5), rgba(3, 19, 75, 0.7));
  border: 1px solid rgba(124, 234, 255, 0.2);
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.3),
    inset 0 0 30px rgba(124, 234, 255, 0.05);
}

.feature-card:hover {
  transform: translateY(-8px);
  border-color: rgba(124, 234, 255, 0.4);
  box-shadow: 
    0 16px 48px rgba(0, 0, 0, 0.4),
    0 0 40px rgba(124, 234, 255, 0.2),
    inset 0 0 40px rgba(124, 234, 255, 0.1);
}

.feature-image {
  width: 100%;
  height: 220px;
  overflow: hidden;
  position: relative;
  background: linear-gradient(180deg, rgba(12, 42, 128, 0.3), rgba(5, 20, 80, 0.5));
}

.feature-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.feature-card:hover .feature-image img {
  transform: scale(1.08);
}

.feature-content {
  padding: 24px;
}

.feature-title {
  font-size: 1.4rem;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 12px;
  text-shadow: 0 0 12px rgba(124, 234, 255, 0.5);
}

.feature-description {
  font-size: 0.95rem;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.85);
  text-shadow: 0 0 8px rgba(124, 234, 255, 0.3);
}

/* Responsive */
@media (max-width: 768px) {
  .features {
    padding: 42px 15px 40px;
  }
  
  .features .section-title {
    font-size: 1.8rem;
    margin-bottom: 35px;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .feature-image {
    height: 180px;
  }
}
</style>
