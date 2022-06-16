package com.example.lovekun.utils;

import com.example.lovekun.config.UploadConfig;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.DefaultFastFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@EnableConfigurationProperties(UploadConfig.class)
public  class  FastDFSUploadUtils {


    /**
     * 上传图片返回图片地址
     * @param file
     * @return
     */
    public static String uploadImage(MultipartFile file) {
        FastFileStorageClient storageClient =new DefaultFastFileStorageClient();
        UploadConfig uploadConfig=new UploadConfig();
        try {
            // 上传到FastDFS
            // 获取扩展名
            String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            // 上传
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);
            // 返回路径
            return uploadConfig.getBaseUrl() + storePath.getFullPath();
        } catch (IOException e) {
            throw new RuntimeException("【File upload】File upload fail！" + e.getMessage());
        }
    }
}
