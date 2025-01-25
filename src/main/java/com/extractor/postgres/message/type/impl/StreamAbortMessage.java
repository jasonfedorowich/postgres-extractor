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
public class StreamAbortMessage implements Message {

    private final long xLogRecPtr;
    private final long transactionId;
    private final long timestampTz;
}
