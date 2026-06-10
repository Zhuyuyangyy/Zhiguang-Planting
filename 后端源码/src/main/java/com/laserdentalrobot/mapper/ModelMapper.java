package com.laserdentalrobot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laserdentalrobot.pojo.entity.ModelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 模型mapper
 */
@Mapper
public interface ModelMapper extends BaseMapper<ModelEntity> {

    @Select("SELECT * FROM model_info WHERE product_id = #{productId}")
    ModelEntity selectByProductId(String productId);
}
