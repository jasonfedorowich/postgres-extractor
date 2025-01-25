package com.extractor.postgres.message.util;

import org.postgresql.replication.LogSequenceNumber;

public class Lsn {

    public static LogSequenceNumber toLsn(long lsn) {
        return LogSequenceNumber.valueOf(lsn);
    }

    public static LogSequenceNumber toLsn(String lsn) {
        return LogSequenceNumber.valueOf(lsn);
    }
}
