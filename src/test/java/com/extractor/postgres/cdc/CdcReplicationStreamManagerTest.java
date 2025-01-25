package com.extractor.postgres.cdc;

import com.extractor.postgres.message.process.MessageSubscriber;
import com.extractor.postgres.message.type.Message;
import com.extractor.postgres.objects.pub.PGPublication;
import com.extractor.postgres.objects.slots.PGLogicalReplicationSlot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.postgresql.PGConnection;
import org.postgresql.replication.LogSequenceNumber;
import org.postgresql.replication.PGReplicationConnection;
import org.postgresql.replication.PGReplicationStream;
import org.postgresql.replication.fluent.ChainedStreamBuilder;
import org.postgresql.replication.fluent.logical.ChainedLogicalCreateSlotBuilder;
import org.postgresql.replication.fluent.logical.ChainedLogicalStreamBuilder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CdcReplicationStreamManagerTest {

    @Mock
    private Connection connection;

    @Mock
    private PGConnection pgConnection;

    @Mock
    private PGPublication pgPublication;

    @Mock
    private PGLogicalReplicationSlot pgLogicalReplicationSlot;

    @Mock
    private StreamContext streamContext;

    private List<MessageSubscriber> subscriberList = new ArrayList<>() {
        {
            add(new MessageSubscriber() {
                @Override
                public void receive(Message message) {

                }

            });
        }
    };

    private CdcReplicationStreamManager cdcReplicationStreamManager;

    @BeforeEach
    void setUp() {
        cdcReplicationStreamManager = new CdcReplicationStreamManager(connection, "test1", "test1", "test1", "test1",
                subscriberList, pgPublication, pgLogicalReplicationSlot, streamContext);
        when(pgPublication.exists()).thenReturn(true);
        when(pgLogicalReplicationSlot.exists()).thenReturn(true);
    }

    @Test
    void when_startStream_from_offset() throws SQLException {
        PGReplicationConnection pgReplicationConnection = mock(PGReplicationConnection.class);
        ChainedStreamBuilder chainedStreamBuilder = mock(ChainedStreamBuilder.class);
        ChainedLogicalStreamBuilder chainedLogicalStreamBuilder = mock(ChainedLogicalStreamBuilder.class);
        PGReplicationStream pgReplicationStream = mock(PGReplicationStream.class);

        when(connection.unwrap(any())).thenReturn(pgConnection);
        when(pgConnection.getReplicationAPI()).thenReturn(pgReplicationConnection);
        when(pgReplicationConnection.replicationStream()).thenReturn(chainedStreamBuilder);
        when(chainedStreamBuilder.logical()).thenReturn(chainedLogicalStreamBuilder);
        when(chainedLogicalStreamBuilder.withStartPosition(any())).thenReturn(chainedLogicalStreamBuilder);
        when(chainedLogicalStreamBuilder.withSlotName(any())).thenReturn(chainedLogicalStreamBuilder);
        when(chainedLogicalStreamBuilder.withSlotOption(any(), any())).thenReturn(chainedLogicalStreamBuilder);
        when(chainedLogicalStreamBuilder.withSlotOption("proto_version", 1)).thenReturn(chainedLogicalStreamBuilder);
        when(chainedLogicalStreamBuilder.start()).thenReturn(pgReplicationStream);

        assertDoesNotThrow(() -> {
            var stream = cdcReplicationStreamManager.startStream(LogSequenceNumber.valueOf(1L));
        });

    }

    @Test
    void when_startStream_from_offset_errors_andThrows() throws SQLException {
        PGReplicationConnection pgReplicationConnection = mock(PGReplicationConnection.class);
        ChainedStreamBuilder chainedStreamBuilder = mock(ChainedStreamBuilder.class);
        ChainedLogicalStreamBuilder chainedLogicalStreamBuilder = mock(ChainedLogicalStreamBuilder.class);
        PGReplicationStream pgReplicationStream = mock(PGReplicationStream.class);

        when(connection.unwrap(any())).thenReturn(pgConnection);
        when(pgConnection.getReplicationAPI()).thenReturn(pgReplicationConnection);
        when(pgReplicationConnection.replicationStream()).thenReturn(chainedStreamBuilder);
        when(chainedStreamBuilder.logical()).thenReturn(chainedLogicalStreamBuilder);
        when(chainedLogicalStreamBuilder.withStartPosition(any())).thenReturn(chainedLogicalStreamBuilder);
        when(chainedLogicalStreamBuilder.withSlotName(any())).thenReturn(chainedLogicalStreamBuilder);
        when(chainedLogicalStreamBuilder.withSlotOption(any(), any())).thenReturn(chainedLogicalStreamBuilder);
        when(chainedLogicalStreamBuilder.withSlotOption("proto_version", 1)).thenReturn(chainedLogicalStreamBuilder);
        when(chainedLogicalStreamBuilder.start()).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> {
            var stream = cdcReplicationStreamManager.startStream(LogSequenceNumber.valueOf(1L));
        });

    }

}