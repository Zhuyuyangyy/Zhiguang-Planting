package com.laserdentalrobot.pojo.dto;

import lombok.Data;

/**
 * 讯飞虚拟人文本驱动dto
 */
@Data
public class XfAvatarTextDriverDTO {
    private String text;
    private Boolean nlp = false; // 是否开语义理解 默认关
}
