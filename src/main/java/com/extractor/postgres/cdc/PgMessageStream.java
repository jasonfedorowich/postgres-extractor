package com.extractor.postgres.cdc;

import com.extractor.postgres.message.process.MessageSubscriber;
import com.extractor.postgres.message.process.StreamMessageContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.replication.PGReplicationStream;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
public class PgMessageStream implements AutoCloseable{

    private final PGOutputStreamProcessor pgOutputStreamProcessor;

    private final PGReplicationStream pgReplicationStream;

    private final Connection connection;

    private final StreamMessageContext streamMessageContext;

    private final StreamContext streamContext;
    private ExecutorService executorService;

    public PgMessageStream(PGReplicationStream pgReplicationStream, List<MessageSubscriber> subscribers,
            Connection connection, StreamContext streamContext) {
        this.pgReplicationStream = pgReplicationStream;
        this.streamMessageContext = new StreamMessageContext();
        this.pgOutputStreamProcessor = new PGOutputStreamProcessor(subscribers, streamMessageContext, streamContext);
        this.connection = connection;
        this.streamContext = streamContext;
        this.streamContext.addStream(this);
    }

    public void read() {
        this.executorService = Executors.newSingleThreadExecutor();
        executorService.submit(()->{
            streamContext.startStream();
            log.debug("Stream initialized");
            while (streamContext.isStreaming()) {
                try {
                    var bb = pgReplicationStream.readPending();
                    log.debug("Bytes read from stream: {}", bb);
                    if(bb == null) continue;
                    pgOutputStreamProcessor.process(bb);
                    // todo why is not commiting?
                    // bb.get
                    // if(bb != null)
                    // log.info("{}", (char)bb.get());
                    // log.info("{}", bb);
                } catch (Exception e) {
                    log.error("Error in stream: {}", e.toString());
                    streamContext.setStreamError(e);
                    // todo change exeception
                    throw new RuntimeException(e);
                }
            }
        });

    }

    public void close() {
        try {
            pgReplicationStream.close();
            executorService.shutdown();
            executorService.shutdownNow();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
