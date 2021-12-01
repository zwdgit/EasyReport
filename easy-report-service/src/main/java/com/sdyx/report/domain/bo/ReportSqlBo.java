package com.sdyx.report.domain.bo;

import com.sdyx.report.domain.ReportSql;
import com.sdyx.report.domain.ReportSqlColumn;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ReportSqlBo {

    @NotNull(message = "报表sql基础信息不能为空")
    private ReportSql reportSql;
    @NotNull(message = "查询列不能为空")
    private List<ReportSqlColumn> reportSqlColumn;

}
