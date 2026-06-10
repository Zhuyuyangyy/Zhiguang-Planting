package com.laserdentalrobot.pojo.vo;

import lombok.Builder;
import lombok.Data;

/**
 * sdk配置vo
 */
@Data
@Builder
public class SdkConfigVO {
    private String serverUrl;
    private String appId;
    private String apiKey;
    private String apiSecret;
    private String sceneId;
}
