package com.laserdentalrobot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laserdentalrobot.config.VirtualAvatarConfig;
import com.laserdentalrobot.pojo.entity.AvatarScript;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 虚拟人配置mapper
 */
@Mapper
public interface AvatarMapper extends BaseMapper<AvatarScript> {

    // 查启用的虚拟人配置 就一条
    @Select("SELECT * FROM virtual_avatar_config WHERE status = 1 LIMIT 1")
    VirtualAvatarConfig selectEnabledConfig();
}
