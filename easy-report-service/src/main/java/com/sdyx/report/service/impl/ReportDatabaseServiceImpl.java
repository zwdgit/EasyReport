package com.sdyx.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdyx.common.enums.DataBaseMation;
import com.sdyx.common.exception.ServiceException;
import com.sdyx.common.utils.DateUtils;
import com.sdyx.common.utils.StringUtils;
import com.sdyx.report.domain.ReportDatabase;
import com.sdyx.report.mapper.ReportDatabaseMapper;
import com.sdyx.report.service.IReportDatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReportDatabaseServiceImpl extends ServiceImpl<ReportDatabaseMapper, ReportDatabase> implements IReportDatabaseService {

    private final ReportDatabaseMapper reportDatabaseMapper;

    /**
     * 查询数据库配置信息
     *
     * @param id 数据库配置信息主键
     * @return 数据库配置信息
     */
    @Override
    public ReportDatabase selectReportDatabaseById(Long id) {
        return this.getById(id);
    }

    @Override
    public List<ReportDatabase> selectReportDatabaseOptionsList() {
        return this.list(new LambdaQueryWrapper<ReportDatabase>()
                        .select(ReportDatabase::getId, ReportDatabase::getName));
    }

    /**
     * 查询数据库配置信息列表
     *
     * @param reportDatabase 数据库配置信息
     * @return 数据库配置信息
     */
    @Override
    public List<ReportDatabase> selectReportDatabaseList(ReportDatabase reportDatabase) {
        return this.list(new LambdaQueryWrapper<ReportDatabase>()
                .like(StringUtils.isNotBlank(reportDatabase.getName()),
                        ReportDatabase::getName, reportDatabase.getName()));
    }

    /**
     * 新增数据库配置信息
     *
     * @param reportDatabase 数据库配置信息
     * @return 结果
     */
    @Override
    public boolean insertReportDatabase(ReportDatabase reportDatabase) {
        reportDatabase.setCreateTime(DateUtils.getNowDate());
        return this.save(reportDatabase);
    }

    /**
     * 修改数据库配置信息
     *
     * @param reportDatabase 数据库配置信息
     * @return 结果
     */
    @Override
    public int updateReportDatabase(ReportDatabase reportDatabase) {
        reportDatabase.setUpdateTime(DateUtils.getNowDate());
        return reportDatabaseMapper.updateReportDatabase(reportDatabase);
    }

    /**
     * 批量删除数据库配置信息
     *
     * @param ids 需要删除的数据库配置信息主键
     * @return 结果
     */
    @Override
    public int deleteReportDatabaseByIds(Long[] ids) {
        return reportDatabaseMapper.deleteReportDatabaseByIds(ids);
    }

    /**
     * 删除数据库配置信息信息
     *
     * @param id 数据库配置信息主键
     * @return 结果
     */
    @Override
    public int deleteReportDatabaseById(Long id) {
        return reportDatabaseMapper.deleteReportDatabaseById(id);
    }

    @Override
    public boolean testConnection(ReportDatabase reportDatabase) {
        return connectionDataBase(DataBaseMation.getDricerClassByType(reportDatabase.getDataType()),
                reportDatabase.getJdbcUrl(),
                reportDatabase.getUser(),
                reportDatabase.getPassword());
    }

    /**
     * 连接数据源
     *
     * @param driverClass 数据源驱动类
     * @param url         数据源连接字符串
     * @param user        用户名
     * @param password    密码
     * @return 是否正常连接
     */
    @Override
    public boolean connectionDataBase(final String driverClass, final String url, final String user,
                                      final String password) {
        Connection conn = null;
        try {
            Class.forName(driverClass);
            conn = DriverManager.getConnection(url, user, password);
            return true;
        } catch (final Exception e) {
            log.warn("testConnection", e);
            throw new ServiceException(e.getMessage());
        } finally {
            this.releaseConnection(conn);
        }
    }

    /**
     * 释放数据源
     *
     * @param conn
     */
    private void releaseConnection(final Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (final SQLException ex) {
                log.warn("测试数据库连接后释放资源失败", ex);
            }
        }
    }
}
