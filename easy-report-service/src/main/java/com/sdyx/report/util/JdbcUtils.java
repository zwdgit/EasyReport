package com.sdyx.report.util;

import com.sdyx.common.enums.DataBaseMation;
import com.sdyx.common.enums.PoolMation;
import com.sdyx.report.domain.ReportDatabase;
import com.sdyx.report.sql.dbpool.DataSourcePoolFactory;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class JdbcUtils {
    private static Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>(100);

    public static DataSource getDataSource(ReportDatabase reportDatabase) {
        // 用数据源 用户名, 密码, jdbcUrl 做为 key
        String key = String.format("%s|%s|%s", reportDatabase.getUser(), reportDatabase.getPassword(), reportDatabase.getJdbcUrl())
            .toLowerCase();
        DataSource dataSource = dataSourceMap.get(key);
        if (dataSource == null) {
            dataSource = DataSourcePoolFactory.create(PoolMation.getPoolClassByType(reportDatabase.getPoolType())).wrap(reportDatabase);
            dataSourceMap.put(key, dataSource);
        }
        return dataSource;
    }

    public static void releaseJdbcResource(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (final SQLException ex) {
            log.error("数据库资源释放异常", ex);
            throw new RuntimeException("数据库资源释放异常", ex);
        }
    }
}
