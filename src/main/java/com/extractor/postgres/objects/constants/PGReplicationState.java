package com.extractor.postgres.objects.constants;

public enum PGReplicationState {

    ALREADY_EXISTS("42710");

    private final String state;

    PGReplicationState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

}
