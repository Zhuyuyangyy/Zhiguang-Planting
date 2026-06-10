
<!--3D模型展示+AI问答-->
<template>
  <div ref="containerRef" class="exhibition-container">

    <!-- 顶部品牌 -->
    <header class="brand-header">
      <div class="brand-inner">
        <div class="brand-logo">
          <svg width="26" height="26" viewBox="0 0 32 32" fill="none">
            <circle cx="16" cy="16" r="14" stroke="#4db8ff" stroke-width="1.5" opacity="0.6"/>
            <path d="M8 16h4m8 0h4M16 8v4m0 8v4" stroke="#4db8ff" stroke-width="1.5" stroke-linecap="round"/>
            <circle cx="16" cy="16" r="3" fill="#4db8ff" opacity="0.9"/>
            <path d="M11.5 11.5l2 2m5 5l2 2M20.5 11.5l-2 2m-5 5l-2 2" stroke="#4db8ff" stroke-width="1" stroke-linecap="round" opacity="0.6"/>
          </svg>
        </div>
        <div class="brand-text">
          <h1>智能激光口腔种植机器人</h1>
        </div>
        <div class="brand-status">
          <span class="status-dot"></span>
          <span>LIVE DEMO</span>
        </div>
      </div>
    </header>

    <!-- 加载遮罩 -->
    <Transition name="fade">
      <div v-if="loading" class="loading-overlay">
        <div class="loading-panel">
          <div class="loading-scan-line"></div>
          <div class="loading-icon">
            <div class="loading-ring r1"></div>
            <div class="loading-ring r2"></div>
            <div class="loading-ring r3"></div>
            <svg class="loading-center-icon" width="28" height="28" viewBox="0 0 32 32" fill="none">
              <path d="M16 4C9.373 4 4 9.373 4 16s5.373 12 12 12 12-5.373 12-12" stroke="#4db8ff" stroke-width="2" stroke-linecap="round"/>
              <path d="M16 9v7l4 2.5" stroke="#4db8ff" stroke-width="1.5" stroke-linecap="round"/>
            </svg>
          </div>
          <div class="loading-label">正在初始化医疗展厅环境</div>
          <div class="loading-sublabel">INITIALIZING 3D SURGICAL DISPLAY SYSTEM</div>
          <div class="loading-bar-track">
            <div class="loading-bar-fill" :style="{ width: progress + '%' }">
              <div class="loading-bar-glow"></div>
            </div>
          </div>
          <div class="loading-pct">{{ progress }}<span>%</span></div>
          <div class="loading-steps">
            <span :class="{ done: progress > 20 }">环境构建</span>
            <span :class="{ done: progress > 50 }">材质加载</span>
            <span :class="{ done: progress > 80 }">模型解码</span>
            <span :class="{ done: progress >= 100 }">渲染就绪</span>
          </div>
        </div>
      </div>
    </Transition>

    <!-- 零件提示气泡 -->
    <div class="part-tooltip" ref="tooltipRef">
      <div class="tooltip-indicator"></div>
      <div class="tooltip-content">
        <div class="tooltip-label">零件识别</div>
        <div class="tooltip-name">{{ tooltipText }}</div>
      </div>
    </div>

    <!-- 右侧控制栏 -->
    <nav class="viewer-controls">
      <div class="control-group">
        <button class="ctrl-btn" @click="resetCamera" title="复位视角">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"/><path d="M3 3v5h5"/></svg>
          <span>复位</span>
        </button>
        <button class="ctrl-btn" :class="{ active: autoRotate }" @click="toggleAutoRotate" title="自动旋转">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21.5 2v6h-6M2.5 22v-6h6M2 11.5a10 10 0 0 1 18.8-4.3M22 12.5a10 10 0 0 1-18.8 4.2"/></svg>
          <span>旋转</span>
        </button>
        <button class="ctrl-btn" :class="{ active: wireframe }" @click="toggleWireframe" title="线框模式">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2"/><line x1="3" y1="9" x2="21" y2="9"/><line x1="3" y1="15" x2="21" y2="15"/><line x1="9" y1="3" x2="9" y2="21"/><line x1="15" y1="3" x2="15" y2="21"/></svg>
          <span>线框</span>
        </button>
      </div>

      <div class="control-divider"></div>

      <!-- 视角距离 -->
      <div class="ctrl-slider-block">
        <div class="ctrl-slider-label">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M2 12h3M19 12h3M4.9 4.9l2.1 2.1M17 17l2.1 2.1M4.9 19.1l2.1-2.1M17 7l2.1-2.1"/></svg>
          视角
        </div>
        <button class="ctrl-icon-btn" @click="cameraDistance = Math.max(5, cameraDistance - 2); applyCameraDistance()" title="拉近">
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        </button>
        <div class="ctrl-track">
          <input type="range" class="ctrl-slider" v-model.number="cameraDistance"
                 :min="5" :max="50" :step="0.5"
                 @input="applyCameraDistance" title="视角距离" />
        </div>
        <button class="ctrl-icon-btn" @click="cameraDistance = Math.min(50, cameraDistance + 2); applyCameraDistance()" title="拉远">
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="5" y1="12" x2="19" y2="12"/></svg>
        </button>
        <div class="ctrl-val">{{ Math.round(cameraDistance) }}m</div>
      </div>

      <div class="control-divider"></div>

      <!-- 场景缩放 -->
      <div class="ctrl-slider-block">
        <div class="ctrl-slider-label">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M15 3h6v6M9 21H3v-6M21 3l-7 7M3 21l7-7"/></svg>
          缩放
        </div>
        <button class="ctrl-icon-btn" @click="sceneScaleUp" title="放大">
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        </button>
        <div class="ctrl-track">
          <input type="range" class="ctrl-slider" v-model.number="sceneScaleValue"
                 :min="30" :max="300" :step="5"
                 @input="applySceneScale" title="整体场景缩放" />
        </div>
        <button class="ctrl-icon-btn" @click="sceneScaleDown" title="缩小">
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="5" y1="12" x2="19" y2="12"/></svg>
        </button>
        <div class="ctrl-val">{{ sceneScaleValue }}%</div>
      </div>

      <div class="control-hint">
        <span>拖拽旋转</span>
        <span>滚轮视角</span>
      </div>
    </nav>

    <!-- 左侧AI助手 -->
    <aside class="ai-sidebar" :class="{ collapsed: chatCollapsed }">
      <button class="sidebar-toggle" @click="chatCollapsed = !chatCollapsed">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <path v-if="!chatCollapsed" d="M15 18l-6-6 6-6"/>
          <path v-else d="M9 18l6-6-6-6"/>
        </svg>
      </button>
      <div class="ai-sidebar-inner">
        <div class="ai-head">
          <div class="ai-avatar">
            <svg width="20" height="20" viewBox="0 0 32 32" fill="none">
              <circle cx="16" cy="12" r="5" stroke="#4db8ff" stroke-width="1.5"/>
              <path d="M6 28c0-5.523 4.477-10 10-10s10 4.477 10 10" stroke="#4db8ff" stroke-width="1.5" stroke-linecap="round"/>
              <circle cx="26" cy="8" r="3" fill="#4db8ff" opacity="0.4"/>
              <path d="M24.5 8h3M26 6.5v3" stroke="#4db8ff" stroke-width="1" stroke-linecap="round"/>
            </svg>
          </div>
          <div class="ai-head-text">
            <div class="ai-name">设备问答助手</div>
            <div class="ai-role">SURGICAL PLANNING AI · ONLINE</div>
          </div>
          <div class="ai-online-dot"></div>
        </div>

        <div class="ai-messages" ref="chatScrollRef">
          <div class="ai-welcome" v-if="aiMessages.length === 0">
            <div class="welcome-icon">🤖</div>
            <p>您好！我是小智种牙。</p>
            <p>点击模型任意零件，我将为您介绍该组件的功能、技术参数及手术作用。</p>
          </div>
          <template v-for="(msg, i) in aiMessages" :key="i">
            <div class="chat-msg" :class="msg.role">
              <div class="msg-meta">{{ msg.role === 'user' ? 'admin' : '小智' }}</div>
              <div class="msg-text">{{ msg.text }}</div>
            </div>
          </template>
          <div class="ai-thinking" v-if="aiLoading">
            <div class="thinking-avatar">AI</div>
            <div class="thinking-bubble">
              <span></span><span></span><span></span>
            </div>
          </div>
        </div>

        <div class="quick-questions">
          <button v-for="q in quickQuestions" :key="q" @click="sendMessage(q)">{{ q }}</button>
        </div>

        <div class="ai-input-area">
          <input v-model="inputText" @keyup.enter="sendMessage()" placeholder="向 AI 提问..." :disabled="aiLoading" />
          <button class="send-btn" @click="sendMessage()" :disabled="aiLoading">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>
          </button>
        </div>
      </div>
    </aside>

    <!-- 激光发射视频弹窗 -->
    <Transition name="fade">
      <div v-if="showVideo" class="laser-video-modal" @click.self="closeVideo">
        <div class="video-box">
          <button class="video-close" @click="closeVideo">✕</button>
          <video ref="videoRef" :src="videoSrc" controls muted playsinline preload="auto" @ended="closeVideo"></video>
        </div>
      </div>
    </Transition>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue';
import * as THREE from 'three';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls';
import { DRACOLoader } from 'three/examples/jsm/loaders/DRACOLoader';
import modelService from '../services/modelService';
import { MEDIA } from '../config/mediaConfig';

// 视频源（使用统一配置）
const videoSrc = MEDIA.video('1003.mp4');

// 状态变量
const containerRef  = ref<HTMLElement | null>(null);
const tooltipRef    = ref<HTMLElement | null>(null);
const chatScrollRef = ref<HTMLElement | null>(null);

const loading    = ref(true);
const progress   = ref(0);
const autoRotate = ref(false);
const wireframe  = ref(false);
const chatCollapsed = ref(false);
const cameraDistance  = ref(8);
const sceneScaleValue = ref(100);

const tooltipText = ref('');
const inputText   = ref('');
const aiLoading   = ref(false);
const aiMessages  = ref<Array<{ role: 'user' | 'ai'; text: string }>>([]);

const quickQuestions = ['这台设备有什么特点？', '激光精度是多少？', '手术流程是怎样的？', '机械臂的自由度？'];
const currentProductId = ref('1002');

const showVideo = ref(false);
const videoRef = ref<HTMLVideoElement | null>(null);

// Three.js 变量
let scene: THREE.Scene;
let camera: THREE.PerspectiveCamera;
let renderer: THREE.WebGLRenderer;
let controls: OrbitControls;
let animationId: number;
let mixer: THREE.AnimationMixer | null = null;
let currentModel: THREE.Object3D | null = null;
let modelGroup: THREE.Group | null = null;

const raycaster = new THREE.Raycaster();
const mouse     = new THREE.Vector2();
let hoveredPart: THREE.Object3D | null = null;

// 零件编号→名称对照表 对照30张图纸

const componentNameMap: Record<string, string> = {
  // 口腔种植治疗椅
  "1": "设备底座", "2": "支撑体", "3": "椅座基座", "4": "靠背调节架",
  "5": "基座", "6": "成像探测器", "7": "清洗液供给装置", "8": "固定支架",
  "9": "探测器", "10": "脚踏支撑板",
  // CBCT成像系统
  "11": "CBCT 成像环体", "12": "成像探测器", "13": "X 射线发射源",
  "14": "旋转支撑座", "15": "行进驱动装置", "16": "管路接口",
  "17": "固定底座", "18": "轨道", "19": "信号传输线", "20": "定位支架",
  // 六轴机械臂
  "21": "射线源", "22": "电动伸缩杆末端", "23": "旋转支架",
  "24": "第二旋转关节", "25": "电动伸缩杆", "26": "小臂",
  "27": "第三旋转关节", "28": "第二摆动关节", "29": "第四旋转关节",
  "30": "腕部壳体", "31": "末端执行器座", "32": "快速对接盘",
  // 激光手术头
  "9002": "激光发射头", "9021": "激光调节关节",
  "9011": "冷却气嘴", "9031": "定位摄像头",
  // 模型里的编号
  "001": "大臂", "002": "小臂旋转关节", "003": "小臂旋转关节",
  "005": "升降支撑关节", "011": "大臂旋转关节", "021": "大臂旋转关节",
  "032": "末端连接臂", "051": "顶部旋转关节",
  "0001": "智能控制终端", "90021": "激光头调节关节",
  "0011": "大臂", "0012": "大臂旋转关节",
  "0021": "小臂旋转关节", "0032": "小臂", "0051": "大臂旋转关节"
};

// 根据模型节点名解析零件中文名
const resolvePartName = (name: string | undefined): string => {
  if (!name) return '智能激光口腔种植机器人功能组件';
  const numStr = name.replace(/\D/g, '');
  if (!numStr) return '智能激光口腔种植机器人功能组件';
  if (componentNameMap[numStr]) return componentNameMap[numStr];
  // 补零匹配
  const s3 = numStr.padStart(3, '0');
  const s4 = numStr.padStart(4, '0');
  if (componentNameMap[s3]) return componentNameMap[s3];
  if (componentNameMap[s4]) return componentNameMap[s4];
  return `机器人组件 ${numStr}`;
};

// AI聊天
async function sendMessage(q?: string) {
  const text = (q ?? inputText.value).trim();
  if (!text || aiLoading.value) return;
  inputText.value = '';
  aiMessages.value.push({ role: 'user', text });
  aiLoading.value = true;
  chatCollapsed.value = false;
  await nextTick(); scrollChat();
  try {
    const ans = await modelService.queryModelByDify(currentProductId.value, text);
    aiMessages.value.push({ role: 'ai', text: ans || '暂无回复' });
  } catch (e: any) {
    aiMessages.value.push({ role: 'ai', text: '请求失败：' + (e?.message ?? '未知错误') });
  } finally {
    aiLoading.value = false;
    await nextTick(); scrollChat();
  }
}

function scrollChat() {
  if (chatScrollRef.value) chatScrollRef.value.scrollTop = chatScrollRef.value.scrollHeight;
}

// PBR材质工厂
const mat = {
  whiteMetal: () => new THREE.MeshStandardMaterial({ color: 0xf6f9fc, roughness: 0.08, metalness: 0.22 }),
  whiteCeramic: () => new THREE.MeshStandardMaterial({ color: 0xfafcff, roughness: 0.06, metalness: 0.05 }),
  darkMetal: (c = 0x1a2030) => new THREE.MeshStandardMaterial({ color: c, roughness: 0.35, metalness: 0.75 }),
  steel: () => new THREE.MeshStandardMaterial({ color: 0xc4d0da, roughness: 0.12, metalness: 0.82 }),
  glowBlue: (intensity = 4) => new THREE.MeshStandardMaterial({
    color: 0x44aaff, emissive: new THREE.Color(0x2277cc), emissiveIntensity: intensity,
    roughness: 0.1, transparent: true, opacity: 0,
  }),
  screen: (emissive = 0x0a2060, ei = 1.8) => new THREE.MeshStandardMaterial({
    color: 0x020a18, emissive: new THREE.Color(emissive), emissiveIntensity: ei, roughness: 0.05,
  }),
  floor: () => new THREE.MeshStandardMaterial({ color: 0xdde4ee, roughness: 0.08, metalness: 0.45 }),
};

// Three.js初始化

async function initThree() {
  if (!containerRef.value) return;

  scene = new THREE.Scene();
  loadPanoramaBackground('/quanjing.png');

  camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.05, 1000);
  camera.position.set(0, 2.2, 8);
  camera.lookAt(0, 1.2, 0);

  renderer = new THREE.WebGLRenderer({ antialias: true, alpha: false, powerPreference: 'high-performance', logarithmicDepthBuffer: true });
  renderer.setSize(window.innerWidth, window.innerHeight);
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
  renderer.shadowMap.enabled = true;
  renderer.shadowMap.type = THREE.PCFSoftShadowMap;
  renderer.toneMapping = THREE.ACESFilmicToneMapping;
  renderer.toneMappingExposure = 1.18;
  renderer.outputColorSpace = THREE.SRGBColorSpace;
  containerRef.value.appendChild(renderer.domElement);

  // 轨道控制 禁止看底部
  controls = new OrbitControls(camera, renderer.domElement);
  controls.enableDamping = true;
  controls.dampingFactor = 0.08;
  controls.minDistance = 2;
  controls.maxDistance = 30;
  controls.minPolarAngle = 0;
  controls.maxPolarAngle = Math.PI / 2;
  controls.target.set(0, 1.2, 0);
  controls.enablePan = false;
  controls.update();

  buildLighting();
  buildFloor();
  buildPlatform();
  buildInfoKiosks();
  buildGroundDetails();

  modelGroup = new THREE.Group();
  modelGroup.position.set(0, 0, 0);
  scene.add(modelGroup);

  await loadGLBModel();
  animate();
  loading.value = false;

  window.addEventListener('mousemove', onMouseMove);
  window.addEventListener('click', onMouseClick);
  window.addEventListener('resize', onResize);
}

// 灯光系统
function buildLighting() {
  // 主顶部射灯
  const mainSpot = new THREE.SpotLight(0xffffff, 1200);
  mainSpot.position.set(0, 13.5, 0);
  mainSpot.angle = Math.PI / 5;
  mainSpot.penumbra = 0.5;
  mainSpot.decay = 1.0;
  mainSpot.distance = 45;
  mainSpot.castShadow = true;
  mainSpot.shadow.mapSize.set(4096, 4096);
  mainSpot.shadow.camera.near = 0.5;
  mainSpot.shadow.camera.far = 48;
  mainSpot.shadow.bias = -0.0003;
  scene.add(mainSpot);
  mainSpot.target.position.set(0, 1.5, 0);
  scene.add(mainSpot.target);

  // 前补光
  const frontSpot = new THREE.SpotLight(0xffffff, 500);
  frontSpot.position.set(0, 8, 9);
  frontSpot.angle = Math.PI / 6;
  frontSpot.penumbra = 0.6;
  frontSpot.decay = 1.2;
  frontSpot.distance = 25;
  scene.add(frontSpot);
  frontSpot.target.position.set(0, 1.5, 0);
  scene.add(frontSpot.target);

  scene.add(new THREE.AmbientLight(0xffffff, 3.5));
  scene.add(new THREE.HemisphereLight(0xffffff, 0xdde4ee, 2.8));

  // 蓝色轮廓补光
  const blueRim = new THREE.DirectionalLight(0x55aaff, 1.5);
  blueRim.position.set(-10, 6, -8);
  scene.add(blueRim);

  // 右侧暖白补光
  const rightFill = new THREE.DirectionalLight(0xffffff, 1.0);
  rightFill.position.set(10, 5, 4);
  scene.add(rightFill);

  // 展台蓝色氛围点光
  const plat = new THREE.PointLight(0x3388ff, 80, 8, 1.6);
  plat.position.set(0, 0.6, 0);
  scene.add(plat);

  // 顶部白色点光
  const topLight = new THREE.PointLight(0xffffff, 150, 22, 1.2);
  topLight.position.set(0, 9, 0);
  scene.add(topLight);

  // 四角射灯
  [[-8, -8], [8, -8], [-8, 4], [8, 4]].forEach(([x, z]) => {
    const s = new THREE.SpotLight(0xffffff, 120);
    s.position.set(x, 10, z);
    s.angle = Math.PI / 10; s.penumbra = 0.65; s.decay = 1.5; s.distance = 20;
    scene.add(s);
    const t = new THREE.Object3D();
    t.position.set(x * 0.15, 0, z * 0.15);
    scene.add(t); s.target = t;
  });
}

// 暗灰色地板+发光圆环
function buildFloor() {
  const g = new THREE.Group();

  const floorMat = new THREE.MeshStandardMaterial({ color: 0x2a2d35, roughness: 0.05, metalness: 0.15, envMapIntensity: 1.2 });
  const floor = new THREE.Mesh(new THREE.CircleGeometry(28, 128), floorMat);
  floor.rotation.x = -Math.PI / 2;
  floor.receiveShadow = true;
  g.add(floor);

  // 展台中心蓝色发光圆环
  [{ r: 2.2, w: 0.035, c: 0x66aaff, ei: 3 }, { r: 3.8, w: 0.022, c: 0x4488ee, ei: 2 }, { r: 5.5, w: 0.018, c: 0x2266cc, ei: 1.2 }].forEach(({ r, w, c, ei }) => {
    const rm = new THREE.Mesh(
        new THREE.RingGeometry(r - (w as number), r + (w as number), 256),
        new THREE.MeshStandardMaterial({ color: c, emissive: new THREE.Color(c as number), emissiveIntensity: ei as number, roughness: 0.05, transparent: true, opacity: 0.7, side: THREE.DoubleSide })
    );
    rm.rotation.x = -Math.PI / 2;
    rm.position.y = 0.003;
    g.add(rm);
  });

  // 边缘渐变
  [{ i: 16, o: 20, op: 0.25 }, { i: 19, o: 24, op: 0.55 }, { i: 22, o: 28, op: 0.88 }].forEach(({ i, o, op }) => {
    const fadeRing = new THREE.Mesh(
        new THREE.RingGeometry(i as number, o as number, 128),
        new THREE.MeshBasicMaterial({ color: 0x2a2d35, transparent: true, opacity: op as number, side: THREE.DoubleSide })
    );
    fadeRing.rotation.x = -Math.PI / 2;
    fadeRing.position.y = 0.002;
    g.add(fadeRing);
  });

  scene.add(g);
}

// 全景图背景

function loadPanoramaBackground(imageUrl: string) {
  const loader = new THREE.TextureLoader();
  loader.load(imageUrl, (texture) => {
    texture.mapping = THREE.EquirectangularReflectionMapping;
    texture.colorSpace = THREE.SRGBColorSpace;
    scene.background = texture;
    scene.environment = texture;
    scene.backgroundIntensity = 1.0;
  }, undefined, (err) => {
    console.warn('全景图加载失败', err);
  });
  scene.background = new THREE.Color(0xdde4ee);
}

// 暗灰色展台
function buildPlatform() {
  const g = new THREE.Group();
  const podMat = new THREE.MeshStandardMaterial({ color: 0x3a3f47, roughness: 0.15, metalness: 0.35, envMapIntensity: 1.2, side: THREE.DoubleSide });

  // 三层台座 贴地
  [{ r: 4.8, rb: 5.0, h: 0.05 }, { r: 3.4, rb: 3.58, h: 0.1 }, { r: 2.05, rb: 2.22, h: 0.2 }].forEach(({ r, rb, h }) => {
    const mesh = new THREE.Mesh(new THREE.CylinderGeometry(r as number, rb as number, h as number, 128), podMat);
    mesh.position.y = 0;
    mesh.receiveShadow = true;
    mesh.castShadow = true;
    g.add(mesh);
  });

  // 顶台面
  const topFace = new THREE.Mesh(new THREE.CylinderGeometry(2.0, 2.0, 0.1, 128),
      new THREE.MeshStandardMaterial({ color: 0x3a3f47, roughness: 0.12, metalness: 0.45, side: THREE.DoubleSide }));
  topFace.position.y = 0;
  g.add(topFace);

  // 蓝色灯带
  [{ r: 4.82, i: 6 }, { r: 3.42, i: 8 }, { r: 2.07, i: 12 }].forEach(({ r, i }) => {
    const torus = new THREE.Mesh(new THREE.TorusGeometry(r as number, 0.028, 20, 300), mat.glowBlue(i as number));
    torus.rotation.x = Math.PI / 2;
    torus.position.y = 0.01;
    g.add(torus);
  });

  // 辉光盘
  const glowDisc = new THREE.Mesh(
      new THREE.CylinderGeometry(1.95, 1.95, 0.008, 128),
      new THREE.MeshStandardMaterial({ color: 0x88ccff, emissive: new THREE.Color(0x4499ff), emissiveIntensity: 1.4, transparent: true, opacity: 0.55, side: THREE.DoubleSide })
  );
  glowDisc.position.y = 0.01;
  g.add(glowDisc);

  scene.add(g);
}

// 3台激光医疗设备

function buildInfoKiosks() {
  const configs: Array<{ pos: [number,number,number]; ry: number }> = [
    { pos: [-5, 0, 3], ry: 0.6 },
    { pos: [-6, 0, -1], ry: 0.3 },
    { pos: [-4, 0, -4], ry: -0.2 },
  ];

  const bodyMat = new THREE.MeshStandardMaterial({ color: 0xd8e2ec, roughness: 0.25, metalness: 0.12 });
  const bodyDark = new THREE.MeshStandardMaterial({ color: 0xb0bec5, roughness: 0.30, metalness: 0.18 });
  const armMat = new THREE.MeshStandardMaterial({ color: 0xcfd8dc, roughness: 0.22, metalness: 0.15 });
  const jointMat = new THREE.MeshStandardMaterial({ color: 0x90a4ae, roughness: 0.18, metalness: 0.35 });
  const screenMat = new THREE.MeshStandardMaterial({ color: 0x0a1020, emissive: new THREE.Color(0x0a2860), emissiveIntensity: 1.4, roughness: 0.05 });
  const wheelMat = new THREE.MeshStandardMaterial({ color: 0x37474f, roughness: 0.85, metalness: 0.1 });
  const wheelRim = new THREE.MeshStandardMaterial({ color: 0x78909c, roughness: 0.4, metalness: 0.6 });
  const laserMat = new THREE.MeshStandardMaterial({ color: 0x00eeff, emissive: new THREE.Color(0x00aaff), emissiveIntensity: 4, roughness: 0.05 });
  const cableMat = new THREE.MeshStandardMaterial({ color: 0x546e7a, roughness: 0.9, metalness: 0.05 });

  configs.forEach(({ pos, ry }) => {
    const root = new THREE.Group();
    root.position.set(...pos);
    root.rotation.y = ry;

    const W = 0.72, D = 0.55, H = 1.0;

    // 底板
    const basePlate = new THREE.Mesh(new THREE.BoxGeometry(W + 0.12, 0.06, D + 0.12), bodyDark);
    basePlate.position.y = 0.03;
    basePlate.castShadow = true;
    root.add(basePlate);

    // 4个脚轮
    [[W/2+0.04, D/2+0.04], [-W/2-0.04, D/2+0.04], [W/2+0.04, -D/2-0.04], [-W/2-0.04, -D/2-0.04]].forEach(([cx, cz]) => {
      const casterGroup = new THREE.Group();
      casterGroup.position.set(cx, 0, cz);
      const fork = new THREE.Mesh(new THREE.CylinderGeometry(0.025, 0.025, 0.09, 8), wheelRim);
      fork.position.y = 0.045;
      casterGroup.add(fork);
      const tire = new THREE.Mesh(new THREE.TorusGeometry(0.065, 0.028, 8, 18), wheelMat);
      tire.rotation.x = Math.PI / 2;
      casterGroup.add(tire);
      const hub = new THREE.Mesh(new THREE.CylinderGeometry(0.018, 0.018, 0.06, 8), wheelRim);
      hub.rotation.x = Math.PI / 2;
      casterGroup.add(hub);
      root.add(casterGroup);
    });

    // 主箱体
    const cabinet = new THREE.Mesh(new THREE.BoxGeometry(W, H, D), bodyMat);
    cabinet.position.y = 0.06 + H / 2;
    cabinet.castShadow = true;
    cabinet.receiveShadow = true;
    root.add(cabinet);

    // 前面板
    const frontPanel = new THREE.Mesh(new THREE.BoxGeometry(W - 0.06, H - 0.1, 0.025), bodyDark);
    frontPanel.position.set(0, 0.06 + H/2, D/2 + 0.013);
    root.add(frontPanel);

    // 触摸屏
    const screen = new THREE.Mesh(new THREE.BoxGeometry(0.32, 0.24, 0.03), screenMat);
    screen.position.set(-0.06, 0.06 + H/2 + 0.16, D/2 + 0.028);
    root.add(screen);

    // 控制旋钮
    const knob = new THREE.Mesh(new THREE.CylinderGeometry(0.028, 0.028, 0.04, 16), jointMat);
    knob.rotation.x = Math.PI / 2;
    knob.position.set(-0.06, 0.06 + H/2 - 0.12, D/2 + 0.035);
    root.add(knob);

    // 通风格栅
    for (let vi = 0; vi < 4; vi++) {
      const vent = new THREE.Mesh(new THREE.BoxGeometry(0.18, 0.012, 0.015), bodyDark);
      vent.position.set(0.2, 0.06 + H/2 - 0.28 + vi * 0.028, D/2 + 0.02);
      root.add(vent);
    }

    // 顶盖
    const topCap = new THREE.Mesh(new THREE.BoxGeometry(W + 0.04, 0.04, D + 0.04), bodyDark);
    topCap.position.y = 0.06 + H + 0.02;
    root.add(topCap);

    // 悬臂系统 从箱顶出发
    const armBaseY = 0.06 + H + 0.04;

    // 转台
    const turret = new THREE.Group();
    turret.position.set(W/2 - 0.15, armBaseY, 0);
    root.add(turret);
    const turretBase = new THREE.Mesh(new THREE.CylinderGeometry(0.09, 0.09, 0.07, 16), jointMat);
    turretBase.position.y = 0.035;
    turret.add(turretBase);

    // 立柱
    const column = new THREE.Group();
    column.position.y = 0.07;
    turret.add(column);
    const columnBody = new THREE.Mesh(new THREE.CylinderGeometry(0.045, 0.055, 0.55, 12), armMat);
    columnBody.position.y = 0.275;
    column.add(columnBody);
    const colTopDisk = new THREE.Mesh(new THREE.CylinderGeometry(0.07, 0.07, 0.045, 16), jointMat);
    colTopDisk.position.y = 0.572;
    column.add(colTopDisk);

    // 肩关节→大臂
    const shoulder = new THREE.Group();
    shoulder.position.y = 0.615;
    column.add(shoulder);
    const shoulderBall = new THREE.Mesh(new THREE.SphereGeometry(0.07, 14, 14), jointMat);
    shoulder.add(shoulderBall);

    const upperArmGroup = new THREE.Group();
    upperArmGroup.rotation.z = -35 * Math.PI / 180;
    shoulder.add(upperArmGroup);
    const upperArmLen = 0.88;
    const upperArmMesh = new THREE.Mesh(new THREE.CylinderGeometry(0.038, 0.045, upperArmLen, 12), armMat);
    upperArmMesh.position.y = upperArmLen / 2;
    upperArmMesh.castShadow = true;
    upperArmGroup.add(upperArmMesh);

    const elbowDisk = new THREE.Mesh(new THREE.CylinderGeometry(0.06, 0.06, 0.04, 14), jointMat);
    elbowDisk.position.y = upperArmLen + 0.02;
    upperArmGroup.add(elbowDisk);

    // 肘关节→小臂
    const foreArmGroup = new THREE.Group();
    foreArmGroup.position.y = upperArmLen + 0.04;
    foreArmGroup.rotation.z = 75 * Math.PI / 180;
    upperArmGroup.add(foreArmGroup);
    const foreArmLen = 0.72;
    const foreArmMesh = new THREE.Mesh(new THREE.CylinderGeometry(0.032, 0.038, foreArmLen, 12), armMat);
    foreArmMesh.position.y = foreArmLen / 2;
    foreArmMesh.castShadow = true;
    foreArmGroup.add(foreArmMesh);

    const wristDisk = new THREE.Mesh(new THREE.CylinderGeometry(0.052, 0.052, 0.036, 14), jointMat);
    wristDisk.position.y = foreArmLen + 0.018;
    foreArmGroup.add(wristDisk);

    // 腕部→激光头
    const wristGroup = new THREE.Group();
    wristGroup.position.y = foreArmLen + 0.036;
    foreArmGroup.add(wristGroup);
    const wristTube = new THREE.Mesh(new THREE.CylinderGeometry(0.028, 0.032, 0.18, 12), armMat);
    wristTube.position.y = 0.09;
    wristGroup.add(wristTube);
    const wristBall = new THREE.Mesh(new THREE.SphereGeometry(0.04, 12, 12), jointMat);
    wristBall.position.y = 0.20;
    wristGroup.add(wristBall);

    const headGroup = new THREE.Group();
    headGroup.position.y = 0.22;
    wristGroup.add(headGroup);
    const headBody = new THREE.Mesh(new THREE.CylinderGeometry(0.018, 0.038, 0.14, 14), armMat);
    headBody.position.y = 0.07;
    headGroup.add(headBody);
    const laserNozzle = new THREE.Mesh(new THREE.CylinderGeometry(0.012, 0.012, 0.05, 10), laserMat);
    laserNozzle.position.y = 0.155;
    headGroup.add(laserNozzle);

    // 线缆
    const cableCurve = new THREE.CatmullRomCurve3([
      new THREE.Vector3(W/2 - 0.1, armBaseY + 0.04, 0),
      new THREE.Vector3(W/2 + 0.05, armBaseY + 0.18, 0.08),
      new THREE.Vector3(W/2 - 0.1, armBaseY + 0.2, -0.05),
      new THREE.Vector3(W/2 - 0.15, armBaseY + 0.07, 0),
    ]);
    const cableTube = new THREE.Mesh(new THREE.TubeGeometry(cableCurve, 18, 0.012, 7, false), cableMat);
    root.add(cableTube);

    scene.add(root);
  });
}

// 地面辉光
function buildGroundDetails() {
  const g = new THREE.Group();
  const glow = new THREE.Mesh(
      new THREE.CircleGeometry(6.2, 128),
      new THREE.MeshBasicMaterial({ color: 0x0d44aa, transparent: true, opacity: 0.14, side: THREE.DoubleSide })
  );
  glow.rotation.x = -Math.PI / 2;
  glow.position.y = 0.005;
  g.add(glow);

  const ledMat = new THREE.MeshStandardMaterial({ color: 0x55bbff, emissive: new THREE.Color(0x2288cc), emissiveIntensity: 6, roughness: 0.05 });
  [6.5, 8.0].forEach(r => {
    const ring = new THREE.Mesh(new THREE.TorusGeometry(r, 0.028, 12, 300), ledMat);
    ring.rotation.x = Math.PI / 2;
    ring.position.y = 0.012;
    g.add(ring);
  });
  scene.add(g);
}

// 加载GLB模型

async function loadGLBModel() {
  try {
    const blob = await modelService.getModelFile('1002', 'glb', (p: number) => {
      progress.value = Math.min(Math.round(p * 0.9), 90);
    });
    if (!(blob instanceof Blob)) throw new Error('返回的数据不是有效的模型文件');

    const url = URL.createObjectURL(blob);
    const loader = new GLTFLoader();
    const draco = new DRACOLoader();
    draco.setDecoderPath('https://www.gstatic.com/draco/versioned/decoders/1.5.6/');
    loader.setDRACOLoader(draco);

    await new Promise<void>((resolve, reject) => {
      loader.load(url, (gltf) => {
        const model = gltf.scene;
        currentModel = model;

        model.traverse((child) => {
          if (child.isMesh) {
            child.material.color.set(0x999999);
          }
        });

        // 自动缩放让模型占满视野
        const box = new THREE.Box3().setFromObject(model);
        const size = box.getSize(new THREE.Vector3());
        const maxDim = Math.max(size.x, size.y, size.z);
        const scale = 4.5 / maxDim;
        model.scale.setScalar(scale);

        // 底部贴地
        const box2 = new THREE.Box3().setFromObject(model);
        const center = box2.getCenter(new THREE.Vector3());
        const minY = box2.min.y;
        model.position.x = -center.x;
        model.position.z = -center.z;
        model.position.y = -minY + 0.05;

        if (modelGroup) modelGroup.add(model);
        progress.value = 100;
        URL.revokeObjectURL(url);
        resolve();
      }, (xhr) => {
        if (xhr.total) progress.value = Math.round((xhr.loaded / xhr.total) * 85);
      }, (err) => { reject(err); });
    });
  } catch (err) {
    console.error('模型加载失败:', err);
    progress.value = 100;
  }
}

// 渲染循环
function animate() {
  animationId = requestAnimationFrame(animate);
  controls.update();
  if (mixer) mixer.update(0.016);
  if (autoRotate.value && modelGroup) modelGroup.rotation.y += 0.05;
  // 同步视角距离
  const d = camera.position.distanceTo(controls.target);
  cameraDistance.value = Math.round(d * 10) / 10;
  renderer.render(scene, camera);
}

// 控制函数
function resetCamera() {
  camera.position.set(0, 2.2, 8);
  camera.fov = 45;
  camera.updateProjectionMatrix();
  controls.target.set(0, 1.2, 0);
  controls.update();
  if (modelGroup) modelGroup.rotation.set(0, 0, 0);
  cameraDistance.value = 8;
  sceneScaleValue.value = 100;
}

function toggleAutoRotate() { autoRotate.value = !autoRotate.value; }

function toggleWireframe() {
  wireframe.value = !wireframe.value;
  currentModel?.traverse((c: any) => {
    if (c.isMesh && c.material) c.material.wireframe = wireframe.value;
  });
}

function applyCameraDistance() {
  const dist = cameraDistance.value;
  const dir = camera.position.clone().sub(controls.target).normalize();
  camera.position.copy(controls.target).addScaledVector(dir, dist);
  controls.update();
}

const BASE_FOV = 45;
function applySceneScale() {
  const fov = BASE_FOV * (100 / sceneScaleValue.value);
  camera.fov = Math.max(20, Math.min(75, fov));
  camera.updateProjectionMatrix();
}
function sceneScaleUp() {
  sceneScaleValue.value = Math.min(300, sceneScaleValue.value + 10);
  applySceneScale();
}
function sceneScaleDown() {
  sceneScaleValue.value = Math.max(30, sceneScaleValue.value - 10);
  applySceneScale();
}

// 鼠标事件
function onMouseMove(e: MouseEvent) {
  if (!containerRef.value || !currentModel || !tooltipRef.value) return;
  const rect = renderer.domElement.getBoundingClientRect();
  mouse.x = ((e.clientX - rect.left) / rect.width) * 2 - 1;
  mouse.y = -((e.clientY - rect.top) / rect.height) * 2 + 1;
  raycaster.setFromCamera(mouse, camera);
  const hits = raycaster.intersectObject(currentModel, true);
  const tip = tooltipRef.value;
  if (hits.length > 0) {
    hoveredPart = hits[0].object;
    tooltipText.value = resolvePartName(hoveredPart.name);
    tip.style.left = `${Math.min(e.clientX + 16, window.innerWidth - 260)}px`;
    tip.style.top = `${Math.min(e.clientY + 16, window.innerHeight - 70)}px`;
    tip.style.opacity = '1';
    tip.style.transform = 'translateY(0) scale(1)';
  } else {
    hoveredPart = null;
    tip.style.opacity = '0';
    tip.style.transform = 'translateY(6px) scale(0.95)';
  }
}

function onMouseClick() {
  if (!hoveredPart) return;
  const partName = resolvePartName(hoveredPart.name);

  // 点击激光发射头就播放视频
  if (partName.includes('激光发射头') || partName.includes('激光头') || hoveredPart.name.includes('003') || hoveredPart.name.includes('9002')) {
    showVideo.value = true;
    nextTick(() => {
      if (videoRef.value) {
        videoRef.value.muted = true;
        videoRef.value.play().catch(() => {
          setTimeout(() => videoRef.value?.play(), 100);
        });
      }
    });
    return;
  }

  sendMessage(`请详细介绍「${partName}」的功能、技术参数及手术应用。`);
}

function closeVideo() {
  showVideo.value = false;
  if (videoRef.value) {
    videoRef.value.pause();
    videoRef.value.currentTime = 0;
  }
}

function onResize() {
  camera.aspect = window.innerWidth / window.innerHeight;
  camera.updateProjectionMatrix();
  renderer.setSize(window.innerWidth, window.innerHeight);
}

// 生命周期
onMounted(() => { initThree(); });
onUnmounted(() => {
  window.removeEventListener('mousemove', onMouseMove);
  window.removeEventListener('click', onMouseClick);
  window.removeEventListener('resize', onResize);
  cancelAnimationFrame(animationId);
  mixer?.stopAllAction();
  renderer?.dispose();
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;600;700&family=IBM+Plex+Mono:wght@300;400;500&family=Rajdhani:wght@400;500;600;700&display=swap');

*, *::before, *::after { margin: 0; padding: 0; box-sizing: border-box; }

:root {
  --c-blue: #4db8ff;
  --c-blue-dim: #1a77cc;
  --c-blue-dark: #0d3a6e;
  --c-text-muted: #5a7a99;
  --c-border: rgba(77,184,255,0.22);
  --font-ui: 'Noto Sans SC', sans-serif;
  --font-mono: 'IBM Plex Mono', monospace;
  --font-display: 'Rajdhani', sans-serif;
}

.exhibition-container {
  position: absolute; inset: 0;
  background: #dde4ee;
  overflow: hidden;
  font-family: var(--font-ui);
  user-select: none;
}

/* 顶部铭牌 */
.brand-header {
  position: fixed; top: 40px; left: 0; right: 0;
  z-index: 1000; pointer-events: none;
  display: flex; justify-content: center; padding: 16px;
}
.brand-inner {
  display: flex; align-items: center; gap: 16px;
  background: rgba(6,14,28,0.88);
  backdrop-filter: blur(20px) saturate(1.6);
  border: 1px solid rgba(77,184,255,0.28);
  border-radius: 16px; padding: 12px 28px 12px 18px;
  box-shadow: 0 0 0 1px rgba(77,184,255,0.08) inset, 0 8px 32px rgba(0,0,0,0.5);
}
.brand-logo {
  width: 44px; height: 44px; flex-shrink: 0;
  background: linear-gradient(135deg, #0d5caa, #0099dd);
  border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 4px 14px rgba(13,100,200,0.45);
}
.brand-text h1 { font-size: 17px; font-weight: 600; color: #aaddff; letter-spacing: 0.4px; line-height: 1.2; }
.brand-status { display: flex; align-items: center; gap: 6px; margin-left: 12px; font-size: 10px; font-weight: 500; color: #22c55e; letter-spacing: 1.5px; font-family: var(--font-display); }
.status-dot { width: 7px; height: 7px; border-radius: 50%; background: #22c55e; box-shadow: 0 0 8px rgba(34,197,94,0.7); animation: blink 2s ease-in-out infinite; }

/* 加载遮罩 */
.loading-overlay {
  position: absolute; inset: 0; z-index: 9999;
  background: linear-gradient(160deg, #080e18 0%, #0b1422 50%, #07101c 100%);
  display: flex; align-items: center; justify-content: center;
}
.loading-panel {
  position: relative;
  display: flex; flex-direction: column; align-items: center; gap: 18px;
  padding: 50px 60px;
  background: rgba(255,255,255,0.028);
  border: 1px solid rgba(77,184,255,0.14);
  border-radius: 24px; overflow: hidden;
}
.loading-scan-line {
  position: absolute; top: 0; left: 0; right: 0; height: 2px;
  background: linear-gradient(90deg, transparent, #4db8ff, transparent);
  animation: scanLine 2.4s linear infinite;
}
@keyframes scanLine { 0% { top: 0%; opacity: 0; } 10% { opacity: 1; } 90% { opacity: 1; } 100% { top: 100%; opacity: 0; } }

.loading-icon { position: relative; width: 82px; height: 82px; display: flex; align-items: center; justify-content: center; }
.loading-ring { position: absolute; border-radius: 50%; border-style: solid; border-color: transparent; }
.loading-ring.r1 { width: 82px; height: 82px; border-width: 2px; border-top-color: rgba(77,184,255,0.9); animation: spin 1.1s linear infinite; }
.loading-ring.r2 { width: 64px; height: 64px; border-width: 2px; border-right-color: rgba(77,184,255,0.5); animation: spin 1.7s linear infinite reverse; }
.loading-ring.r3 { width: 46px; height: 46px; border-width: 1.5px; border-top-color: rgba(77,184,255,0.28); animation: spin 2.3s linear infinite; }
.loading-center-icon { position: relative; z-index: 1; }
@keyframes spin { to { transform: rotate(360deg); } }

.loading-label { font-size: 15px; font-weight: 500; color: #c8ddf0; letter-spacing: 0.5px; }
.loading-sublabel { font-family: var(--font-display); font-size: 10px; color: rgba(77,184,255,0.58); letter-spacing: 2.5px; margin-top: -8px; }
.loading-bar-track { width: 230px; height: 4px; background: rgba(77,184,255,0.1); border-radius: 2px; overflow: hidden; }
.loading-bar-fill { height: 100%; border-radius: 2px; background: linear-gradient(90deg, #0d60bb, #4db8ff); transition: width 0.4s ease; position: relative; }
.loading-bar-glow { position: absolute; right: 0; top: -2px; width: 26px; height: 8px; background: radial-gradient(ellipse, rgba(77,184,255,0.8), transparent); }
.loading-pct { font-family: var(--font-mono); font-size: 30px; font-weight: 300; color: #4db8ff; letter-spacing: -1px; }
.loading-pct span { font-size: 14px; color: rgba(77,184,255,0.48); margin-left: 2px; }
.loading-steps { display: flex; gap: 22px; font-family: var(--font-display); font-size: 10px; letter-spacing: 1.5px; }
.loading-steps span { color: rgba(77,184,255,0.28); transition: color 0.4s; }
.loading-steps span.done { color: rgba(77,184,255,0.9); }

.fade-leave-active { transition: opacity 0.8s ease; }
.fade-leave-to { opacity: 0; }

/* 零件提示 */
.part-tooltip {
  position: fixed; z-index: 8000; pointer-events: none;
  display: flex; align-items: center; gap: 10px;
  background: rgba(6,14,28,0.96);
  backdrop-filter: blur(16px);
  border: 1px solid rgba(77,184,255,0.3); border-radius: 10px;
  padding: 9px 16px 9px 12px;
  box-shadow: 0 6px 28px rgba(0,0,0,0.5), 0 0 12px rgba(77,184,255,0.08);
  opacity: 0; transform: translateY(6px) scale(0.95);
  transition: opacity 0.16s ease, transform 0.16s ease;
}
.tooltip-indicator { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; background: var(--c-blue); box-shadow: 0 0 10px rgba(77,184,255,0.9); animation: blink 1.5s ease-in-out infinite; }
.tooltip-label { font-family: var(--font-display); font-size: 9px; font-weight: 500; letter-spacing: 1.5px; color: #4a7799; line-height: 1; }
.tooltip-name { font-size: 13px; font-weight: 600; color: #aaddff; line-height: 1.2; margin-top: 2px; }

/* 右侧控制栏 */
.viewer-controls {
  position: fixed; right: 20px; top: 50%; transform: translateY(-50%);
  z-index: 1000;
  display: flex; flex-direction: column; align-items: center; gap: 4px;
  background: rgba(8,18,34,0.88); backdrop-filter: blur(16px) saturate(1.4);
  border: 1px solid rgba(77,184,255,0.28); border-radius: 18px; padding: 12px 8px;
  box-shadow: 0 8px 36px rgba(0,0,0,0.5), 0 0 0 1px rgba(77,184,255,0.1) inset;
}
.control-group { display: flex; flex-direction: column; gap: 2px; }
.control-divider { width: 30px; height: 1px; background: var(--c-border); margin: 4px 0; }
.ctrl-btn {
  width: 54px; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 4px;
  padding: 9px 4px; border-radius: 12px; background: transparent;
  border: 1px solid transparent; color: #5aabdd; cursor: pointer; transition: all 0.2s ease;
}
.ctrl-btn span { font-size: 9.5px; font-weight: 500; letter-spacing: 0.3px; line-height: 1; }
.ctrl-btn:hover { background: rgba(77,184,255,0.15); border-color: rgba(77,184,255,0.35); color: #aaddff; transform: scale(1.06); }
.ctrl-btn.active { background: linear-gradient(135deg, #1060b8, #0099dd); color: #fff; border-color: transparent; box-shadow: 0 3px 14px rgba(16,96,184,0.5); }
.control-hint { display: flex; flex-direction: column; align-items: center; gap: 3px; margin-top: 4px; font-family: var(--font-display); font-size: 8.5px; letter-spacing: 0.8px; color: #4a7799; opacity: 0.7; }

.ctrl-slider-block { display: flex; flex-direction: column; align-items: center; gap: 2px; width: 54px; }
.ctrl-slider-label { display: flex; align-items: center; gap: 3px; font-family: var(--font-display); font-size: 8px; letter-spacing: 0.8px; color: #4a7799; white-space: nowrap; margin-bottom: 1px; }
.ctrl-icon-btn {
  width: 28px; height: 28px; border-radius: 8px;
  background: rgba(77,184,255,0.07); border: 1px solid rgba(77,184,255,0.18);
  color: #5aabdd; cursor: pointer; display: flex; align-items: center; justify-content: center;
  transition: all 0.18s ease; flex-shrink: 0;
}
.ctrl-icon-btn:hover { background: rgba(77,184,255,0.2); border-color: rgba(77,184,255,0.45); color: #aaddff; }
.ctrl-val { font-family: var(--font-mono); font-size: 9.5px; color: #5aabdd; letter-spacing: 0.3px; margin-top: 1px; }
.ctrl-track { width: 32px; padding: 2px 0; }
.ctrl-slider { -webkit-appearance: none; appearance: none; width: 100%; height: 52px; writing-mode: vertical-lr; direction: rtl; background: transparent; cursor: pointer; outline: none; }
.ctrl-slider::-webkit-slider-runnable-track { width: 3px; background: rgba(77,184,255,0.2); border-radius: 2px; }
.ctrl-slider::-webkit-slider-thumb { -webkit-appearance: none; width: 14px; height: 14px; border-radius: 50%; background: linear-gradient(135deg, #1060b8, #0099dd); box-shadow: 0 0 8px rgba(77,184,255,0.6); border: none; margin-left: -5.5px; }
.ctrl-slider::-moz-range-track { width: 3px; background: rgba(77,184,255,0.2); border-radius: 2px; }
.ctrl-slider::-moz-range-thumb { width: 14px; height: 14px; border-radius: 50%; background: linear-gradient(135deg, #1060b8, #0099dd); box-shadow: 0 0 8px rgba(77,184,255,0.6); border: none; }

/* 视频弹窗 */
.laser-video-modal { position: fixed; inset: 0; z-index: 99999; background: rgba(0,0,0,0.75); backdrop-filter: blur(6px); display: flex; align-items: center; justify-content: center; }
.video-box { position: relative; width: 80%; max-width: 900px; background: #0a1528; border: 1px solid rgba(77,184,255,0.3); border-radius: 16px; overflow: hidden; box-shadow: 0 0 60px rgba(77,184,255,0.2); }
.video-box video { display: block; width: 100%; height: auto; }
.video-close { position: absolute; right: 12px; top: 12px; width: 32px; height: 32px; border-radius: 50%; background: rgba(0,0,0,0.5); border: 1px solid rgba(77,184,255,0.4); color: #fff; font-size: 16px; cursor: pointer; z-index: 2; }

/* AI侧边栏 */
.ai-sidebar {
  position: fixed; left: 18px; top: 50%; transform: translateY(-50%);
  width: 332px; max-height: calc(100vh - 120px); z-index: 1000;
  transition: transform 0.38s cubic-bezier(0.4,0,0.2,1), opacity 0.38s ease;
}
.ai-sidebar.collapsed { transform: translateY(-50%) translateX(calc(-100% - 32px)); opacity: 0; pointer-events: none; }
.sidebar-toggle {
  position: absolute; right: -32px; top: 50%; transform: translateY(-50%);
  width: 32px; height: 54px;
  background: rgba(8,18,34,0.88); backdrop-filter: blur(12px);
  border: 1px solid rgba(77,184,255,0.28); border-left: none; border-radius: 0 12px 12px 0;
  display: flex; align-items: center; justify-content: center;
  color: #5aabdd; cursor: pointer; pointer-events: auto;
  box-shadow: 5px 0 14px rgba(0,0,0,0.4); transition: background 0.2s;
}
.sidebar-toggle:hover { background: rgba(77,184,255,0.12); }
.ai-sidebar-inner {
  background: rgba(6,14,26,0.94); backdrop-filter: blur(20px) saturate(1.5);
  border: 1px solid rgba(77,184,255,0.28); border-radius: 20px; overflow: hidden;
  display: flex; flex-direction: column; max-height: calc(100vh - 120px);
  box-shadow: 0 10px 48px rgba(0,0,0,0.5), 0 0 0 1px rgba(77,184,255,0.08) inset;
}
.ai-head { display: flex; align-items: center; gap: 12px; padding: 16px 18px 14px; border-bottom: 1px solid rgba(77,184,255,0.12); flex-shrink: 0; }
.ai-avatar { width: 38px; height: 38px; flex-shrink: 0; background: linear-gradient(135deg, #1060b8, #0099dd); border-radius: 10px; display: flex; align-items: center; justify-content: center; box-shadow: 0 4px 14px rgba(16,96,184,0.3); }
.ai-name { font-size: 13.5px; font-weight: 600; color: #aaddff; }
.ai-role { font-family: var(--font-display); font-size: 9px; letter-spacing: 1.8px; color: #4a7799; margin-top: 2px; }
.ai-online-dot { width: 9px; height: 9px; border-radius: 50%; background: #22c55e; margin-left: auto; box-shadow: 0 0 8px rgba(34,197,94,0.7); animation: blink 2s ease-in-out infinite; }

.ai-messages { flex: 1; overflow-y: auto; padding: 14px 16px; display: flex; flex-direction: column; gap: 12px; min-height: 0; }
.ai-messages::-webkit-scrollbar { width: 3px; }
.ai-messages::-webkit-scrollbar-thumb { background: rgba(77,184,255,0.35); border-radius: 2px; }
.ai-welcome { background: rgba(77,184,255,0.06); border: 1px dashed rgba(77,184,255,0.2); border-radius: 12px; padding: 14px 16px; text-align: center; }
.welcome-icon { font-size: 26px; margin-bottom: 8px; }
.ai-welcome p { color: #5a9ab8; font-size: 13px; line-height: 1.72; }
.ai-welcome p + p { margin-top: 6px; }

.chat-msg { display: flex; gap: 8px; align-items: flex-start; }
.chat-msg.user { flex-direction: row-reverse; }
.msg-meta { width: 28px; height: 28px; flex-shrink: 0; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 10.5px; font-weight: 700; }
.chat-msg.user .msg-meta { background: linear-gradient(135deg, #1060b8, #0099dd); color: #fff; }
.chat-msg.ai .msg-meta { background: rgba(77,184,255,0.1); color: #5aabdd; border: 1px solid rgba(77,184,255,0.24); }
.msg-text { max-width: 232px; padding: 10px 14px; font-size: 13px; line-height: 1.66; border-radius: 14px; }
.chat-msg.user .msg-text { background: linear-gradient(135deg, #1060b8, #0099dd); color: #fff; border-radius: 14px 4px 14px 14px; }
.chat-msg.ai .msg-text { background: rgba(8,22,44,0.95); color: #a8cce8; border: 1px solid rgba(77,184,255,0.18); border-radius: 4px 14px 14px 14px; }

.ai-thinking { display: flex; align-items: center; gap: 10px; }
.thinking-avatar { width: 28px; height: 28px; border-radius: 8px; flex-shrink: 0; background: rgba(77,184,255,0.1); display: flex; align-items: center; justify-content: center; font-size: 10.5px; font-weight: 700; color: #5aabdd; border: 1px solid rgba(77,184,255,0.24); }
.thinking-bubble { display: flex; gap: 5px; align-items: center; background: rgba(8,22,44,0.95); border: 1px solid rgba(77,184,255,0.18); border-radius: 4px 14px 14px 14px; padding: 12px 16px; }
.thinking-bubble span { width: 7px; height: 7px; border-radius: 50%; background: var(--c-blue); animation: bounce 1.3s ease-in-out infinite; }
.thinking-bubble span:nth-child(2) { animation-delay: 0.2s; }
.thinking-bubble span:nth-child(3) { animation-delay: 0.4s; }
@keyframes bounce { 0%, 60%, 100% { transform: translateY(0); opacity: 0.38; } 30% { transform: translateY(-5px); opacity: 1; } }

.quick-questions { display: flex; flex-wrap: wrap; gap: 6px; padding: 10px 16px; border-top: 1px solid rgba(77,184,255,0.1); flex-shrink: 0; }
.quick-questions button { background: rgba(77,184,255,0.07); color: #5aabdd; border: 1px solid rgba(77,184,255,0.2); border-radius: 20px; padding: 5px 12px; font-size: 11.5px; cursor: pointer; font-family: var(--font-ui); transition: all 0.18s ease; white-space: nowrap; }
.quick-questions button:hover { background: rgba(77,184,255,0.18); border-color: rgba(77,184,255,0.45); color: #aaddff; transform: translateY(-1px); }

.ai-input-area { display: flex; gap: 8px; padding: 12px 14px; border-top: 1px solid rgba(77,184,255,0.1); flex-shrink: 0; }
.ai-input-area input { flex: 1; background: rgba(8,20,38,0.85); color: #a8cce8; border: 1px solid rgba(77,184,255,0.25); border-radius: 11px; padding: 10px 14px; font-size: 13px; outline: none; font-family: var(--font-ui); transition: border-color 0.2s, box-shadow 0.2s; }
.ai-input-area input:focus { border-color: var(--c-blue); box-shadow: 0 0 0 3px rgba(77,184,255,0.12); }
.ai-input-area input::placeholder { color: #3a6688; }
.ai-input-area input:disabled { opacity: 0.45; }
.send-btn { width: 42px; height: 42px; flex-shrink: 0; background: linear-gradient(135deg, #1060b8, #0099dd); color: #fff; border: none; border-radius: 11px; display: flex; align-items: center; justify-content: center; cursor: pointer; box-shadow: 0 4px 14px rgba(16,96,184,0.4); transition: all 0.2s ease; }
.send-btn:hover:not(:disabled) { transform: translateY(-1px); box-shadow: 0 6px 18px rgba(16,96,184,0.55); }
.send-btn:disabled { opacity: 0.38; cursor: not-allowed; }

@keyframes blink { 0%, 100% { opacity: 1; } 50% { opacity: 0.45; } }
</style>
