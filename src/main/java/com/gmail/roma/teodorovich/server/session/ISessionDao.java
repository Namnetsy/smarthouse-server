package com.gmail.roma.teodorovich.server.session;

import redis.clients.jedis.exceptions.JedisException;

import java.util.List;
import java.util.Map;

public interface ISessionDao {

    Map<String, Map<String, String>> getSessions(String userId) throws JedisException;

    void removeSession(String userId, String sessionId) throws JedisException;

    void removeAllSessions(String userId) throws JedisException;

    void createSession(Session session) throws JedisException;

}
