package com.example.lovekun.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@TableName("data3")
public class Data3  extends Model<Data3>  {
    private static final long serialVersionUID=1L;

    private String longitude;

    private String latitude;

    private String name;

    @TableField(exist = false)
    private Object address;

    public void setAddress1(Object address1) {
        if(address1==null||address1 instanceof List) {
            this.address1=null;
            return;
        }
        this.address1= (String) address1;
    }

    private String address1;

    @TableField(exist = false)
    private String location;


}
