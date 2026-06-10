package com.laserdentalrobot.service.impl;

import com.laserdentalrobot.config.VirtualAvatarConfig;
import com.laserdentalrobot.mapper.AvatarMapper;
import com.laserdentalrobot.service.AIChatService;
import com.laserdentalrobot.service.AvatarService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 虚拟人服务实现 配置和说话
 */
@Slf4j
@Service
public class AvatarServiceImpl implements AvatarService {

    private static final String OK_CODE = "200";
    private static final String FAIL_CODE = "500";
    private static final String DEFAULT_AVATAR_ID = "110117005";
    private static final String DEFAULT_VOICE = "x4_lingxiaoyu_assist";
    private static final String DEFAULT_ANS = "我暂时无法回答";

    // 下面这些默认值是照着讯飞文档写的
    private static final int BITRATE_DEF = 1000000;
    private static final int FPS_DEF = 25;
    private static final int WIDTH_DEF = 720;
    private static final int HEIGHT_DEF = 1280;
    private static final int[] MASK_DEF = {0, 0, 1080, 1920};

    @Resource
    private AvatarMapper avatarMapper;

    @Resource
    private AIChatService aiChatService;

    // 空值安全取字符串
    private String safeGet(String val) {
        return StringUtils.isEmpty(val) ? "" : val;
    }

    // 组装返回的层级结构 {code, msg, data}
    
    private Map<String, Object> buildResp(String code, String msg, Object data) {
        Map<String, Object> resp = new HashMap<>(3);
        resp.put("code", code);
        resp.put("msg", StringUtils.defaultIfBlank(msg, ""));
        Map<String, Object> dataLayer = new HashMap<>(1);
        if (data != null) {
            dataLayer.put("content", data);
        }
        resp.put("data", dataLayer);
        return resp;
    }

    // sdk配置
    private Map<String, Object> makeSdkCfg(VirtualAvatarConfig cfg) {
        Map<String, Object> m = new HashMap<>(5);
        if (cfg == null) {
            // 配置为空就全给空串
            m.put("serverUrl", "");
            m.put("appId", "");
            m.put("apiKey", "");
            m.put("apiSecret", "");
            m.put("sceneId", "");
            return m;
        }
        m.put("serverUrl", safeGet(cfg.getServerUrl()));
        m.put("appId", safeGet(cfg.getAppId()));
        m.put("apiKey", safeGet(cfg.getApiKey()));
        m.put("apiSecret", safeGet(cfg.getApiSecret()));
        m.put("sceneId", safeGet(cfg.getSceneId()));
        return m;
    }

    // 流配置
    private Map<String, Object> makeStreamCfg() {
        Map<String, Object> m = new HashMap<>(5);
        m.put("enableAlpha", true);
        m.put("bitrate", BITRATE_DEF);
        m.put("fps", FPS_DEF);
        m.put("width", WIDTH_DEF);
        m.put("height", HEIGHT_DEF);
        return m;
    }

    // 虚拟人配置
    private Map<String, Object> makeAvatarCfg(VirtualAvatarConfig cfg) {
        Map<String, Object> m = new HashMap<>(10);
        m.put("interruptMode", "text");
        m.put("appendMode", false);
        m.put("stream", makeStreamCfg());
        m.put("avatarId", safeGet(cfg != null ? cfg.getAvatarId() : DEFAULT_AVATAR_ID));
        m.put("enableSentiment", false);
        m.put("maskRegion", MASK_DEF);
        m.put("scale", 1);
        m.put("moveH", 0);
        m.put("moveV", 0);
        m.put("sampleRate", "16k");
        return m;
    }

    // 语音配置
    private Map<String, Object> makeVoiceCfg(VirtualAvatarConfig cfg) {
        Map<String, Object> m = new HashMap<>(4);
        m.put("voiceName", safeGet(cfg != null ? cfg.getVcn() : DEFAULT_VOICE));
        m.put("speed", 50);
        m.put("pitch", 50);
        m.put("volume", 100);
        return m;
    }

    // 字幕配置
    private Map<String, Object> makeSubtitleCfg() {
        Map<String, Object> m = new HashMap<>(3);
        m.put("enable", false);
        m.put("textColor", "#ffffff");
        m.put("bgColor", "#000000");
        return m;
    }

    // 播放器配置
    private Map<String, Object> makePlayerCfg() {
        Map<String, Object> m = new HashMap<>(2);
        m.put("renderDom", "leftContainer");
        m.put("volume", 100);
        return m;
    }

    
    @Override
    public Map<String, Object> getAvatarConfig() {
        log.info("开始获取虚拟人配置");
        try {
            VirtualAvatarConfig cfg = avatarMapper.selectEnabledConfig();

            Map<String, Object> bizData = new HashMap<>(5);
            bizData.put("sdkConfig", makeSdkCfg(cfg));
            bizData.put("avatarConfig", makeAvatarCfg(cfg));
            bizData.put("voiceConfig", makeVoiceCfg(cfg));
            bizData.put("subtitleConfig", makeSubtitleCfg());
            bizData.put("playerConfig", makePlayerCfg());

            return buildResp(OK_CODE, "获取虚拟形象配置成功", bizData);
        } catch (Exception e) {
            log.error("获取虚拟人配置失败", e);
            return buildResp(FAIL_CODE, "获取配置失败：" + e.getMessage(), null);
        }
    }

    // dify风格的响应结构
    private Map<String, Object> buildDifyResp(String code, String msg, Map<String, Object> bizData) {
        Map<String, Object> resp = new HashMap<>(3);
        resp.put("code", code);
        resp.put("msg", msg);

        Map<String, Object> dataLayer = new HashMap<>();
        if (bizData != null) {
            dataLayer.putAll(bizData);
            dataLayer.put("created_at", System.currentTimeMillis() / 1000);
            dataLayer.put("event", "message");
        }
        resp.put("data", dataLayer);
        return resp;
    }

    
    @Override
    public Map<String, Object> speak(String question) {
        if (StringUtils.isBlank(question)) {
            return buildDifyResp(FAIL_CODE, "提问内容不能为空", null);
        }

        log.info("虚拟人问答 提问：{}", question);
        try {
            String answer = aiChatService.chatVirtual(question);
            String finalAns = StringUtils.isBlank(answer) ? DEFAULT_ANS : answer;

            Map<String, Object> difyData = new HashMap<>();
            difyData.put("answer", finalAns);
            difyData.put("query", question);
            difyData.put("message_id", "msg_" + System.currentTimeMillis());

            Map<String, Object> meta = new HashMap<>();
            meta.put("type", "text");
            difyData.put("metadata", meta);

            log.info("AI回复: {}", finalAns);
            return buildDifyResp(OK_CODE, "AI回复成功", difyData);

        } catch (Exception e) {
            log.error("AI问答失败", e);
            return buildDifyResp(FAIL_CODE, "AI回复失败：" + e.getMessage(), null);
        }
    }
}
