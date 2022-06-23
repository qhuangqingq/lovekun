package com.example.lovekun.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.Id;

import javax.persistence.GeneratedValue;
import java.io.Serializable;
import java.util.UUID;

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
    private String tableId;

    private String columnName;

    private String columnComment;

    private String columnType;

    private String columnLength;

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid",strategy="uuid")//使用uuid的主键注解
    private String id;

    private int version;

    private String useState;

    private String doId;

    @TableField(exist = false)
    private String type;


    /**
     * 列精度
     */
    private String columnDefinition;

    public TableColumnHis(){
    }

    public TableColumnHis(TableColumns tableColumns){
        this.id= UUID.randomUUID().toString();
        this.tableId=tableColumns.getTableId();
        this.columnComment=tableColumns.getColumnComment();
        this.columnLength=tableColumns.getColumnLength();
        this.columnName=tableColumns.getColumnName();
        this.columnType=tableColumns.getColumnType();
        this.version=tableColumns.getVersion();
        this.doId=tableColumns.getId();
    }

}
