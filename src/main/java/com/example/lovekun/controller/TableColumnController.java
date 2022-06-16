package com.example.lovekun.controller;


import com.example.lovekun.entity.TableColumns;
import com.example.lovekun.entity.TableDomain;
import com.example.lovekun.service.ITableColumnsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hq
 * @since 2022-06-09
 */
@Api(value = "tableTest接口")
@RestController
@RequestMapping("/table-column")
public class TableColumnController {


    @Autowired
    ITableColumnsService service;


    /**
     *
     * @param
     * @return
     */
    @PostMapping("insert")
    @ApiOperation(value = "插入表", notes = "插入表")
    public void insert(@RequestBody TableDomain tableDomain) {
//            int a=1;
        service.insert(tableDomain.getList(),tableDomain);
    }

    /**
     *
     * @param
     * @return
     */
    @PostMapping("updateColumn")
    @ApiOperation(value = "更新表结构", notes = "更新表结构")
    public void updateColumn(@RequestBody List<TableColumns> list) {
        service.updateColumn(list);

    }

    /**
     *
     * @param
     * @return
     */
    @PostMapping("update")

    public void update(@RequestBody TableDomain tableDomain) {
//            int a=1;
        service.updateTable(tableDomain.getList(),tableDomain);
    }
}

