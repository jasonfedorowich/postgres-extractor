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
public class StreamCommitMessage implements Message {

    private final byte flags;
    private final long xLogRecPtrStart;
    private final long xLogRecPtrEnd;
    private final long timestampTz;
}
