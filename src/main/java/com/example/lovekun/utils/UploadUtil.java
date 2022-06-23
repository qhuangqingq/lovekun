package com.example.lovekun.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@Component
public class UploadUtil {
    @Value("${uploadFileUrl}")
    private String uploadFileUrl;


    public String uploadFile(HttpServletRequest request, MultipartFile uploadFile, String type) throws IOException {
        //设置的保存路径  为项目上一级目录
        //生成文件名
        String newFileName = DateUtil.format(DateUtil.date(), "yyyyMMddHHmmssSS")
                + (int) Math.floor(Math.random() * 10) + "~" + uploadFile.getOriginalFilename();

        File newFile = createDir(uploadFile, type, newFileName);

        //将内存中的数据写入磁盘
        uploadFile.transferTo(newFile);

        long size = uploadFile.getSize();
        //完整的url
        String fileUrl = type + "/" + DateUtil.thisYear() + "/" + (DateUtil.thisMonth() + 1) + "/" + DateUtil.thisDayOfMonth() + "/" + newFileName;

        return fileUrl;
    }

    public File createDir(MultipartFile uploadFile, String type, String newFileName) {

        //原始名称
        String substring = StrUtil.EMPTY;
        if (uploadFile.getOriginalFilename() != null) {
            substring = uploadFile.getOriginalFilename().substring(uploadFile.getOriginalFilename().lastIndexOf(".") + 1);
        }

        //创建年月日文件夹
        File dateDirs = new File(type + File.separator + DateUtil.thisYear()
                + File.separator + (DateUtil.thisMonth() + 1) + File.separator + DateUtil.thisDayOfMonth());
        //新文件
        File newFile = new File(uploadFileUrl + File.separator + dateDirs + File.separator + newFileName);
        //判断目标文件所在的目录是否存在
        if (!newFile.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            newFile.getParentFile().mkdirs();
        }

        return newFile;
    }
}
