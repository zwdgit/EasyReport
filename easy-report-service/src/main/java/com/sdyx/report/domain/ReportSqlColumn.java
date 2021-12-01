package com.sdyx.report.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("report_sql_column")
public class ReportSqlColumn implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 绑定的sql
     */
    private Long sqlId;

    /**
     * sql查询字段
     */
    private String sqlField;

    /**
     * 列名称（用来回显）
     */
    private String columnName;

    /**
     * 是否列表字段（1是）
     */
    private String isList;

    /**
     * 是否导出字段（1是）
     */
    private String isExport;

    /**
     * 是否查询字段（1是）
     */
    private String isQuery;

    /**
     * 查询方式（等于、不等于、大于、大于等于、小于、小于等于、范围、模糊）
     */
    private String queryType;

    /**
     * 显示类型（文本框、下拉框、日期）
     */
    private String htmlType;

    /**
     * 字典类型
     */
    private String dictType;

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
