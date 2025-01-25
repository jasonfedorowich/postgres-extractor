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
public class Truncate implements Message {

    private final int numberOfRelations;
    private final byte option;
    private final int oid;
    private final Relation relation;
}
