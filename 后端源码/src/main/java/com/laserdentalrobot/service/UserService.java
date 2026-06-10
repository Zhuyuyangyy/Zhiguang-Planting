package com.laserdentalrobot.service;

import com.laserdentalrobot.pojo.dto.LoginRequest;
import com.laserdentalrobot.pojo.dto.RegisterRequest;
import com.laserdentalrobot.pojo.entity.UserEntity;
import com.laserdentalrobot.pojo.vo.LoginVO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    void register(RegisterRequest request);
    LoginVO login(LoginRequest request);
    List<UserEntity> getUserListByPage(Integer pageNum, Integer pageSize);
    long getUserTotalCount();
    UserEntity getUserById(Long userId);
    boolean updateUserStatus(Long userId, Integer status);
    UserEntity getById(Long userId);
}
