package com.example.lovekun.service.serviceImpl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lovekun.config.mycode.AbExcept;
import com.example.lovekun.config.mycode.CodeEnum;
import com.example.lovekun.dao.TableColumnDao;
import com.example.lovekun.dao.TableDomainDao;
import com.example.lovekun.entity.TableColumnHis;
import com.example.lovekun.entity.TableColumns;
import com.example.lovekun.entity.TableDomain;
import com.example.lovekun.entity.TableDomainHis;
import com.example.lovekun.service.ITableColumnHisService;
import com.example.lovekun.service.ITableColumnsService;
import com.example.lovekun.service.ITableDomainHisService;
import com.example.lovekun.service.ITableDomainService;
import com.example.lovekun.utils.DmSqlUtil;
import com.example.lovekun.utils.MysqlSqlUtil;
import com.example.lovekun.utils.UploadUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hq
 * @since 2022-06-09
 */
@Service
public class TableColumnsServiceImpl extends ServiceImpl<TableColumnDao, TableColumns> implements ITableColumnsService {



    @Autowired
    private ITableDomainHisService domainHisService;

    @Autowired
    private ITableDomainService domainService;
    @Autowired
    private ITableColumnHisService columnHisService;

    @Value(value = "${spring.datasource.di70.driver-class-name}")
    private String driver;

    @Value(value = "${spring.datasource.di70.jdbc-url}")
    private String url;

    @Value(value = "${spring.datasource.di70.username}")
    private String userName;

    @Value(value = "${spring.datasource.di70.password}")
    private String password;
    @Autowired
    private TableDomainDao domainDao;

    @Resource
    private UploadUtil uploadUtil;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insert(List<TableColumns> tableColumns, TableDomain domain,String database)  {
        TableColumns idColumn=new TableColumns();
        idColumn.setColumnComment("id").setColumnName("id").setColumnLength("50").setColumnType("string");
        domain.setVersion(1);
        try {
            Integer.parseInt(domain.getTableName());
            throw new AbExcept(CodeEnum.unkon,"表名不能全数字");
        } catch (NumberFormatException e) {

        }
        String sql = null;
        //生成执行sql
        if("mysql".equals(database.toLowerCase(Locale.ROOT))){
            sql = MysqlSqlUtil.createTable(tableColumns,domain);
        }else if("dm".equals(database.toLowerCase(Locale.ROOT))){
            sql= DmSqlUtil.createTable(tableColumns,domain);
        }

        try {
            List<String> sqlList=new ArrayList<>();
            sqlList.add(sql);
            //执行sql
            dosql(sqlList);
            tableColumns.add(idColumn);
            TableDomainHis domainHis=new TableDomainHis(domain);
            //插入表生成记录
            domain.setId(UUID.randomUUID().toString());
            try {
                domainService.save(domain);
            } catch (Exception e) {
                e.printStackTrace();
            }
            domainHis.setDoId(domain.getId());
            //插入表生成记录历史记录
            domainHisService.save(domainHis);

            //插入列生成记录及列生成历史记录
            List<TableColumnHis> hisList=new ArrayList<>();
            for (TableColumns vo:tableColumns) {
                vo.setTableId(domain.getId());
                vo.setVersion(domain.getVersion());
                vo.setId(UUID.randomUUID().toString());
                this.save(vo);
                hisList.add(new TableColumnHis(vo));
            }

            columnHisService.saveBatch(hisList);
        } catch (Exception throwables) {
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw  new AbExcept(CodeEnum.unkon,throwables.getMessage());
        }


    }

    @Override
    public void updateTable(List<TableColumns> list, TableDomain tableDomain) {
        updateDomain(tableDomain);

    }

    /**
     * 根据表明获取数据
     * @param tableName
     * @return
     */
    @Override
    public  HashMap<String,Object>  getTableDataByName(String  tableName,Integer limit,Integer offset) {
        int count=domainDao.getCount(tableName);
        List<Map<String,Object>> list = new ArrayList<>();
        if(count>0){
         list=domainDao.getTableDataByName(tableName,limit,offset);
        }
        HashMap<String,Object> map=new HashMap<>();
        map.put("dataList",list);
        map.put("count",count);
        return map;
    }

    /**
     * 插入数据接口
     * @param map
     */
    @Override
    public void insertData(Map<String, Object> map) {
        if(!Optional.ofNullable(map.get("tableId")).isPresent()){
            throw  new AbExcept(CodeEnum.unkon,"请输入tableId");
        }
        String tableId= String.valueOf(map.get("tableId"));
        TableDomain byId = domainService.getById(tableId);
        LambdaQueryWrapper<TableColumns> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(TableColumns::getTableId,tableId);
        List<TableColumns> list = this.list(queryWrapper);
        StringBuffer buffer=new StringBuffer("insert into "+byId.getTableName());
        StringBuffer columnSql=new StringBuffer("(id,");
        StringBuffer valueSql=new StringBuffer("('"+ UUID.randomUUID()+"',");
        for (TableColumns vo:list) {
            if(Optional.ofNullable(map.get(vo.getColumnName())).isPresent()){
                if("date".equals(vo.getColumnType())){
                    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                         df1.parse(map.get(vo.getColumnName()).toString());
                    } catch (ParseException e) {
                        throw  new AbExcept(CodeEnum.unkon,"请输入规范日期格式：yyyy-MM-dd HH:mm:ss");
                    }
                }
                valueSql.append("'"+map.get(vo.getColumnName())+"',");
                columnSql.append(vo.getColumnName()+",");

            }

        }
        buffer.append(columnSql.toString().substring(0,
                columnSql.toString().length() - 1)
                + " ) ") ;

        buffer.append("values") ;
        buffer.append(valueSql.toString().substring(0,
                valueSql.toString().length() - 1)
                + " ) ") ;
        List<String> sqlList=new ArrayList<>();
        sqlList.add(buffer.toString());
        dosql(sqlList);

    }

    /**
     * 更新表结构
     * @param list1
     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public HashMap<String,Object> updateColumn(List<TableColumns> list1) {
//        HashMap<String,Object> map=new HashMap<>();
//        String tableId = list1.get(0).getTableId();
//        map.put("tableId",tableId);
//        LambdaQueryWrapper<TableColumns> queryWrapper=new LambdaQueryWrapper();
//        queryWrapper.eq(TableColumns::getTableId,tableId);
//        List<TableColumns> list = this.list(queryWrapper);
//        TableDomain byId = domainService.getById(list1.get(0).getTableId());
//        List<String> sqlList=new ArrayList<>();
//        for (int i = 0; i < list1.size(); i++) {
//            TableColumns vo=list1.get(i);
//            if("id".equals(vo.getColumnName().toLowerCase(Locale.ROOT))){
//                throw new AbExcept(CodeEnum.unkon,"id为系统默认列，不能将列名改为id");
//            }
//            sqlList.add(getUpdateSql(list,vo,byId.getTableName()));
//        }
//
//        //修改数据库表结构
//        try {
//            dosql(sqlList);
//        } catch (Exception e) {
//            throw new AbExcept(CodeEnum.unkon,e.getMessage());
//        }
//
//        //记录修改操作
//        LambdaQueryWrapper<TableDomainHis> queryWrapper1=new LambdaQueryWrapper<>();
//        queryWrapper1.eq(TableDomainHis::getDoId,byId.getId()).orderByDesc(TableDomainHis::getVersion);
//        List<TableDomainHis> list2 = domainHisService.list(queryWrapper1);
//
//        int version=list2.get(0).getVersion()+1;
//        map.put("version",version);
//        byId.setVersion(version);
//        domainService.updateById(byId);
//        TableDomainHis domainHis=new TableDomainHis(byId);
//        domainHis.setId(UUID.randomUUID().toString());
//        domainHisService.save(domainHis);
//        for (int i = 0; i < list1.size(); i++) {
//            TableColumns vo=list1.get(i);
//            vo.setVersion(version);
//            TableColumnHis columnHis=new TableColumnHis(vo);
//            columnHis.setVersion(version);
//            if("add".equals(vo.getType())){
//                vo.setId(UUID.randomUUID().toString());
//                this.save(vo);
//                columnHis.setDoId(vo.getId());
//                columnHis.setId(UUID.randomUUID().toString());
//                columnHisService.save(columnHis);
//            }else if("delete".equals(vo.getType())){
//                this.removeById(vo.getId());
//            }else {
//                this.updateById(vo);
//                columnHis.setId(UUID.randomUUID().toString());
//                columnHisService.save(columnHis);
//            }
//        }
//        return map;
//    }

    /**
     * 切换版本
     * @param columnHisList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeVersion(List<TableColumnHis> columnHisList, TableDomain byId, List<TableColumns> list,String database) {
        Integer version=columnHisList.get(0).getVersion();
        List<String> sqlList=new ArrayList<>();
        List<String> sqlDeleteList=new ArrayList<>();
        //记录数据库原来就有的列
        List<String> idList=new ArrayList<>();
        //循环该版本列，数据表有这个列就更新列属性，没有就新增列
        if(columnHisList.size()>0){
            for (int i = 0; i < columnHisList.size(); i++) {
                TableColumnHis his=columnHisList.get(i);
                Boolean flag=Boolean.TRUE;
                for (int j = 0; j < list.size(); j++) {
                    TableColumns vo=list.get(j);
                    String voId=vo.getId();
                    if(vo.getId().equals(his.getDoId())||vo.getColumnName().equals(his.getColumnName())){
                        if((!vo.getColumnType().equals("number"))&&(his.getColumnType().equals("number"))){
                            throw new AbExcept(CodeEnum.unkon,vo.getColumnName()+"不能将"+vo.getColumnType()+"类型转成number类型");
                        }
                        flag=Boolean.FALSE;
                        int length=0;
                        if(Optional.ofNullable(his.getColumnLength()).isPresent()){
                            length=Integer.parseInt(his.getColumnLength());
                        }
                        idList.add(vo.getId());
                        StringBuffer buffer=new StringBuffer(" ALTER TABLE "+byId.getTableName()+" change "+vo.getColumnName()+" ");
                       if("mysql".equals(database.toLowerCase(Locale.ROOT))){
                           buffer.append(MysqlSqlUtil.getMysqlTypeSql(his.getColumnName(), his.getColumnType(), length, his.getColumnDefinition(), his.getColumnComment()));

                       }else if("dm".equals(database.toLowerCase(Locale.ROOT))){
                           buffer.append(DmSqlUtil.getDMTypeSql(his.getColumnName(), his.getColumnType(), length, his.getColumnDefinition()));
                           buffer.append(";");
                           buffer.append(" COMMENT ON COLUMN "+byId.getTableName()+"."+his.getColumnName()+" is '"+his.getColumnComment()+"';");

                       }
                       sqlList.add(buffer.toString());

                        //把his的属性copy给vo
                        BeanUtils.copyProperties(his,vo);
                        vo.setId(voId);
                        this.updateById(vo);
                    }
                }
                if(flag){
                    //数据库表不存在这个列，新增列
                    TableColumns doma=new TableColumns();
                    BeanUtils.copyProperties(his,doma);
                    doma.setId(UUID.randomUUID().toString());
                    this.save(doma);
                    //刷新列历史信息
                    his.setDoId(doma.getId());
                    columnHisService.updateById(his);
                    int length=0;
                    if(Optional.ofNullable(his.getColumnLength()).isPresent()){
                        length=Integer.parseInt(his.getColumnLength());
                    }
                    StringBuffer buffer=new StringBuffer(" ALTER TABLE "+byId.getTableName()+" add ");
                    if("mysql".equals(database.toLowerCase(Locale.ROOT))){
                        buffer.append(MysqlSqlUtil.getMysqlTypeSql(his.getColumnName(), his.getColumnType(), length, his.getColumnDefinition(), his.getColumnComment()));

                    }else if("dm".equals(database.toLowerCase(Locale.ROOT))){
                        buffer.append(DmSqlUtil.getDMTypeSql(his.getColumnName(), his.getColumnType(), length, his.getColumnDefinition()));
                        buffer.append(";");
                        buffer.append(" COMMENT ON COLUMN "+byId.getTableName()+"."+his.getColumnName()+" is '"+his.getColumnComment()+"';");

                    }
                    sqlList.add(buffer.toString());
                }
            }

        }
        //删除掉数据库中多余的字段
        for (int i = 0; i < list.size(); i++) {
            TableColumns vo=list.get(i);
            Boolean flag=Boolean.TRUE;
            for (int j = 0; j < idList.size(); j++) {
                String item=idList.get(j);
                if (vo.getId().toLowerCase(Locale.ROOT).equals(item.toLowerCase(Locale.ROOT))){
                    flag=Boolean.FALSE;
                }
            }
            if (flag){
                this.removeById(vo);
                StringBuffer buffer=new StringBuffer(" ALTER TABLE "+byId.getTableName()+" drop "+vo.getColumnName());
                sqlDeleteList.add(buffer.toString());
            }
        }
        //将删除sql操作放在前面，先执行删除
        sqlDeleteList.addAll(sqlList);


        //更新表信息
        byId.setVersion(version);
        domainService.updateById(byId);
        try {
            dosql(sqlDeleteList);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 更新表数据
     * @param map
     */
    @Override
    public void updateData(Map<String, Object> map) {
        if(StringUtils.isBlank(map.get("tableId").toString().trim())){
            throw  new AbExcept(CodeEnum.unkon,"请输入tableId");
        }
        if(StringUtils.isBlank(map.get("id").toString().trim())){
            throw  new AbExcept(CodeEnum.unkon,"请输入id");
        }
        String tableId= String.valueOf(map.get("tableId"));
        TableDomain byId = domainService.getById(tableId);
        LambdaQueryWrapper<TableColumns> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(TableColumns::getTableId,tableId);
        List<TableColumns> list = this.list(queryWrapper);
        StringBuffer buffer=new StringBuffer(" update "+byId.getTableName()+" set ");
        for (TableColumns vo:list) {
            if(Optional.ofNullable(map.get(vo.getColumnName())).isPresent()){
                if("date".equals(vo.getColumnType())){
                    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        df1.parse(map.get(vo.getColumnName()).toString());
                    } catch (ParseException e) {
                        throw  new AbExcept(CodeEnum.unkon,"请输入规范日期格式：yyyy-MM-dd HH:mm:ss");
                    }
                }
                buffer.append(vo.getColumnName() +" = '"+map.get(vo.getColumnName())+"',");

            }

        }
        String sql=buffer.toString().substring(0,
                buffer.toString().length() - 1)
                + " where id='" +map.get("id")+"'";

        List<String> sqlList=new ArrayList<>();
        sqlList.add(sql);
        dosql(sqlList);
    }

    /**
     * 删除表数据
     * @param map
     */
    @Override
    public void deleteData(Map<String, Object> map) {
        if(StringUtils.isBlank(map.get("tableId").toString().trim())){
            throw  new AbExcept(CodeEnum.unkon,"请输入tableId");
        }
        if(StringUtils.isBlank(map.get("id").toString().trim())){
            throw  new AbExcept(CodeEnum.unkon,"请输入id");
        }
        String tableId= String.valueOf(map.get("tableId"));
        TableDomain byId = domainService.getById(tableId);
        StringBuffer buffer=new StringBuffer(" delete from "+byId.getTableName()+"  where id='"+map.get("id")+"'");
        List<String> sqlList=new ArrayList<>();
        sqlList.add(buffer.toString());
        dosql(sqlList);
    }

    /**
     * 新增版本
     * @param tableColumnHisList
     * @return
     */
    @Override
    public HashMap<String, Object> addVersion(List<TableColumnHis> tableColumnHisList) {
        List<TableColumnHis> tableColumnHis = tableColumnHisList.stream().filter(e ->!"delete".equals(e.getType())).collect(Collectors.toList());
        HashMap<String,Object> map=new HashMap<>();
        String tableId = tableColumnHis.get(0).getTableId();
        map.put("tableId",tableId);
        TableDomain byId = domainService.getById(tableId);
        LambdaQueryWrapper<TableDomainHis> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.eq(TableDomainHis::getDoId,byId.getId()).orderByDesc(TableDomainHis::getVersion);
        List<TableDomainHis> list2 = domainHisService.list(queryWrapper1);
        int version=list2.get(0).getVersion()+1;
        TableDomainHis domainHis=new TableDomainHis(byId);
        domainHis.setId(UUID.randomUUID().toString());
        domainHis.setVersion(version);
        domainHisService.save(domainHis);

        for (int i = 0; i < tableColumnHis.size(); i++) {
            TableColumnHis vo=tableColumnHis.get(i);
            vo.setVersion(version);
            vo.setId(UUID.randomUUID().toString());
            columnHisService.save(vo);
        }

       map.put("version",version);

        return map;
    }

    @Override
    public HashMap<String, Object> uploadFile(HttpServletRequest request, MultipartFile multipartFile, String type) {
        HashMap<String,Object> map=new HashMap<>();
        try {
            String s = uploadUtil.uploadFile(request, multipartFile, type);
            map.put("url",s);
            return map;
        } catch (Exception e) {
            throw new AbExcept(CodeEnum.unkon,"上传失败："+e.getMessage());
        }

    }


//    /**
//     * 构造插入表sql
//     * @param tableColumns
//     * @param domain
//     * @return
//     */
//    private String getInsertSql(List<TableColumns> tableColumns, TableDomain domain){
//        StringBuffer sql = new StringBuffer("create table "+domain.getTableName().toString() +"( id varchar(50) not null,");
//        for (TableColumns vo : tableColumns) {
//            if("id".equals(vo.getColumnName().toLowerCase(Locale.ROOT))){
//                throw new AbExcept(CodeEnum.unkon,"id列为系统默认新增数据列，请删除");
//            }
//            String fieldname = vo.getColumnName().trim();
//            String type = vo.getColumnType();
//            String businame = vo.getColumnComment();
//            String lengthV = "38";
//            if (vo.getColumnLength() != null) {
//                lengthV = vo.getColumnLength() ;
//            }
//
//            String definitionV = "";
//            if (vo.getColumnDefinition() != null) {
//                definitionV = vo.getColumnDefinition() ;
//            }
//            if (lengthV == null || lengthV.length() == 0) {
//                lengthV = "38";
//            }
//            int length = Integer.parseInt(lengthV);
//            sql.append(getTypeSql(fieldname, type, length, definitionV, businame)+",");
//            // String comment = "comment on column " + tableName + "."
//            // + fieldname + " is '" + businame + "'";
//            // conments.add(comment);
//        }
//        String creatTableSql = null;
//        if (sql.toString().endsWith(",")) {
//            creatTableSql = sql.toString().substring(0,
//                    sql.toString().length() - 1)
//                    + " ) ";
//        }
//        if (Optional.ofNullable(domain.getRemark()).isPresent()){
//            creatTableSql+=" COMMENT ='" +domain.getRemark()+"'";
//        }
//        return creatTableSql;
//    }

//    private String getTypeSql(String fieldname,String type,Integer length ,String definitionV,String businame){
//        String sql="";
//        if ("string".equals(type)) {
//            if (length > 4000) {
//                sql=fieldname + " VARCHAR(4000) COMMENT '"
//                        + businame + "'";
//            } else {
//                sql=fieldname + " VARCHAR(" + length
//                        + ") COMMENT '" + businame + "'";
//            }
//        } else if ("number".equals(type)) {
//            if (length > 30) {
//                length = 30;
//            }
//            if (definitionV == null || definitionV.length() == 0) {
//                sql=fieldname + " numeric(" + length
//                        + ") COMMENT '" + businame + "'";
//            } else {
//                int definition = 0;
//                if(Optional.ofNullable(definitionV).isPresent()){
//                    definition=Integer.parseInt(definitionV);
//                }
//                if (definition > 30) {
//                    definition = 30;
//                }
//                sql=fieldname + " numeric(" + length + ","
//                        + definition + ")  COMMENT '" + businame + "'";
//            }
//        } else if ("date".equals(type)) {
//            sql=fieldname + " timestamp COMMENT '"
//                    + businame + "'";
//        } else if ("file".equals(type) || "picture".equals(type)) { // 文件
//            sql=fieldname + " VARCHAR(4000) COMMENT '"
//                    + businame + "'";
//        }  else if ("int".equals(type)) { //
//            sql=fieldname + " int(" + length
//                    + ") COMMENT '" + businame + "'";
//        } else{
//            if (length > 4000) {
//                sql=fieldname + " VARCHAR(4000) COMMENT '"
//                        + businame + "'";
//            } else {
//                sql=fieldname + " VARCHAR(" + length
//                        + ") COMMENT '" + businame + "'";
//            }
//        }
//        return sql;
//    }


//    /**
//     * 初始化生成表
//     * @param sql
//     * @param tableName
//     */
//    private void inittable( String sql,String tableName) throws Exception {
//
//        try {
//            //连接数据库
//            Class.forName(driver);
//
//            //测试url中是否包含useSSL字段，没有则添加设该字段且禁用
//            if( url.indexOf("?") == -1 ){
//                url = url + "?useSSL=false" ;
//            }
//            else if( url.indexOf("useSSL=false") == -1 || url.indexOf("useSSL=true") == -1 )
//            {
//                url = url + "&useSSL=false";
//            }
//            Connection conn = DriverManager.getConnection(url, userName, password);
//            Statement stat = conn.createStatement();
//            //获取数据库表名
//            ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null);
//
//            // 判断表是否存在，如果存在则什么都不做，否则创建表
//            try {
//                if( rs.next() ){
//                    return;
//                }
//                else{
//                    // 先判断是否纯在表名，有则先删除表在创建表
//        //          stat.executeUpdate("DROP TABLE IF EXISTS sys_admin_divisions;CREATE TABLE sys_admin_divisions("
//                    stat.executeUpdate(sql);
//                }
//            } catch (Exception throwables) {
//                throw throwables;
//            } finally {
//                // 释放资源
//                stat.close();
//                conn.close();
//            }
//        } catch (Exception e) {
//            throw e;
//        }
//
//    }

    /**
     * 更新表（domain）
     * @param
     * @param domain
     */
    private void updateDomain(TableDomain domain){
        TableDomain byId = domainService.getById(domain.getId());
        List<String> sqlList=new ArrayList<>();
        StringBuffer sql = new StringBuffer(" ALTER TABLE "+byId.getTableName() +" rename to " + domain.getTableName() +";");
        sqlList.add(sql.toString());
        StringBuffer sql1=new StringBuffer(" ALTER TABLE "+domain.getTableName() +" comment '" + domain.getRemark() +"';");
        sqlList.add(sql1.toString());
        dosql(sqlList);
        domainService.updateById(domain);
        domainHisService.save(new TableDomainHis(domain));
    }

//    /**
//     * 构造更新表字段sql
//     * @param list 原列集合
//     * @param tableColumn 要修改的表的列
//     * @return
//     */
//    private String getUpdateSql( List<TableColumns> list,TableColumns tableColumn,String tableName){
//
//        if(!Optional.ofNullable(tableColumn.getColumnName()).isPresent()){
//            throw new AbExcept(CodeEnum.unkon,"列名不能为空");
//        }
//        StringBuffer buffer=new StringBuffer(" ALTER TABLE "+tableName);
//        String fieldname=tableColumn.getColumnName();
//        String columntType=tableColumn.getColumnType();
//        Integer length=0;
//        if(Optional.ofNullable(tableColumn.getColumnLength()).isPresent()){
//            length= Integer.parseInt(tableColumn.getColumnLength());
//        }
//        String definitionV=  tableColumn.getColumnDefinition();
//        String columnComment=  tableColumn.getColumnComment();
//        if(Optional.ofNullable(tableColumn.getId()).isPresent()){
//            for (int i = 0; i < list.size(); i++) {
//                TableColumns vo=list.get(i);
//                if("delete".equals(tableColumn.getType())) {
//                    if(vo.getId().equals(tableColumn.getId())){
//                        buffer.append(" DROP "+tableColumn.getColumnName());
//                    }
//                }else{
//                    if(vo.getId().equals(tableColumn.getId())){
//                        buffer.append(" change "+vo.getColumnName()+" ");
//                        buffer.append(getTypeSql(fieldname, columntType, length, definitionV, columnComment));
//                    }
//                }
//            }
//        }else{
//             if("add".equals(tableColumn.getType())) {
//                    buffer.append(" add ");
//                    buffer.append(getTypeSql(fieldname, columntType, length, definitionV, columnComment));
//             }else{
//                 throw new AbExcept(CodeEnum.unkon,"数据格式不正确");
//             }
//        }
//
//        return buffer.toString();
//    }



    private void dosql(List<String> list){
        for (int i = 0; i <list.size() ; i++) {
            domainDao.dosql(list.get(i));
        }
    }
//    private void dosql(List<String> list){
//        try {
//            //连接数据库
//            Class.forName(driver);
//
//            //测试url中是否包含useSSL字段，没有则添加设该字段且禁用
//            if( url.indexOf("?") == -1 ){
//                url = url + "?useSSL=false" ;
//            }
//            else if( url.indexOf("useSSL=false") == -1 || url.indexOf("useSSL=true") == -1 )
//            {
//                url = url + "&useSSL=false";
//            }
//            Connection conn = DriverManager.getConnection(url, userName, password);
//            Statement stat = conn.createStatement();
//            try {
//                conn.setAutoCommit(false);
//                for (String sql:list) {
//                    stat.executeUpdate(sql);
//                }
//                conn.commit();
//            } catch (Exception e) {
//                conn.rollback();
//                System.out.println(e);
//                throw new AbExcept(CodeEnum.unkon,e.getMessage());
//            } finally {
//                // 释放资源
//                stat.close();
//                conn.close();
//            }
//        } catch (Exception e) {
//            throw new AbExcept(CodeEnum.unkon,e.getMessage());
//        }
//    }

}
