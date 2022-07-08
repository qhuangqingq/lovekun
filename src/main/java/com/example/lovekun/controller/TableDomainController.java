package com.example.lovekun.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.lovekun.config.mycode.AbExcept;
import com.example.lovekun.config.mycode.CodeEnum;
import com.example.lovekun.constant.DataTypeEnum;
import com.example.lovekun.entity.ChangeVersionLog;
import com.example.lovekun.entity.TableColumnHis;
import com.example.lovekun.entity.TableColumns;
import com.example.lovekun.entity.TableDomain;
import com.example.lovekun.entity.TableDomainHis;
import com.example.lovekun.service.IChangeVersionLogService;
import com.example.lovekun.service.ITableColumnHisService;
import com.example.lovekun.service.ITableColumnsService;
import com.example.lovekun.service.ITableDomainHisService;
import com.example.lovekun.service.ITableDomainService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
@RequestMapping(value = "/table-domain",produces = MediaType.ALL_VALUE,consumes = MediaType.ALL_VALUE)
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

    @Autowired
    private IChangeVersionLogService logService;

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

    public  List<TableColumnHis>  getByTableIdAndVersion(String  id,Integer version,String database) {
        LambdaQueryWrapper<TableColumnHis> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(TableColumnHis::getTableId,id).eq(TableColumnHis::getVersion,version);
        List<TableColumnHis> list = columnHisService.list(queryWrapper);

        return list;
    }
    @ApiOperation(value = "根据表名查出表数据", notes = "根据表名查出表数据")
    @GetMapping("getByTableName")
    public   HashMap<String,Object> getByTableName(String  tableName,Integer limit,Integer offset) {
        HashMap<String,Object> map =columnsService.getTableDataByName(tableName,limit,offset);
        return map;
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

    @ApiOperation(value = "更新表数据", notes = "更新表数据")
    @PostMapping("updateData")
    public  void updateData(@RequestBody Map<String,Object> map) {

        columnsService.updateData(map);

    }

    @ApiOperation(value = "删除表数据", notes = "删除表数据")
    @PostMapping("deleteData")
    public  void deleteData(@RequestBody Map<String,Object> map) {

        columnsService.deleteData(map);

    }


    @ApiOperation(value = "根据表id获取表的所有版本", notes = "根据表id获取表的所有版本")
    @GetMapping("getAllVersion")
    public  List<TableDomainHis> getAllVersion(String id) {
        LambdaQueryWrapper<TableDomainHis> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(TableDomainHis::getDoId,id).orderByDesc(TableDomainHis::getVersion);
        List<TableDomainHis> list = domainHisService.list(queryWrapper);
        return list;
    }

    @ApiOperation(value = "切换版本", notes = "切换版本")
    @PostMapping("changeVersion")
    public  void changeVersion(HttpServletRequest request, @RequestBody TableColumnHis his) {
        String database = request.getParameter("database");
        DataTypeEnum dataTypeEnum;
        try {
            dataTypeEnum= DataTypeEnum.valueOf(database.toLowerCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw  new AbExcept(CodeEnum.unkon,"找不到枚举对应的数据库");
        }

        ChangeVersionLog log=new ChangeVersionLog();
        log.setId(UUID.randomUUID().toString()).setCreateTime(new Date()).setTableId(his.getTableId()).setParameter(his.toString());
        try {
            LambdaQueryWrapper<TableColumnHis> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(TableColumnHis::getTableId,his.getTableId()).eq(TableColumnHis::getVersion,his.getVersion());
            List<TableColumnHis> columnHis = columnHisService.list(queryWrapper);
            TableDomain byId = null;
            List<TableColumns> list=null;
            if(Optional.ofNullable(his.getTableId()).isPresent()) {
                byId = domainService.getById(his.getTableId());
            }
            log.setDataChange(columnHis.toString());
            if (byId!=null) {
                LambdaQueryWrapper<TableColumns> queryWrapper1=new LambdaQueryWrapper<>();
                queryWrapper1.eq(TableColumns::getTableId,his.getTableId());
                list = columnsService.list(queryWrapper1);
                log.setData(list.toString()).setTableName(byId.getTableName());
            }
            columnsService.changeVersion(columnHis,byId,list,dataTypeEnum.getType());
            log.setResult("成功");
        } catch (Exception e) {
            log.setResult("失败："+e.getMessage());
            throw new AbExcept(CodeEnum.unkon,e.getMessage());
        }finally {
            logService.save(log);
        }


    }

    @ApiOperation(value = "根据表id获取表的当前使用版本", notes = "根据表id获取表的当前使用版本")
    @GetMapping("getVersionById")
    public  HashMap<String,Object> getVersionById(String id) {
        TableDomain byId = domainService.getById(id);
        HashMap<String,Object> map=new HashMap<>();
        map.put("version",byId.getVersion());
        return map;
    }


    @ApiOperation(value = "测试切换数据源", notes = "测试切换数据源")
    @GetMapping("/{type}/testDataSource")
    public  List<TableDomainHis> testDataSource(@PathVariable("type")String type, String id) {

        LambdaQueryWrapper<TableDomainHis> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(TableDomainHis::getDoId,id).orderByDesc(TableDomainHis::getVersion);
        List<TableDomainHis>  list = domainHisService.list(queryWrapper);
        List<TableDomain> list1 = domainService.list();
        return list;
//        String database=type;
//        DataTypeEnum dataTypeEnum = DataTypeEnum.valueOf(database);
//        return null;
    }
}

