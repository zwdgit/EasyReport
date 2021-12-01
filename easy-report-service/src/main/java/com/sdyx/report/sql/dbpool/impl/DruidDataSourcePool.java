package com.sdyx.report.sql.dbpool.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONObject;
import com.sdyx.common.enums.DataBaseMation;
import com.sdyx.report.domain.ReportDatabase;
import com.sdyx.report.sql.dbpool.DataSourcePoolWrapper;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DruidDataSourcePool implements DataSourcePoolWrapper {

    @Override
    public DataSource wrap(ReportDatabase database) {
        try {
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setDriverClassName(DataBaseMation.getDricerClassByType(database.getDataType()));
            dataSource.setUrl(database.getJdbcUrl());
            dataSource.setUsername(database.getUser());
            dataSource.setPassword(database.getPassword());
            String optionsStr = database.getOptions();
            Map<String, Object> options = new HashMap<>();

            if (StringUtils.isNotEmpty(optionsStr)) {
                List<Map> optionsList = JSONObject.parseArray(optionsStr, Map.class);
                optionsList.forEach(bean -> options.put(bean.get("configKey").toString(), bean.get("configValue").toString()));
            }

            dataSource.setMaxActive(MapUtils.getInteger(options, "maxActive", 20));
            dataSource.setMinIdle(MapUtils.getInteger(options, "minIdle", 1));
            dataSource.setMaxWait(MapUtils.getInteger(options, "maxWait", 60000));
            dataSource.setTimeBetweenEvictionRunsMillis(
                    MapUtils.getInteger(options, "timeBetweenEvictionRunsMillis", 60000));
            dataSource.setMinEvictableIdleTimeMillis(
                    MapUtils.getInteger(options, "minEvictableIdleTimeMillis", 300000));
            dataSource.setTestWhileIdle(MapUtils.getBoolean(options, "testWhileIdle", true));
            dataSource.setTestOnBorrow(MapUtils.getBoolean(options, "testOnBorrow", false));
            dataSource.setTestOnReturn(MapUtils.getBoolean(options, "testOnReturn", false));
            dataSource.setMaxOpenPreparedStatements(
                    MapUtils.getInteger(options, "maxOpenPreparedStatements", 20));
            dataSource.setRemoveAbandoned(MapUtils.getBoolean(options, "removeAbandoned", true));
            dataSource.setRemoveAbandonedTimeout(
                    MapUtils.getInteger(options, "removeAbandonedTimeout", 1800));
            dataSource.setLogAbandoned(MapUtils.getBoolean(options, "logAbandoned", true));
            return dataSource;
        } catch (final Exception ex) {
            throw new RuntimeException("C3p0DataSourcePool Create Error", ex);
        }
    }

}
