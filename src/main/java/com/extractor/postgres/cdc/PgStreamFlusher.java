package com.extractor.postgres.cdc;

import com.extractor.exceptions.StreamFlushException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.replication.LogSequenceNumber;
import org.postgresql.replication.PGReplicationStream;

import java.sql.SQLException;

@RequiredArgsConstructor
@Slf4j
public class PgStreamFlusher {

    private final PGReplicationStream pgReplicationStream;
    private final StreamContext streamContext;

    public void flush(LogSequenceNumber appliedLsn, LogSequenceNumber flushedLsn) {
        try{
            pgReplicationStream.setAppliedLSN(appliedLsn);
            pgReplicationStream.setFlushedLSN(flushedLsn);
            pgReplicationStream.forceUpdateStatus();
            streamContext.setLsn(appliedLsn, flushedLsn);
        }catch (SQLException e){
            log.error("Error received when trying to flush lsn: {}", e.toString());
            throw new StreamFlushException("Error from flushing stream", e);
        }

    }
}
