package com.laserdentalrobot.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 模型信息实体 对应model_info表
 */
@Data
@TableName("model_info")
public class ModelEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String productId;
    private String format;
    private Long fileSize;
    private Integer resolution;
    private String filePath;
    private LocalDateTime createTime;
    private String minioPath; // minio里存的路径
}
