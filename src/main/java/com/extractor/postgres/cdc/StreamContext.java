package com.extractor.postgres.cdc;

import com.extractor.postgres.message.process.MessageSubscriber;
import lombok.Getter;
import org.postgresql.replication.LogSequenceNumber;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class StreamContext {

    private final AtomicBoolean streamRunning = new AtomicBoolean(false);

    private final AtomicBoolean streamInError = new AtomicBoolean(false);

    private final AtomicReference<LogSequenceNumber> appliedLsn = new AtomicReference<>();

    private final AtomicReference<LogSequenceNumber> flushedLsn = new AtomicReference<>();


    @Getter
    private final List<Throwable> errors = new LinkedList<>();

    private PgOutputStreamProcessor pgOutputStreamProcessor;
    private PgMessageStream pgMessageStream;

    public boolean startStream() {
        streamRunning.set(true);
        return true;
    }

    public boolean stopStream() {
        streamRunning.set(false);
        pgMessageStream.close();
        return false;
    }

    public boolean isStreaming() {
        return streamRunning.get();
    }

    public void setStreamError(Exception e) {
        streamInError.set(true);
        streamRunning.set(false);
        errors.add(e);
    }

    public boolean isStreamInError() {
        return streamInError.get();
    }

    void setProcessor(PgOutputStreamProcessor pgOutputStreamProcessor) {
        this.pgOutputStreamProcessor = pgOutputStreamProcessor;
    }

    public void subscribe(MessageSubscriber messageSubscriber) {
        pgOutputStreamProcessor.subscribe(messageSubscriber);
    }

    public void addStream(PgMessageStream pgMessageStream) {
        this.pgMessageStream = pgMessageStream;
    }

    public LogSequenceNumber getAppliedLsn(){
        return this.appliedLsn.get();
    }
    public LogSequenceNumber getFlushedLsn(){
        return this.flushedLsn.get();
    }

    public void setLsn(LogSequenceNumber appliedLsn, LogSequenceNumber flushedLsn) {
        this.appliedLsn.set(appliedLsn);
        this.flushedLsn.set(flushedLsn);
    }
}
