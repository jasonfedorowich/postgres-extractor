package com.extractor;

import com.extractor.postgres.cdc.CdcReplicationStreamManager;
import com.extractor.postgres.cdc.StreamContext;
import com.extractor.postgres.message.process.LoggingMessageSubscriber;
import com.extractor.postgres.message.process.MessageSubscriber;
import com.extractor.postgres.message.type.Message;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.postgresql.PGProperty;
import org.postgresql.replication.LogSequenceNumber;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class PostgresExtractorIT {



    Connection connection = null;
    Connection dataConnection = null;

    @BeforeEach
    public void setUp() throws SQLException {
        Properties props = new Properties();
        PGProperty.REPLICATION.set(props, "database");
        PGProperty.ASSUME_MIN_SERVER_VERSION.set(props, "9.4");
        PGProperty.USER.set(props, "postgres");
        PGProperty.PASSWORD.set(props, "postgres");
        PGProperty.PREFER_QUERY_MODE.set(props, "simple");

        // props.setProperty("options", "-c replication=database -c assumeMinServerVersion=15");
        String url = "jdbc:postgresql://localhost/postgres";
        try {
            connection = DriverManager.getConnection(url, props);
            dataConnection = DriverManager.getConnection(url, props);
            dataConnection.setAutoCommit(false);
          //  connection.setAutoCommit();
         //   TestHelper.createSchema(connection);
            TestHelper.createFilmsTable(connection);
            // connection.setAutoCommit(false);
        } catch (SQLException e) {
            log.error("Error: {}", e);
        }

        //  TestHelper.createFilmsTable(connection);

    }

    @AfterEach
    public void tearDown() throws SQLException {
        TestHelper.dropFilmsTable(connection);
        TestHelper.dropSchema(connection);
        connection.close();
        dataConnection.close();

    }




    @Test
    @Timeout(30)
    public void when_dataInserted_then_read_and_published_successfully() throws SQLException, InterruptedException {
        StreamContext streamContext = new StreamContext();
        TestHelper.CollectionMessageSubscriber collectionMessageSubscriber = new TestHelper.CollectionMessageSubscriber();


        CdcReplicationStreamManager cdcReplicationStreamer = CdcReplicationStreamManager.builder().setTableName("films")
                .setSlotName("test_slot1").setSlotPluginName("pgoutput").setConnection(connection)
                .setPublicationName("pub_test12345678").setMessageSubscribers(List.of(collectionMessageSubscriber))
                .setContext(streamContext).build();

        var appliedSequenceBefore = streamContext.getAppliedLsn();
        var flushedLsnBefore = streamContext.getFlushedLsn();

        Executors.newSingleThreadScheduledExecutor().schedule(()->{
            try {
                log.info("Inserting data");
                TestHelper.insertRandomDataIntoFilms(dataConnection);

            } catch (SQLException e) {
                log.error("Error inserting: {}", e.toString());
                throw new RuntimeException(e);
            }
        }, 3000, TimeUnit.MILLISECONDS);

        Executors.newSingleThreadScheduledExecutor().schedule(()->{
            log.info("Stopping stream");
            streamContext.stopStream();
        }, 9000, TimeUnit.MILLISECONDS);

        var stream = cdcReplicationStreamer.startStream();
        stream.read();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await(20, TimeUnit.SECONDS);
        assertFalse(collectionMessageSubscriber.counter.isEmpty());
        log.info("Finished test!");
        var appliedSequenceAfter = streamContext.getAppliedLsn();
        var flushedLsnAfter = streamContext.getFlushedLsn();
        assertNotEquals(appliedSequenceBefore, appliedSequenceAfter);
        assertNotEquals(flushedLsnBefore, flushedLsnAfter);

    }


}
