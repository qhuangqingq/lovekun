package com.example.lovekun.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.lovekun.entity.Student;
import com.example.lovekun.entity.StudentDto;
import com.example.lovekun.service.studentService;
import com.example.lovekun.utils.CustomImageModifyHandler;
import com.example.lovekun.utils.FastDFSUploadUtils;
import io.swagger.annotations.Api;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Api(value = "excel接口")
@RestController
@RequestMapping("/excelController")
public class ExcelController {

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    @Autowired
    studentService service;


    public final static String STUDENT_UPLOAD_PATH = "D:\\images\\student\\";

    /**
     * 导入方法
     * @param file
     * @return
     */
    @PostMapping("upload")
    public void upload(MultipartFile file) {
        try {
            //params有很多参数可以自己调
            ImportParams params = new ImportParams();
            List<Student> students = ExcelImportUtil.importExcel(file.getInputStream(), Student.class, params);
            List<Student> students1 = saveImage1(students);
            service.saveBatch(students1);
//            service.saveBatch(students);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 导出方法
     */
    @GetMapping("export")
    public void export() throws IOException {
        try {
            List<Student> students = service.list();
            ExportParams exportParams = new ExportParams();
            exportParams.setSheetName("学生信息");
            exportParams.setType(ExcelType.HSSF);
            Workbook workbook = ExcelExportUtil.exportExcel(exportParams,Student.class, students);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("学生数据表", "UTF-8") + ".xls");
            response.setCharacterEncoding("UTF-8");
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    /**
     * easyExcel导出方法
     */
    @GetMapping("export1")
    public void export1() throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("学生数据表", "UTF-8") + ".xls");
        response.setCharacterEncoding("UTF-8");
        ServletOutputStream out = response.getOutputStream();
        List<Student> students = service.list();
        List<StudentDto> list=new ArrayList<>();

        List<URL> urlList=new ArrayList<>();
        for (int i=0;i<students.size();i++){
            StudentDto studentDto=new StudentDto();
            BeanUtils.copyProperties(students.get(i),studentDto);
            list.add(studentDto);
            URL url=new URL(students.get(i).getPhoto());
            urlList.add(url);

        }

        list.get(0).setListImage(urlList);

        EasyExcel.write(out, StudentDto.class)
                .registerWriteHandler(new CustomImageModifyHandler()).sheet().doWrite(list);

    }

    /**
     * 文件转存
     * @param students
     */
    private List<Student> saveImage(List<Student> students) {
        //将图片上传到FastDFS并且将地址保存
        students.forEach(food->{
            if(Optional.ofNullable(food.getPhoto()).isPresent()){
                try {
                    File file = new File(food.getPhoto());
                    FileInputStream fileInputStream = new FileInputStream(file);
                    MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain",IOUtils.toByteArray(fileInputStream));
                    String imgUrl = FastDFSUploadUtils.uploadImage(multipartFile);
                    food.setPhoto(imgUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return students;

    }

    private List<Student> saveImage1(List<Student> students){
        for (Student student : students) {
            if (StringUtils.isNotEmpty(student.getPhoto())) {
                try {
                    //获取到暂存的文件
                    File tmpFile = new File(student.getPhoto());
                    FileInputStream fileInputStream = new FileInputStream(tmpFile);
                    //转换为 multipartFile 类
                    MultipartFile multipartFile = new MockMultipartFile("file", tmpFile.getName(), "text/plain", IOUtils.toByteArray(fileInputStream));
                    //获取当前的日期，按日期归档
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
                    String format = sdf.format(new Date());
                    //获取到本地磁盘的路径，先建立路径
                    File file = new File(STUDENT_UPLOAD_PATH + format);
                    if (!file.isDirectory()) {
                        file.mkdirs();
                    }
                    //初始文件名
                    String originName = tmpFile.getName();
                    //后缀名
                    String suffix = originName.substring(originName.lastIndexOf("."));
                    //存加密后的uuid+后缀作为存到path里的文件名
                    String fileName = (int) Math.random() * 100 + suffix;
                    File dest = new File(file.getAbsoluteFile() + File.separator + fileName);
                    String filePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/student/" + format + fileName;
                    multipartFile.transferTo(dest);
                    student.setPhoto(filePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return students;
    }

}
