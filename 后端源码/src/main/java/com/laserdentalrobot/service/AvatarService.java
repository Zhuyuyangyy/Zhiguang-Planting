package com.laserdentalrobot.service;

import java.util.Map;

/**
 * 虚拟人服务接口
 */
public interface AvatarService {
    Map<String, Object> getAvatarConfig();
    Map<String, Object> speak(String question);
}
