package com.sdyx.report.domain.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ParseSqlBo {

    @NotBlank(message = "待解析SQL语句不能为空")
    private String querySql;

    @NotNull(message = "请先选择目标数据库")
    private Long databaseId;
}
