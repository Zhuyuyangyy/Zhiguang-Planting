package com.laserdentalrobot.service.impl;

import com.laserdentalrobot.mapper.DeviceMapper;
import com.laserdentalrobot.pojo.vo.DeviceParamVO;
import com.laserdentalrobot.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设备服务实现 从数据库读配置
 */
@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceMapper deviceMapper;

    @Override
    public DeviceParamVO getLaserParams() {
        // 查laser类型的5条配置
        List<Map<String, Object>> list = deviceMapper.listByType("laser");

        // 转成map方便取值
        Map<String, Object> map = list.stream()
                .collect(Collectors.toMap(
                        m -> (String) m.get("config_key"),
                        m -> (String) m.get("config_value")
                ));

        // 封装vo
        
        return DeviceParamVO.builder()
                .pulseFrequency((String) map.get("pulseFrequency"))
                .waterFlow((String) map.get("waterFlow"))
                .positioningAccuracy((String) map.get("positioningAccuracy"))
                .laserPower((String) map.get("laserPower"))
                .status(Integer.parseInt((String) map.get("status")))
                .build();
    }

    @Override
    public Map<String, Object> getComplianceInfo() {
        List<Map<String, Object>> list = deviceMapper.listByType("compliance");
        return list.stream()
                .collect(Collectors.toMap(
                        m -> (String) m.get("config_key"),
                        m -> (String) m.get("config_value")
                ));
    }
}
