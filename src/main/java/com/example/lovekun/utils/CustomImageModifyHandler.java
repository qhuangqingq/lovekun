package com.example.lovekun.utils;

import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.util.CollectionUtils;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.Units;

import java.util.ArrayList;
import java.util.List;

public class CustomImageModifyHandler implements CellWriteHandler {
    private List<String> repeats = new ArrayList<>();
    // 单元格的图片最大张数（每列的单元格图片张数不确定，单元格宽度需按照张数最多的长度来设置）
    private Integer maxDataSize = 0;
    @Override
    public void beforeCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Head head, Integer columnIndex, Integer relativeRowIndex, Boolean isHead) {

    }

    @Override
    public void afterCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {

    }

    @Override
    public void afterCellDataConverted(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, CellData cellData, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        //  在 数据转换成功后 不是头就把类型设置成空
        if (isHead) {
            return;
        }
        //将要插入图片的单元格的type设置为空,下面再填充图片
        if(cellData.getImageValue()!=null||cellData.getData() instanceof ArrayList){
            cellData.setType(CellDataTypeEnum.EMPTY);
        }
    }

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, List<CellData> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        //  在 单元格写入完毕后 ，自己填充图片
        if (isHead || CollectionUtils.isEmpty(cellDataList)) {
            return;
        }
        Boolean listFlag = false;
        ArrayList data = null;
        Sheet sheet = cell.getSheet();
        // 此处为ListUrlConverterUtil的返回值
        if (cellDataList.get(0).getData() instanceof ArrayList){
            data = (ArrayList) cellDataList.get(0).getData();
            if (CollectionUtils.isEmpty(data)) {
                return;
            }
            if (data.get(0) instanceof CellData){
                CellData cellData = (CellData) data.get(0);
                if (cellData.getImageValue() == null){
                    return;
                }else {
                    listFlag = true;
                }
            }
        }
        if (!listFlag && cellDataList.get(0).getImageValue() == null){
            return;
        }
        String key = cell.getRowIndex() + "_" + cell.getColumnIndex();
        if (repeats.contains(key)){
            return;
        }
        repeats.add(key);
        if (data.size() > maxDataSize) {
            maxDataSize = data.size();
        }
        // 默认要导出的图片大小为175*175px,175px的行高大约是900,175px列宽大概是25*256
        sheet.getRow(cell.getRowIndex()).setHeight((short)1800);
        sheet.setColumnWidth(cell.getColumnIndex(),listFlag?25*256*maxDataSize:25*256);

        if (listFlag){
            for (int i = 0; i < data.size(); i++) {
                CellData cellData= (CellData) data.get(i);
                if(cellData.getImageValue()==null){
                    continue;
                }
                this.insertImage(sheet,cell,cellData.getImageValue(),i);
            }
        }else {
            // cellDataList 是list的原因是 填充的情况下 可能会多个写到一个单元格 但是如果普通写入 一定只有一个
            this.insertImage(sheet,cell,cellDataList.get(0).getImageValue(),0);
        }

    }

    private void insertImage(Sheet sheet,Cell cell,byte[] pictureData,int i){
        int picWidth = Units.pixelToEMU(175);
        int index = sheet.getWorkbook().addPicture(pictureData, HSSFWorkbook.PICTURE_TYPE_PNG);
        Drawing drawing = sheet.getDrawingPatriarch();
        if (drawing == null) {
            drawing = sheet.createDrawingPatriarch();
        }
        CreationHelper helper = sheet.getWorkbook().getCreationHelper();
        ClientAnchor anchor = helper.createClientAnchor();
        // 设置图片坐标
        anchor.setDx1(picWidth*i);
        anchor.setDx2(picWidth+picWidth*i);
        anchor.setDy1(0);
        anchor.setDy2(0);
        //设置图片位置
        anchor.setCol1(cell.getColumnIndex());
        anchor.setCol2(cell.getColumnIndex());
        anchor.setRow1(cell.getRowIndex());
        anchor.setRow2(cell.getRowIndex() + 1);
        // 设置图片可以随着单元格移动
        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        drawing.createPicture(anchor, index);
    }
}
