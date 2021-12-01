package com.sdyx.report.sql.query;

import com.sdyx.report.domain.ReportSqlColumn;

import java.util.List;
import java.util.Map;

public interface Queryer {
    /**
     * 从sql语句中解析出报表元数据列集合
     *
     * @param sqlText sql语句
     * @return 数据列集合
     */
    List<ReportSqlColumn> parseMetaDataColumns(String sqlText);

}
