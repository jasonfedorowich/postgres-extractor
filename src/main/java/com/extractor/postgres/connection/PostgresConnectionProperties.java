package com.extractor.postgres.connection;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class PostgresConnectionProperties {

    private int numberOfConnections;
    private String url;
    private String userName;
    private String password;
    private int port;

}
