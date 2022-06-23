package com.example.lovekun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.lovekun.entity.TableColumnHis;
import com.example.lovekun.entity.TableColumns;
import com.example.lovekun.entity.TableDomain;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
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

    HashMap<String,Object>  getTableDataByName(String  tableName, Integer limit, Integer offset);

    void insertData(Map<String, Object> map);

    HashMap<String,Object> updateColumn(List<TableColumns> list1);

    void changeVersion(List<TableColumnHis> columnHis, TableDomain byId, List<TableColumns> list);

    void updateData(Map<String, Object> map);

    void deleteData(Map<String, Object> map);

    HashMap<String, Object> addVersion(List<TableColumnHis> tableColumnHis);

    HashMap<String, Object> uploadFile(HttpServletRequest request, MultipartFile multipartFile, String type);
}
