package com.sdyx.report.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdyx.common.exception.ServiceException;
import com.sdyx.common.utils.StringUtils;
import com.sdyx.report.domain.Layout;
import com.sdyx.report.domain.ReportDatabase;
import com.sdyx.report.domain.ReportSql;
import com.sdyx.report.domain.ReportSqlColumn;
import com.sdyx.report.domain.bo.ParseSqlBo;
import com.sdyx.report.domain.bo.ReportSqlBo;
import com.sdyx.report.mapper.ReportSqlMapper;
import com.sdyx.report.service.IReportDatabaseService;
import com.sdyx.report.service.IReportSqlColumnService;
import com.sdyx.report.service.IReportSqlService;
import com.sdyx.report.sql.query.Queryer;
import com.sdyx.report.sql.query.factory.QueryerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReportSqlServiceImpl extends ServiceImpl<ReportSqlMapper, ReportSql> implements IReportSqlService {

    private final IReportDatabaseService databaseService;
    private final IReportSqlColumnService sqlColumnService;

    @Override
    public List<ReportSql> selectReportSqlList(ReportSql reportSql) {
        return this.list(new LambdaQueryWrapper<ReportSql>()
                .like(StringUtils.isNotBlank(reportSql.getSqlName()),
                        ReportSql::getSqlName, reportSql.getSqlName()));

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addReportSql(ReportSqlBo reportSqlBo) {
        List<ReportSqlColumn> reportSqlColumn = reportSqlBo.getReportSqlColumn();
        if (CollectionUtil.isEmpty(reportSqlColumn)) {
            throw new ServiceException("查询列不能为空");
        }
        ArrayList<Layout> layoutList = getLayouts(reportSqlColumn);
        ReportSql reportSql = reportSqlBo.getReportSql();
        reportSql.setLayout(layoutList);
        boolean save = this.save(reportSql);
        if (save) {
            reportSqlColumn.forEach(col -> col.setSqlId(reportSql.getId()));
            sqlColumnService.saveBatch(reportSqlColumn);
        }
    }

    /**
     * 初始化查询列的布局
     *
     * @param reportSqlColumn
     * @return
     */
    private ArrayList<Layout> getLayouts(List<ReportSqlColumn> reportSqlColumn) {
        ArrayList<Layout> layoutList = CollectionUtil.newArrayList();
        int mx = 0;
        int my = 0;
        if (CollectionUtil.isNotEmpty(reportSqlColumn)) {
            int gridIdx = 0;
            for (ReportSqlColumn sqlColumn : reportSqlColumn) {
                if ("1".equals(sqlColumn.getIsQuery())) {
                    int x = gridIdx * 5;
                    int y = Double.valueOf(Math.floor(gridIdx / 6d)).intValue();
                    mx = Math.max(mx, gridIdx * 5);
                    my = Math.max(my, y);
                    layoutList.add(new Layout(sqlColumn.getSqlField(), x, y, 5, 1, 3));
                    gridIdx++;
                }
            }
        }
        // 添加搜索按钮和表格布局配置
        if (mx < 14) {
            my++;
        }
        Layout searchLayout = new Layout("#search", 19, my, 5, 1, 4);
        Layout tableLayout = new Layout("#table", mx, my + 2, 24, 9, 12);
        tableLayout.setMinH(9);
        layoutList.add(searchLayout);
        layoutList.add(tableLayout);
        return layoutList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReportSql(ReportSqlBo reportSqlBo) {
        List<ReportSqlColumn> reportSqlColumn = reportSqlBo.getReportSqlColumn();
        if (CollectionUtil.isEmpty(reportSqlColumn)) {
            throw new ServiceException("查询列不能为空");
        }
        ReportSql reportSql = reportSqlBo.getReportSql();
        // 初始化布局
        reportSql.setLayout(getLayouts(reportSqlColumn));
        boolean update = this.updateById(reportSql);
        if (update) {
            // 删除旧数据
            sqlColumnService.remove(new LambdaQueryWrapper<ReportSqlColumn>().eq(ReportSqlColumn::getSqlId, reportSql.getId()));
            reportSqlColumn.forEach(item -> item.setSqlId(reportSql.getId()));
            // 保存新数据
            sqlColumnService.saveBatch(reportSqlColumn);
        }
    }

    @Override
    public List<ReportSqlColumn> parseSQLText(ParseSqlBo parseSqlBo) {
        // 获取数据源信息
        ReportDatabase database = databaseService.getById(parseSqlBo.getDatabaseId());
        Assert.notNull(database, "查询目标数据库失败");
        // 解析字段
        Queryer queryer = QueryerFactory.create(database);
        return queryer.parseMetaDataColumns(parseSqlBo.getQuerySql());
    }

    @Override
    public List<ReportSqlColumn> getReportSqlColumn(Long id) {
        return sqlColumnService.list(new LambdaQueryWrapper<ReportSqlColumn>().eq(ReportSqlColumn::getSqlId, id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delById(Long id) {
        this.removeById(id);
        sqlColumnService.remove(new LambdaQueryWrapper<ReportSqlColumn>().eq(ReportSqlColumn::getSqlId, id));
    }
}
