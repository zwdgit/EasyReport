package com.sdyx.report.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.sdyx.common.core.controller.BaseController;
import com.sdyx.common.core.domain.AjaxResult;
import com.sdyx.common.core.page.TableDataInfo;
import com.sdyx.report.domain.Layout;
import com.sdyx.report.domain.ReportSql;
import com.sdyx.report.domain.bo.ParseSqlBo;
import com.sdyx.report.domain.bo.ReportSqlBo;
import com.sdyx.report.domain.bo.ReportSqlInfo;
import com.sdyx.report.service.IReportSqlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequestMapping("/report/sql")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReportSqlController extends BaseController {

    private final IReportSqlService reportSqlService;

    /**
     * 查询报表sql集合
     */
    @GetMapping("/list")
    public TableDataInfo list(ReportSql reportSql) {
        startPage();
        List<ReportSql> list = reportSqlService.selectReportSqlList(reportSql);
        return getDataTable(list);
    }

    /**
     * 新增
     */
    @PostMapping
    public AjaxResult addReportSql(@Validated @RequestBody ReportSqlBo reportSqlBo) {
        reportSqlService.addReportSql(reportSqlBo);
        return AjaxResult.success();
    }

    /**
     * 修改
     */
    @PutMapping
    public AjaxResult updateReportSql(@Validated @RequestBody ReportSqlBo reportSqlBo) {
        reportSqlService.updateReportSql(reportSqlBo);
        return AjaxResult.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public AjaxResult delReportSql(@PathVariable("id") Long id) {
        reportSqlService.delById(id);
        return AjaxResult.success();
    }

    /**
     * 获取数据库配置信息详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(reportSqlService.getById(id));
    }

    /**
     * 解析 SQL
     */
    @PostMapping("/parseSQLText")
    public AjaxResult parseSQLText(@Validated @RequestBody ParseSqlBo parseSqlBo) {
        return AjaxResult.success(reportSqlService.parseSQLText(parseSqlBo));
    }

    /**
     * 获取报表信息(主要包括列信息和布局配置)
     *
     * @param id
     * @return
     */
    @GetMapping("/getReportSqlInfo")
    public AjaxResult getReportSqlInfo(@RequestParam Long id) {
        ReportSqlInfo reportSqlInfo = new ReportSqlInfo();
        ReportSql reportSql = reportSqlService.getById(id);
        Assert.notNull(reportSql, "获取Sql信息异常");
        reportSqlInfo.setReportSqlColumns(reportSqlService.getReportSqlColumn(id));
        reportSqlInfo.setLayout(reportSql.getLayout());
        return AjaxResult.success(reportSqlInfo);
    }

    /**
     * 保存自定义布局
     *
     * @return
     */
    @PostMapping("/updateLayout/{reportId}")
    public AjaxResult updateLayout(@NotNull @PathVariable Long reportId,
                                   @RequestBody List<Layout> updateLayout) {
        Assert.isTrue(CollectionUtil.isNotEmpty(updateLayout), "更新布局参数缺失");
        ReportSql updateReportSql = ReportSql.builder().id(reportId).layout(updateLayout).build();
        reportSqlService.updateById(updateReportSql);
        return AjaxResult.success();
    }
}
