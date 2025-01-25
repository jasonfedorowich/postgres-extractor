package com.extractor.postgres.connection.query;

import lombok.Getter;

@Getter
public enum Query {

    GET_REPLICATION_SLOT_BY_NAME(
            "select * from pg_replication_slots where slot_name = '%s'"), GET_PG_PUBLICATION_BY_NAME(
                    "select * from pg_publication where pubname = '%s'"), CREATE_PUBLICATION_BY_NAME_FOR_ALL_TABLES(
                            "CREATE PUBLICATION %s FOR ALL TABLES"), CREATE_PUBLICATION_BY_NAME_FOR_TABLE(
                                    "CREATE PUBLICATION %s FOR TABLE %s"), DROP_PUBLICATION_BY_NAME(
                                            "DROP PUBLICATION %s");

    private String queryString;

    private Query(String queryString) {
        this.queryString = queryString;
    }

    public String get(Object... args) {
        return String.format(queryString, args);
    }

    public static String get(Query query, Object... args) {
        return query.get(args);
    }

}
