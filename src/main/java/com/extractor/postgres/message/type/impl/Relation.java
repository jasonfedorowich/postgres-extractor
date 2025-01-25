package com.extractor.postgres.message.type.impl;

import com.extractor.postgres.message.type.Message;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class Relation implements Message {

    private final List<Column> columns;
    private final int oid;
    private final String schema;
    private final String table;
    private final byte replicaIdentitySetting;
    private final short numberOfColumns;

}
