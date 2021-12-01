package com.sdyx.report.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据库配置信息对象 report_database
 *
 * @author easy-report
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("report_database")
public class ReportDatabase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 数据源名称
     */
    private String name;

    /**
     * 连接url
     */
    private String jdbcUrl;

    /**
     * 数据源登录用户名
     */
    private String user;

    /**
     * 数据源登录密码
     */
    private String password;

    /**
     * 数据源配置选项(JSON格式）
     */
    private String options;

    /**
     * 数据库类型
     */
    private String dataType;

    /**
     * 连接池类型
     */
    private String poolType;

    /**
     * 说明备注
     */
    private String comment;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updateTime;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;
}
