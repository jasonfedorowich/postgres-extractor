package com.extractor.postgres.connection;

import lombok.RequiredArgsConstructor;
import org.postgresql.PGConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PostgresConnectionManager implements AutoCloseable {

    private Queue<Connection> connections;
    private final PostgresConnectionProperties postgresConnectionProperties;

    public static PostgresConnectionManager create(PostgresConnectionProperties postgresConnectionProperties) {
        return new PostgresConnectionManager(postgresConnectionProperties);
    }

    private PostgresConnectionManager(PostgresConnectionProperties postgresConnectionProperties) {
        this.connections = new ConcurrentLinkedQueue<>();
        this.postgresConnectionProperties = postgresConnectionProperties;
    }

    public synchronized Connection getConnection() {
        if (connections.isEmpty()) {
            Connection connection = createConnection();
            connections.add(connection);
        }
        return connections.poll();
    }

    public static PGConnection toPGConnection(Connection connection) {
        try {
            return connection.unwrap(PGConnection.class);
        } catch (SQLException e) {
            throw new RuntimeException("Cannot unwrap Connection class check if postgres connection is valid", e);
        }
    }

    private Connection createConnection() {
        try {
            return DriverManager.getConnection(postgresConnectionProperties.getUrl(),
                    postgresConnectionProperties.getUserName(), postgresConnectionProperties.getPassword());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create new connection with error", e);
        }
    }

    @Override
    public void close() throws Exception {
        for (Connection connection : connections) {
            connection.close();
        }
    }
}
