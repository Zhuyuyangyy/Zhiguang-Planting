// avatarService.ts
/**
 * @author 刘宏伟
 * @version 1.0
 * @date 2026/4/8
 * */
import AvatarPlatform from '../testSdk/3.1.2.1002/avatar-sdk-web_3.1.2.1002/index.js';
import apiService from './apiService';
//AI辅助生成：豆包,2026-04-02 虚拟人SDK集成
// 1. 定义类型接口（规范配置结构）
interface SdkConfig {
  serverUrl: string;
  appId: string;
  apiKey: string;
  apiSecret: string;
  sceneId: string;
}

interface AvatarConfig {
  avatarId: string;
  stream: {
    width: number;
    height: number;
  };
  maskRegion: number[];
  scale: number;
  moveH: number;
  moveV: number;
}

interface VoiceConfig {
  voiceName: string;
  speed: number;
  pitch: number;
  volume: number;
}

interface FullAvatarConfig {
  sdkConfig: SdkConfig;
  avatarConfig: AvatarConfig;
  voiceConfig: VoiceConfig;
}

interface SpeakResponse {
  code: number;
  msg?: string;
  data?: {
    answer: string;
  };
  answer?: string; // 兼容后端直接返回answer的情况
}

// 弹幕数据结构
interface Danmaku {
  text: string;
  top: number;
  duration: number;
}

// 2. 模拟配置（开发环境兜底）- 使用新的讯飞开放平台凭证
const MOCK_FULL_CONFIG: FullAvatarConfig = {
  sdkConfig: {
    appId: 'bb0c70fd',
    apiKey: '9386b8d95d4ef358463fed7cf05590db',
    apiSecret: 'ZTdjNDk2NzU0MjY1MTMyYmNhZDNlMGJl',
    sceneId: '314319524864331776',
    serverUrl: 'wss://avatar.cn-huadong-1.xf-yun.com/v1/interact',
  },
  avatarConfig: {
    avatarId: '110117005',
    stream: { width: 720, height: 1280 },
    maskRegion: [0, 0, 1080, 1920],
    scale: 1,
    moveH: 0,
    moveV: 0
  },
  voiceConfig: {
    voiceName: 'x4_yezi',
    speed: 50,
    pitch: 50,
    volume: 100
  }
};

class AvatarService {
  private interativeRef: any = null;
  private isInitialized = false;
  private useMock = import.meta.env.MODE === 'development'; // 开发环境默认用mock
  private recorder: any = null;
  private player: any = null; // 缓存播放器实例
  private danmakus: Danmaku[] = []; // 字幕列表
  private danmakuCallback: ((text: string) => void) | null = null; // 字幕回调
  private danmakuTimeout: ReturnType<typeof setTimeout> | null = null; // 字幕定时器

  // ==================== 核心初始化方法 ====================
  /**
   * 初始化SDK实例
   */
  async initialize(): Promise<void> {
    if (this.isInitialized && this.interativeRef) return;

    try {
      this.interativeRef = new (AvatarPlatform as any)({
        useInlinePlayer: true,
      });
      this.isInitialized = true;
      this.bindEvents(); // 初始化后自动绑定事件
      console.log('✅ SDK 初始化成功');
    } catch (error) {
      this.isInitialized = false;
      console.error('❌ SDK 初始化失败:', error);
      throw new Error(`SDK初始化失败: ${(error as Error).message}`);
    }
  }

  // ==================== 配置管理 ====================
  /**
   * 从后端获取虚拟人配置（带兜底逻辑）
   */
  async getAvatarConfig(): Promise<FullAvatarConfig> {
    // 强制mock模式直接返回本地配置
    if (this.useMock) {
      console.log('📌 使用本地模拟配置');
      return Promise.resolve(MOCK_FULL_CONFIG);
    }

    try {
      // 调用后端配置接口
      const response = await apiService.get<FullAvatarConfig>('/api/avatar/config');

      // 后端返回格式校验
      if (response.code !== 200 || !response.data) {
        throw new Error(response.msg || '后端配置接口返回异常');
      }

      console.log('✅ 从后端获取配置成功');
      return response.data;
    } catch (error) {
      console.warn(`⚠️ 获取后端配置失败: ${(error as Error).message}，使用本地兜底配置`);
      return MOCK_FULL_CONFIG;
    }
  }

  // ==================== 虚拟人启动/停止 ====================
  /**
   * 启动虚拟人（核心方法）
   * @param wrapper 渲染容器DOM元素
   */
  async startAvatar(wrapper: HTMLElement): Promise<void> {
    if (!wrapper) {
      throw new Error('渲染容器不能为空');
    }

    // 1. 确保SDK已初始化
    await this.initialize();

    // 2. 获取配置（优先后端，兜底本地）
    const fullConfig = await this.getAvatarConfig();
    const { sdkConfig, avatarConfig, voiceConfig } = fullConfig;

    // 3. 设置SDK鉴权信息
    this.interativeRef.setApiInfo({
      serverUrl: sdkConfig.serverUrl,
      appId: sdkConfig.appId,
      apiKey: sdkConfig.apiKey,
      apiSecret: sdkConfig.apiSecret,
      sceneId: sdkConfig.sceneId,
    });

    // 4. 组装全局参数（适配讯飞SDK）
    const globalParams = {
      avatar_dispatch: { interactive_mode: 1, content_analysis: 0 },
      stream: { protocol: 'xrtc', alpha: 1, bitrate: 1000000, fps: 25 },
      avatar: {
        avatar_id: avatarConfig.avatarId,
        width: avatarConfig.stream.width,
        height: avatarConfig.stream.height,
        mask_region: `[${avatarConfig.maskRegion.join(',')}]`,
        scale: avatarConfig.scale,
        move_h: avatarConfig.moveH,
        move_v: avatarConfig.moveV,
        audio_format: 1
      },
      tts: {
        vcn: voiceConfig.voiceName,
        speed: voiceConfig.speed,
        pitch: voiceConfig.pitch,
        volume: voiceConfig.volume
      },
      air: {
        air: 1,
        add_nonsemantic: 1
      },
      tdp: {
        botId: sdkConfig.appId,      // 使用你已有的 appId 即可
        url: sdkConfig.serverUrl     // 使用你已有的服务地址
      }
    };

    this.interativeRef.setGlobalParams(globalParams);

    // 5. 启动虚拟人渲染
    return new Promise((resolve, reject) => {
      this.interativeRef
          .start({ wrapper })
          .then(() => {
            console.log('✅ 虚拟人启动成功');

            // 处理音频自动播放限制
            this.player = this.interativeRef.player || this.interativeRef.createPlayer();
            this.handleAudioAutoPlay();

            resolve();
          })
          .catch((e: any) => {
            console.error('❌ 虚拟人启动失败:', e);
            reject(new Error(`虚拟人启动失败: ${e.message}`));
          });
    });
  }

  /**
   * 停止虚拟人
   */
  stopAvatar(): void {
    if (this.interativeRef) {
      this.interativeRef.stop();
      console.log('🛑 虚拟人已停止');
    }
    this.stopVoiceInteraction(); // 停止语音交互
  }

  /**
   * 销毁SDK实例（组件卸载时调用）
   */
  destroy(): void {
    this.stopAvatar();
    if (this.interativeRef) {
      this.interativeRef.destroy();
      this.interativeRef = null;
      this.isInitialized = false;
      console.log('🗑️ SDK实例已销毁');
    }
    this.recorder = null;
    this.player = null;
  }

  // ==================== 交互功能 ====================
  /**
   * 文本驱动（调用后端Dify接口获取回答后播报）
   * @param text 用户输入的问题
   * @returns 虚拟人说的文字
   */
  async speak(text: string): Promise<string> {
    if (!text?.trim()) {
      console.warn('⚠️ 播报文本不能为空');
      return '';
    }

    try {
      // 1. 调用后端对话接口（获取Dify回答）
      const response = await apiService.post<SpeakResponse>('/api/avatar/speak', {
        question: text.trim()
      });

      // 2. 解析后端返回的回答（兼容两种格式）
      const difyAnswer = response.answer || response.data?.answer;
      if (!difyAnswer) {
        throw new Error('未获取到AI回答内容');
      }

      // 3. 调用SDK播报文本（不等待播报完成，直接返回）
      if (this.interativeRef) {
        console.log('🗣️ 虚拟人即将播报:', difyAnswer);
        this.interativeRef.writeText(difyAnswer);
        // 4. 自动添加弹幕
        this.addDanmaku(difyAnswer);
        return difyAnswer;
      } else {
        throw new Error('SDK未初始化，无法播报');
      }
    } catch (error) {
      console.error('❌ 对话驱动失败:', error);
      throw new Error(`对话驱动失败: ${(error as Error).message}`);
    }
  }

  /**
   * 直接发送文本（跳过后端，仅用于调试）
   * @param text 要播报的文本
   * @param options 额外配置
   */
  async sendText(text: string, options?: any): Promise<string> {
    await this.initialize(); // 确保初始化

    if (!text?.trim()) {
      throw new Error('发送文本不能为空');
    }

    return this.interativeRef.writeText(text, options || {});
  }

  /**
   * 启动语音交互（全双工模式）
   */
  async startVoiceInteraction(): Promise<void> {
    await this.initialize();

    if (!this.recorder) {
      this.recorder = this.interativeRef.recorder || this.interativeRef.createRecorder();
    }

    return new Promise((resolve, reject) => {
      this.recorder
          .startRecord(0, () => {
            console.log('🛑 语音交互自动停止');
          })
          .then(() => {
            console.log('🎙️ 语音交互已启动');
            resolve();
          })
          .catch((err: any) => {
            console.error('❌ 启动语音交互失败:', err);
            reject(new Error(`启动语音交互失败: ${err.message}`));
          });
    });
  }

  /**
   * 停止语音交互
   */
  stopVoiceInteraction(): void {
    if (this.recorder) {
      this.recorder.stopRecord();
      console.log('🛑 语音交互已手动停止');
    }
  }

  /**
   * 发送动作指令
   * @param actionId 动作ID
   */
  async sendAction(actionId: string): Promise<string> {
    await this.initialize();

    if (!actionId?.trim()) {
      throw new Error('动作ID不能为空');
    }

    return this.interativeRef.writeCmd('action', actionId.trim());
  }

  /**
   * 打断虚拟人当前播报
   */
  async interrupt(): Promise<void> {
    await this.initialize();

    try {
      await this.interativeRef.interrupt();
      console.log('🔇 已打断虚拟人播报');
    } catch (error) {
      console.error('❌ 打断虚拟人失败:', error);
      throw new Error(`打断虚拟人失败: ${(error as Error).message}`);
    }
  }

  // ==================== 事件/辅助方法 ====================
  /**
   * 绑定SDK事件监听
   */
  bindEvents(): void {
    if (!this.interativeRef) return;

    // 清除旧事件监听
    this.interativeRef.removeAllListeners();

    // SDK核心事件
    const eventHandlers = {
      connected: (initResp: any) => console.log('🔗 SDK已连接', initResp),
      stream_start: () => console.log('🎥 视频流已启动'),
      disconnected: (e: any) => {
        console.log('🔌 SDK已断开');
        if (e) console.error('断开原因:', e.code, e.message);
      },
      error: (error: any) => console.error('❌ SDK错误', error),
      subtitle_info: (subtitleData: any) => console.log('📝 字幕更新', subtitleData),
      nlp: (nlpData: any) => console.log('🧠 NLP结果', nlpData),
      asr: (asrData: any) => console.log('🎤 ASR识别结果', asrData),
      frame_start: (frameData: any) => console.log('🎬 帧开始', frameData),
      frame_stop: (frameData: any) => console.log('🎬 帧结束', frameData),
      action_start: (actionData: any) => console.log('💃 动作开始', actionData),
      action_stop: (actionData: any) => console.log('💃 动作结束', actionData),
    };

    // 绑定所有事件
    Object.entries(eventHandlers).forEach(([event, handler]) => {
      this.interativeRef.on(event, handler);
    });

    // 播放器事件
    this.player = this.interativeRef.player || this.interativeRef.createPlayer();
    this.player
        .on('play', () => console.log('▶️ 播放器播放'))
        .on('waiting', () => console.log('⌛ 播放器缓冲中'))
        .on('playing', () => console.log('▶️ 播放器正在播放'))
        .on('error', (err: any) => console.error('❌ 播放器错误', err));
  }

  /**
   * 处理音频自动播放限制（需要用户交互）
   */
  private handleAudioAutoPlay(): void {
    if (!this.player) return;

    this.player.on('playNotAllowed', () => {
      console.log('🔊 音频自动播放被限制，需要用户点击页面恢复');

      const unlockAudio = () => {
        try {
          this.player.resume();
          console.log('🔊 音频播放已恢复');
        } catch (e) {
          console.error('🔊 恢复音频播放失败:', e);
        }
        // 移除监听，避免重复触发
        document.removeEventListener('click', unlockAudio);
        document.removeEventListener('touchstart', unlockAudio);
      };

      // 监听用户交互
      document.addEventListener('click', unlockAudio);
      document.addEventListener('touchstart', unlockAudio);
    });
  }

  // ==================== 工具方法 ====================
  /**
   * 切换模拟/真实接口模式
   * @param useMock true=使用本地mock，false=使用真实后端接口
   */
  setMockMode(useMock: boolean): void {
    this.useMock = useMock;
    console.log(`📌 已切换${useMock ? '模拟' : '真实'}接口模式`);
  }

  /**
   * 获取播放器实例
   */
  getPlayer() {
    if (!this.isInitialized) {
      throw new Error('SDK 未初始化');
    }
    return this.player || (this.interativeRef.player || this.interativeRef.createPlayer());
  }

  /**
   * 检查SDK是否已初始化
   */
  isSdkReady(): boolean {
    return this.isInitialized && !!this.interativeRef;
  }

  // ==================== 字幕功能 ====================
  /**
   * 设置字幕回调
   * @param callback 字幕回调函数
   */
  setDanmakuCallback(callback: (text: string) => void): void {
    this.danmakuCallback = callback;
  }

  /**
   * 移除字幕回调
   */
  removeDanmakuCallback(): void {
    this.danmakuCallback = null;
  }

  /**
   * 添加字幕
   * @param text 字幕文本
   */
  addDanmaku(text: string): void {
    if (!text?.trim()) return;

    // 计算显示时间（假设平均语速为每秒 3 个汉字）
    const charCount = text.length;
    const duration = Math.max(5, Math.min(12, charCount / 2));

    // 清除之前的定时器
    if (this.danmakuTimeout) {
      clearTimeout(this.danmakuTimeout);
    }

    // 只保留一个字幕，新的替换旧的
    this.danmakus = [{ text, top: 0, duration }];

    // 调用回调通知组件
    if (this.danmakuCallback) {
      this.danmakuCallback(text);
    }

    // 一段时间后移除此字幕
    this.danmakuTimeout = setTimeout(() => {
      this.danmakus = [];
    }, duration * 1000);
  }

  /**
   * 获取字幕列表
   */
  getDanmakus(): Danmaku[] {
    return this.danmakus;
  }

  /**
   * 清空字幕
   */
  clearDanmakus(): void {
    this.danmakus = [];
  }
}

// 导出单例实例
export default new AvatarService();