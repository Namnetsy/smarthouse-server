package com.gmail.roma.teodorovich.server.tests;

import com.gmail.roma.teodorovich.server.RedisUnitTest;
import com.gmail.roma.teodorovich.server.db.RedisPool;
import com.gmail.roma.teodorovich.server.email.impl.EmailDaoRedis;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("[DAO:Redis] Email Tests")
public class EmailDaoRedisTest extends RedisUnitTest {

    @Test
    @DisplayName("Save Verification Record")
    public void saveVerificationRecord() {
        String email = "john.smith@gmail.com";
        String code = "123456";
        EmailDaoRedis.getInstance().saveVerificationRecord(email, code, 5 * 60);

        Assertions.assertTrue(RedisPool.getConnection().exists(email));

        RedisPool.getConnection().del(email);
    }

    @Test
    @DisplayName("Remove Verification Record")
    public void removeVerificationRecord() {
        String email = "john.smith@gmail.com";
        String code = "123456";
        RedisPool.getConnection().set(email, code);
        EmailDaoRedis.getInstance().removeVerificationRecord(email);

        Assertions.assertFalse(RedisPool.getConnection().exists(email));
    }

    @Test
    @DisplayName("Check If Email Doesn't Exit")
    public void checkIfEmailDoesntExist() {
        String email = "john.smith@gmail.com";

        Assertions.assertFalse(EmailDaoRedis.getInstance().checkEmailRecord(email));
    }

    @Test
    @DisplayName("Check If Email Exists")
    public void checkIfEmailExist() {
        String email = "john.smith@gmail.com";
        String code = "123456";
        RedisPool.getConnection().set(email, code);

        Assertions.assertTrue(EmailDaoRedis.getInstance().checkEmailRecord(email));

        RedisPool.getConnection().del(email);
    }

}
