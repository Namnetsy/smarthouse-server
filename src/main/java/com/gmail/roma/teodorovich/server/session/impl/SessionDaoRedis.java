package com.gmail.roma.teodorovich.server.session.impl;

import com.gmail.roma.teodorovich.server.db.RedisPool;
import com.gmail.roma.teodorovich.server.helper.AccessTokenHelper;
import com.gmail.roma.teodorovich.server.session.ISessionDao;
import com.gmail.roma.teodorovich.server.session.Session;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisException;

import java.util.*;

public class SessionDaoRedis implements ISessionDao {

    private SessionDaoRedis() {}

    private static ISessionDao sessionDao = null;

    public static synchronized ISessionDao getInstance() {
        return (sessionDao == null) ? sessionDao = new SessionDaoRedis() : sessionDao;
    }

    @Override
    public Map<String, Map<String, String>> getSessions(String userId) throws JedisException {
        Map<String, Map<String, String>> sessions = new HashMap<>();

        try (Jedis jedis = RedisPool.getConnection()) {
            Map<String, String> tokens = jedis.hgetAll(userId);

            for (var value : tokens.keySet()) {
                Map<String, String> tmp = jedis.hgetAll(tokens.get(value));
                tmp.remove("uid");

                sessions.put(value, tmp);
            }
        }

        return sessions;
    }

    @Override
    public void removeSession(String userId, String sessionId) throws JedisException {
        try (Jedis jedis = RedisPool.getConnection()) {
            Pipeline pipeline = jedis.pipelined();
            pipeline.del(jedis.hget(userId, sessionId));
            pipeline.hdel(userId, sessionId);
            pipeline.sync();
        }
    }

    @Override
    public void removeAllSessions(String userId) throws JedisException {
        try (Jedis jedis = RedisPool.getConnection()) {
            Map<String, String> data = jedis.hgetAll(userId);
            Pipeline pipeline = jedis.pipelined();

            for (var value : data.keySet()) {
                pipeline.del(data.get(value));
            }

            pipeline.del(userId);
            pipeline.sync();
        }
    }

    @Override
    public void createSession(Session session) throws JedisException {
        try (Jedis jedis = RedisPool.getConnection()) {
            Map<String, String> data = new HashMap<>();
            data.put("ip", session.getIp());
            data.put("country", session.getCountry());
            data.put("city", session.getCity());
            data.put("uid", session.getUserId());

            Pipeline pipeline = jedis.pipelined();
            pipeline.hset(session.getAccessToken(), data);
            pipeline.hset(session.getUserId(), session.getSessionId(), session.getAccessToken());
            pipeline.expire(session.getAccessToken(), AccessTokenHelper.EXPIRATION_TIME);
            pipeline.expire(session.getUserId(), AccessTokenHelper.EXPIRATION_TIME);
            pipeline.sync();
        }
    }

}
