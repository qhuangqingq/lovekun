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
import java.util.List;

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
@TableName("table_domain")
public class TableDomain   implements Serializable {

    private static final long serialVersionUID=1L;

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid",strategy="uuid")//使用uuid的主键注解
    private String id;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 表备注
     */
    private String remark;

    /**
     * 当前生效版本
     */
    private int version;


    @TableField(exist = false)
    private  List<TableColumns> list;

}
