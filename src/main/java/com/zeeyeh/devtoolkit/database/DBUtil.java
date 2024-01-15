package com.zeeyeh.devtoolkit.database;

import com.zeeyeh.devtoolkit.database.handler.DBEntityHandler;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DBUtil {
    private String tableName;
    private DataSource dataSource;
    private final SqlFactory sqlFactory;

    public DBUtil(String tableName, SqlFactory sqlFactory, DataSource dataSource) {
        this.tableName = tableName;
        this.sqlFactory = sqlFactory;
        this.dataSource = dataSource;
    }

    public static DBUtil create(String tableName, DataSource dataSource) {
        return new DBUtil(tableName, new SqlFactory(null, new DBEntity()), dataSource);
    }

    public DBUtil setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public DBUtil setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public DBUtil setSql(String sql) {
        this.sqlFactory.setSql(sql);
        return this;
    }

    public DBUtil appendSql(String sql) {
        String oldSql = this.sqlFactory.getSql();
        this.sqlFactory.setSql(oldSql + sql);
        return this;
    }

    private DBUtil formatSql() {
        String sql = this.sqlFactory.getSql();
        if (sql.endsWith("AND")) {
            sql = sql.substring(0, sql.length() - 3);
        }
        setSql(sql.trim());
        return this;
    }

    public List<DBEntity> find(DBEntity entity) throws SQLException {
        setSql("SELECT * FROM " + tableName);
        if (entity != null) {
            appendSql(" WHERE");
            for (Map.Entry<String, Object> entry : entity.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                appendSql(" " + key + " = ? AND");
                this.sqlFactory.appendParam(key, value);
            }
            formatSql();
        }
        appendSql(";");
        return executeQuery(this.dataSource, this.sqlFactory);
    }

    public int insert(DBEntity entity) throws SQLException {
        setSql("INSERT INTO " + tableName + " (");
        List<String> keys = entity.getKeys();
        StringBuilder builder = new StringBuilder();
        for (String key : keys) {
            builder.append(", ").append(key);
        }
        String fieldString = builder.toString().trim();
        fieldString = fieldString.startsWith(",") ? fieldString.substring(1) : fieldString;
        appendSql(fieldString.trim() + ") VALUES (");
        String valueString = ", ?".repeat(keys.size()).trim();
        valueString = valueString.startsWith(",") ? valueString.substring(1) : valueString;
        appendSql(valueString.trim() + ");");
        this.sqlFactory.setParams(entity);
        return executeUpdate(dataSource, sqlFactory);
    }

    public int update(DBEntity entity, DBEntity where) throws SQLException {
        setSql("UPDATE " + tableName);
        List<String> keys = entity.getKeys();
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (String key : keys) {
            Object obj = entity.get(key);
            builder.append(",").append(key).append(" = ").append("?");
            this.sqlFactory.appendParam(key + "SET" + i++, obj);
            //if (obj instanceof String) {
            //    builder.append("'");
            //    builder.append(obj);
            //    builder.append("'");
            //} else {
            //    builder.append(obj);
            //}
        }
        String fieldString = builder.toString().trim();
        fieldString = fieldString.startsWith(",") ? fieldString.substring(1) : fieldString;
        appendSql(" SET " + fieldString);
        if (where != null) {
            for (Map.Entry<String, Object> entry : where.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                appendSql(" WHERE " + key + " = ? AND");
                this.sqlFactory.appendParam(key + "WHERE" + i++, value);
            }
            formatSql();
        }
        //this.sqlFactory.setParams(entity);
        appendSql(";");
        return executeUpdate(dataSource, sqlFactory);
    }

    public int delete(DBEntity where) throws SQLException {
        setSql("DELETE FROM " + tableName);
        if (where != null) {
            appendSql(" WHERE");
            for (Map.Entry<String, Object> entry : where.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                appendSql(" " + key + " = ? AND");
                this.sqlFactory.appendParam(key, value);
            }
            formatSql();
        }
        appendSql(";");
        return executeUpdate(dataSource, sqlFactory);
    }

    public List<List<DBEntity>> execute(DataSource dataSource, SqlFactory sqlFactory) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(dataSource);
        Object[] params = getParams(sqlFactory.getParams());
        return queryRunner.execute(sqlFactory.getSql(), new DBEntityHandler(), params);
    }

    public int executeUpdate(DataSource dataSource, SqlFactory sqlFactory) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(dataSource);
        Object[] params = getParams(sqlFactory.getParams());
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        int updated = queryRunner.update(connection, sqlFactory.getSql(), params);
        connection.commit();
        return updated;
    }

    public List<DBEntity> executeQuery(DataSource dataSource, SqlFactory sqlFactory) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(dataSource);
        Object[] params = getParams(sqlFactory.getParams());
        return queryRunner.query(sqlFactory.getSql(), new DBEntityHandler(), params);
    }

    public Object[] getParams(Map<String, Object> params) {
        Object[] paramsArray = new Object[params.size()];
        int i = 0;
        for (String result : params.keySet()) {
            paramsArray[i] = params.get(result);
            i++;
        }
        return paramsArray;
    }
}
