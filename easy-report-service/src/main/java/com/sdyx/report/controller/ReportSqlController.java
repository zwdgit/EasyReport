package com.sdyx.report.controller;

import com.sdyx.common.core.controller.BaseController;
import com.sdyx.common.core.domain.AjaxResult;
import com.sdyx.common.core.page.TableDataInfo;
import com.sdyx.report.domain.bo.ParseSqlBo;
import com.sdyx.report.domain.ReportSql;
import com.sdyx.report.domain.bo.ReportSqlBo;
import com.sdyx.report.service.IReportSqlColumnService;
import com.sdyx.report.service.IReportSqlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/getReportSqlColumn")
    public AjaxResult getReportSqlColumn(@RequestParam Long id) {
        return AjaxResult.success(reportSqlService.getReportSqlColumn(id));
    }

}
