package com.laserdentalrobot.controller;

import com.laserdentalrobot.common.constant.ErrorCode;
import com.laserdentalrobot.common.exception.BusinessException;
import com.laserdentalrobot.common.util.JwtUtil;
import com.laserdentalrobot.pojo.entity.UserEntity;
import com.laserdentalrobot.pojo.vo.Result;
import com.laserdentalrobot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 管理员接口 权限校验在这做
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@Validated
@Tag(name = "管理员接口", description = "用户管理与权限控制")
@AllArgsConstructor
public class AdminController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    // 检查是不是管理员 不是就扔异常
    
    private void checkAdminPermission(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }

        Integer role = null;
        try {
            role = jwtUtil.getRoleFromToken(token);
        } catch (Exception e) {
            log.error("Token解析失败: {}", e.getMessage());
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Token无效或已过期");
        }

        if (role == null || role != 1) {
            log.warn("非管理员越权访问 role={}", role);
            throw new BusinessException(ErrorCode.FORBIDDEN, "无管理员权限，禁止访问");
        }
    }

    @GetMapping("/user/list")
    @Operation(summary = "分页查询用户列表")
    public Result<PageResult<UserEntity>> getUserList(
            @Min(value = 1, message = "页码不能小于1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Min(value = 1, message = "每页条数不能小于1")
            @Max(value = 100, message = "每页条数不能大于100")
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {

        checkAdminPermission(request);
        log.info("管理员查用户列表 pageNum={} pageSize={}", pageNum, pageSize);

        List<UserEntity> userList = userService.getUserListByPage(pageNum, pageSize);
        // 密码不能返回
        userList.forEach(u -> u.setPassword(null));
        long total = userService.getUserTotalCount();

        PageResult<UserEntity> pageResult = new PageResult<>(userList, total, pageNum, pageSize);
        return Result.success(pageResult);
    }

    @PostMapping("/user/status")
    @Operation(summary = "更新用户状态 封禁/解封")
    public Result<String> updateUserStatus(
            @NotNull(message = "用户ID不能为空")
            @RequestParam Long userId,
            @NotNull(message = "状态值不能为空")
            @RequestParam Integer status,
            HttpServletRequest request) {

        checkAdminPermission(request);

        if (status != 0 && status != 1) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "状态值只能0或1");
        }

        // 不能封自己
        String token = request.getHeader("token");
        Long currentAdminId = null;
        try {
            currentAdminId = jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Token解析失败");
        }

        if (userId.equals(currentAdminId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "禁止封禁/解封自身账号");
        }

        UserEntity existUser = userService.getUserById(userId);
        if (existUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }

        boolean ok = userService.updateUserStatus(userId, status);
        if (!ok) {
            throw new BusinessException(ErrorCode.SERVER_ERROR, "状态更新失败");
        }

        String msg = status == 1 ? "用户解封成功" : "用户封禁成功";
        log.info("管理员[{}]更新用户[{}]状态{}", currentAdminId, userId, msg);
        return Result.success(msg);
    }

    // 分页结果内部类 就放这了
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PageResult<T> {
        private List<T> list;
        private long total;
        private int pageNum;
        private int pageSize;
        private int totalPages;

        public PageResult(List<T> list, long total, int pageNum, int pageSize) {
            this.list = list;
            this.total = total;
            this.pageNum = pageNum;
            this.pageSize = pageSize;
            this.totalPages = pageSize == 0 ? 0 : (int) Math.ceil((double) total / pageSize);
        }
    }
}
