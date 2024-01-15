package com.zeeyeh.jobscraft.utils;

import com.zeeyeh.devtoolkit.database.DBEntity;
import com.zeeyeh.devtoolkit.database.DBUtil;
import com.zeeyeh.devtoolkit.database.handler.DBEntityHandler;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public record DbUtil(String tableName, DataSource dataSource) {

    public static DbUtil builder(String tableName, DataSource dataSource) {
        return new DbUtil(tableName, dataSource);
    }

    public List<DBEntity> find(DBEntity where) {
        List<String> keys = where.getKeys();
        try {
            return DBUtil.create(this.tableName, this.dataSource).find(where);
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<DBEntity> findAll() {
        try {
            Connection connection = this.dataSource.getConnection();
            String sql = "SELECT * FROM " + this.tableName;
            QueryRunner queryRunner = new QueryRunner();
            return queryRunner.query(connection, sql, new DBEntityHandler());
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public int update(DBEntity entity, DBEntity where) {
        try {
            return DBUtil.create(this.tableName, this.dataSource).update(entity, where);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int delete(DBEntity where) {
        try {
            return DBUtil.create(this.tableName, this.dataSource).delete(where);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int insert(DBEntity entity) {
        try {
            return DBUtil.create(this.tableName, this.dataSource).insert(entity);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int execute(String sql) {
        try {
            return new QueryRunner(this.dataSource).execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
