package com.extractor.postgres.message.type.impl;

import com.extractor.postgres.message.type.Message;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class PrepareCommitMessage implements Message {

    private final long xLogRecPtrStart;
    private final long xLogRecPtrEnd;
    private final long timestampTz;
    private final int transactionId;
    private final String gid;

}
