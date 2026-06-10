package com.laserdentalrobot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 设备参数mapper
 */
@Mapper
public interface DeviceMapper {

    @Select("""
        SELECT config_key, config_value
        FROM device_config
        WHERE type = #{type}
    """)
    List<Map<String, Object>> listByType(String type);
}
