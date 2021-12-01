package com.sdyx.report.sql.dbpool;

public class DataSourcePoolFactory {
    public static DataSourcePoolWrapper create(final String className) {
        try {
            return (DataSourcePoolWrapper)Class.forName(className).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException("DataSourcePoolFactory Load Class Error", e);
        }
    }
}
