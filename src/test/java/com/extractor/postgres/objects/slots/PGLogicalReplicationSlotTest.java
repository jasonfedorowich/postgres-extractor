package com.extractor.postgres.objects.slots;

import com.extractor.postgres.connection.query.QueryExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.postgresql.PGConnection;
import org.postgresql.replication.PGReplicationConnection;
import org.postgresql.replication.ReplicationSlotInfo;
import org.postgresql.replication.fluent.ChainedCreateReplicationSlotBuilder;
import org.postgresql.replication.fluent.logical.ChainedLogicalCreateSlotBuilder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.extractor.postgres.objects.slots.PGLogicalReplicationSlot.GET_REPLICATION_SLOT_BY_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PGLogicalReplicationSlotTest {

    private PGLogicalReplicationSlot pgLogicalReplicationSlot;

    @Mock
    private Connection connection;

    @Mock
    private QueryExecutor queryExecutor;

    @BeforeEach
    public void setUp() {
        pgLogicalReplicationSlot = new PGLogicalReplicationSlot(connection, queryExecutor, "test", "pgoutput");
    }

    @Test
    void when_exists_success_thenReturns() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(queryExecutor.executeQuery(String.format(GET_REPLICATION_SLOT_BY_NAME, "test"))).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        assertTrue(pgLogicalReplicationSlot.exists());

    }

    @Test
    void when_exists_fails_thenThrows() throws SQLException {
        when(queryExecutor.executeQuery(String.format(GET_REPLICATION_SLOT_BY_NAME, "test")))
                .thenThrow(new RuntimeException("Err"));

        assertThrows(RuntimeException.class, () -> {
            pgLogicalReplicationSlot.exists();
        });

    }

    @Test
    void when_exists_throws_thenThrows() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(queryExecutor.executeQuery(String.format(GET_REPLICATION_SLOT_BY_NAME, "test"))).thenReturn(resultSet);
        when(resultSet.next()).thenThrow(new RuntimeException("err"));

        assertThrows(RuntimeException.class, () -> {
            pgLogicalReplicationSlot.exists();
        });

    }

    @Test
    void when_create_success_thenReturns() throws SQLException {
        PGConnection pg = mock(PGConnection.class);
        PGReplicationConnection pgReplicationConnection = mock(PGReplicationConnection.class);
        ChainedCreateReplicationSlotBuilder chainedCreateReplicationSlotBuilder = mock(
                ChainedCreateReplicationSlotBuilder.class);
        ChainedLogicalCreateSlotBuilder chainedLogicalCreateSlotBuilder = mock(ChainedLogicalCreateSlotBuilder.class);
        ReplicationSlotInfo replicationSlotInfo = mock(ReplicationSlotInfo.class);

        when(connection.unwrap(PGConnection.class)).thenReturn(pg);
        when(pg.getReplicationAPI()).thenReturn(pgReplicationConnection);
        when(pgReplicationConnection.createReplicationSlot()).thenReturn(chainedCreateReplicationSlotBuilder);
        when(chainedCreateReplicationSlotBuilder.logical()).thenReturn(chainedLogicalCreateSlotBuilder);
        when(chainedLogicalCreateSlotBuilder.withOutputPlugin(anyString())).thenReturn(chainedLogicalCreateSlotBuilder);
        when(chainedLogicalCreateSlotBuilder.withSlotName(anyString())).thenReturn(chainedLogicalCreateSlotBuilder);
        when(chainedLogicalCreateSlotBuilder.make()).thenReturn(replicationSlotInfo);

        assertDoesNotThrow(() -> {
            pgLogicalReplicationSlot.create();
        });

    }

    @Test
    void when_create_fails_thenThrows() throws SQLException {
        PGConnection pg = mock(PGConnection.class);
        PGReplicationConnection pgReplicationConnection = mock(PGReplicationConnection.class);
        ChainedCreateReplicationSlotBuilder chainedCreateReplicationSlotBuilder = mock(
                ChainedCreateReplicationSlotBuilder.class);
        ChainedLogicalCreateSlotBuilder chainedLogicalCreateSlotBuilder = mock(ChainedLogicalCreateSlotBuilder.class);

        when(connection.unwrap(PGConnection.class)).thenReturn(pg);
        when(pg.getReplicationAPI()).thenReturn(pgReplicationConnection);
        when(pgReplicationConnection.createReplicationSlot()).thenReturn(chainedCreateReplicationSlotBuilder);
        when(chainedCreateReplicationSlotBuilder.logical()).thenReturn(chainedLogicalCreateSlotBuilder);
        when(chainedLogicalCreateSlotBuilder.withOutputPlugin(anyString())).thenReturn(chainedLogicalCreateSlotBuilder);
        when(chainedLogicalCreateSlotBuilder.withSlotName(anyString())).thenReturn(chainedLogicalCreateSlotBuilder);
        when(chainedLogicalCreateSlotBuilder.make()).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> {
            pgLogicalReplicationSlot.create();
        });

    }

    @Test
    void when_create_failsWithDuplicatedSlot_thenReturns() throws SQLException {
        PGConnection pg = mock(PGConnection.class);
        PGReplicationConnection pgReplicationConnection = mock(PGReplicationConnection.class);
        ChainedCreateReplicationSlotBuilder chainedCreateReplicationSlotBuilder = mock(
                ChainedCreateReplicationSlotBuilder.class);
        ChainedLogicalCreateSlotBuilder chainedLogicalCreateSlotBuilder = mock(ChainedLogicalCreateSlotBuilder.class);
        ReplicationSlotInfo replicationSlotInfo = mock(ReplicationSlotInfo.class);

        when(connection.unwrap(PGConnection.class)).thenReturn(pg);
        when(pg.getReplicationAPI()).thenReturn(pgReplicationConnection);
        when(pgReplicationConnection.createReplicationSlot()).thenReturn(chainedCreateReplicationSlotBuilder);
        when(chainedCreateReplicationSlotBuilder.logical()).thenReturn(chainedLogicalCreateSlotBuilder);
        when(chainedLogicalCreateSlotBuilder.withOutputPlugin(anyString())).thenReturn(chainedLogicalCreateSlotBuilder);
        when(chainedLogicalCreateSlotBuilder.withSlotName(anyString())).thenReturn(chainedLogicalCreateSlotBuilder);
        when(chainedLogicalCreateSlotBuilder.make()).thenThrow(new SQLException("Error", "42710"));

        assertDoesNotThrow(() -> {
            pgLogicalReplicationSlot.create();
        });

    }

    @Test
    void when_drop_success_thenReturns() throws SQLException {
        PGConnection pg = mock(PGConnection.class);
        PGReplicationConnection pgReplicationConnection = mock(PGReplicationConnection.class);

        when(connection.unwrap(PGConnection.class)).thenReturn(pg);
        when(pg.getReplicationAPI()).thenReturn(pgReplicationConnection);

        assertDoesNotThrow(() -> {
            pgLogicalReplicationSlot.drop();
        });
    }

    @Test
    void when_drop_throws_thenThrows() throws SQLException {
        PGConnection pg = mock(PGConnection.class);
        PGReplicationConnection pgReplicationConnection = mock(PGReplicationConnection.class);

        when(connection.unwrap(PGConnection.class)).thenReturn(pg);
        when(pg.getReplicationAPI()).thenReturn(pgReplicationConnection);

        doThrow(RuntimeException.class).when(pgReplicationConnection).dropReplicationSlot(anyString());

        assertThrows(RuntimeException.class, () -> {
            pgLogicalReplicationSlot.drop();
        });
    }
}