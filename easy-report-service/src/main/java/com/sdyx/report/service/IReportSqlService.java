package com.sdyx.report.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdyx.report.domain.bo.ParseSqlBo;
import com.sdyx.report.domain.ReportSql;
import com.sdyx.report.domain.ReportSqlColumn;
import com.sdyx.report.domain.bo.ReportSqlBo;

import java.util.List;

public interface IReportSqlService extends IService<ReportSql> {
    /**
     * 查询报表sql集合
     *
     * @param reportSql
     * @return 报表查询sql集合
     */
    List<ReportSql> selectReportSqlList(ReportSql reportSql);

    /**
     * 新增报表sql
     * @param reportSqlBo
     */
    void addReportSql(ReportSqlBo reportSqlBo);
    /**
     * 修改报表sql
     * @param reportSqlBo
     */
    void updateReportSql(ReportSqlBo reportSqlBo);
    /**
     * 解析sql字段
     * @param parseSqlBo
     * @return
     */
    List<ReportSqlColumn> parseSQLText(ParseSqlBo parseSqlBo);

    /**
     * 获取某报表sql对应的查询列信息
     * @param id
     * @return
     */
    List<ReportSqlColumn> getReportSqlColumn(Long id);

    void delById(Long id);
}
