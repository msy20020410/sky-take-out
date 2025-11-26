package com.sky.controller.admin;

import com.sky.service.MinioService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/admin/common")
public class CommonController {

    @Resource
    private MinioService minioService;

    @PostMapping("/upload")
    public String upload(@RequestPart("file") MultipartFile file) throws Exception {
        return minioService.upload(file);
    }
}
