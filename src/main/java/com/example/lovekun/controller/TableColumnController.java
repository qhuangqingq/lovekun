package com.example.lovekun.controller;


import cn.hutool.json.JSONObject;
import com.example.lovekun.config.mycode.AbExcept;
import com.example.lovekun.config.mycode.CodeEnum;
import com.example.lovekun.constant.DataTypeEnum;
import com.example.lovekun.entity.TableColumnHis;
import com.example.lovekun.entity.TableColumns;
import com.example.lovekun.entity.TableDomain;
import com.example.lovekun.service.ITableColumnsService;
import com.example.lovekun.vo.Data1;
import com.example.lovekun.vo.Data3;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
@CrossOrigin(allowedHeaders = "*")
public class TableColumnController {


    @Autowired
    ITableColumnsService service;



    /**
     *
     * @param
     * @return
     */
    @PostMapping("/insert")
    @ApiOperation(value = "插入表", notes = "插入表")
    public void insert(HttpServletRequest request, @RequestBody TableDomain tableDomain) {

        String database = request.getParameter("database");
        DataTypeEnum dataTypeEnum;
        try {
            dataTypeEnum= DataTypeEnum.valueOf(database.toLowerCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw  new AbExcept(CodeEnum.unkon,"找不到枚举对应的数据库");
        }
        service.insert(tableDomain.getList(),tableDomain,dataTypeEnum.getType());
//            int a=1;
    }

    /**
     *
     * @param
     * @return
     */
//    @PostMapping("updateColumn")
//    @ApiOperation(value = "更新表结构", notes = "更新表结构")
//    public HashMap<String,Object> updateColumn(@RequestBody List<JSONObject> objects ) {
//
//        List list=objects.stream().map(this::toTableColumns).collect(Collectors.toList());
//       return service.updateColumn(list);
//
//    }
    private TableColumns toTableColumns(JSONObject jsonObject){
        TableColumns tableColumns = jsonObject.toBean(TableColumns.class);
        tableColumns.setId((String) jsonObject.getOrDefault("doId",null));
        return tableColumns;
    }

    /**
     *
     * @param
     * @return
     */
    @ApiOperation(value = "更新表名或表注释", notes = "更新表名或表注释")
    @PostMapping("update")

    public void update(@RequestBody TableDomain tableDomain) {
//            int a=1;
        service.updateTable(tableDomain.getList(),tableDomain);
    }


    /**
     *
     * @param
     * @return
     */
    @PostMapping("addVersion")
    @ApiOperation(value = "新增版本", notes = "新增版本")
    public HashMap<String,Object> addVersion(@RequestBody List<TableColumnHis> tableColumnHis ) {

        return service.addVersion(tableColumnHis);

    }

    /**
     *
     * @param
     * @return
     */
    @PostMapping(value = "uploadFile",consumes = "multipart/*",headers = "content-type=multipart/form-date")
    @ApiOperation(value = "上传文件", notes = "上传文件")
    public HashMap<String,Object> uploadFile(HttpServletRequest request, @RequestPart("file")MultipartFile multipartFile
                                             ) {
        String type="file";
        return service.uploadFile(request, multipartFile, type);

    }

    /**
     *
     * @param
     * @return
     */
    @PostMapping(value = "uploadPic",consumes = "multipart/*",headers = "content-type=multipart/form-date")
    @ApiOperation(value = "上传图片", notes = "上传图片")
    public HashMap<String,Object> uploadPic(HttpServletRequest request, @RequestPart("file")MultipartFile multipartFile) {
        String type="pic";
        return service.uploadFile(request, multipartFile, type);
    }


    @GetMapping(value = "getDatabases")
    @ApiOperation(value = "获取数据库", notes = "获取数据库")
    public HashMap<String,Object> getDatabases() {
         List<String> list=new ArrayList<>();
         String a="esb8";
         String b="di70";
        list.add(a);
        list.add(b);
        HashMap<String,Object> map=new HashMap<>();
        map.put("list",list);
        return map;
    }

    @PostMapping(value = "getData")
    @ApiOperation(value = "获取公交", notes = "获取公交")
    public void getData(@RequestBody Data1 data) {
        List<Data3> poi_list = data.getEntrances();
        for (Data3 vo:poi_list
             ) {
            String[] split = vo.getLocation().split(",");
            vo.setLatitude(split[0]);
            vo.setLongitude(split[1]);
            vo.setAddress1(vo.getAddress());
            vo.insert();
        }

    }


}

