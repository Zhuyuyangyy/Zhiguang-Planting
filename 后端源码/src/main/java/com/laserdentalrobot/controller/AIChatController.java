package com.laserdentalrobot.controller;

import com.laserdentalrobot.pojo.dto.AIChatRequest;
import com.laserdentalrobot.pojo.vo.AIChatResponse;
import com.laserdentalrobot.service.AIChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI聊天接口
 */
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class AIChatController {

    private final AIChatService aiChatService;

    // AI问答接口
    @PostMapping("/chat-messages")
    public AIChatResponse chat(@RequestBody AIChatRequest request) {
        try {
            String answer = aiChatService.chat(request.getQuestion());
            return AIChatResponse.success(answer);
        } catch (Exception e) {
            return AIChatResponse.fail(500, "服务器请求Dify失败：" + e.getMessage());
        }
    }

    // 虚拟人专用接口
    @PostMapping("/virtual-chat")
    public AIChatResponse virtualChat(@RequestBody AIChatRequest request) {
        try {
            String answer = aiChatService.chatVirtual(request.getQuestion());
            return AIChatResponse.success(answer);
        } catch (Exception e) {
            return AIChatResponse.fail(500, "虚拟人请求失败：" + e.getMessage());
        }
    }
}
