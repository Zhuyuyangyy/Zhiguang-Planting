package com.laserdentalrobot.controller;

import com.laserdentalrobot.pojo.vo.DeviceParamVO;
import com.laserdentalrobot.pojo.vo.Result;
import com.laserdentalrobot.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 设备参数接口
 */
@RestController
@RequestMapping("/api/v1/device")
@RequiredArgsConstructor
@Tag(name = "设备接口", description = "激光参数/合规信息查询")
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping("/laser-params")
    @Operation(summary = "激光参数查询")
    public Result<DeviceParamVO> getLaserParams() {
        DeviceParamVO vo = deviceService.getLaserParams();
        return Result.success(vo);
    }

    @GetMapping("/compliance")
    @Operation(summary = "设备合规信息查询")
    public Result<?> getComplianceInfo() {
        return Result.success(deviceService.getComplianceInfo());
    }

    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("设备接口测试成功");
    }
}
