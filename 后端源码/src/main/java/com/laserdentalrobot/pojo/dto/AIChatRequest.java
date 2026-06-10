package com.laserdentalrobot.pojo.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

/**
 * AI聊天请求体
 */
@Data
public class AIChatRequest {
    // 前端可能传query也可能传question 都接
    @JsonAlias({"query", "userQuestion", "question"})
    private String question;
}
