package com.laserdentalrobot.mapper;

import com.laserdentalrobot.pojo.entity.UserEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户mapper 操作user表
 */
@Mapper
public interface UserMapper {

    @Select("SELECT id, username, password, role, status FROM user WHERE username = #{username}")
    UserEntity findByUsername(String username);

    @Insert("INSERT INTO user (username, password, role, status) VALUES (#{username}, #{password}, #{role}, #{status})")
    int insert(UserEntity user);

    @Select("SELECT id, username, role, status FROM user")
    List<UserEntity> selectAll();

    @Select("SELECT COUNT(*) FROM user")
    long countAll();

    @Select("SELECT id, username, role, status FROM user WHERE id = #{id}")
    UserEntity selectById(Long id);

    @Update("UPDATE user SET status = #{status} WHERE id = #{id}")
    int updateById(UserEntity user);
}
