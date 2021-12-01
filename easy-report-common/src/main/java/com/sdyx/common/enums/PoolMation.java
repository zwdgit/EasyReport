package com.sdyx.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 连接池类型
 */
public enum PoolMation {

    DRUID("DRUID", "druid", "com.sdyx.report.sql.dbpool.impl.DruidDataSourcePool", "{\"initialSize\":3,\"maxActive\":20,\"minIdle\":1,\"maxWait\":60000,\"timeBetweenEvictionRunsMillis\":60000,\"minEvictableIdleTimeMillis\":300000,\"testWhileIdle\":true,\"testOnBorrow\":false,\"testOnReturn\":false,\"maxOpenPreparedStatements\":20,\"removeAbandoned\":true,\"removeAbandonedTimeout\":1800,\"logAbandoned\":true}");

    private String title;
    private String type;
    private String poolClass;
    private String options;

    PoolMation(String title, String type, String poolClass, String options) {
        this.title = title;
        this.type = type;
        this.poolClass = poolClass;
        this.options = options;
    }

    public static List<Map<String, Object>> getPoolMationList() {
        List<Map<String, Object>> beans = new ArrayList<>();
        for (PoolMation item : PoolMation.values()) {
            Map<String, Object> bean = new HashMap<>();
            bean.put("id", item.getType());
            bean.put("name", item.getTitle());
            bean.put("options", item.getOptions());
            beans.add(bean);
        }
        return beans;
    }

    public static String getPoolClassByType(String type) {
        for (PoolMation bean : PoolMation.values()) {
            if (bean.getType().equals(type)) {
                return bean.getPoolClass();
            }
        }
        return null;
    }

    public static String getTitleByPoolClass(String poolClass) {
        for (PoolMation bean : PoolMation.values()) {
            if (bean.getPoolClass().equals(poolClass)) {
                return bean.getTitle();
            }
        }
        return null;
    }

    public static String getTypeByPoolClass(String poolClass) {
        for (PoolMation bean : PoolMation.values()) {
            if (bean.getPoolClass().equals(poolClass)) {
                return bean.getType();
            }
        }
        return null;
    }

    public static String getOptionsByType(String type) {
        for (PoolMation bean : PoolMation.values()) {
            if (bean.getType().equals(type)) {
                return bean.getOptions();
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public String getPoolClass() {
        return poolClass;
    }

    public String getOptions() {
        return options;
    }

    public String getTitle() {
        return title;
    }
    }