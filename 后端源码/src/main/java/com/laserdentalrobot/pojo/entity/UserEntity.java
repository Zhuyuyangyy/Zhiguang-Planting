package com.laserdentalrobot.pojo.entity;

import lombok.Data;

/**
 * 用户实体
 */
@Data
public class UserEntity {
    private Long id;
    private String username;
    private String password; // 数据库存的是明文 后面再改加密
    private Integer role;    // 0普通 1管理员
    private Integer status;  // 1正常 0禁用
    // private LocalDateTime createTime;
    // private LocalDateTime updateTime;
}
