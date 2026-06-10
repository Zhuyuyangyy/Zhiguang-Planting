package com.laserdentalrobot.service.impl;

import com.laserdentalrobot.common.constant.ErrorCode;
import com.laserdentalrobot.common.exception.BusinessException;
import com.laserdentalrobot.common.util.JwtUtil;
import com.laserdentalrobot.pojo.dto.LoginRequest;
import com.laserdentalrobot.pojo.dto.RegisterRequest;
import com.laserdentalrobot.pojo.entity.UserEntity;
import com.laserdentalrobot.pojo.vo.LoginVO;
import com.laserdentalrobot.mapper.UserMapper;
import com.laserdentalrobot.service.UserService;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public List<UserEntity> getUserListByPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<UserEntity> list = userMapper.selectAll();
        list.forEach(u -> u.setPassword(null));
        return list;
    }

    @Override
    public long getUserTotalCount() {
        return userMapper.countAll();
    }

    @Override
    public UserEntity getUserById(Long userId) {
        UserEntity user = userMapper.selectById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    @Override
    public boolean updateUserStatus(Long userId, Integer status) {
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setStatus(status);
        return userMapper.updateById(user) > 0;
    }

    @Override
    public void register(RegisterRequest request) {
        if (!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户名和密码不能为空");
        }

        // 看看用户名有没有被占
        UserEntity existUser = userMapper.findByUsername(request.getUsername());
        if (existUser != null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户名已存在");
        }

        // 明文存密码 先这样 后面再加密
        String rawPwd = request.getPassword();

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPassword(rawPwd);
        user.setRole(0);
        user.setStatus(1);

        int cnt = userMapper.insert(user);
        if (cnt <= 0) {
            throw new BusinessException(ErrorCode.SERVER_ERROR, "注册失败，请重试");
        }
    }

    @Override
    public LoginVO login(LoginRequest request) {
        if (!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户名和密码不能为空");
        }

        UserEntity user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }

        // 明文比对 后面要改
        if (!request.getPassword().equals(user.getPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "账号已被封禁，请联系管理员");
        }

        // 生成jwt
        
        String token = jwtUtil.generateToken(user.getId(), user.getRole());

        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRole(user.getRole());

        return vo;
    }

    @Override
    public UserEntity getById(Long userId) {
        UserEntity user = userMapper.selectById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }
}
