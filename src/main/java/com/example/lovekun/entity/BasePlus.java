package com.example.lovekun.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data

@Accessors(chain = true)
public class BasePlus<T extends Model> extends Model {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * TableLogic 指定删除表示
     */
    @TableLogic
    @TableField(value = "is_delete",fill = FieldFill.INSERT)
    @JsonIgnore
    private boolean isDelete;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private int age;

    @Override
    protected Serializable pkVal() {
        return this.id;

    }
}
