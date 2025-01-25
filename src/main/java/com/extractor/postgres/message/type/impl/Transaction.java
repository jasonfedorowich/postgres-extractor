package com.extractor.postgres.message.type.impl;

import com.extractor.postgres.message.type.Message;
import com.extractor.postgres.message.util.Lsn;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.postgresql.replication.LogSequenceNumber;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class Transaction implements Message {

    private final long xLogRecPtr;
    private final long timestampTz;
    private final int transactionId;

    public LogSequenceNumber getLogSequenceNumber() {
        return Lsn.toLsn(xLogRecPtr);
    }

}
