package com.example.lovekun.config.mybatisPlus;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;


public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 获取公共字段的值
        Object fieldValue = getFieldValByName("age", metaObject);
        // 判断是否为空,如果为空则进行填充
        if (fieldValue == null) {
            setFieldValByName("age", 66, metaObject);
        }
        setFieldValByName("is_delete", 0,metaObject);
    }

    /**
     * 修改填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        Object fieldValue = getFieldValByName("age", metaObject);
        if (fieldValue == null) {
            setFieldValByName("age", 66, metaObject);
        }
    }
}