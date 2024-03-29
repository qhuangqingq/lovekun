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
public class TableColumns  implements Serializable {

    private static final long serialVersionUID=1L;

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid",strategy="uuid")//使用uuid的主键注解
    private String id;
    /**
     * 表id
     */
    private String tableId;
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
