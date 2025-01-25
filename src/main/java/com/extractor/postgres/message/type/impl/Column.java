package com.extractor.postgres.message.type.impl;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class Column {

    private final byte flags;
    private final int oid;
    private final String name;
    private final int typeModifier;
}
