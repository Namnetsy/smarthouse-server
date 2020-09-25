package com.gmail.roma.teodorovich.server.user;

import java.sql.SQLException;

public interface IUserDao {

    User getUser(String email) throws SQLException;

    User getUserById(String id) throws SQLException;

    void setUsername(String id, String newUsername) throws SQLException;

    void setEmail(String id, String newEmail) throws SQLException;

    void setFCMToken(String id, String fcmToken) throws SQLException;

    void setPassword(String id, String passwordHash) throws SQLException;

    String getPassword(String id) throws SQLException;

    String getUsername(String id) throws SQLException;

    void createUser(User user) throws SQLException;

    void deleteUser(String id) throws SQLException;

    String getFCMToken(String id) throws SQLException;

    boolean isEmailUsed(String email) throws SQLException;
}