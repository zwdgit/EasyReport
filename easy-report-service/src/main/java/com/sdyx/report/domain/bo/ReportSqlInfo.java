package com.sdyx.report.domain.bo;

import com.sdyx.report.domain.Layout;
import com.sdyx.report.domain.ReportSqlColumn;
import lombok.Data;

import java.util.List;

@Data
public class ReportSqlInfo {
    private List<ReportSqlColumn> reportSqlColumns;
    private List<Layout> layout;
}
