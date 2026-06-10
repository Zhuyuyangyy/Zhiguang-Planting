package com.laserdentalrobot.service;

import com.laserdentalrobot.pojo.vo.ModelInfoVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 模型服务接口
 */
public interface ModelService {
    InputStream getModelFile(String productId, String format);
    ModelInfoVO getModelMetadata(String productId);
    void uploadModel(MultipartFile file, String productId, String format);
}
