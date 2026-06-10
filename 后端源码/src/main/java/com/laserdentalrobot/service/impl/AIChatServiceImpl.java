package com.laserdentalrobot.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laserdentalrobot.service.AIChatService;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;

/**
 * AI聊天服务实现 调dify的接口
 */
@Service
public class AIChatServiceImpl implements AIChatService {

    @Value("${dify.api.base-url}")
    private String difyBaseUrl;
    @Value("${dify.api.key-ai}")
    private String apiKeyAi;
    @Value("${dify.api.key-virtual}")
    private String apiKeyVirtual;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String chat(String userQuestion) {
        return executeDifyRequest(userQuestion, apiKeyAi);
    }

    @Override
    public String chatVirtual(String userQuestion) {
        return executeDifyRequest(userQuestion, apiKeyVirtual);
    }

    
    @Override
    public String executeDifyRequest(String userQuestion, String apiKey) {
        String fullUrl = difyBaseUrl.replaceAll("/$", "") + "/chat-messages";
        StringBuilder fullAnswer = new StringBuilder();

        try {
            // 转义一下双引号 不然json会炸
            String safeQ = userQuestion.replace("\"", "\\\"");
            String json = String.format(
                    "{\"inputs\":{},\"query\":\"%s\",\"response_mode\":\"streaming\",\"user\":\"server-user-001\"}",
                    safeQ
            );

            // 超时设置 连接5秒 读取20秒
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(5000)
                    .setSocketTimeout(20000)
                    .build();

            try (CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(config).build()) {
                HttpPost post = new HttpPost(fullUrl);
                post.setHeader("Authorization", "Bearer " + apiKey.trim());
                post.setHeader("Content-Type", "application/json;charset=UTF-8");
                post.setEntity(new StringEntity(json, StandardCharsets.UTF_8));

                CloseableHttpResponse response = client.execute(post);
                BufferedReader reader = new BufferedReader(
                        new java.io.InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8)
                );

                // 解析sse流
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith("data: ")) continue;
                    String data = line.substring(6);
                    if (data.equals("[DONE]")) break;

                    try {
                        JsonNode node = objectMapper.readTree(data);
                        if (node.has("answer")) {
                            fullAnswer.append(node.get("answer").asText());
                        }
                    } catch (Exception ignored) {
                        // json解析失败就跳过 有时候dify会发一些奇怪的数据
                    }
                }

                response.close();
                return cleanMd(fullAnswer.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 如果已经收了一部分就返回 不然给个默认提示
            return fullAnswer.length() > 0 ? cleanMd(fullAnswer.toString()) : "AI 正在回复，请稍候...";
        }
    }

    // 清理markdown符号 前端不要#
    
    private String cleanMd(String text) {
        if (text == null || text.isEmpty()) return "";
        return text
                .replaceAll("#", "")
                .replaceAll("\\*", "")
                .replaceAll("-", "")
                .replaceAll("\\=", "")
                .replaceAll(">", "")
                .replaceAll("`", "")
                .replaceAll("~", "")
                .replaceAll("\\n{3,}", "\n\n");
    }
}
