package com.extractor;

import com.extractor.postgres.message.process.MessageSubscriber;
import com.extractor.postgres.message.type.Message;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
public class TestHelper {

    @Slf4j
    static class CollectionMessageSubscriber implements MessageSubscriber {
        Map<Message, Integer> counter = new LinkedHashMap<>();

        @Override
        public void receive(Message message) {
            log.info("Message: {}", message);
            counter.put(message, counter.getOrDefault(message, 0) + 1);
        }
    }

    private static String FILMS_TABLE_DDL = "CREATE TABLE IF NOT EXISTS films (\n" +
            "    code        char(5),\n" +
            "    title       varchar(40),\n" +
            "    did         integer,\n" +
            "    date_prod   date,\n" +
            "    kind        varchar(10),\n" +
            "    len         interval hour to minute,\n" +
            "    CONSTRAINT code_title PRIMARY KEY(code,title)\n" +
            ")";


    private static String INSERT_STATEMENT_FILMS = "INSERT INTO films values ('%s', '%s', %d)";

    private static String DROP_FILMS_TABLE_DDL = "DROP TABLE IF EXISTS films CASCADE";

    private static String CREATE_SCHEMA = "CREATE SCHEMA IF NOT EXISTS public";

    private static String DROP_SCHEMA = "DROP SCHEMA IF EXISTS public CASCADE";

    public static void createFilmsTable(Connection connection) throws SQLException {
        connection.createStatement().execute(FILMS_TABLE_DDL);
    }

    public static void createSchema(Connection connection) throws SQLException {
        connection.createStatement().execute(CREATE_SCHEMA);
    }

    public static void dropSchema(Connection connection) throws SQLException {
        connection.createStatement().execute(DROP_SCHEMA);
    }

    public static void dropFilmsTable(Connection connection) throws SQLException {
        connection.createStatement().execute(DROP_FILMS_TABLE_DDL);
    }

    public static void insertRandomDataIntoFilms(Connection connection) throws SQLException {
        String code = randomChars(4);
        String title = randomChars(10);
        Random random = new Random();
        int did = random.nextInt(10);
        String film = String.format(INSERT_STATEMENT_FILMS, code, title, did);
        log.info("Inserting new row: {}", film);
        connection.beginRequest();
        connection.createStatement().executeUpdate(film);
        connection.commit();


    }

    public static String randomChars(int i){
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for(int j = 0; j < i; j++){
            char c = (char) ('a' + random.nextInt(26));
            sb.append(c);
        }
        return sb.toString();
    }
}
