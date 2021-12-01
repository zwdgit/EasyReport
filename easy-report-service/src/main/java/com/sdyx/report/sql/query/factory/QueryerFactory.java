package com.sdyx.report.sql.query.factory;

import com.sdyx.common.enums.DataBaseMation;
import com.sdyx.report.domain.ReportDatabase;
import com.sdyx.report.sql.query.Queryer;

import java.lang.reflect.Constructor;

public class QueryerFactory {

    public static Queryer create(ReportDatabase database) {
        if (database != null) {
            try {
                Class<?> clazz = Class.forName(DataBaseMation.getQueryerClassByType(database.getDataType()));
                Constructor<?> constructor = clazz.getConstructor(ReportDatabase.class);
                return (Queryer)constructor.newInstance(database);
            } catch (final Exception ex) {
                throw new RuntimeException("create report engine queryer error", ex);
            }
        }
        return null;
    }

}
