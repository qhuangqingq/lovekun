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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Value(value = "${spring.datasource.driver-class-name}")
    private String driver;

    @Value(value = "${spring.datasource.url}")
    private String url;

    @Value(value = "${spring.datasource.username}")
    private String userName;

    @Value(value = "${spring.datasource.password}")
    private String password;
    @Autowired
    private TableDomainDao domainDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insert(List<TableColumns> tableColumns, TableDomain domain)  {
        domain.setVersion(1);
        try {
            Integer.parseInt(domain.getTableName());
            throw new AbExcept(CodeEnum.unkon,"表名不能全数字");
        } catch (NumberFormatException e) {

        }

        //生成执行sql
        String sql=getInsertSql(tableColumns,domain);
        //执行sql
        try {
            inittable(sql,domain.getTableName());

            TableDomainHis domainHis=new TableDomainHis(domain);
            //插入表生成记录
            domain.insert();
            domainHis.setDoId(domain.getId());
            //插入表生成记录历史记录
            domainHisService.save(domainHis);

            //插入列生成记录及列生成历史记录
            List<TableColumnHis> hisList=new ArrayList<>();
            for (TableColumns vo:tableColumns) {
                vo.setTableId(domain.getId());
                vo.setVersion(domain.getVersion());
                vo.insert();
                hisList.add(new TableColumnHis(vo));
            }
            columnHisService.saveBatch(hisList);
        } catch (Exception throwables) {
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw  new AbExcept(CodeEnum.unkon,"sql执行异常");
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
    public List<Map<String,Object>> getTableDataByName(String  tableName,Integer limit,Integer offset) {
        List<Map<String,Object>> list=domainDao.getTableDataByName(tableName,limit,offset);
        return list;
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
        Integer tableId= Integer.valueOf(String.valueOf(map.get("tableId")));
        TableDomain byId = domainService.getById(tableId);
        LambdaQueryWrapper<TableColumns> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(TableColumns::getTableId,tableId);
        List<TableColumns> list = this.list(queryWrapper);
        StringBuffer buffer=new StringBuffer("insert into "+byId.getTableName());
        StringBuffer columnSql=new StringBuffer("(");
        StringBuffer valueSql=new StringBuffer("(");
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
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateColumn(List<TableColumns> list1) {
        Integer tableId = list1.get(0).getTableId();
        LambdaQueryWrapper<TableColumns> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(TableColumns::getTableId,tableId);
        List<TableColumns> list = this.list(queryWrapper);
        TableDomain byId = null;
        if(Optional.ofNullable(list.get(0).getTableId()).isPresent()) {
             byId = domainService.getById(list.get(0).getTableId());
        }
        List<String> sqlList=new ArrayList<>();
        for (TableColumns vo:list1) {
            sqlList.add(getUpdateSql(list,vo,byId.getTableName()));
        }
        //修改数据库表结构
        try {
            dosql(sqlList);
        } catch (Exception e) {
            throw new AbExcept(CodeEnum.unkon,"修改失败");
        }

        //记录修改操作
        LambdaQueryWrapper<TableDomainHis> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.eq(TableDomainHis::getDoId,byId.getId()).orderByDesc(TableDomainHis::getVersion);
        List<TableDomainHis> list2 = domainHisService.list(queryWrapper1);
        int version=list2.get(0).getVersion()+1;


        byId.setVersion(version);
        domainService.updateById(byId);
        TableDomainHis domainHis=new TableDomainHis(byId);
        domainHisService.save(domainHis);
        for (TableColumns vo:list1) {
            vo.setVersion(version);
            TableColumnHis columnHis=new TableColumnHis(vo);
            if("update".equals(vo.getType())){
                this.updateById(vo);
                columnHisService.save(columnHis);
            }else if("add".equals(vo.getType())){
                vo.insert();
                columnHis.setDoId(vo.getId());
                columnHisService.save(columnHis);
            }else if("delete".equals(vo.getType())){
                this.removeById(vo.getId());
            }else {
                throw new AbExcept(CodeEnum.unkon,"数据列修改状态不正确");
            }
        }
    }

    /**
     * 切换版本
     * @param columnHisList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeVersion(List<TableColumnHis> columnHisList) {
        LambdaQueryWrapper<TableColumns> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(TableColumns::getTableId,columnHisList.get(0).getTableId());
        List<TableColumns> list = this.list(queryWrapper);
        TableDomain byId = null;
        if(Optional.ofNullable(list.get(0).getTableId()).isPresent()) {
            byId = domainService.getById(list.get(0).getTableId());
        }
        List<String> sqlList=new ArrayList<>();
        //记录数据库原来就有的列
        List<Integer> idList=new ArrayList<>();
        //循环该版本列，数据表有这个列就更新列属性，没有就新增列
        for (TableColumnHis his:columnHisList) {
            Boolean flag=Boolean.TRUE;
            for (TableColumns vo:list) {
                if(vo.getId()==his.getDoId()||vo.getColumnName().equals(his.getColumnName())){
                    flag=Boolean.FALSE;
                    idList.add(vo.getId());
                    StringBuffer buffer=new StringBuffer(" ALTER TABLE "+byId.getTableName()+" change "+vo.getColumnName()+" ");
                    buffer.append(getTypeSql(his.getColumnName(), his.getColumnType(), Integer.parseInt(his.getColumnLength()), his.getColumnDefinition(), his.getColumnComment()));
                    sqlList.add(buffer.toString());

                    BeanUtils.copyProperties(his,vo);
                    vo.setId(his.getDoId());
                    vo.updateById();
                }
            }
            if(flag){
                //数据库表不存在这个列，新增列
                TableColumns doma=new TableColumns();
                BeanUtils.copyProperties(his,doma);
                doma.setId(null);
                doma.insert();
                //刷新列历史信息
                his.setDoId(doma.getId());
                columnHisService.updateById(his);

                StringBuffer buffer=new StringBuffer(" ALTER TABLE "+byId.getTableName()+" add ");
                buffer.append(getTypeSql(his.getColumnName(), his.getColumnType(), Integer.parseInt(his.getColumnLength()), his.getColumnDefinition(), his.getColumnComment()));
                sqlList.add(buffer.toString());
            }
        }
        //删除掉数据库中多余的字段
        for (TableColumns vo:list) {
            Boolean flag=Boolean.TRUE;
            for (Integer item:idList) {
                if (vo.getId()==item){
                    flag=Boolean.FALSE;
                }
            }
            if (flag){
                vo.deleteById();
                StringBuffer buffer=new StringBuffer(" ALTER TABLE "+byId.getTableName()+" drop "+vo.getColumnName());
                sqlList.add(buffer.toString());
            }
        }

        //更新表信息
        byId.setVersion(columnHisList.get(0).getVersion());
        domainService.updateById(byId);
        try {
            dosql(sqlList);
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 构造插入表sql
     * @param tableColumns
     * @param domain
     * @return
     */
    private String getInsertSql(List<TableColumns> tableColumns, TableDomain domain){
        StringBuffer sql = new StringBuffer("create table "+domain.getTableName().toString() +"( ");
        for (TableColumns vo : tableColumns) {
            String fieldname = vo.getColumnName().trim();
            String type = vo.getColumnType();
            String businame = vo.getColumnComment();
            String lengthV = "38";
            if (vo.getColumnLength() != null) {
                lengthV = vo.getColumnLength() ;
            }

            String definitionV = "";
            if (vo.getColumnDefinition() != null) {
                definitionV = vo.getColumnDefinition() ;
            }
            if (lengthV == null || lengthV.length() == 0) {
                lengthV = "38";
            }
            int length = Integer.parseInt(lengthV);
            sql.append(getTypeSql(fieldname, type, length, definitionV, businame)+",");
            // String comment = "comment on column " + tableName + "."
            // + fieldname + " is '" + businame + "'";
            // conments.add(comment);
        }
        String creatTableSql = null;
        if (sql.toString().endsWith(",")) {
            creatTableSql = sql.toString().substring(0,
                    sql.toString().length() - 1)
                    + " ) ";
        }
        if (Optional.ofNullable(domain.getRamake()).isPresent()){
            creatTableSql+=" COMMENT ='" +domain.getRamake()+"'";
        }
        return creatTableSql;
    }

    private String getTypeSql(String fieldname,String type,Integer length ,String definitionV,String businame){
        String sql="";
        if ("string".equals(type)) {
            if (length > 4000) {
                sql=fieldname + " VARCHAR(4000) COMMENT '"
                        + businame + "'";
            } else {
                sql=fieldname + " VARCHAR(" + length
                        + ") COMMENT '" + businame + "'";
            }
        } else if ("number".equals(type)) {
            if (length > 38) {
                length = 38;
            }
            if (definitionV == null || definitionV.length() == 0) {
                sql=fieldname + " numeric(" + length
                        + ") COMMENT '" + businame + "'";
            } else {
                int definition = Integer.parseInt(definitionV);
                if (definition > 38) {
                    definition = 38;
                }
                sql=fieldname + " numeric(" + length + ","
                        + definition + ")  COMMENT '" + businame + "'";
            }
        } else if ("date".equals(type)) {
            sql=fieldname + " timestamp COMMENT '"
                    + businame + "'";
        } else if ("file".equals(type) || "picture".equals(type)) { // 文件
            sql=fieldname + " VARCHAR(4000) COMMENT '"
                    + businame + "'";
        }  else if ("int".equals(type)) { //
            sql=fieldname + " int(" + length
                    + ") COMMENT '" + businame + "'";
        }
        return sql;
    }


    /**
     * 初始化生成表
     * @param sql
     * @param tableName
     */
    private void inittable( String sql,String tableName) throws Exception {

        try {
            //连接数据库
            Class.forName(driver);

            //测试url中是否包含useSSL字段，没有则添加设该字段且禁用
            if( url.indexOf("?") == -1 ){
                url = url + "?useSSL=false" ;
            }
            else if( url.indexOf("useSSL=false") == -1 || url.indexOf("useSSL=true") == -1 )
            {
                url = url + "&useSSL=false";
            }
            Connection conn = DriverManager.getConnection(url, userName, password);
            Statement stat = conn.createStatement();
            //获取数据库表名
            ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null);

            // 判断表是否存在，如果存在则什么都不做，否则创建表
            try {
                if( rs.next() ){
                    return;
                }
                else{
                    // 先判断是否纯在表名，有则先删除表在创建表
        //          stat.executeUpdate("DROP TABLE IF EXISTS sys_admin_divisions;CREATE TABLE sys_admin_divisions("
                    stat.executeUpdate(sql);
                }
            } catch (Exception throwables) {
                throw throwables;
            } finally {
                // 释放资源
                stat.close();
                conn.close();
            }
        } catch (Exception e) {
            throw e;
        }

    }

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
        StringBuffer sql1=new StringBuffer(" ALTER TABLE "+domain.getTableName() +" comment '" + domain.getRamake() +"';");
        sqlList.add(sql1.toString());
        dosql(sqlList);
        domainService.updateById(domain);
        domainHisService.save(new TableDomainHis(domain));
    }

    /**
     * 构造更新表字段sql
     * @param list 原列集合
     * @param tableColumn 要修改的表的列
     * @return
     */
    private String getUpdateSql( List<TableColumns> list,TableColumns tableColumn,String tableName){

        if(!Optional.ofNullable(tableColumn.getType()).isPresent()){
            throw new AbExcept(CodeEnum.unkon,"修改状态不能为空");
        }
        if(!Optional.ofNullable(tableColumn.getColumnName()).isPresent()){
            throw new AbExcept(CodeEnum.unkon,"列名不能为空");
        }
        StringBuffer buffer=new StringBuffer(" ALTER TABLE "+tableName);
        String fieldname=tableColumn.getColumnName();
        String columntType=tableColumn.getColumnType();
        Integer length= Integer.parseInt(tableColumn.getColumnLength());
        String definitionV=  tableColumn.getColumnDefinition();
        String columnComment=  tableColumn.getColumnComment();
        if(Optional.ofNullable(tableColumn.getId()).isPresent()){
            for (TableColumns vo:list) {
                if("update".equals(tableColumn.getType())){
                    if(vo.getId()==tableColumn.getId()){
                        buffer.append(" change "+vo.getColumnName()+" ");
                        buffer.append(getTypeSql(fieldname, columntType, length, definitionV, columnComment));
                    }

                }else if("delete".equals(tableColumn.getType())) {
                    if(vo.getId()==tableColumn.getId()){
                        buffer.append(" DROP "+tableColumn.getColumnName());
                    }
                }else{
                    throw new AbExcept(CodeEnum.unkon,"数据格式不正确");
                }

            }
        }else{
             if("add".equals(tableColumn.getType())) {
                    buffer.append(" add ");
                    buffer.append(getTypeSql(fieldname, columntType, length, definitionV, columnComment));
             }else{
                 throw new AbExcept(CodeEnum.unkon,"数据格式不正确");
             }
        }

        return buffer.toString();
    }

    private void dosql(List<String> list){
        try {
            //连接数据库
            Class.forName(driver);

            //测试url中是否包含useSSL字段，没有则添加设该字段且禁用
            if( url.indexOf("?") == -1 ){
                url = url + "?useSSL=false" ;
            }
            else if( url.indexOf("useSSL=false") == -1 || url.indexOf("useSSL=true") == -1 )
            {
                url = url + "&useSSL=false";
            }
            Connection conn = DriverManager.getConnection(url, userName, password);
            Statement stat = conn.createStatement();

            // 判断表是否存在，如果存在则什么都不做，否则创建表
            try {
                for (String sql:list) {
                    stat.executeUpdate(sql);
                }
            } catch (Exception e) {
                throw new AbExcept(CodeEnum.unkon,"sql执行异常");
            } finally {
                // 释放资源
                stat.close();
                conn.close();
            }
        } catch (Exception e) {
            throw new AbExcept(CodeEnum.unkon,"sql执行异常");
        }
    }

}
