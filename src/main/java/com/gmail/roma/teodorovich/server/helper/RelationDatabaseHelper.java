package com.gmail.roma.teodorovich.server.helper;

import com.gmail.roma.teodorovich.server.db.PostgreSqlPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RelationDatabaseHelper implements AutoCloseable {

    public static class Data {
        private final ResultSet set;

        public Data(ResultSet set) {
            this.set = set;
        }

        public Object getValue(int index) throws SQLException {
            if (set == null) {
                return null;
            }

            return set.getObject(index);
        }

        public boolean next() throws SQLException {
            if (set == null) {
                return false;
            }

            return set.next();
        }
    }

    private final PreparedStatement statement;

    private final Connection conn;

    public RelationDatabaseHelper(String sql) throws SQLException {
        conn = PostgreSqlPool.getConnection();
        statement = conn.prepareStatement(sql);
    }

    private boolean isConnected() {
        return conn != null;
    }

    public RelationDatabaseHelper setValue(int index, Object object) throws SQLException {
        if (!isConnected()) {
            return this;
        }

        statement.setObject(index, object);

        return this;
    }

    public void execute() throws SQLException {
        if (!isConnected()) {
            return;
        }

        statement.executeUpdate();
    }

    public Data executeWithData() throws SQLException {
        if (!isConnected()) {
            return null;
        }

        return new Data(statement.executeQuery());
    }

    @Override
    public void close() throws SQLException {
        statement.close();
        conn.close();
    }

}
