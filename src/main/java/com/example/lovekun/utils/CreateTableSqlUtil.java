package com.example.lovekun.utils;

import com.example.lovekun.config.mycode.AbExcept;
import com.example.lovekun.config.mycode.CodeEnum;
import com.example.lovekun.entity.TableColumns;
import com.example.lovekun.entity.TableDomain;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public  class CreateTableSqlUtil {
    /**
     *
     * @param tableColumns 前端传过来的要新增的列信息
     * @param domain 前端传过来的要新增的表信息
     * @return
     */
    public static  String createTable(List<TableColumns> tableColumns, TableDomain domain){
        if((!Optional.ofNullable(domain).isPresent())|| StringUtils.isBlank(domain.getTableName().trim())){
            throw  new AbExcept(CodeEnum.unkon,"表名不能为空");
        }

        String typeDabase="mysql";
        StringBuffer sql = new StringBuffer("create table "+domain.getTableName() +"( id VARCHAR(255) not null,");
        for (TableColumns vo : tableColumns) {
            if("id".equals(vo.getColumnName().toLowerCase(Locale.ROOT))){
                throw new AbExcept(CodeEnum.unkon,"id列为系统默认新增数据列，请删除");
            }
            if((!Optional.ofNullable(vo).isPresent())|| StringUtils.isBlank(vo.getColumnName().trim())){
                throw  new AbExcept(CodeEnum.unkon,"列名不能为空");
            }
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
            if("mysql".equals(typeDabase.toLowerCase(Locale.ROOT))){
                sql.append(getMysqlTypeSql(fieldname, type, length, definitionV, businame)+",");
            }else if("dm".equals(typeDabase.toLowerCase(Locale.ROOT))){
                sql.append(getDMTypeSql(fieldname, type, length, definitionV, businame)+",");
            }

        }

        return getEnd(sql,tableColumns,domain,typeDabase);
    }


    /**
     *
     * @param list 数据库保存的表的所有列
     * @param tableColumn 需要修改的列信息
     * @param tableName 表名
     * @return
     */
    public static  String updateTable( List<TableColumns> list,TableColumns tableColumn,String tableName){
        String typeDabase="mysql";
        if(!Optional.ofNullable(tableColumn.getType()).isPresent()){
            throw new AbExcept(CodeEnum.unkon,"修改状态不能为空");
        }
        if(!Optional.ofNullable(tableColumn.getColumnName()).isPresent()){
            throw new AbExcept(CodeEnum.unkon,"列名不能为空");
        }
        StringBuffer buffer=new StringBuffer(" ALTER TABLE "+tableName);
        String fieldname=tableColumn.getColumnName();
        String columntType=tableColumn.getColumnType();
        Integer length=0;
        if(Optional.ofNullable(tableColumn.getColumnLength()).isPresent()){
            length= Integer.parseInt(tableColumn.getColumnLength());
        }
        String definitionV=  tableColumn.getColumnDefinition();
        String columnComment=  tableColumn.getColumnComment();
        if(Optional.ofNullable(tableColumn.getId()).isPresent()){
            //循环找到修改列对应的数据库列
            for (TableColumns vo:list) {
                if("update".equals(tableColumn.getType())){
                    if(vo.getId()==tableColumn.getId()){
                        buffer.append(" change "+vo.getColumnName()+" ");
                        if("mysql".equals(typeDabase.toLowerCase(Locale.ROOT))){
                            buffer.append(getMysqlTypeSql(fieldname, columntType, length, definitionV, columnComment));
                        }else if("dm".equals(typeDabase.toLowerCase(Locale.ROOT))){
                            buffer.append(getDMTypeSql(fieldname, columntType, length, definitionV, columnComment));
                            buffer.append(";");
                            buffer.append(" COMMENT ON COLUMN "+tableName+"."+vo.getColumnName()+" is '"+vo.getColumnComment()+"';");
                        }

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
            //没有id就是新增
            if("add".equals(tableColumn.getType())) {
                buffer.append(" add ");
                if("mysql".equals(typeDabase.toLowerCase(Locale.ROOT))){
                    buffer.append(getMysqlTypeSql(fieldname, columntType, length, definitionV, columnComment));
                }else if("dm".equals(typeDabase.toLowerCase(Locale.ROOT))){
                    buffer.append(getDMTypeSql(fieldname, columntType, length, definitionV, columnComment));
                    buffer.append(";");
                    buffer.append(" COMMENT ON COLUMN "+tableName+"."+fieldname+" is '"+columnComment+"';");
                }
            }else{
                throw new AbExcept(CodeEnum.unkon,"数据格式不正确");
            }
        }

        return buffer.toString();
    }


    private static String getMysqlTypeSql(String fieldname,String type,Integer length ,String definitionV,String businame){
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
                int definition = 0;
                if(Optional.ofNullable(definitionV).isPresent()){
                    definition=Integer.parseInt(definitionV);
                }
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

    private static String getDMTypeSql(String fieldname,String type,Integer length ,String definitionV,String businame){
        String sql="";
        if ("string".equals(type)) {
            if (length > 4000) {
                sql=fieldname + " VARCHAR(4000)";
            } else {
                sql=fieldname + " VARCHAR(" + length
                        + ") ";
            }
        } else if ("number".equals(type)) {
            if (length > 38) {
                length = 38;
            }
            if (definitionV == null || definitionV.length() == 0) {
                sql=fieldname + " numeric(" + length
                        + ") ";
            } else {
                int definition = 0;
                if(Optional.ofNullable(definitionV).isPresent()){
                    definition=Integer.parseInt(definitionV);
                }
                if (definition > 38) {
                    definition = 38;
                }
                sql=fieldname + " numeric(" + length + ","
                        + definition + ")  ";
            }
        } else if ("date".equals(type)) {
            sql=fieldname + " timestamp ";
        } else if ("file".equals(type) || "picture".equals(type)) { // 文件
            sql=fieldname + " VARCHAR(4000) ";
        }  else if ("int".equals(type)) { //
            sql=fieldname + " int(" + length
                    + ") ";
        }
        return sql;
    }

    private static String getEnd(StringBuffer sql,List<TableColumns> tableColumns, TableDomain domain,String type){
        StringBuffer creatTableSql = new StringBuffer();
        if (sql.toString().endsWith(",")) {
            creatTableSql.append(sql.toString().substring(0,
                    sql.toString().length() - 1)
                    + " ) ") ;
        }
        if("mysql".equals(type.toLowerCase(Locale.ROOT))){
            if (Optional.ofNullable(domain.getRemark()).isPresent()){
                creatTableSql.append(" COMMENT ='" +domain.getRemark()+"'");
            }
        }else if("dm".equals(type.toLowerCase(Locale.ROOT))){
            creatTableSql.append(";");
            creatTableSql.append("COMMENT ON TABLE "+domain.getTableName()+" is '"+domain.getRemark()+"';");
            for (TableColumns vo:tableColumns) {
                creatTableSql.append(" COMMENT ON COLUMN "+domain.getTableName()+"."+vo.getColumnName()+" is '"+vo.getColumnComment()+"';");
            }
        }

        return creatTableSql.toString();
    }
}
