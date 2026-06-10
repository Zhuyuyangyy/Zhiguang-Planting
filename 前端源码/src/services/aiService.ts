/**
 * @author 朱玉阳
 * @version 1.0
 * @date 2026/04/08
 * */
// aiService.ts - 完整修复版
import apiService from './apiService';

// AI聊天请求参数
interface AIChatRequest {
  question: string;
}

// AI聊天响应
interface AIChatResponse {
  code: number;
  msg: string;
  data: string;
}

class AIService {
  // AI对话（对接Dify）
  async chat(question: string): Promise<string> {
    // 调用apiService的request方法
    const response = await apiService.request<AIChatResponse>({
      url: '/api/v1/chat-messages',
      method: 'POST',
      data: { question }
    });

    // 增加返回值校验
    if (response.code !== 200) {
      throw new Error(response.msg || 'Dify接口返回错误');
    }
    return response.data;
  }

  // 虚拟人专属AI对话
  async virtualChat(question: string): Promise<string> {
    const response = await apiService.request<AIChatResponse>({
      url: '/v1/virtual-chat',
      method: 'POST',
      data: { question }
    });

    if (response.code !== 200) {
      throw new Error(response.msg || '虚拟人对话接口返回错误');
    }
    return response.data;
  }
}

export default new AIService();