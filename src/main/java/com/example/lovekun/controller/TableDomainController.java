package com.example.lovekun.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.lovekun.entity.TableColumnHis;
import com.example.lovekun.entity.TableColumns;
import com.example.lovekun.entity.TableDomain;
import com.example.lovekun.entity.TableDomainHis;
import com.example.lovekun.service.ITableColumnHisService;
import com.example.lovekun.service.ITableColumnsService;
import com.example.lovekun.service.ITableDomainHisService;
import com.example.lovekun.service.ITableDomainService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hq
 * @since 2022-06-09
 */
@Api(value = "获取表数据接口")
@RestController
@RequestMapping("/table-domain")
@CrossOrigin(allowedHeaders = "*")
public class TableDomainController {
    @Autowired
    private ITableColumnsService columnsService;
    @Autowired
    private ITableColumnHisService columnHisService;
    @Autowired
    private ITableDomainService domainService;

    @Autowired
    private ITableDomainHisService domainHisService;

    @ApiOperation(value = "根据表id查出表结构", notes = "根据表id查出表结构")
    @GetMapping("getByTableId")

    public  List<TableColumns>  getByTableId(String  id) {
        LambdaQueryWrapper<TableColumns> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(TableColumns::getTableId,id);
        List<TableColumns> list = columnsService.list(queryWrapper);

        return list;
    }

    @ApiOperation(value = "根据表id和版本查出历史版本表结构", notes = "根据表id和版本查出历史版本表结构")
    @GetMapping("getByTableIdAndVersion")

    public  List<TableColumnHis>  getByTableIdAndVersion(String  id,Integer version) {
        LambdaQueryWrapper<TableColumnHis> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(TableColumnHis::getTableId,id).eq(TableColumnHis::getVersion,version);
        List<TableColumnHis> list = columnHisService.list(queryWrapper);

        return list;
    }
    @ApiOperation(value = "根据表表名查出表数据", notes = "根据表表名查出表数据")
    @GetMapping("getByTableName")
    public  List<Map<String,Object>>getByTableName(String  tableName,Integer limit,Integer offset) {

        List<Map<String,Object>> data=columnsService.getTableDataByName(tableName,limit,offset);
        return data;
    }
    @ApiOperation(value = "获取所有表", notes = "获取所有表")
    @PostMapping("getAllTable")
    public  List<TableDomain> getAllTable() {
        List<TableDomain> list = domainService.list();
        return list;
    }

    @ApiOperation(value = "插入表数据", notes = "插入表数据")
    @PostMapping("insertData")
    public  void insertData(@RequestBody Map<String,Object> map) {
        columnsService.insertData(map);

    }

    @ApiOperation(value = "获取表的所有版本", notes = "获取表的所有版本")
    @PostMapping("getAllVersion")
    public  List<TableDomainHis> getAllVersion(@RequestBody TableDomain domain) {
        LambdaQueryWrapper<TableDomainHis> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(TableDomainHis::getDoId,domain.getId());
        List<TableDomainHis> list = domainHisService.list(queryWrapper);
        return list;
    }

    @ApiOperation(value = "切换版本", notes = "切换版本")
    @PostMapping("changeVersion")
    public  void changeVersion(@RequestBody List<TableColumnHis> columnHis) {
        columnsService.changeVersion(columnHis);
    }
}

