package com.laserdentalrobot.controller;

import com.laserdentalrobot.pojo.dto.AIChatRequest;
import com.laserdentalrobot.pojo.vo.Result;
import com.laserdentalrobot.service.AvatarService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 虚拟人相关接口
 */
@RestController
@RequestMapping("/api/avatar")
@CrossOrigin(origins = "http://localhost:5173")
public class AvatarController {

    @Resource
    private AvatarService avatarService;

    // 拿虚拟人配置
    @GetMapping("/config")
    public Result<Map<String, Object>> getAvatarConfig() {
        try {
            Map<String, Object> serviceResult = avatarService.getAvatarConfig();
            // service返回的是{code,msg,data}结构 取出data再包装
            
            Map<String, Object> bizData = (Map<String, Object>) serviceResult.get("data");
            return Result.success(bizData);
        } catch (Exception e) {
            return Result.error("获取配置失败：" + e.getMessage());
        }
    }

    // 虚拟人说话
    @PostMapping("/speak")
    public Result<Map<String, Object>> speak(@RequestBody AIChatRequest request) {
        try {
            Map<String, Object> fullResp = avatarService.speak(request.getQuestion());
            Map<String, Object> bizData = (Map<String, Object>) fullResp.get("data");
            return Result.success(bizData);
        } catch (Exception e) {
            return Result.error("虚拟人响应失败：" + e.getMessage());
        }
    }
}
