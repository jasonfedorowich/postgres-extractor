package com.extractor.postgres.objects.pub;

import com.extractor.postgres.connection.query.Query;
import com.extractor.postgres.connection.query.QueryExecutor;
import com.extractor.postgres.objects.PGObject;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Objects;

import static com.extractor.postgres.connection.query.Query.*;

@Builder
@Slf4j
public class PGPublication implements PGObject {

    private final Connection connection;

    private final QueryExecutor queryExecutor;

    private final String name;

    private final String table;

    public PGPublication(Connection connection, QueryExecutor queryExecutor, String name, String table) {
        this.connection = Objects.requireNonNull(connection, "Connection is not set");
        this.queryExecutor = Objects.requireNonNullElseGet(queryExecutor, () -> new QueryExecutor(connection));
        this.name = Objects.requireNonNull(name, "Name must not be null");
        this.table = Objects.requireNonNull(table, "Table name must not be null");
    }

    @Override
    public boolean exists() {
        try {
            ResultSet resultSet = queryExecutor.executeQuery(Query.get(GET_PG_PUBLICATION_BY_NAME, name));
            return resultSet.next();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PGObject create() {
        try {
            String sql;
            if (table == null || table.isEmpty() || table.equals("ALL TABLES")) {
                sql = get(CREATE_PUBLICATION_BY_NAME_FOR_TABLE, name);
            } else {
                sql = get(CREATE_PUBLICATION_BY_NAME_FOR_TABLE, name, table);
            }
            queryExecutor.executeStatement(sql);
        } catch (Exception e) {
            if (e.getMessage().contains("already exists")) {
                log.info("Duplicated publication: {}", name);
                return this;
            }
            throw new RuntimeException(e);
        }

        return this;
    }

    @Override
    public PGObject drop() {
        try {
            String sql = get(DROP_PUBLICATION_BY_NAME, name);
            if (!queryExecutor.executeStatement(sql))
                throw new RuntimeException(String.format("Failed to execute CREATE_PUBLICATION with: %s", sql));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

}
