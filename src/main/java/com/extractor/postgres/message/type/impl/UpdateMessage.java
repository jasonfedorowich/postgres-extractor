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
public class UpdateMessage implements Message {

    private final int oid;
    private final char tupleDataType;
    private final TupleData oldTuple;
    private final char newDataType;
    private final TupleData newData;
}
