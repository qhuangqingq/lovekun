package com.example.lovekun.config.mybatisPlus;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {

        this.setFieldValByName("age",66,metaObject);

        this.setFieldValByName("isDelete", false, metaObject);
    }

    /**
     * 修改填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("age",66,metaObject);
    }
}