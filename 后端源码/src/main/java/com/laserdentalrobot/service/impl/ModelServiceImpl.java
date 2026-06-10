package com.laserdentalrobot.service.impl;

import com.laserdentalrobot.common.constant.ErrorCode;
import com.laserdentalrobot.common.exception.BusinessException;
import com.laserdentalrobot.mapper.ModelMapper;
import com.laserdentalrobot.pojo.entity.ModelEntity;
import com.laserdentalrobot.pojo.vo.ModelInfoVO;
import com.laserdentalrobot.service.ModelService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;

/**
 * 模型服务实现 上传下载
 */
@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

    private final ModelMapper modelMapper;

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucket;

    @Value("${minio.endpoint}")
    private String minioEndpoint;

    // 直接http下载模型文件
    @Override
    public InputStream getModelFile(String productId, String format) {
        ModelEntity model = modelMapper.selectByProductId(productId);
        if (model == null) {
            throw new BusinessException(ErrorCode.MODEL_NOT_FOUND, "模型不存在");
        }
        try {
            String fileUrl = minioEndpoint + "/" + bucket + "/" + model.getMinioPath();
            URL url = new URL(fileUrl);
            return url.openStream();
        } catch (Exception e) {
            throw new BusinessException("500", "模型下载失败：" + e.getMessage());
        }
    }

    // 查元信息
    @Override
    public ModelInfoVO getModelMetadata(String productId) {
        ModelEntity model = modelMapper.selectByProductId(productId);
        if (model == null) {
            throw new BusinessException(ErrorCode.MODEL_NOT_FOUND, "模型不存在");
        }

        ModelInfoVO vo = new ModelInfoVO();
        vo.setProductId(model.getProductId());
        vo.setFormat(model.getFormat());
        vo.setSize(model.getFileSize());
        vo.setMinioPath(model.getMinioPath());
        return vo;
    }

    
    @Override
    public void uploadModel(MultipartFile file, String productId, String format) {
        try {
            String objectName = "modle/" + productId + "." + format;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // 有就更新 没有就新增
            ModelEntity existing = modelMapper.selectByProductId(productId);
            if (existing != null) {
                existing.setMinioPath(objectName);
                existing.setFormat(format);
                existing.setFileSize(file.getSize());
                modelMapper.updateById(existing);
            } else {
                ModelEntity entity = new ModelEntity();
                entity.setProductId(productId);
                entity.setMinioPath(objectName);
                entity.setFormat(format);
                entity.setFileSize(file.getSize());
                modelMapper.insert(entity);
            }
        } catch (Exception e) {
            throw new BusinessException("500", "上传失败：" + e.getMessage());
        }
    }
}
