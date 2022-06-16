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
@TableName("table_domain_his")
public class TableDomainHis implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 表备注
     */
    private String ramake;

    /**
     * 版本
     */
    private int version;


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer doId;

    public TableDomainHis(TableDomain domain){
        this.doId=domain.getId();
        this.tableName=domain.getTableName();
        this.ramake=domain.getRamake();
        this.version=domain.getVersion();
    }

    public TableDomainHis(){
    }


}
