package com.extractor.postgres.objects.slots;

import com.extractor.postgres.connection.query.QueryExecutor;
import com.extractor.postgres.objects.PGObject;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.replication.LogSequenceNumber;
import org.postgresql.replication.ReplicationSlotInfo;
import org.postgresql.replication.ReplicationType;
import org.postgresql.replication.fluent.logical.ChainedLogicalCreateSlotBuilder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import static com.extractor.postgres.connection.PostgresConnectionManager.toPGConnection;
import static com.extractor.postgres.objects.constants.PGReplicationState.ALREADY_EXISTS;

@Getter
@Builder
@Slf4j
public class PGLogicalReplicationSlot implements PGObject {

    // todo remove
    public static final String GET_REPLICATION_SLOT_BY_NAME = "select * from pg_replication_slots where slot_name = '%s'";

    // todo add validation constructor
    private final Connection connection;
    private final QueryExecutor queryExecutor;
    private final String name;
    private final String pluginName;

    public PGLogicalReplicationSlot(Connection connection, QueryExecutor queryExecutor, String name,
            String pluginName) {
        this.connection = Objects.requireNonNull(connection, "Connection is not set");
        this.queryExecutor = Objects.requireNonNullElseGet(queryExecutor, () -> new QueryExecutor(connection));
        this.name = Objects.requireNonNull(name, "Name must not be null");
        this.pluginName = Objects.requireNonNull(pluginName, "PluginName must not be null");
    }

    //
    // @Override
    // public boolean exists(String name, Map<String, String> options) {
    //// return toPGConnection(connection)
    //// .getReplicationAPI()
    //// .
    // return false;
    //
    //
    // }

    // @Override
    // public PGObject create(String name, Map<String, String> options) {
    // ChainedLogicalCreateSlotBuilder builder;
    // builder = toPGConnection(connection)
    // .getReplicationAPI()
    // .createReplicationSlot()
    // .logical()
    // .withSlotName(name)
    // .withOutputPlugin(options.get(OUTPUT_PLUGIN.getOptionName()));
    //
    // try {
    // this.replicationSlotInfo = builder.make();
    // } catch (SQLException e) {
    // if(e.getSQLState().equals(ALREADY_EXISTS.getState())){
    // // todo get replicationslot info here
    // return this;
    // }
    // throw new RuntimeException("Cannot make replication slot with error", e);
    // }
    // return this;
    //
    // }

    public ReplicationSlotInfo getReplicationSlotInfo() {
        return toReplicationSlot(getReplicationSlot());
    }

    @Override
    public boolean exists() {
        ResultSet rs = getReplicationSlot();
        try {
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PGObject create() {
        ChainedLogicalCreateSlotBuilder builder;
        builder = toPGConnection(connection).getReplicationAPI().createReplicationSlot().logical().withSlotName(name)
                .withOutputPlugin(pluginName);
        try {
            builder.make();
        } catch (Exception e) {
            if (e instanceof SQLException) {
                if (((SQLException) e).getSQLState().equals(ALREADY_EXISTS.getState())) {
                    log.info("Duplicate slot exists with name: {}", name);
                    return this;
                }
            }
            log.error("Error {} with creating replication slot", e.toString());
            throw new RuntimeException("Cannot make replication slot with error", e);

        }
        return this;
    }

    @Override
    public PGObject drop() {
        try {
            toPGConnection(connection).getReplicationAPI().dropReplicationSlot(name);
        } catch (Exception e) {
            log.error("Error {} with dropping replication slot", e.toString());
            throw new RuntimeException("Cannot drop replication slot: " + name, e);
        }
        return this;
    }

    private ReplicationSlotInfo toReplicationSlot(ResultSet resultSet) {

        try {
            if (!resultSet.next())
                return null;
            String slotName = resultSet.getString("slot_name");
            String pluginName = resultSet.getString("plugin");
            String confirmedFlushLsn = resultSet.getString("confirmed_flush_lsn");
            return new ReplicationSlotInfo(slotName, ReplicationType.LOGICAL,
                    LogSequenceNumber.valueOf(confirmedFlushLsn), null, pluginName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ResultSet getReplicationSlot() {
        return queryExecutor.executeQuery(String.format(GET_REPLICATION_SLOT_BY_NAME, name));
    }

}
