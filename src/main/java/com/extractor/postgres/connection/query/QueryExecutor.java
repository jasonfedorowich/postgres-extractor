package com.extractor.postgres.connection.query;

import lombok.RequiredArgsConstructor;

import java.sql.*;

@RequiredArgsConstructor
public class QueryExecutor {

    private final Connection connection;

    public ResultSet executeQuery(String sql) {
        try {
            return connection.createStatement().executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean executeStatement(String sql) {
        try {
            return connection.createStatement().execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public PreparedStatement createPreparedStatement(String sql) {
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
