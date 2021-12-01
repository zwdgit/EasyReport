package com.sdyx.report.sql.dbpool;

import com.sdyx.report.domain.ReportDatabase;

import javax.sql.DataSource;


public interface DataSourcePoolWrapper {

    DataSource wrap(ReportDatabase reportDatabase);

}
