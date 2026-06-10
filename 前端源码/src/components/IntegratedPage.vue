<!--@author 刘宏伟-->
<!--@version 1.0-->
<!--@date 2026/3/28-->
<template>
  <div class="integrated-page">
    <div class="model-section">
      <ModelViewer />
    </div>

    <div class="chat-section" :class="{ collapsed: isChatCollapsed }">
      <div class="chat-header-bar">
        <span>参数知识助手</span>
        <button @click="isChatCollapsed = !isChatCollapsed">
          {{ isChatCollapsed ? '展开' : '收起' }}
        </button>
      </div>
      <ParameterAIChat v-if="!isChatCollapsed" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import ModelViewer from './ModelViewer.vue'; // 确保路径正确
import ParameterAIChat from './ParameterAIChat.vue'; // 确保路径正确

const isChatCollapsed = ref(false);
</script>

<style scoped>
.integrated-page {
  display: flex;
  height: 100vh;
  background: #f0f2f5;
  overflow: hidden;
}

.model-section {
  flex: 1;
  min-width: 0;
  overflow: hidden;
}

.chat-section {
  width: 420px;
  background: white;
  border-left: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
}

.chat-section.collapsed {
  width: 60px;
}

.chat-header-bar {
  padding: 16px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
}

.chat-header-bar h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.chat-section.collapsed .chat-header-bar h3 {
  display: none;
}

.toggle-btn {
  padding: 4px 12px;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.chat-content {
  flex: 1;
  overflow: hidden;
}

.chat-section.collapsed .chat-content {
  display: none;
}

@media (max-width: 1200px) {
  .integrated-page {
    flex-direction: column;
  }

  .chat-section {
    width: 100%;
    height: 50vh;
    border-left: none;
    border-top: 1px solid #e8e8e8;
  }

  .chat-section.collapsed {
    width: 100%;
    height: 60px;
  }
}
</style>