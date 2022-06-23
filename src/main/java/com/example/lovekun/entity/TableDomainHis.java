package com.example.lovekun.entity;

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
    private String remark;

    /**
     * 版本
     */
    private int version;


    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid",strategy="uuid")//使用uuid的主键注解
    private String id;

    private String doId;

    public TableDomainHis(TableDomain domain){
        this.id= UUID.randomUUID().toString();
        this.doId=domain.getId();
        this.tableName=domain.getTableName();
        this.remark=domain.getRemark();
        this.version=domain.getVersion();
    }

    public TableDomainHis(){
    }


}
