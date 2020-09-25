package com.gmail.roma.teodorovich.server.revokeSession;

import redis.clients.jedis.exceptions.JedisException;

import java.util.Map;

public interface IRevokeLinkDao {

    Map<String, String> getRevokeLinkData(String secret) throws JedisException;

    void createLink(RevokeLink link) throws JedisException;

    boolean exists(String secret) throws JedisException;

    void deleteLink(String secret) throws JedisException;

}
