package com.laserdentalrobot.pojo.vo;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * 虚拟人详细配置vo 目前好像没用到 先留着
 */
@Data
@Builder
public class AvatarDetailConfigVO {
    private String interruptMode;
    private Boolean enableSentiment;
    private StreamConfig stream;
    private String avatarId;
    private List<Integer> maskRegion;
    private Integer scale;
    private Integer moveH;
    private Integer moveV;
    private String sampleRate;
    private VoiceConfig voiceConfig;
    private SubtitleConfig subtitleConfig;

    @Data
    @Builder
    public static class StreamConfig {
        private Boolean enableAlpha;
        private Integer bitrate;
        private Integer fps;
        private Integer width;
        private Integer height;
    }

    @Data
    @Builder
    public static class VoiceConfig {
        private String voiceName;
        private Integer speed;
        private Integer pitch;
        private Integer volume;
    }

    @Data
    @Builder
    public static class SubtitleConfig {
        private Boolean enable;
        private String textColor;
    }
}
