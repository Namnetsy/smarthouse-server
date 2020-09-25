package com.gmail.roma.teodorovich.server.email.impl;

import com.gmail.roma.teodorovich.server.db.RedisPool;
import com.gmail.roma.teodorovich.server.email.IEmailDao;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisException;

public class EmailDaoRedis implements IEmailDao {

    private EmailDaoRedis() {}

    private static IEmailDao emailDao = null;

    public static synchronized IEmailDao getInstance() {
        return (emailDao == null) ? emailDao = new EmailDaoRedis() : emailDao;
    }

    @Override
    public boolean checkVerificationCode(String email, String code) throws JedisException {
        try (Jedis jedis = RedisPool.getConnection()) {
            String result = jedis.get(email);

            return result != null && result.equals(code);
        }
    }

    @Override
    public void saveVerificationRecord(String email, String code, int expireSeconds) {
        try (Jedis jedis = RedisPool.getConnection()) {
            Pipeline pipeline = jedis.pipelined();
            pipeline.set(email, code); // OK
            pipeline.expire(email, expireSeconds); // 1

            pipeline.sync();
            pipeline.close();
        }
    }

    @Override
    public void removeVerificationRecord(String email) {
        try (Jedis jedis = RedisPool.getConnection()) {
            jedis.del(email); // 1 - OK, 0 - Not OK
        }
    }

    @Override
    public boolean checkEmailRecord(String email) {
        try (Jedis jedis = RedisPool.getConnection()) {
            return jedis.exists(email);
        }
    }

}
