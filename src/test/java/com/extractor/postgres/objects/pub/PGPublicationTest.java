package com.extractor.postgres.objects.pub;

import com.extractor.postgres.connection.query.QueryExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PGPublicationTest {

    @Mock
    private Connection connection;

    @Mock
    private QueryExecutor queryExecutor;

    private String pubName = "test-pub";

    private String tableName = "test-table";

    private PGPublication pgPublication;

    @BeforeEach
    void setUp() {
        pgPublication = new PGPublication(connection, queryExecutor, pubName, tableName);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void when_exists_success_thenReturns() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true);
        when(queryExecutor.executeQuery(anyString())).thenReturn(resultSet);

        assertTrue(pgPublication.exists());

    }

    @Test
    void when_exists_failsWithQuery_thenThrows() throws SQLException {
        mock(ResultSet.class);
        when(queryExecutor.executeQuery(anyString())).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> {
            pgPublication.exists();
        });

    }

    @Test
    void when_exists_failsWithCheck_thenThrows() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenThrow(new RuntimeException());
        when(queryExecutor.executeQuery(anyString())).thenReturn(resultSet);

        assertThrows(RuntimeException.class, () -> {
            pgPublication.exists();
        });

    }

    @Test
    void when_create_success_thenReturns() {
        when(queryExecutor.executeStatement(anyString())).thenReturn(true);
        assertDoesNotThrow(() -> {
            pgPublication.create();
        });
    }

    @Test
    void when_create_executeThrowsException_thenReturns() {
        when(queryExecutor.executeStatement(anyString())).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> {
            pgPublication.create();
        });
    }

    @Test
    void when_create_executeReturnsFalse_thenReturns() {
        when(queryExecutor.executeStatement(anyString())).thenThrow(new RuntimeException("already exists"));
        assertDoesNotThrow(() -> {
            pgPublication.create();
        });
    }

    @Test
    void when_drop_success_thenReturns() {
        when(queryExecutor.executeStatement(anyString())).thenReturn(true);
        assertDoesNotThrow(() -> {
            pgPublication.drop();
        });
    }

    @Test
    void when_drop_fails_thenThrows() {
        when(queryExecutor.executeStatement(anyString())).thenReturn(false);
        assertThrows(RuntimeException.class, () -> {
            pgPublication.drop();
        });
    }

    @Test
    void when_drop_throws_thenThrows() {
        when(queryExecutor.executeStatement(anyString())).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> {
            pgPublication.drop();
        });
    }
}