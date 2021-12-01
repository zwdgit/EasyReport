package com.sdyx.report.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sdyx.common.core.controller.BaseController;
import com.sdyx.common.core.page.TableDataInfo;
import com.sdyx.report.service.IReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReportController extends BaseController {

    private final IReportService reportService;

    /**
     * 查询数据库配置信息列表
     */
    @GetMapping("/list/{reportId}")
    public TableDataInfo list(@PathVariable Long reportId, @RequestParam Map<String, Object> params) {
        IPage<Map<String, Object>> pageList = reportService.selectPageList(reportId, params);
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setRows(pageList.getRecords());
        tableDataInfo.setTotal(pageList.getTotal());
        return tableDataInfo;
    }
}
