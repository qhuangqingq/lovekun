package com.example.lovekun.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.lovekun.entity.Teacher;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author huangqing
 * @since 2020-11-12
 */
public interface TeacherMapper extends BaseMapper<Teacher> {

    @Select({"<script>",
            "select c.* from T_COM_FUNCTION c where (c.ID in ",
            "(select f.FUNCTION_ID from T_PERSION_FUNCTION f where f.USER_ID=#{code} ) or c.is_base=1)",
            "<when test='functionType!=null'>",
            " and  c.FUNCTION_TYPE=#{functionType}",
            "</when>",
            "<when test='systemType!=null'>",
            " and  c.SYSTEM_TYPE=#{systemType}",
            "</when>",
            " and c.IS_DELETE=0 order by c.SORT desc",
            "</script>"})
    List<Object> getList(@Param("code") String code, @Param("functionType") Integer functionType,
                         @Param("systemType")String systemType);
}
