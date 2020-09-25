package com.gmail.roma.teodorovich.server;

import com.gmail.roma.teodorovich.server.db.RedisPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import redis.clients.jedis.Jedis;

public class RedisUnitTest {

    protected Jedis jedis;

    @BeforeAll
    public void init() {
        App.startServer();
        jedis = RedisPool.getConnection();
    }

    @AfterAll
    public void stopServer() {
        App.stopServer();
        jedis.close();
    }

}
