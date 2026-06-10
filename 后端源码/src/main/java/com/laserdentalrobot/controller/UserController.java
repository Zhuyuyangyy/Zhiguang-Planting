package com.laserdentalrobot.controller;

import com.laserdentalrobot.common.util.JwtUtil;
import com.laserdentalrobot.pojo.dto.LoginRequest;
import com.laserdentalrobot.pojo.dto.RegisterRequest;
import com.laserdentalrobot.pojo.entity.UserEntity;
import com.laserdentalrobot.pojo.vo.LoginVO;
import com.laserdentalrobot.pojo.vo.Result;
import com.laserdentalrobot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 用户接口 登录注册那些
 */
@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.success("注册成功");
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        LoginVO loginVO = userService.login(request);
        return Result.success(loginVO);
    }

    @GetMapping("/info")
    public Result<UserEntity> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            return Result.error("请先登录");
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("Token无效");
        }

        UserEntity user = userService.getById(userId);
        if (user != null) {
            user.setPassword(null); // 密码不能返回
        }
        return Result.success(user);
    }
}
