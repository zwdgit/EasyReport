package com.sdyx.report.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface ReportMapper {

    IPage<Map<String, Object>> selectPageList(Page<Map<String, Object>> page, @Param("querySql") String querySql);
}
