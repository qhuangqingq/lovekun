package com.example.lovekun.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@MapperScan(basePackages = "com.example.lovekun.dao",sqlSessionFactoryRef = "SqlSessionFactory")
public class DataSourceConfig {

    /**
     * 数据源1
     * spring.datasource：application.properteis中对应属性的前缀
     * @return
     */
    @Bean(name = "di70")
    @ConfigurationProperties(prefix = "spring.datasource.di70")
    public DataSource dataSourceOne() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 数据源2
     * spring.datasource.esb8：application.properteis中对应属性的前缀
     * @return
     */
    @Bean(name = "esb8")
    @ConfigurationProperties(prefix = "spring.datasource.esb8")
    public DataSource dataSourceTwo() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 动态数据源: 通过AOP在不同数据源之间动态切换
     * @return
     */
//    @Primary
//    @Bean(name = "dynamicDataSource")
//    public DataSource dynamicDataSource() {
//        DynamicDataSource dynamicDataSource = new DynamicDataSource();
//        // 默认数据源
//        dynamicDataSource.setDefaultTargetDataSource(dataSourceOne());
//        // 配置多数据源
//        Map<Object, Object> dsMap = new HashMap<>();
//        dsMap.put("di70", dataSourceOne());
//        dsMap.put("esb8", dataSourceTwo());
//
//        dynamicDataSource.setTargetDataSources(dsMap);
//        return dynamicDataSource;
//    }

    /**
     * 将动态代理数据源对象放入Spring容器中
     */
    @Bean(name = "dynamicDataSource")
    public DynamicDataSource dynamicDataSource(@Qualifier("di70") DataSource primaryDataSource,
                                               @Qualifier("esb8") DataSource secondaryDataSource) {
        // 这个地方是比较核心的targetDataSource 集合是我们数据库和名字之间的映射
        Map<Object, Object> targetDataSource = new HashMap<>();
        targetDataSource.put("di70", primaryDataSource);
        targetDataSource.put("esb8", secondaryDataSource);

        DynamicDataSource dataSource = new DynamicDataSource();
        // 设置所有的数据源
        dataSource.setTargetDataSources(targetDataSource);
        // 设置默认使用的数据源对象
        dataSource.setDefaultTargetDataSource(primaryDataSource);

        return dataSource;
    }

    /**
     * 配置多数据源后IOC中存在多个数据源了，事务管理器需要重新配置，不然器不知道选择哪个数据源
     * 事务管理器此时管理的数据源将是动态数据源dynamicDataSource
     * 配置@Transactional注解
     * @return
     */
//    @Bean
//    public PlatformTransactionManager transactionManager() {
//        return new DataSourceTransactionManager(dynamicDataSource());
//    }



    @Bean(name = "SqlSessionFactory")
    public SqlSessionFactory SqlSessionFactory(@Qualifier("dynamicDataSource") DataSource dynamicDataSource)
            throws Exception {

        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean ();
        sqlSessionFactoryBean.setDataSource(dynamicDataSource);
        //也是改成pojo路径（pojo实体类）
        sqlSessionFactoryBean.setTypeAliasesPackage("com.example.lovekun.entity");
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        sqlSessionFactoryBean.setConfiguration(configuration);
//        sqlSessionFactoryBean.setTransactionFactory(new ManagedTransactionFactory());
        sqlSessionFactoryBean.setTransactionFactory(new SpringManagedTransactionFactory());
        return sqlSessionFactoryBean.getObject();
    }
}
