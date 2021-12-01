package com.sdyx.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sdyx.report.domain.ReportDatabase;

public interface ReportDatabaseMapper extends BaseMapper<ReportDatabase> {

    /**
     * 修改数据库配置信息
     *
     * @param reportDatabase 数据库配置信息
     * @return 结果
     */
    public int updateReportDatabase(ReportDatabase reportDatabase);

    /**
     * 删除数据库配置信息
     *
     * @param id 数据库配置信息主键
     * @return 结果
     */
    public int deleteReportDatabaseById(Long id);

    /**
     * 批量删除数据库配置信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteReportDatabaseByIds(Long[] ids);
}
