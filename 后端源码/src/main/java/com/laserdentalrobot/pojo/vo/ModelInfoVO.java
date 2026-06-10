package com.laserdentalrobot.pojo.vo;

import lombok.Data;

/**
 * 模型元信息vo
 */
@Data
public class ModelInfoVO {
    private String productId;
    private String format;
    private Long size;
    private Integer resolution;
    private String minioPath;
}
