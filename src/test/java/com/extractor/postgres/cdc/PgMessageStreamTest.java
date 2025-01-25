package com.extractor.postgres.cdc;

import com.extractor.postgres.message.process.StreamMessageContext;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.postgresql.replication.PGReplicationStream;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PgMessageStreamTest {

    private PgMessageStream pgMessageStream;

    @Mock
    private PGReplicationStream pgReplicationStream;

    @Mock
    private Connection connection;

    @Mock
    private StreamMessageContext streamMessageContext;

    @Mock
    private PGOutputStreamProcessor pgOutputStreamProcessor;

    public static StreamContext streamContext = new StreamContext();

    @BeforeEach
    void setUp() {
        pgMessageStream = new PgMessageStream(pgOutputStreamProcessor, pgReplicationStream, connection,
                streamMessageContext, streamContext);
    }

    @Test
    void when_read_isSuccess_noErrors() throws SQLException, ExecutionException, InterruptedException {
        ByteBuffer bb = ByteBuffer.wrap(new byte[] { 1, 2 });
        lenient().when(pgReplicationStream.readPending()).thenReturn(bb);
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            streamContext.stopStream();
        }, 5000, TimeUnit.MILLISECONDS);

        assertDoesNotThrow(() -> {
            pgMessageStream.read();
        });

    }

    @Test
    void when_read_isErrors_thenHasErrors() throws SQLException, ExecutionException, InterruptedException {
        ByteBuffer bb = ByteBuffer.wrap(new byte[] { 1, 2 });
        when(pgReplicationStream.readPending()).thenThrow(new RuntimeException());
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            streamContext.stopStream();
        }, 5000, TimeUnit.MILLISECONDS);

        pgMessageStream.read();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await(1000, TimeUnit.MILLISECONDS);
        assertInstanceOf(RuntimeException.class, streamContext.getErrors().get(0));

    }
}