package com.example.lovekun.utils;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CodeGeneratorUtil {
    public static void main(String[] args) {

        // 项目包路径
        String parent = "org.gocom.coframe";
        // 数据库连接地址
        String url = "jdbc:oracle:thin:@120.78.81.46:1521:orcl";
        // 数据库驱动
        String driver = "oracle.jdbc.driver.OracleDriver";
        // 用户名
        String user = "STGDB";
        // 密码
        String pwd = "STGDB";

        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 获取项目路径 这里只会获取到idea工作目录 例 C:\private-idea-workspace\5-21
        //String projectPath = System.getProperty("D:\\IdeaProjects\\gdceg\\server\\EOS_Microservices_5.1.0_CoFrame_Source");
        String projectPath = "D:\\work\\gzjz\\EOS_Microservices_5.1.0_CoFrame_Source";
                // 全局配置                      在这需要指定到java源码包目录
        setGlobalConfig(mpg, projectPath + "\\coframe-sync\\src\\main\\java");

        // 数据源配置
        setDataSourceConfig(mpg, url, driver, user, pwd);

        // 包配置
        PackageConfig pc = setPackageConfig(mpg, parent);

        // 自定义配置
        setInjectionConfig(mpg, projectPath);

        // 配置模板
        setTemplateConfig(mpg);

        // 策略配置
        setStrategyConfig(mpg, pc);

        mpg.execute();
    }


    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入" + tip + "：");
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotEmpty(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    /**
     * 全局配置
     *
     * @param mpg         {@link AutoGenerator}
     * @param projectPath 项目路径
     */
    private static void setGlobalConfig(AutoGenerator mpg, String projectPath) {
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir(projectPath);
        gc.setAuthor("hq");
        gc.setOpen(false);
        gc.setIdType(IdType.AUTO);
        gc.setFileOverride(true);
        gc.setDateType(DateType.TIME_PACK);
        gc.setEnableCache(false);
        // %s 为占位符 可以指定生成文件的结尾形式 (Po、Entity、Model...)
        gc.setEntityName("%s");
        gc.setMapperName("%sDao");
        gc.setXmlName("%sDao");
        gc.setServiceName("");
        gc.setServiceImplName("");
        gc.setControllerName("");
        // gc.setSwagger2(true); 实体属性 Swagger2 注解
        mpg.setGlobalConfig(gc);
    }

    /**
     * 数据源配置
     *
     * @param mpg    {@link AutoGenerator}
     * @param url    数据库连接地址
     * @param driver 数据库渠道
     * @param user   用户名
     * @param pwd    密码
     */
    private static void setDataSourceConfig(AutoGenerator mpg, String url, String driver, String user, String pwd) {
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(url);
        dsc.setDriverName(driver);
        dsc.setUsername(user);
        dsc.setPassword(pwd);
        // 设置数据库类型
        dsc.setDbType(DbType.ORACLE);
        mpg.setDataSource(dsc);
    }

    /**
     * 包配置
     *
     * @param mpg    {@link AutoGenerator}
     * @param parent 包路径
     */
    private static PackageConfig setPackageConfig(AutoGenerator mpg, String parent) {
        PackageConfig pc = new PackageConfig();
        String moduleName = scanner("模块名");
//        pc.setModuleName(moduleName);
        // 即  src/main/java  目录下的项目包路径
        pc.setParent(parent);
        pc.setController("controller." + moduleName);
        pc.setEntity("entity." + moduleName);
        pc.setService("service." + moduleName);
        pc.setServiceImpl("service." + moduleName + ".impl");
        pc.setMapper("dao.capital." + moduleName);
        /*pc.setController("controller");
        pc.setEntity("entity");
        pc.setService("service");
        pc.setServiceImpl("service.impl");
        pc.setMapper("dao.capital");*/
        mpg.setPackageInfo(pc);
        return pc;
    }

    /**
     * 自定义配置
     *
     * @param mpg         {@link AutoGenerator}
     * @param projectPath 项目路径
     */
    private static void setInjectionConfig(AutoGenerator mpg, String projectPath) {
        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 如果模板引擎是 freemarker
//        String templatePath = "/templates/mapper.xml.ftl";
        // 如果模板引擎是 velocity
        String templatePath = "/templates/mapper.xml.vm";

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        System.out.println("path1:"+projectPath);
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                System.out.println("path2:"+projectPath);
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "\\coframe-portal\\src\\main\\resources\\mapper\\capital\\"
                        + CamelUtil.underlineToCamel(tableInfo.getName() != null ? tableInfo.getName().toLowerCase().substring(1) : "") + "Mapper" + StringPool.DOT_XML;
            }
        });

        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);
    }

    /**
     * 配置模板
     *
     * @param mpg {@link AutoGenerator}
     */
    private static void setTemplateConfig(AutoGenerator mpg) {
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);
    }

    /**
     * 策略配置
     *
     * @param mpg {@link AutoGenerator}
     * @param pc  包配置 {@link PackageConfig}
     */
    private static void setStrategyConfig(AutoGenerator mpg, PackageConfig pc) {
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
//        strategy.setSuperEntityClass("你自己的父类实体,没有就不用设置!");
//         公共父类
//        strategy.setSuperControllerClass("你自己的父类控制器,没有就不用设置!");
        // 写于父类中的公共字段
        strategy.setSuperEntityColumns("id");
        strategy.setInclude(scanner("表名，多个英文逗号分割").split(","));
        strategy.setControllerMappingHyphenStyle(true);
        //strategy.setTablePrefix(pc.getModuleName() + "_");
        strategy.setTablePrefix(new String[] { "T_" });
        mpg.setStrategy(strategy);
    }
}
