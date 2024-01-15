package com.zeeyeh.devtoolkit.database.handler;

import com.zeeyeh.devtoolkit.database.DBEntity;
import org.apache.commons.dbutils.handlers.AbstractListHandler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DBEntityHandler extends AbstractListHandler<DBEntity> {

    @Override
    protected DBEntity handleRow(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        DBEntity dbEntity = new DBEntity();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            Object object = resultSet.getObject(i);
            dbEntity.set(columnName, object);
        }
        return dbEntity;
    }
}
