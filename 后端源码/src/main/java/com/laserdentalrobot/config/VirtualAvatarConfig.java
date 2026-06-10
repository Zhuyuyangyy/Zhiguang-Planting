package com.laserdentalrobot.config;

import lombok.Data;

/**
 * 虚拟人配置实体 从数据库读的
 */
@Data
public class VirtualAvatarConfig {
    private Long id;
    private String avatarName;
    private String appId;
    private String apiKey;
    private String apiSecret;
    private String sceneId;
    private String avatarId;
    private String vcn;
    private String serverUrl;
    private Integer status;
}
