package com.laserdentalrobot.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 设备参数vo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DeviceParamVO {
    private String pulseFrequency;
    private String waterFlow;
    private String positioningAccuracy;
    private String laserPower;
    private Integer status;
}
