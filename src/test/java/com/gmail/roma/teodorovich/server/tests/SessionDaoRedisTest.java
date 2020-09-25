package com.gmail.roma.teodorovich.server.tests;

import com.gmail.roma.teodorovich.server.RedisUnitTest;
import com.gmail.roma.teodorovich.server.UserHelper;
import com.gmail.roma.teodorovich.server.db.RedisPool;
import com.gmail.roma.teodorovich.server.session.impl.SessionDaoRedis;
import org.junit.jupiter.api.*;
import redis.clients.jedis.Jedis;

import java.sql.SQLException;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("[DAO:Redis] Session Tests")
public class SessionDaoRedisTest extends RedisUnitTest {

    @Test
    @DisplayName("Create a session")
    public void createSession() throws SQLException {
        try (UserHelper helper = new UserHelper()) {
            Assertions.assertTrue(jedis.exists(helper.getSessions().get(0).getAccessToken()));
        }
    }

    @Test
    @DisplayName("Get a list of sessions")
    public void getSessions() throws SQLException {
        try (UserHelper helper = new UserHelper()) {
            helper.addSession().addSession();
            int amountOfSessions = SessionDaoRedis.getInstance().getSessions(helper.getUser().getId()).size();

            Assertions.assertTrue(amountOfSessions >= 1);
        }
    }

    @Test
    @DisplayName("Remove a session")
    public void removeSession() throws SQLException {
        try (UserHelper helper = new UserHelper()) {
            SessionDaoRedis.getInstance().removeSession(helper.getUser().getId(),
                    helper.getSessions().get(0).getSessionId());

            Assertions.assertFalse(jedis.exists(helper.getSessions().get(0).getAccessToken()));
        }
    }

    @Test
    @DisplayName("Get a session with wrong user ID")
    public void getSessionsWithWrongUserId() throws SQLException {
        try (UserHelper helper = new UserHelper()) {
            Map<String, Map<String, String>> sessions = SessionDaoRedis.getInstance()
                    .getSessions("1234567");

            Assertions.assertTrue(sessions.isEmpty());
        }
    }

    @Test
    @DisplayName("Remove all sessions")
    public void removeAllSessions() throws SQLException {
        try (UserHelper helper = new UserHelper()) {
            helper.addSession().addSession();

            SessionDaoRedis.getInstance().removeAllSessions(helper.getUser().getId());

            Assertions.assertFalse(RedisPool.getConnection().exists(helper.getUser().getId()));
        }
    }

}
