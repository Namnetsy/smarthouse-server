package com.gmail.roma.teodorovich.server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class PostgreSqlUnitTest {

    @BeforeAll
    public void init() {
        App.startServer();
    }

    @AfterAll
    public void stopServer() {
        App.stopServer();
    }

}
