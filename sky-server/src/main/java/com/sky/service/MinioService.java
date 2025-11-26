package com.sky.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@Service
@Slf4j
public class MinioService {

    @Resource
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    /**
     * 上传文件
     */
    public String upload(MultipartFile file) throws Exception {

        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();

        // 上传
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(fileName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        // 返回外链 URL
        String path = String.format("http://124.222.138.172:19000/%s/%s", bucket, fileName);
        log.info("上传成功，图片地址为：{}", path);
        return path;
    }
}
