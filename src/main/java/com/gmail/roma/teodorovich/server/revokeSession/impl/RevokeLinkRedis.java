package com.gmail.roma.teodorovich.server.revokeSession.impl;

import com.gmail.roma.teodorovich.server.db.RedisPool;
import com.gmail.roma.teodorovich.server.revokeSession.IRevokeLinkDao;
import com.gmail.roma.teodorovich.server.revokeSession.RevokeLink;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisException;

import java.util.HashMap;
import java.util.Map;

public class RevokeLinkRedis implements IRevokeLinkDao {

    private RevokeLinkRedis() {}

    private static IRevokeLinkDao revokeLinkDao = null;

    public static synchronized IRevokeLinkDao getInstance() {
        return (revokeLinkDao == null) ? revokeLinkDao = new RevokeLinkRedis() : revokeLinkDao;
    }

    @Override
    public Map<String, String> getRevokeLinkData(String secret) throws JedisException {
        try (Jedis jedis = RedisPool.getConnection()) {
            return jedis.hgetAll(secret);
        }
    }

    @Override
    public void createLink(RevokeLink link) throws JedisException {
        try (Jedis jedis = RedisPool.getConnection()) {
            Pipeline pipeline = jedis.pipelined();
            Map<String, String> data = new HashMap<>();
            data.put("sid", link.getSession().getSessionId());
            data.put("uid", link.getSession().getUserId());

            pipeline.hset(link.getSecret(), data);
            pipeline.expire(link.getSecret(), 60 * 5);
            pipeline.sync();
        }
    }

    @Override
    public boolean exists(String secret) throws JedisException {
        try (Jedis jedis = RedisPool.getConnection()) {
            return jedis.exists(secret);
        }
    }

    @Override
    public void deleteLink(String secret) throws JedisException {
        try (Jedis jedis = RedisPool.getConnection()) {
            jedis.del(secret);
        }
    }

}
