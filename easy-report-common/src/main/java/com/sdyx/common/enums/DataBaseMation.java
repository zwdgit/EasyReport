package com.sdyx.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库类型
 */
public enum DataBaseMation {

    MYSQL("MySQL", "com.mysql.cj.jdbc.Driver", "com.sdyx.report.sql.query.impl.MySqlQueryer");

    private String type;
    private String driverClass;
    private String queryerClass;

    DataBaseMation(String type, String driverClass, String queryerClass) {
        this.type = type;
        this.driverClass = driverClass;
        this.queryerClass = queryerClass;
    }

    public static List<Map<String, Object>> getDataBaseMationList() {
        List<Map<String, Object>> beans = new ArrayList<>();
        for (DataBaseMation item : DataBaseMation.values()) {
            Map<String, Object> bean = new HashMap<>();
            bean.put("id", item.getType());
            bean.put("name", item.getType());
            bean.put("driverClass", item.getDriverClass());
            beans.add(bean);
        }
        return beans;
    }

    public static String getDricerClassByType(String type) {
        for (DataBaseMation bean : DataBaseMation.values()) {
            if (bean.getType().equals(type)) {
                return bean.getDriverClass();
            }
        }
        return null;
    }

    public static String getTypeByDricerClass(String driverClass) {
        for (DataBaseMation bean : DataBaseMation.values()) {
            if (bean.getDriverClass().equals(driverClass)) {
                return bean.getType();
            }
        }
        return null;
    }

    public static String getQueryerClassByType(String type) {
        for (DataBaseMation bean : DataBaseMation.values()) {
            if (bean.getType().equals(type)) {
                return bean.getQueryerClass();
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public String getQueryerClass() {
        return queryerClass;
    }
}
