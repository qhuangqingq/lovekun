package com.example.lovekun.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.lovekun.entity.TableDomain;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hq
 * @since 2022-06-09
 */
public interface TableDomainDao extends BaseMapper<TableDomain> {

    @Select("select * from ${tableName} limit ${limit} offset ${offset}")
    List<Map<String,Object>> getTableDataByName(@Param("tableName")String tableName,
                                                @Param("limit") Integer limit,
                                                @Param("offset")Integer offset
                                                );
}
