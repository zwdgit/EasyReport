package com.sdyx.report.sql.query.impl;

import com.sdyx.report.domain.ReportDatabase;
import com.sdyx.report.sql.query.AbstractQueryer;
import com.sdyx.report.sql.query.Queryer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class MySqlQueryer extends AbstractQueryer implements Queryer {

    public MySqlQueryer(ReportDatabase dataSource) {
        super(dataSource);
    }

    @Override
    protected String preprocessSqlText(String sqlText) {
        sqlText = StringUtils.stripEnd(sqlText.trim(), ";");
        Pattern pattern = Pattern.compile("limit.*?$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sqlText);
        if (matcher.find()) {
            sqlText = matcher.replaceFirst("");
        }
        return sqlText + " limit 1";
    }

}
