<!--@author 刘宏伟-->
<!--@version 1.0-->
<!--@date 2026/3/28-->
<template>
  <div class="user-login">
    <!-- 标签切换 -->
    <div class="tabs">
      <button 
        @click="activeTab = 'login'" 
        :class="['tab-btn', { active: activeTab === 'login' }]"
      >
        登录
      </button>
      <button 
        @click="activeTab = 'register'" 
        :class="['tab-btn', { active: activeTab === 'register' }]"
      >
        注册
      </button>
    </div>
    
    <!-- 状态提示 -->
    <div v-if="message" :class="['message', messageType]">
      {{ message }}
      <button @click="message = ''" class="message-close">×</button>
    </div>
    
    <!-- 登录表单 -->
    <form v-if="activeTab === 'login'" @submit.prevent="handleLogin" class="login-form">
      <div class="form-group">
        <label for="login-username">用户名</label>
        <input 
          type="text" 
          id="login-username" 
          v-model="loginForm.username" 
          placeholder="请输入用户名"
          :class="{ 'error': loginErrors.username }"
          @blur="validateLoginField('username')"
        />
        <div v-if="loginErrors.username" class="error-message">{{ loginErrors.username }}</div>
      </div>
      
      <div class="form-group">
        <label for="login-password">密码</label>
        <div class="password-input">
          <input 
            :type="showPassword ? 'text' : 'password'" 
            id="login-password" 
            v-model="loginForm.password" 
            placeholder="请输入密码"
            :class="{ 'error': loginErrors.password }"
            @blur="validateLoginField('password')"
          />
          <button type="button" @click="showPassword = !showPassword" class="password-toggle">
            {{ showPassword ? '👁️' : '👁️‍🗨️' }}
          </button>
        </div>
        <div v-if="loginErrors.password" class="error-message">{{ loginErrors.password }}</div>
      </div>
      
      <div class="form-actions">
        <button type="submit" class="login-btn" :disabled="loading || !isLoginFormValid">
          <span v-if="loading" class="loading-spinner"></span>
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </div>
    </form>
    
    <!-- 注册表单 -->
    <form v-else-if="activeTab === 'register'" @submit.prevent="handleRegister" class="login-form">
      <div class="form-group">
        <label for="register-username">用户名</label>
        <input 
          type="text" 
          id="register-username" 
          v-model="registerForm.username" 
          placeholder="请设置用户名"
          :class="{ 'error': registerErrors.username }"
          @blur="validateRegisterField('username')"
        />
        <div v-if="registerErrors.username" class="error-message">{{ registerErrors.username }}</div>
      </div>
      
      <div class="form-group">
        <label for="register-password">密码</label>
        <div class="password-input">
          <input 
            :type="showRegisterPassword ? 'text' : 'password'" 
            id="register-password" 
            v-model="registerForm.password" 
            placeholder="请设置密码（至少6位）"
            :class="{ 'error': registerErrors.password }"
            @blur="validateRegisterField('password')"
          />
          <button type="button" @click="showRegisterPassword = !showRegisterPassword" class="password-toggle">
            {{ showRegisterPassword ? '👁️' : '👁️‍🗨️' }}
          </button>
        </div>
        <div v-if="registerErrors.password" class="error-message">{{ registerErrors.password }}</div>
      </div>
      
      <div class="form-group">
        <label for="register-confirm-password">确认密码</label>
        <div class="password-input">
          <input 
            :type="showRegisterPassword ? 'text' : 'password'" 
            id="register-confirm-password" 
            v-model="registerForm.confirmPassword" 
            placeholder="请确认密码"
            :class="{ 'error': registerErrors.confirmPassword }"
            @blur="validateRegisterField('confirmPassword')"
          />
          <button type="button" @click="showRegisterPassword = !showRegisterPassword" class="password-toggle">
            {{ showRegisterPassword ? '👁️' : '👁️‍🗨️' }}
          </button>
        </div>
        <div v-if="registerErrors.confirmPassword" class="error-message">{{ registerErrors.confirmPassword }}</div>
      </div>
      
      <div class="form-actions">
        <button type="submit" class="register-btn" :disabled="loading || !isRegisterFormValid">
          <span v-if="loading" class="loading-spinner"></span>
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </div>
    </form>
    
    <!-- 登录结果 -->
    <div v-if="loginSuccess" class="success-card">
      <h3>登录成功</h3>
      <div class="success-info">
        <p><strong>用户名:</strong> {{ loginUser.username }}</p>
        <p><strong>用户ID:</strong> {{ loginUser.id }}</p>
        <p><strong>角色:</strong> {{ loginUser.role === 1 ? '管理员' : '普通用户' }}</p>
      </div>
      <button @click="handleLogout" class="logout-btn">退出登录</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { userService } from '../services';

// 标签状态
const activeTab = ref('login');

// 登录表单
const loginForm = ref({
  username: 'admin',
  password: '123456'
});

// 注册表单
const registerForm = ref({
  username: '',
  password: '',
  confirmPassword: ''
});

// 状态管理
const loading = ref(false);
const showPassword = ref(false);
const showRegisterPassword = ref(false);
const message = ref('');
const messageType = ref('info'); // success, error, info
const loginSuccess = ref(false);
const loginUser = ref({
  id: 0,
  username: '',
  role: 0
});

// 登录表单验证错误
const loginErrors = ref({
  username: '',
  password: ''
});

// 注册表单验证错误
const registerErrors = ref({
  username: '',
  password: '',
  confirmPassword: ''
});

// 计算属性：登录表单是否有效
const isLoginFormValid = computed(() => {
  return !loginErrors.value.username && !loginErrors.value.password && 
         loginForm.value.username.trim() && loginForm.value.password.trim();
});

// 计算属性：注册表单是否有效
const isRegisterFormValid = computed(() => {
  return !registerErrors.value.username && !registerErrors.value.password && 
         !registerErrors.value.confirmPassword && 
         registerForm.value.username.trim() && 
         registerForm.value.password.trim() && 
         registerForm.value.confirmPassword.trim();
});

// 显示消息
const showMessage = (text: string, type: 'success' | 'error' | 'info' = 'info') => {
  message.value = text;
  messageType.value = type;
  setTimeout(() => {
    message.value = '';
  }, 3000);
};
//AI辅助生成：豆包,2026-03-25 表单验证规则
// 登录表单验证
const validateLoginField = (field: string) => {
  if (field === 'username') {
    if (!loginForm.value.username.trim()) {
      loginErrors.value.username = '用户名不能为空';
    } else {
      loginErrors.value.username = '';
    }
  } else if (field === 'password') {
    if (!loginForm.value.password.trim()) {
      loginErrors.value.password = '密码不能为空';
    } else if (loginForm.value.password.length < 6) {
      loginErrors.value.password = '密码长度不能少于6位';
    } else {
      loginErrors.value.password = '';
    }
  }
};

// 注册表单验证
const validateRegisterField = (field: string) => {
  if (field === 'username') {
    if (!registerForm.value.username.trim()) {
      registerErrors.value.username = '用户名不能为空';
    } else {
      registerErrors.value.username = '';
    }
  } else if (field === 'password') {
    if (!registerForm.value.password.trim()) {
      registerErrors.value.password = '密码不能为空';
    } else if (registerForm.value.password.length < 6) {
      registerErrors.value.password = '密码长度不能少于6位';
    } else {
      registerErrors.value.password = '';
    }
  } else if (field === 'confirmPassword') {
    if (!registerForm.value.confirmPassword.trim()) {
      registerErrors.value.confirmPassword = '请确认密码';
    } else if (registerForm.value.confirmPassword !== registerForm.value.password) {
      registerErrors.value.confirmPassword = '两次输入的密码不一致';
    } else {
      registerErrors.value.confirmPassword = '';
    }
  }
};

// 登录处理
//AI辅助生成：豆包,2026-03-25 登录注册表单组件
const handleLogin = async () => {
  // 验证表单
  validateLoginField('username');
  validateLoginField('password');
  
  if (!isLoginFormValid.value) {
    showMessage('请检查表单填写是否正确', 'error');
    return;
  }
  
  try {
    loading.value = true;
    const result = await userService.login(loginForm.value);
    userService.saveToken(result.token);
    
    // 保存登录用户信息
    loginUser.value = {
      id: result.id,
      username: result.username,
      role: result.role
    };
    
    loginSuccess.value = true;
    showMessage('登录成功', 'success');
    console.log('登录成功:', result);
  } catch (error: any) {
    const errorMessage = error.message || '登录失败，请检查用户名和密码';
    showMessage(errorMessage, 'error');
    console.error('登录失败:', error);
  } finally {
    loading.value = false;
  }
};

// 注册处理
const handleRegister = async () => {
  // 验证表单
  validateRegisterField('username');
  validateRegisterField('password');
  validateRegisterField('confirmPassword');
  
  if (!isRegisterFormValid.value) {
    showMessage('请检查表单填写是否正确', 'error');
    return;
  }
  
  try {
    loading.value = true;
    await userService.register({
      username: registerForm.value.username,
      password: registerForm.value.password
    });
    
    showMessage('注册成功，请登录', 'success');
    console.log('注册成功:', registerForm.value.username);
    
    // 切换到登录标签
    activeTab.value = 'login';
    // 清空注册表单
    registerForm.value = {
      username: '',
      password: '',
      confirmPassword: ''
    };
    // 清空注册错误
    registerErrors.value = {
      username: '',
      password: '',
      confirmPassword: ''
    };
  } catch (error: any) {
    const errorMessage = error.message || '注册失败，请稍后重试';
    showMessage(errorMessage, 'error');
    console.error('注册失败:', error);
  } finally {
    loading.value = false;
  }
};

// 退出登录
const handleLogout = () => {
  userService.clearToken();
  loginSuccess.value = false;
  loginUser.value = { id: 0, username: '', role: 0 };
  showMessage('已退出登录', 'info');
};
</script>

<style scoped>
.user-login {
  max-width: 400px;
  margin: 100px auto 50px;
  padding: 30px;
  background: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 标签切换 */
.tabs {
  display: flex;
  margin-bottom: 24px;
  border-bottom: 1px solid #e8e8e8;
}

.tab-btn {
  flex: 1;
  padding: 10px 16px;
  background: none;
  border: none;
  font-size: 14px;
  font-weight: 500;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
  border-bottom: 2px solid transparent;
}

.tab-btn:hover {
  color: #1890ff;
}

.tab-btn.active {
  color: #1890ff;
  border-bottom-color: #1890ff;
}

/* 消息提示 */
.message {
  padding: 12px 16px;
  border-radius: 4px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.message.success {
  background: #f6ffed;
  border: 1px solid #b7eb8f;
  color: #52c41a;
}

.message.error {
  background: #fff2f0;
  border: 1px solid #ffccc7;
  color: #ff4d4f;
}

.message.info {
  background: #e6f7ff;
  border: 1px solid #91d5ff;
  color: #1890ff;
}

.message-close {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  color: inherit;
  padding: 0;
  margin-left: 10px;
}

/* 表单样式 */
.login-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

label {
  font-size: 14px;
  font-weight: 500;
  color: #666;
}

.password-input {
  position: relative;
}

input {
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.2s;
  width: 100%;
  box-sizing: border-box;
}

input:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

input.error {
  border-color: #ff4d4f;
}

.password-toggle {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  padding: 0;
}

.error-message {
  font-size: 12px;
  color: #ff4d4f;
  margin-top: 2px;
}

.form-actions {
  margin-top: 8px;
}

.login-btn {
  padding: 10px 16px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.login-btn:hover:not(:disabled) {
  background: #40a9ff;
}

.register-btn {
  padding: 10px 16px;
  background: #52c41a;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.register-btn:hover:not(:disabled) {
  background: #73d13d;
}

.login-btn:disabled,
.register-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 加载动画 */
.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 成功卡片 */
.success-card {
  margin-top: 24px;
  padding: 20px;
  background: #f6ffed;
  border: 1px solid #b7eb8f;
  border-radius: 4px;
}

.success-card h3 {
  margin: 0 0 16px 0;
  color: #52c41a;
  font-size: 16px;
}

.success-info {
  margin-bottom: 16px;
}

.success-info p {
  margin: 8px 0;
  font-size: 14px;
  color: #333;
}

.logout-btn {
  padding: 8px 16px;
  background: #f0f0f0;
  color: #666;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.logout-btn:hover {
  background: #e0e0e0;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .user-login {
    padding: 20px;
    margin: 0 10px;
  }
}
</style>
