<!--@author 刘宏伟-->
<!--@version 1.0-->
<!--@date 2026/4/1-->
<template>
  <div class="parameter-ai-chat">
    <!-- 头部 -->
    <div class="chat-header">
      <div class="header-icon">🤖</div>
      <div class="header-text">
        <h3>参数知识助手</h3>
        <p>智能解答口腔种植机器人参数问题</p>
      </div>
    </div>

    <!-- 快捷问题 -->
    <div class="quick-questions">
      <span class="quick-tag" @click="askQuickQuestion('机器人的尺寸参数是多少？')">尺寸参数</span>
      <span class="quick-tag" @click="askQuickQuestion('运动范围有多大？')">运动范围</span>
      <span class="quick-tag" @click="askQuickQuestion('精度参数是多少？')">精度参数</span>
      <span class="quick-tag" @click="askQuickQuestion('重量是多少？')">重量</span>
    </div>

    <!-- 消息列表 -->
    <div class="message-list" ref="messageList">
      <div class="welcome-msg" v-if="messages.length === 0">
        <div class="welcome-icon">💡</div>
        <p>您好！我是参数知识助手，请问有什么可以帮您？</p>
      </div>

      <div
          v-for="(msg, index) in messages"
          :key="index"
          :class="['message', msg.role]"
      >
        <div class="avatar">
          {{ msg.role === 'user' ? '👤' : '🤖' }}
        </div>
        <div class="content-wrapper">
          <div class="content">{{ msg.content }}</div>
          <div class="time">{{ msg.time }}</div>
        </div>
      </div>

      <div v-if="loading" class="message bot">
        <div class="avatar">🤖</div>
        <div class="content-wrapper">
          <div class="typing-indicator">
            <span></span><span></span><span></span>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="input-area">
      <textarea
          v-model="inputText"
          placeholder="请输入您的问题，例如：机器人的重复定位精度是多少？"
          rows="2"
          @keydown.enter.ctrl="sendMessage"
          @keydown.enter.meta="sendMessage"
      />
      <div class="input-footer">
        <span class="hint">Ctrl + Enter 发送</span>
        <button
            @click="sendMessage"
            :disabled="loading || !inputText.trim()"
            class="send-btn"
        >
          {{ loading ? '查询中...' : '发送' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue';
import aiService from '../services/AIService'; // 确保路径正确

interface Message {
  role: 'user' | 'bot';
  content: string;
  time: string;
}

const messages = ref<Message[]>([]);
const inputText = ref('');
const loading = ref(false);
const messageList = ref<HTMLElement | null>(null);

const getCurrentTime = () => {
  const now = new Date();
  return `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;
};

const scrollToBottom = () => {
  nextTick(() => {
    if (messageList.value) {
      messageList.value.scrollTop = messageList.value.scrollHeight;
    }
  });
};

const askQuickQuestion = (question: string) => {
  inputText.value = question;
  sendMessage();
};

const sendMessage = async () => {
  const question = inputText.value.trim();
  if (!question || loading.value) return;

  messages.value.push({
    role: 'user',
    content: question,
    time: getCurrentTime()
  });

  const userQuestion = question;
  inputText.value = '';
  loading.value = true;
  scrollToBottom();

  try {
    const answer = await aiService.chat(userQuestion);
    messages.value.push({
      role: 'bot',
      content: answer,
      time: getCurrentTime()
    });
  } catch (err) {
    console.error('参数查询失败：', err);
    messages.value.push({
      role: 'bot',
      content: '抱歉，参数查询失败，请稍后重试。',
      time: getCurrentTime()
    });
  } finally {
    loading.value = false;
    scrollToBottom();
  }
};
</script>

<style scoped>
.parameter-ai-chat {
  width: 100%;
  height: 100%;
  background: #ffffff;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.chat-header {
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  gap: 14px;
}

.header-icon {
  font-size: 32px;
}

.header-text h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.header-text p {
  margin: 4px 0 0 0;
  font-size: 13px;
  opacity: 0.9;
}

.quick-questions {
  padding: 12px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.quick-tag {
  padding: 6px 14px;
  background: white;
  border: 1px solid #d9d9d9;
  border-radius: 16px;
  font-size: 13px;
  color: #667eea;
  cursor: pointer;
  transition: all 0.2s;
}

.quick-tag:hover {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.message-list {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: #fafafa;
}

.welcome-msg {
  text-align: center;
  padding: 40px 20px;
  color: #666;
}

.welcome-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.message {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.message.user {
  flex-direction: row-reverse;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.message.user .avatar {
  background: #667eea;
}

.content-wrapper {
  max-width: 70%;
}

.message.user .content-wrapper {
  align-items: flex-end;
}

.content {
  padding: 12px 16px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  word-break: break-word;
}

.message.user .content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.time {
  font-size: 11px;
  color: #999;
  margin-top: 4px;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: #667eea;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) { animation-delay: 0.2s; }
.typing-indicator span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-8px); }
}

.input-area {
  padding: 16px 20px;
  background: white;
  border-top: 1px solid #e8e8e8;
}

.input-area textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  font-size: 14px;
  resize: none;
  box-sizing: border-box;
}

.input-area textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.hint {
  font-size: 12px;
  color: #999;
}

.send-btn {
  padding: 8px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
}

.send-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>