package com.laserdentalrobot.controller;

import com.laserdentalrobot.common.exception.BusinessException;
import com.laserdentalrobot.pojo.vo.ModelInfoVO;
import com.laserdentalrobot.pojo.vo.Result;
import com.laserdentalrobot.service.ModelService;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * 3D模型接口 下载和上传模型文件
 */
@RestController
@RequestMapping("/api/v1/model")
@CrossOrigin(originPatterns = "*")
@Tag(name = "3D模型接口", description = "3D模型资源获取")
public class ModelController {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private ModelService modelService;

    @Value("${minio.bucket-name}")
    private String bucket;

    // 下载模型文件
    @GetMapping("/{productId}")
    public void streamModel(
            @PathVariable String productId,
            @RequestParam(defaultValue = "glb") String format,
            @RequestHeader(value = "Range", required = false) String rangeHeader,
            HttpServletResponse response) {
        try {
            ModelInfoVO modelInfo = modelService.getModelMetadata(productId);
            String objectName = modelInfo.getMinioPath();

            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder().bucket(bucket).object(objectName).build()
            );

            response.setContentType("model/gltf-binary");
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Content-Disposition", "inline; filename=model." + format);
            response.setHeader("Cache-Control", "public, max-age=3600");
            response.setContentLengthLong(stat.size());

            // 流式输出 不能一次全加载到内存
            
            try (GetObjectResponse in = minioClient.getObject(
                    GetObjectArgs.builder().bucket(bucket).object(objectName).build());
                 OutputStream out = response.getOutputStream()) {

                byte[] buf = new byte[65536];
                int len;
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                response.flushBuffer();
            }

        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    // 上传模型
    @PostMapping("/upload")
    public Result uploadModel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("productId") String productId,
            @RequestParam(value = "format", defaultValue = "glb") String format) {
        try {
            modelService.uploadModel(file, productId, format);
            return Result.success("上传成功");
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    // 下载视频 就是模型接口复用了一下
    @GetMapping("/video/{productId}")
    public void streamVideo(
            @PathVariable String productId,
            @RequestHeader(value = "Range", required = false) String rangeHeader,
            HttpServletResponse response) {
        try {
            ModelInfoVO modelInfo = modelService.getModelMetadata(productId);
            String objectName = modelInfo.getMinioPath();

            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder().bucket(bucket).object(objectName).build()
            );

            // 视频的contentType要写对
            response.setContentType("video/mp4");
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Content-Disposition", "inline; filename=background.mp4");
            response.setHeader("Cache-Control", "public, max-age=3600");
            response.setContentLengthLong(stat.size());

            try (GetObjectResponse in = minioClient.getObject(
                    GetObjectArgs.builder().bucket(bucket).object(objectName).build());
                 OutputStream out = response.getOutputStream()) {

                byte[] buf = new byte[65536];
                int len;
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                response.flushBuffer();
            }

        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }
}
