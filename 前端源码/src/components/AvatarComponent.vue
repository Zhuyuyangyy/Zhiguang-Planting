<!--
 * @author  刘宏伟
 * @version 1.0
 * @date    2026/04/08
 * @description 虚拟人
-->
<template>
  <div class="avatar-layout">
    <!-- 左侧：虚拟人显示区域 -->
    <div class="avatar-display">
      <!-- 字幕容器 -->
      <div class="subtitle-container">
        <div
            v-if="danmakus.length > 0"
            class="subtitle-item"
        >
          {{ danmakus[0].text }}
        </div>
      </div>

      <div class="avatar-wrapper" ref="avatarWrapper">
        <div v-if="loading" class="loading">
          <div class="loading-spinner"></div>
          <p>正在加载虚拟人...</p>
        </div>
      </div>
    </div>

    <div class="panel-container" :class="{ collapsed: isPanelCollapsed }">
      <!-- 折叠按钮（极简不丑） -->
      <button class="toggle-btn" @click="togglePanel">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M15 18l-6-6 6-6" />
        </svg>
      </button>
      <!-- 右侧：控制面板 -->
      <div class="control-panel">
        <!-- 面板头部 -->
        <div class="panel-header">
          <h3>控制面板</h3>
          <button
              @click="toggleAvatar"
              :disabled="loading"
              :class="['power-btn', { 'active': isAvatarRunning }]"
          >
            <span class="power-icon">⏻</span>
          </button>
        </div>

        <!-- 面板内容 -->
        <div class="panel-body">
          <!-- 文本输入 -->
          <div class="control-section">
            <label>文本输入</label>
            <textarea
                v-model="text"
                placeholder="输入文本让虚拟人说话..."
                rows="2"
            ></textarea>
            <div class="btn-row">
              <button @click="sendText" :disabled="loading" class="btn btn-primary">
                发送
              </button>
              <button @click="interrupt" :disabled="loading" class="btn btn-secondary">
                打断
              </button>
            </div>
          </div>

          <!-- 语音控制 -->
          <div class="control-section">
            <label>语音交互</label>
            <button
                @click="toggleVoiceInteraction"
                :disabled="loading"
                :class="['btn btn-voice', { 'recording': isVoiceInteractionActive }]"
            >
              <span class="btn-icon">{{ isVoiceInteractionActive ? '🔴' : '🎤' }}</span>
              {{ isVoiceInteractionActive ? '停止对话' : '开始对话' }}
            </button>
          </div>

          <!-- 动作控制 -->
          <div class="control-section">
            <label>动作控制</label>
            <div class="action-grid">
              <button @click="sendAction('A_Hello_l_F')" :disabled="loading" class="btn btn-action">
                挥手
              </button>
              <button @click="sendAction('A_W_walk_left_O')" :disabled="loading" class="btn btn-action">
                左走
              </button>
              <button @click="sendAction('A_W_walk_right_O')" :disabled="loading" class="btn btn-action">
                右走
              </button>
              <button @click="sendAction('A_RH_hello_O')" :disabled="loading" class="btn btn-action">
                问候
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import avatarService from '../services/avatarService';

interface Danmaku {
  text: string;
  top: number;
  duration: number;
}

const avatarWrapper = ref<HTMLElement | null>(null);
const text = ref('你好，我是虚拟助手');
const loading = ref(false);
const isVoiceInteractionActive = ref(false);
const isAvatarRunning = ref(false);
const danmakus = ref<Danmaku[]>([]);

// 只加这两句
const isPanelCollapsed = ref(false)
const togglePanel = () => { isPanelCollapsed.value = !isPanelCollapsed.value }
//AI辅助生成：豆包,2026-04-03 虚拟人组件
// 启动虚拟人
const startAvatar = async () => {
  if (!avatarWrapper.value) return;

  try {
    loading.value = true;
    await avatarService.startAvatar(avatarWrapper.value);
    avatarService.bindEvents();
    isAvatarRunning.value = true;
    console.log('虚拟人启动成功');
  } catch (error) {
    console.error('虚拟人启动失败:', error);
  } finally {
    loading.value = false;
  }
};
//AI辅助生成：豆包,2026-04-03 虚拟人字幕显示
let danmakuTimeout: ReturnType<typeof setTimeout> | null = null;

const addDanmaku = (text: string) => {
  // 计算显示时间（假设平均语速为每秒 3 个汉字）
  const charCount = text.length;
  const duration = Math.max(5, Math.min(12, charCount / 2));

  // 清除之前的定时器
  if (danmakuTimeout) {
    clearTimeout(danmakuTimeout);
  }

  // 只保留一个字幕，新的替换旧的
  danmakus.value = [{ text, top: 0, duration }];

  // 一段时间后移除字幕
  danmakuTimeout = setTimeout(() => {
    danmakus.value = [];
  }, duration * 1000);
};

// 停止虚拟人
const stopAvatar = async () => {
  try {
    loading.value = true;
    avatarService.stopAvatar();
    isAvatarRunning.value = false;
    console.log('已停止虚拟人运行');
  } catch (error) {
    console.error('停止运行失败:', error);
  } finally {
    loading.value = false;
  }
};

// 切换虚拟人运行状态
const toggleAvatar = async () => {
  if (isAvatarRunning.value) {
    await stopAvatar();
  } else {
    await startAvatar();
  }
};

const sendText = async () => {
  if (!text.value.trim()) return;

  try {
    loading.value = true;
    // 👇 关键：同时调用 sendText（语义理解）+ speak（发声说话）
    await avatarService.sendText(text.value, { nlp: true });
    // 👇 这行就是让 speak 从灰色变亮、让虚拟人说话的核心代码
    const answer = await avatarService.speak(text.value);
    // 只添加AI回答的字幕
    if (answer) {
      addDanmaku(answer);
    }

    console.log('文本发送成功，虚拟人开始说话');
  } catch (error) {
    console.error('发送失败:', error);
  } finally {
    loading.value = false;
  }
};

const toggleVoiceInteraction = async () => {
  try {
    loading.value = true;
    if (isVoiceInteractionActive.value) {
      avatarService.stopVoiceInteraction();
      isVoiceInteractionActive.value = false;
    } else {
      await avatarService.startVoiceInteraction();
      isVoiceInteractionActive.value = true;
    }
  } catch (error) {
    console.error('语音交互操作失败:', error);
  } finally {
    loading.value = false;
  }
};

const sendAction = async (actionId: string) => {
  try {
    loading.value = true;
    await avatarService.sendAction(actionId);
    console.log('动作发送成功:', actionId);
  } catch (error) {
    console.error('动作发送失败:', error);
  } finally {
    loading.value = false;
  }
};

const interrupt = async () => {
  try {
    loading.value = true;
    await avatarService.interrupt();
    console.log('已打断虚拟人');
  } catch (error) {
    console.error('打断操作失败:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(async () => {
  await startAvatar();
  // 设置弹幕回调
  avatarService.setDanmakuCallback(addDanmaku);
});

onUnmounted(() => {
  avatarService.removeDanmakuCallback();
  avatarService.destroy();
});
</script>

<style scoped>
/* 扁平化设计风格 */
.avatar-layout {
  display: flex;
  gap: 20px;
  padding: 0;
  margin: 0;

  /* 👇 全屏核心样式 */
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  min-height: 100vh;
  z-index: 0;

  /* 👇 你的全屏背景图（和之前 dashboard 用同一张） */
  background: url('/dashboard-robot/beijingavatar.png') center / cover no-repeat;

  /* 如果你想要深色遮罩（更高级），用下面这行，替换上面那行即可 */
  /* background: linear-gradient(rgba(0,0,0,0.5), rgba(0,0,0,0.7)), url('/dashboard-robot/beijing.png') center / cover no-repeat; */
}

/* 左侧虚拟人显示区 */
.avatar-display {
  flex: 1;
  background: transparent;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

/* 字幕容器 */
.subtitle-container {
  position: absolute;
  bottom: 10%;
  left: 0;
  width: 100%;
  display: flex;
  justify-content: center;
  pointer-events: none;
  z-index: 10;
}

/* 字幕项 */
.subtitle-item {
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 12px 30px;
  border-radius: 25px;
  font-size: 18px;
  font-weight: 500;
  text-align: center;
  max-width: 80%;
  word-wrap: break-word;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
}

.avatar-wrapper {
  width: 65%;
  height: 65%;
  max-width: 1100px;
  max-height: 750px;
  margin: 0 auto;
  position: relative;
  background: transparent;

  /* 👇 加这一行就能往下移动！*/
  top: 10%;
}

/* 右侧控制面板 */
.control-panel {
  width: 320px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 8px;
  box-shadow: 0 2px 20px rgba(0, 150, 255, 0.3);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(0, 150, 255, 0.2);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 面板头部 */
.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #fafafa;
  border-bottom: 1px solid #e8e8e8;
}

.panel-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.power-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background: #e0e0e0;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.power-btn:hover {
  background: #d0d0d0;
}

.power-btn.active {
  background: #52c41a;
  color: white;
}

.power-icon {
  font-size: 18px;
}

/* 面板内容 */
.panel-body {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

/* 控制区块 */
.control-section {
  margin-bottom: 24px;
}

.control-section:last-child {
  margin-bottom: 0;
}

.control-section label {
  display: block;
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
  font-weight: 500;
}

.control-section textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  resize: none;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.control-section textarea:focus {
  outline: none;
  border-color: #40a9ff;
}

/* 按钮组 */
.btn-row {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}

/* 扁平按钮 */
.btn {
  padding: 8px 16px;
  border-radius: 4px;
  border: none;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-primary {
  background: #1890ff;
  color: white;
  flex: 1;
}

.btn-primary:hover:not(:disabled) {
  background: #40a9ff;
}

.btn-secondary {
  background: #f0f0f0;
  color: #666;
}

.btn-secondary:hover:not(:disabled) {
  background: #e0e0e0;
}

.btn-voice {
  width: 100%;
  background: #f0f0f0;
  color: #666;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.btn-voice:hover:not(:disabled) {
  background: #e0e0e0;
}

.btn-voice.recording {
  background: #ff4d4f;
  color: white;
}

.btn-icon {
  font-size: 14px;
}

/* 动作按钮网格 */
.action-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
}

.btn-action {
  background: #f5f5f5;
  color: #666;
  padding: 10px;
  font-size: 13px;
}

.btn-action:hover:not(:disabled) {
  background: #e8e8e8;
}

/* 加载状态 */
.loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f0f0f0;
  border-top-color: #1890ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 12px;
}

.loading p {
  margin: 0;
  color: #999;
  font-size: 14px;
}
/* 折叠面板容器（新增） */
.panel-container {
  position: fixed;
  top: 0;
  right: 0;
  height: 100vh;
  display: flex;
  align-items: center;
  transition: transform 0.32s ease;
  z-index: 99;
}
.panel-container.collapsed {
  transform: translateX(calc(100% - 44px));
}

/* 折叠按钮（极简科技风） */
.toggle-btn {
  width: 44px;
  height: 44px;
  background: rgba(0,160,255,0.15);
  border: 1px solid rgba(0,160,255,0.3);
  border-radius: 10px 0 0 10px;
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #00aaff;
  transition: 0.2s;
}
.toggle-btn:hover {
  background: rgba(0,160,255,0.25);
  border-color: rgba(0,160,255,0.5);
}

/* 你原来的 .control-panel 只改 3 个属性（高度+定位+圆角） */
.control-panel {
  width: 320px;
  height: 100vh; /* 改这个 */
  background: rgba(255, 255, 255, 0.95);
  border-radius: 0; /* 改这个 */
  box-shadow: 0 2px 20px rgba(0, 150, 255, 0.3);
  backdrop-filter: blur(10px);
  border-left: 1px solid rgba(0, 150, 255, 0.2);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 响应式 */
@media (max-width: 900px) {
  .avatar-layout {
    flex-direction: column;
    height: auto;
  }

  .avatar-display {
    height: 400px;
  }

  .control-panel {
    width: 100%;
    max-height: none;
  }
}

@media (max-width: 480px) {
  .avatar-display {
    height: 300px;
  }

  .control-panel {
    border-radius: 0;
  }
}
</style>