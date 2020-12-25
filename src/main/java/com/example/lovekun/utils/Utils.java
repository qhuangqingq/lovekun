package com.example.lovekun.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public   class  Utils {
    public static void dowloadFile(MultipartFile file) throws IOException {

        String dest = "D:\\ud\\xx";
        BufferedInputStream buffer=null;
        FileOutputStream fos=null;
        File file1  =new File(dest);
            if(!file1.exists()&&!file1.isDirectory()){
                      file1.mkdir();
                 }
            String url=file1+"\\"+file.getOriginalFilename();
        try {
            InputStream inputStream = file.getInputStream();
            buffer= new BufferedInputStream(inputStream);
            // 2、创建FileoutputStream
            fos = new FileOutputStream(url);
            // 5、读取对应的文件内容
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = buffer.read(b)) != -1) {
                // 6将读取内容写到目标地点
                fos.write(b, 0, len);
                fos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            buffer.close();
            fos.close();
        }
    }
}
