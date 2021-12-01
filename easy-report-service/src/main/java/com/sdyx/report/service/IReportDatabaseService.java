package com.sdyx.report.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdyx.report.domain.ReportDatabase;

import java.util.List;

public interface IReportDatabaseService extends IService<ReportDatabase> {
    /**
     * 查询数据库配置信息
     *
     * @param id 数据库配置信息主键
     * @return 数据库配置信息
     */
    ReportDatabase selectReportDatabaseById(Long id);

    /**
     * 查询数据库配置信息(用户下拉框)
     * @return 数据库配置信息
     */
    List<ReportDatabase> selectReportDatabaseOptionsList();

    /**
     * 查询数据库配置信息列表
     *
     * @param reportDatabase 数据库配置信息
     * @return 数据库配置信息集合
     */
    List<ReportDatabase> selectReportDatabaseList(ReportDatabase reportDatabase);

    /**
     * 新增数据库配置信息
     *
     * @param reportDatabase 数据库配置信息
     * @return 结果
     */
    boolean insertReportDatabase(ReportDatabase reportDatabase);

    /**
     * 修改数据库配置信息
     *
     * @param reportDatabase 数据库配置信息
     * @return 结果
     */
    int updateReportDatabase(ReportDatabase reportDatabase);

    /**
     * 批量删除数据库配置信息
     *
     * @param ids 需要删除的数据库配置信息主键集合
     * @return 结果
     */
    int deleteReportDatabaseByIds(Long[] ids);

    /**
     * 删除数据库配置信息信息
     *
     * @param id 数据库配置信息主键
     * @return 结果
     */
    int deleteReportDatabaseById(Long id);

    /**
     * 连接测试
     *
     * @param reportDatabase 数据库配置
     */
    boolean testConnection(ReportDatabase reportDatabase);

    boolean connectionDataBase(final String driverClass, final String url, final String user,
                               final String password);

}
