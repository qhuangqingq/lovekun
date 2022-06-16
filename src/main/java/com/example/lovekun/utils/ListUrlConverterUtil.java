package com.example.lovekun.utils;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.util.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
//import java.util.List;

public class ListUrlConverterUtil implements Converter<List<URL>> {
    @Override
    public Class supportJavaTypeKey() {
        return List.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        /**
         *这里记得枚举类型为IMAGE
         */
        return CellDataTypeEnum.IMAGE;
    }

    @Override
    public List convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return null;
    }

    @Override
    public CellData convertToExcelData(List<URL> value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        // 这里进行对数据实体类URL集合处理
        List<CellData> data = new ArrayList<>();
        // for 循环一次读取
        for (URL url : value) {
            InputStream inputStream = null;
            try {
                inputStream = url.openStream();
                byte[] bytes = IoUtils.toByteArray(inputStream);
                data.add(new CellData(bytes));
            } catch (Exception e) {
                throw new IOException("导出excel图片读取异常");
            } finally {
                if (inputStream != null){
                    inputStream.close();
                }
            }
        }

        // 这种方式并不能返回一个List,所以只好通过CellData cellData = new CellData(data);将这个list对象塞到返回值CellData对象的data属性中；
        CellData cellData = new CellData(data);
        cellData.setType(CellDataTypeEnum.IMAGE);
        return cellData;
    }

}
