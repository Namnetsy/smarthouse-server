package com.gmail.roma.teodorovich.server.db;

import com.gmail.roma.teodorovich.server.Config;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class PostgreSqlPool {

    private static final String DRIVER_CLASS = "org.postgresql.Driver";
    private static final String DB_CONNECTION_URL = Config.getDbURL();
    private static final String DB_USER = Config.getDbUser();
    private static final String DB_PASS = Config.getDbPassword();

    private static final BasicDataSource ds;

    static {
        ds = new BasicDataSource();
        ds.setDriverClassName(DRIVER_CLASS);
        ds.setUrl(DB_CONNECTION_URL);
        ds.setUsername(DB_USER);
        ds.setPassword(DB_PASS);

        ds.setMinIdle(5);
        ds.setMaxIdle(10);
        ds.setMaxOpenPreparedStatements(100);
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

}