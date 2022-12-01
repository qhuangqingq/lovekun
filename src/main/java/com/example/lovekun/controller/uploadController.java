package com.example.lovekun.controller;

import com.example.lovekun.service.uploadService;
import io.swagger.annotations.Api;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Api(value = "excel导出图片接口")
@RestController
@RequestMapping("/uploadController")
public class uploadController {
    @Autowired
    private uploadService service;

    // 导出图片
    @RequestMapping("/export")
    public void exportExcelWithPic(HttpServletResponse response) throws IOException {

        try {
            XSSFWorkbook excel = service.exportExcelWithPic();
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename="  + "XMH.xls");
            response.setCharacterEncoding("UTF-8");
            excel.write(response.getOutputStream());
            excel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
