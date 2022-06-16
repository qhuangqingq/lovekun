package com.example.lovekun.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
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
 * @since 2022-06-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("table_column")
public class TableColumns extends Model<TableColumns> implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 表id
     */
    private Integer tableId;
    /**
     * 列名
     */
    private String columnName;
    /**
     * 列备注
     */
    private String columnComment;
    /**
     * 列类型
     */
    private String columnType;
    /**
     * 列长度
     */
    private String columnLength;
    /**
     * 列精度
     */
    private String columnDefinition;
    /**
     * 版本
     */
    private int version;

    /**
     * 修改类型（update,delete,add）
     */
    @TableField(exist = false)
    private String type;

}
