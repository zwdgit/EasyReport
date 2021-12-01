package com.sdyx.report.controller;

import com.sdyx.common.annotation.Log;
import com.sdyx.common.core.controller.BaseController;
import com.sdyx.common.core.domain.AjaxResult;
import com.sdyx.common.core.page.TableDataInfo;
import com.sdyx.common.enums.BusinessType;
import com.sdyx.common.enums.DataBaseMation;
import com.sdyx.common.enums.PoolMation;
import com.sdyx.report.domain.ReportDatabase;
import com.sdyx.report.service.IReportDatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据库配置信息Controller
 *
 * @author easy-report
 */
@RestController
@RequestMapping("/report/database")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReportDatabaseController extends BaseController {

    private final IReportDatabaseService reportDatabaseService;

    /**
     * 查询数据库配置信息列表
     */
    @GetMapping("/list")
    public TableDataInfo list(ReportDatabase reportDatabase) {
        startPage();
        List<ReportDatabase> list = reportDatabaseService.selectReportDatabaseList(reportDatabase);
        return getDataTable(list);
    }

    /**
     * 查询数据库配置信息列表
     */
    @GetMapping("/list/options")
    public AjaxResult listOptions() {
        List<ReportDatabase> list = reportDatabaseService.selectReportDatabaseOptionsList();
        return AjaxResult.success(list);
    }

    /**
     * 获取数据库配置信息详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(reportDatabaseService.selectReportDatabaseById(id));
    }

    /**
     * 连接测试
     */
    @PostMapping("/testConnection")
    public AjaxResult testConnection(@RequestBody ReportDatabase reportDatabase) {
        return toAjax(reportDatabaseService.testConnection(reportDatabase));
    }

    /**
     * 新增数据库配置信息
     */
    @Log(title = "数据库配置信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ReportDatabase reportDatabase) {
        return toAjax(reportDatabaseService.insertReportDatabase(reportDatabase));
    }

    /**
     * 修改数据库配置信息
     */
    @Log(title = "数据库配置信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ReportDatabase reportDatabase) {
        return toAjax(reportDatabaseService.updateReportDatabase(reportDatabase));
    }

    /**
     * 删除数据库配置信息
     */
    @Log(title = "数据库配置信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(reportDatabaseService.deleteReportDatabaseByIds(ids));
    }

    /**
     * 获取数据库类型
     */
    @GetMapping("/getDataType")
    public AjaxResult getDataType() {
        return AjaxResult.success(DataBaseMation.getDataBaseMationList());
    }

    /**
     * 获取连接池类型
     */
    @GetMapping("/getPoolType")
    public AjaxResult getPoolType() {
        return AjaxResult.success(PoolMation.getPoolMationList());
    }

}
