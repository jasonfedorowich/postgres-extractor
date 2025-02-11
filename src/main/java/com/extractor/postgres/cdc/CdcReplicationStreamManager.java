package com.extractor.postgres.cdc;

import com.extractor.exceptions.StreamStartException;
import com.extractor.postgres.connection.PostgresConnectionManager;
import com.extractor.postgres.connection.query.QueryExecutor;
import com.extractor.postgres.message.process.MessageSubscriber;
import com.extractor.postgres.objects.pub.PGPublication;
import com.extractor.postgres.objects.slots.PGLogicalReplicationSlot;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.replication.LogSequenceNumber;
import org.postgresql.replication.fluent.logical.ChainedLogicalStreamBuilder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class CdcReplicationStreamManager {

    private final Connection connection;

    private final String tableName;

    private final String publicationName;

    private final String slotName;

    private final String slotPluginName;

    private final List<MessageSubscriber> subscribers;

    private final StreamContext streamContext;

    private PGPublication publication;

    private PGLogicalReplicationSlot pgLogicalReplicationSlot;

    public CdcReplicationStreamManager(Connection connection, String tableName, String publicationName, String slotName,
            String slotPluginName, List<MessageSubscriber> subscribers, PGPublication publication,
            PGLogicalReplicationSlot pgLogicalReplicationSlot, StreamContext streamContext) {
        this.connection = connection;
        this.publicationName = publicationName;
        this.slotName = slotName;
        this.tableName = tableName;
        this.slotPluginName = slotPluginName;
        this.subscribers = subscribers;
        this.publication = publication;
        this.pgLogicalReplicationSlot = pgLogicalReplicationSlot;
        this.streamContext = streamContext;
    }

    public CdcReplicationStreamManager(Connection connection, String tableName, String publicationName, String slotName,
            String slotPluginName, List<MessageSubscriber> subscribers, StreamContext streamContext) {
        this.connection = connection;
        this.publicationName = publicationName;
        this.slotName = slotName;
        this.tableName = tableName;
        this.slotPluginName = slotPluginName;
        this.subscribers = subscribers;
        this.streamContext = streamContext;
    }

    public PgMessageStream startStream(LogSequenceNumber startPosition) {
        initStream();
        PGConnection pgConnection = PostgresConnectionManager.toPGConnection(connection);
        try {
            ChainedLogicalStreamBuilder chainedLogicalStreamBuilder = pgConnection.getReplicationAPI()
                    .replicationStream().logical().withSlotName(slotName)
                    .withSlotOption("publication_names", publicationName).withSlotOption("proto_version", 1);

            if (startPosition != null)
                chainedLogicalStreamBuilder.withStartPosition(startPosition);

            return new PgMessageStream(chainedLogicalStreamBuilder.start(), subscribers, connection, streamContext);
        } catch (SQLException e) {
            log.error("{}", e.toString());
            throw new StreamStartException("Error with starting stream", e);
        }

    }

    // todo add start position
    // todo write back to source db to release wal log
    // todo add someway to persist wal lsn
    public PgMessageStream startStream() {
        return startStream(null);
    }

    private void initStream() {
        // todo check if exists
        if (publication == null)
            publication = PGPublication.builder().table(tableName).connection(connection)
                    .queryExecutor(new QueryExecutor(connection)).name(publicationName).build();

        if (!publication.exists())
            publication.create();

        if (pgLogicalReplicationSlot == null)
            pgLogicalReplicationSlot = PGLogicalReplicationSlot.builder().pluginName(slotPluginName)
                    .connection(connection).queryExecutor(new QueryExecutor(connection)).name(slotName).build();
        if (!pgLogicalReplicationSlot.exists())
            pgLogicalReplicationSlot.create();
    }

    @Getter
    public static class Builder {

        private Connection connection;

        private String tableName;

        private String publicationName;

        private String slotName;

        private String slotPluginName;

        private List<MessageSubscriber> subscribers;

        private StreamContext streamContext;

        public Builder() {

        }

        public Builder setContext(StreamContext context) {
            this.streamContext = context;
            return this;
        }

        public Builder setConnection(Connection connection) {
            this.connection = connection;
            return this;
        }

        public Builder setTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder setPublicationName(String publicationName) {
            this.publicationName = publicationName;
            return this;
        }

        public Builder setSlotName(String slotName) {
            this.slotName = slotName;
            return this;
        }

        public Builder setSlotPluginName(String slotPluginName) {
            this.slotPluginName = slotPluginName;
            return this;
        }

        public Builder setMessageSubscribers(List<MessageSubscriber> messageSubscribers) {
            this.subscribers = messageSubscribers;
            return this;
        }

        public CdcReplicationStreamManager build() {
            return new CdcReplicationStreamManager(connection, tableName, publicationName, slotName, slotPluginName,
                    subscribers, streamContext);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // public CdcEvent stream(){
    // pgConnection.getReplicationAPI().
    // }
}
