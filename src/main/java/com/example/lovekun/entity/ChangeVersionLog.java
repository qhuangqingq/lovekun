package com.example.lovekun.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 切换版本记录
 * </p>
 *
 * @author hq
 * @since 2022-06-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("change_version_log")
public class ChangeVersionLog implements Serializable {

    private static final long serialVersionUID=1L;

    private String id;

    private String data;

    private String dataChange;

    private Date createTime;

    private String tableId;

    private String tableName;

    private String result;

    private String parameter;


}
