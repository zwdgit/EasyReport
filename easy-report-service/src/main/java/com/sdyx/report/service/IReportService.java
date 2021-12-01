package com.sdyx.report.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Map;

public interface IReportService {

    IPage<Map<String, Object>> selectPageList(Long reportId, Map<String, Object> queryParams);

}
