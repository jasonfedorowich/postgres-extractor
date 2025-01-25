package com.extractor.postgres.message.type.impl;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class TupleData {

    private final List<ColumnData> columnData;

    @Builder
    @Getter
    @ToString
    public static class ColumnData {
        private final Column column;
        private final Data data;

    }

}
