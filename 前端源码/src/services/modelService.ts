/**
 * @author 朱玉阳
 * @version 1.0
 * @date 2026/3/31
 * */
import apiService from './apiService';

// Dify 接口返回类型（和后端保持一致）
interface AIChatResponse {
  code: number;
  msg: string;
  data: string;
}

class ModelService {
  // ==================== 1. 获取 3D 模型文件 ====================
  async getModelFile(
      productId: string,
      format: string = 'glb',
      onProgress?: (percent: number) => void
  ): Promise<Blob> {
    return apiService.downloadModel(productId, format, onProgress);
  }

  // ==================== 2. 下载模型到本地 ====================
  async downloadModel(productId: string, format: string = 'glb'): Promise<void> {
    const blob = await this.getModelFile(productId, format);
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${productId}.${format}`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  }

  // ==================== 3. 上传 3D 模型（直连 MinIO）====================
  async uploadModel(
      file: File,
      productId: string,
      format: string = 'glb',
      onProgress?: (percent: number) => void
  ): Promise<void> {
    return apiService.uploadModel(file, productId, format, onProgress);
  }

  // ==================== 3. AI 问答（对接 Dify）====================
  // ✅✅✅ 这里已 100% 匹配你的后端接口 ✅✅✅
  async queryModelByDify(productId: string, question: string): Promise<string> {
    if (!question?.trim()) {
      throw new Error('查询问题不能为空');
    }

    try {
      // 拼接问题（和你原来逻辑一样）
      const fullQuestion = productId
          ? `产品ID${productId}的模型：${question}`
          : question;

      // ==============================================
      // 🔥🔥🔥 两个关键修复：路径 + 参数 完全对应后端
      // 后端接口：@PostMapping("/chat-messages")
      // 类上：@RequestMapping("/v1")
      // 接收参数：private String question;
      // ==============================================
      const response = await apiService.post<AIChatResponse>('/api/v1/chat-messages', {
        question: fullQuestion,  // 只传 question，后端能接收
      });

      if (response.code !== 200) {
        throw new Error(response.msg || 'AI 调用失败');
      }

      return response.data || '暂无回复';
    } catch (error) {
      console.error('AI 查询异常：', error);
      throw error;
    }
  }

  // 获取模型说明文档
  async getModelDocByDify(productId: string): Promise<string> {
    return this.queryModelByDify(
        productId,
        '请详细说明这个模型的参数、适用场景、加载方式和注意事项'
    );
  }
}

export default new ModelService();