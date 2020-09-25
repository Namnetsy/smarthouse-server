package com.gmail.roma.teodorovich.server.email;

import redis.clients.jedis.exceptions.JedisException;

public interface IEmailDao {

    boolean checkVerificationCode(String email, String code) throws JedisException;

    void saveVerificationRecord(String email, String code, int expireSeconds) throws JedisException;

    void removeVerificationRecord(String email) throws JedisException;

    boolean checkEmailRecord(String email) throws JedisException;

}
