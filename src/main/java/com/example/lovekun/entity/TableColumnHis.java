package com.example.lovekun.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author hq
 * @since 2022-06-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("table_column_his")
public class TableColumnHis implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 表id
     */
    private Integer tableId;

    private String columnName;

    private String columnComment;

    private String columnType;

    private String columnLength;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private int version;

    private String useState;

    private Integer doId;
    /**
     * 列精度
     */
    private String columnDefinition;

    public TableColumnHis(){
    }

    public TableColumnHis(TableColumns tableColumns){
        this.tableId=tableColumns.getTableId();
        this.columnComment=tableColumns.getColumnComment();
        this.columnLength=tableColumns.getColumnLength();
        this.columnName=tableColumns.getColumnName();
        this.columnType=tableColumns.getColumnType();
        this.version=tableColumns.getVersion();
        this.doId=tableColumns.getId();
    }

}
