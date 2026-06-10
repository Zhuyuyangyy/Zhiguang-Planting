package com.laserdentalrobot.pojo.entity;

import lombok.Data;

/**
 * 虚拟人脚本表
 */
@Data
public class AvatarScript {
    private Long id;
    private String sceneKey;
    private String content;
    private Integer sort;
}
