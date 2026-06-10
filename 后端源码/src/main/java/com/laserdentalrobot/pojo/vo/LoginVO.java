package com.laserdentalrobot.pojo.vo;

import lombok.Data;

/**
 * 登录返回的数据
 */
@Data
public class LoginVO {
    private String token;  // jwt生成的
    private Long id;
    private String username;
    private Integer role;  // 前端根据role显示不同菜单
}
