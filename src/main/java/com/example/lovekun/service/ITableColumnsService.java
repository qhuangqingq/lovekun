package com.example.lovekun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.lovekun.entity.TableColumnHis;
import com.example.lovekun.entity.TableColumns;
import com.example.lovekun.entity.TableDomain;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hq
 * @since 2022-06-09
 */
public interface ITableColumnsService extends IService<TableColumns> {

    void insert(List<TableColumns> columns, TableDomain domain) ;

    void updateTable(List<TableColumns> list, TableDomain tableDomain);

    List<Map<String,Object>> getTableDataByName(String  tableName,Integer limit,Integer offset);

    void insertData(Map<String, Object> map);

    void updateColumn(List<TableColumns> list);

    void changeVersion(List<TableColumnHis> columnHis);
}
