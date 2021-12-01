package com.sdyx.report.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdyx.report.domain.ReportSqlColumn;
import com.sdyx.report.mapper.ReportSqlColumnMapper;
import com.sdyx.report.service.IReportSqlColumnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReportSqlColumnServiceImpl extends ServiceImpl<ReportSqlColumnMapper, ReportSqlColumn> implements IReportSqlColumnService {
}
