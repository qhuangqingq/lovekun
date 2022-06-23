package com.example.lovekun.config;

import com.example.lovekun.utils.DataSourceUtil;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源类
 * @date 2022/2/11
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceUtil.getDB();
    }
}
