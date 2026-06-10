package com.laserdentalrobot.service;

/**
 * AI聊天服务接口
 */
public interface AIChatService {
    // AI问答用的
    String chat(String userQuestion);
    // 虚拟人用的
    String chatVirtual(String userQuestion);
    // 底层请求dify的逻辑
    String executeDifyRequest(String userQuestion, String targetAppId);
}
