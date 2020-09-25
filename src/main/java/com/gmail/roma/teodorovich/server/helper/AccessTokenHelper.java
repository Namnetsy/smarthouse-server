package com.gmail.roma.teodorovich.server.helper;

import com.gmail.roma.teodorovich.server.db.RedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisException;

import java.security.SecureRandom;
import java.util.Map;

public class AccessTokenHelper {

    public static final int EXPIRATION_TIME = ((60 * 60) * 24) * 7; // 7 days

    public static Map<String, String> getUserData(String accessToken) {
        Map<String, String> data = null;

        try (Jedis jedis = RedisPool.getConnection()) {
            data = jedis.hgetAll(accessToken);

            if (data.isEmpty()) {
                return data;
            } else {
                Pipeline pipeline = jedis.pipelined();
                pipeline.expire(accessToken, EXPIRATION_TIME);
                pipeline.expire(data.get("uid"), EXPIRATION_TIME);
                pipeline.sync();

                return data;
            }
        } catch (JedisException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static String parseAuthHeader(String authHeader) {
        if (authHeader == null) {
            return null;
        }

        String accessToken = null;

        try {
            String[] data = authHeader.split("\\s+");

            accessToken = (data[0].equals("Bearer")) ? data[1] : null;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return accessToken;
    }

}
