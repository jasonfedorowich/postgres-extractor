package com.extractor;

import com.extractor.postgres.cdc.CdcReplicationStreamManager;
import com.extractor.postgres.cdc.StreamContext;
import com.extractor.postgres.message.process.LoggingMessageSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGProperty;
import org.postgresql.replication.LogSequenceNumber;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

@Slf4j
public class Main {
    public static void main(String[] args) {
        Properties props = new Properties();
        PGProperty.REPLICATION.set(props, "database");
        PGProperty.ASSUME_MIN_SERVER_VERSION.set(props, "9.4");
        PGProperty.USER.set(props, "admin");
        PGProperty.PASSWORD.set(props, "root");
        PGProperty.PREFER_QUERY_MODE.set(props, "simple");

        // props.setProperty("options", "-c replication=database -c assumeMinServerVersion=15");
        String url = "jdbc:postgresql://localhost/test_db";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, props);
            // connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        int port = 5432;
        String user = "admin";
        String password = "root";
        // PostgresConnectionManager postgresConnectionManager =
        // PostgresConnectionManager.create(PostgresConnectionProperties
        // .builder()
        // .userName(user)
        // .password(password)
        // .url(url)
        // .numberOfConnections(1)
        // .port(5432)
        // .build());

        // try {
        //// String createCommand = String.format(
        //// "CREATE_REPLICATION_SLOT \"%s\" LOGICAL %s",
        //// "test1232224555",
        //// "pgoutput");
        // // String createCommand = String.format("SELECT * FROM pg_create_logical_replication_slot('%s', '%s')",
        // "test2321", "test_decoding");
        // //String createCommand = "SELECT * FROM films";
        //// connection.createStatement()
        //// .execute(createCommand);
        // }catch (SQLException e){
        // System.out.printf(e.toString());
        // }
        // PGLogicalReplicationSlot pgLogicalReplicationSlot = PGLogicalReplicationSlot.builder()
        // .name("test12344")
        // .pluginName("pgoutput")
        // .connection(connection)
        // .queryExecutor(new QueryExecutor(connection))
        // .build();
        // boolean exists = pgLogicalReplicationSlot.exists();
        // pgLogicalReplicationSlot.create();
        // ReplicationSlotInfo replicationSlotInfo = pgLogicalReplicationSlot.getReplicationSlotInfo();

        // PGPublication pgPublication = PGPublication.builder()
        // .table("films")
        // .name("pub_test12356")
        // .connection(connection)
        // .queryExecutor(new QueryExecutor(connection))
        // .build();
        //
        // boolean r = pgPublication.exists();
        // pgPublication.create();
        // pgPublication.drop();

        CdcReplicationStreamManager cdcReplicationStreamer = CdcReplicationStreamManager.builder().setTableName("films")
                .setSlotName("test_slot1").setSlotPluginName("pgoutput").setConnection(connection)
                .setPublicationName("pub_test12345678").setMessageSubscribers(List.of(new LoggingMessageSubscriber()))
                .setContext(new StreamContext()).build();

        var stream = cdcReplicationStreamer.startStream();
        stream.read();
    }
}