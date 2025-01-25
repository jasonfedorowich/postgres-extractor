package com.extractor.postgres.message.type.impl;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public abstract class Data {

    // todo change to customize
    public static final Object UNCHANGED_VALUE_OBJECT = new Object();

    private final char type;

    public Data(char type) {
        this.type = type;
    }

    public abstract Object getValue();

    public char getType() {
        return type;
    }

    @ToString
    public static class ValueData extends Data {

        private final String value;

        public ValueData(String value, char type) {
            super(type);
            this.value = value;
        }

        @Override
        public Object getValue() {
            return value;
        }

    }

    @ToString
    public static class UnchangedData extends Data {

        public UnchangedData(char type) {
            super(type);
        }

        @Override
        public Object getValue() {
            return UNCHANGED_VALUE_OBJECT;
        }
    }

    @ToString
    public static class NullData extends Data {

        public NullData(char type) {
            super(type);
        }

        @Override
        public Object getValue() {
            return null;
        }
    }
}
