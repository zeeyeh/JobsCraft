package com.zeeyeh.devtoolkit.database;

import java.util.HashMap;
import java.util.Map;

public class SqlFactory {
    private String sql;
    private Map<String, Object> params;

    public SqlFactory() {
    }

    public SqlFactory(String sql) {
        this(sql, new HashMap<>());
    }

    public SqlFactory(String sql, Map<String, Object> params) {
        this.sql = sql;
        this.params = params;
    }

    public static SqlFactory creator(String sql) {
        return new SqlFactory(sql);
    }

    public static SqlFactory creator(String sql, Map<String, Object> params) {
        return new SqlFactory(sql, params);
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void appendParam(String key, Object value) {
        this.params.put(key, value);
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
