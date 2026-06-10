package com.laserdentalrobot.pojo.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;

/**
 * 模型查询请求
 */
@Data
public class ModelQueryRequest {

    @NotNull(message = "产品ID不能为空")
    @Pattern(regexp = "^\\d+$", message = "产品ID必须为数字")
    private String productId;

    private String modelType;   // glb/gltf/obj
    private Integer resolution; // 分辨率
}
