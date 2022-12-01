package com.example.lovekun.service.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import com.example.lovekun.service.uploadService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class uploadServiceImpl implements uploadService {

    @Override
    public XSSFWorkbook exportExcelWithPic()  {

        List<JSONObject> excelDataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("name","张三");
            jsonObject.put("age","20");
            jsonObject.put("pic","D:\\uploadFile\\pic\\2022\\6\\23\\202206231427202252~image1.png");
            excelDataList.add(jsonObject);
        }
        try {

            XSSFWorkbook book = new XSSFWorkbook();
            XSSFSheet sheet = book.createSheet("导出图片");
            int ossPicTotal = 0; // excel图片
            XSSFDrawing drawingPatriarch = sheet.createDrawingPatriarch(); // 插入图片
            XSSFRow row;
            int orderRowLength = excelDataList.size();
            for (int j = 0; j < orderRowLength; j++) {
                JSONObject excelData = excelDataList.get(j);
                int rowIndex = j + 1;
                row = sheet.createRow(rowIndex);
                row.setHeight((short)(100*20));
//                JSONObject parseDataJson = parseTruckUserData(excelData);
                JSONObject parseDataJson = excelData;
                // 1 序号
                row.createCell(0).setCellValue(j+1);
                // 2 店铺名
                row.createCell(1).setCellValue(excelData.getString("name"));
                // 3 产品款号
                row.createCell(2).setCellValue(excelData.getString("age"));


//                sheet.setColumnWidth(3,sheet.getColumnWidth(3)*17/10);
                // 4 商品图片
                int picIndex = 3;
                String briefPath = excelData.getString("brief_path");
                String productId = excelData.getString("product_id");
                try{
                    row.createCell(picIndex).setCellValue("");
                    if( ossPicTotal < 1000){ // 导出图片最大

                        String productLocalPic = excelData.getString("pic");

                        if(StringUtils.isNotBlank(productLocalPic)){// 插入图片
                            sheet.setColumnWidth(3,256 * 50 + 184);
                            insertExcelPic(book, drawingPatriarch, rowIndex, picIndex, productLocalPic,   0, 1);
                            insertExcelPic(book, drawingPatriarch, rowIndex, picIndex, productLocalPic,1,2);
                            ossPicTotal++;
                        }
                    }
                }catch (Exception e){

                }


                for(int k = 0; k < 3; k++) {
                    row.getCell(k).setCellStyle(getStyle(null,book));
                }

            }
            return book;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    private CellStyle getStyle(String type,XSSFWorkbook book){
        if("number".equals(type)){
            CellStyle numStyle = book.createCellStyle();
            numStyle.setBorderBottom(BorderStyle.THIN); //下边框
            numStyle.setBorderLeft(BorderStyle.THIN);//左边框
            numStyle.setBorderTop(BorderStyle.THIN);//上边框
            numStyle.setBorderRight(BorderStyle.THIN);//右边框
            numStyle.setWrapText(true);
            XSSFDataFormat df = book.createDataFormat();
            numStyle.setDataFormat(df.getFormat("#,#0.00"));
            return numStyle;
        }else if("date".equals(type)){
            // 导出时间

            XSSFFont font10 = book.createFont();
            font10.setFontHeightInPoints((short) 11);
            font10.setFontName("宋体");
            font10.setBold(true);
            CellStyle style01 = book.createCellStyle();
            style01.setFont(font10);
            style01.setAlignment(HorizontalAlignment.CENTER);
            style01.setVerticalAlignment(VerticalAlignment.CENTER);
            return style01;
        }else{
            CellStyle style = book.createCellStyle();
            style.setBorderBottom(BorderStyle.THIN); //下边框
            style.setBorderLeft(BorderStyle.THIN);//左边框
            style.setBorderTop(BorderStyle.THIN);//上边框
            style.setBorderRight(BorderStyle.THIN);//右边框
            style.setVerticalAlignment(VerticalAlignment.CENTER); // 水平居中
            style.setAlignment(HorizontalAlignment.CENTER); // 垂直居中


            style.setWrapText(true);
            XSSFFont font = book.createFont();
            font.setFontHeightInPoints((short) 10);
            font.setFontName("宋体");
            style.setFont(font);
            return style;
        }
    }

    /**
     * <一句话功能简述> excel插入图片
     * <功能详细描述>
     * author: zhanggw
     * 创建时间:  2022/5/25
     * @param book poi book对象
     * @param drawingPatriarch 用于图片插入Represents a SpreadsheetML drawing
     * @param rowIndex 图片插入的单元格第几行
     * @param colIndex 图片插入的单元格第几列
     * @param localPicPath 本地图片路径
     */
    private void insertExcelPic(XSSFWorkbook book, XSSFDrawing drawingPatriarch, int rowIndex, int colIndex, String localPicPath,int colbegin,int colend) throws IOException {
        // 获取图片后缀格式
        String fileSuffix = localPicPath.substring(localPicPath.lastIndexOf(".") + 1);
        fileSuffix = fileSuffix.toLowerCase();

        // 将图片写入到字节数组输出流中
        BufferedImage bufferImg;
        ByteArrayOutputStream picByteOut = new ByteArrayOutputStream();
        bufferImg = ImageIO.read(new File(localPicPath));
        ImageIO.write(bufferImg, fileSuffix, picByteOut);

        // 将图片字节数组输出流写入到excel中
        ClientAnchor anchor = new XSSFClientAnchor(  colbegin*500000+50000,
                0, colend * 500000, 1* 10000000,
                (short) colIndex, rowIndex, (short) colIndex, rowIndex);
        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        drawingPatriarch.createPicture(anchor, book.addPicture(picByteOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));
        picByteOut.close();
    }
}
