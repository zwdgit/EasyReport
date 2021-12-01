package com.sdyx.report.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdyx.report.domain.ReportDatabase;
import com.sdyx.report.domain.ReportSql;
import com.sdyx.report.domain.ReportSqlColumn;
import com.sdyx.report.mapper.ReportMapper;
import com.sdyx.report.service.IReportDatabaseService;
import com.sdyx.report.service.IReportService;
import com.sdyx.report.service.IReportSqlService;
import com.sdyx.report.util.DbPoolUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReportServiceImpl implements IReportService {

    private final DbPoolUtils dbPoolUtils;
    private final ReportMapper reportMapper;
    private final IReportSqlService reportSqlService;
    private final IReportDatabaseService reportDatabaseService;

    @Override
    public IPage<Map<String, Object>> selectPageList(Long reportId, Map<String, Object> queryParams) {
        Page<Map<String, Object>> page = new Page<>();
        page.setCurrent(Long.parseLong(queryParams.getOrDefault("pageNum", "1").toString()));
        page.setSize(Long.parseLong(queryParams.getOrDefault("pageSize", "10").toString()));

        ReportSql reportSql = reportSqlService.getById(reportId);
        Assert.notNull(reportSql, "获取报表信息失败");
        Long databaseId = reportSql.getDatabaseId();
        ReportDatabase reportDatabase = reportDatabaseService.getById(databaseId);
        List<ReportSqlColumn> reportSqlColumn = reportSqlService.getReportSqlColumn(reportSql.getId());

        // 格式化查询 sql
        String sql = formatConditionQuerySql(reportSql, reportSqlColumn, queryParams);
        log.info("QuerySQL：" + sql);
        Set<String> datasources = dbPoolUtils.add(reportDatabase);
        log.info("当前数据源集合：" + JSONObject.toJSONString(datasources));
        // 切换数据源
        DynamicDataSourceContextHolder.push(reportDatabase.getName());
        IPage<Map<String, Object>> pageList = reportMapper.selectPageList(page, sql);
        DynamicDataSourceContextHolder.clear();
        return pageList;
    }

    /**
     * 格式化条件查询sql
     *
     * @param reportSql 报表sql
     * @param params    参数
     * @return
     */
    private String formatConditionQuerySql(ReportSql reportSql, List<ReportSqlColumn> reportSqlColumn, Map<String, Object> params) {
        // 移除无关参数
        params.remove("pageNum");
        params.remove("pageSize");
        if (params.keySet().size() > 0) {
            List<ReportSqlColumn> queryFields = reportSqlColumn.stream().filter(item -> "1".equals(item.getIsQuery())).collect(Collectors.toList());
            StringBuilder sqlBuilder = new StringBuilder();
            if (queryFields.size() > 0) {
                sqlBuilder.append(String.format("select * from ( %s ) as rt where 1 = 1", reportSql.getQuerySql()));
                queryFields.forEach(column -> {
                    String value = params.getOrDefault(column.getSqlField(), "").toString();
                    if (!StringUtils.isEmpty(value)) {
                        switch (column.getQueryType()) {
                            case "LIKE":
                                sqlBuilder.append(String.format(" and %s like '%%%s%%'", column.getSqlField(), value));
                                break;
                            case "EQ":
                                sqlBuilder.append(String.format(" and %s = '%s'", column.getSqlField(), value));
                                break;
                            case "BETWEEN":
                                String secValue = params.getOrDefault(column.getSqlField() + "SecValue", "").toString();
                                sqlBuilder.append(String.format(" and (%s between '%s' and '%s')", column.getSqlField(), value, secValue));
                                break;
                        }
                    }
                });
                return sqlBuilder.toString();
            }
            return reportSql.getQuerySql();
        } else {
            return reportSql.getQuerySql();
        }
    }

}
