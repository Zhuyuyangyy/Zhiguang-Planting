package com.laserdentalrobot.service;

import com.laserdentalrobot.pojo.vo.DeviceParamVO;

import java.util.Map;

/**
 * 设备服务接口
 */
public interface DeviceService {
    DeviceParamVO getLaserParams();
    Map<String, Object> getComplianceInfo();
}
