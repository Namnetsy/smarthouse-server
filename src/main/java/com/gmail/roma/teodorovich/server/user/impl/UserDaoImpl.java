package com.gmail.roma.teodorovich.server.user.impl;

import com.gmail.roma.teodorovich.server.helper.RelationDatabaseHelper;
import com.gmail.roma.teodorovich.server.user.IUserDao;
import com.gmail.roma.teodorovich.server.user.User;

import java.sql.SQLException;

public class UserDaoImpl implements IUserDao {

    private static IUserDao userDao = null;

    private UserDaoImpl() {}

    public static synchronized IUserDao getInstance() {
        return (userDao == null) ? userDao = new UserDaoImpl() : userDao;
    }

    @Override
    public void setUsername(String id, String newUsername) throws SQLException {
        try (RelationDatabaseHelper helper =
                new RelationDatabaseHelper("UPDATE user_account SET username = ? WHERE id = ?")) {
            helper.setValue(1, newUsername).setValue(2, id).execute();
        }
    }

    @Override
    public void setEmail(String id, String newEmail) throws SQLException {
        RelationDatabaseHelper helper =
                new RelationDatabaseHelper("UPDATE user_account SET email = ? WHERE id = ?");

        helper.setValue(1, newEmail).setValue(2, id).execute();
    }

    @Override
    public void setFCMToken(String id, String fcmToken) throws SQLException {
        try (RelationDatabaseHelper helper =
                new RelationDatabaseHelper("UPDATE user_account SET fcm_token = ? WHERE id = ?")) {
            helper.setValue(1, fcmToken).setValue(2, id).execute();
        }
    }

    @Override
    public void setPassword(String id, String passwordHash) throws SQLException {
        try (RelationDatabaseHelper helper =
                new RelationDatabaseHelper("UPDATE user_account SET pass_hash = ? WHERE id = ?")) {
            helper.setValue(1, passwordHash).setValue(2, id).execute();
        }
    }

    @Override
    public String getPassword(String id) throws SQLException {
        try (RelationDatabaseHelper helper =
                new RelationDatabaseHelper("SELECT pass_hash FROM user_account WHERE id = ?")) {
            RelationDatabaseHelper.Data data = helper.setValue(1, id).executeWithData();

            return (data.next()) ? (String) data.getValue(1) : null;
        }
    }

    @Override
    public String getUsername(String id) throws SQLException {
        try (RelationDatabaseHelper helper =
                     new RelationDatabaseHelper("SELECT username FROM user_account WHERE id = ?")) {
            RelationDatabaseHelper.Data data = helper.setValue(1, id).executeWithData();

            return (data.next()) ? (String) data.getValue(1) : null;
        }
    }

    @Override
    public User getUser(String email) throws SQLException {
        try (RelationDatabaseHelper helper =
                     new RelationDatabaseHelper("SELECT * FROM user_account WHERE email = ?")) {
            RelationDatabaseHelper.Data data = helper.setValue(1, email).executeWithData();

            if (!data.next()) {
                return null;
            }

            return new User.Builder()
                    .withId((String) data.getValue(1))
                    .withUsername((String) data.getValue(2))
                    .withEmail((String) data.getValue(3))
                    .withPassHash((String) data.getValue(4))
                    .withFcmToken((String) data.getValue(5)).build();
        }
    }

    @Override
    public User getUserById(String id) throws SQLException {
        try (RelationDatabaseHelper helper =
                     new RelationDatabaseHelper("SELECT * FROM user_account WHERE id = ?")) {
            RelationDatabaseHelper.Data data = helper.setValue(1, id).executeWithData();

            if (!data.next()) {
                return null;
            }

            return new User.Builder()
                    .withId((String) data.getValue(1))
                    .withUsername((String) data.getValue(2))
                    .withEmail((String) data.getValue(3))
                    .withPassHash((String) data.getValue(4))
                    .withFcmToken((String) data.getValue(5)).build();
        }
    }

    @Override
    public void deleteUser(String id) throws SQLException {
        try (RelationDatabaseHelper helper =
                new RelationDatabaseHelper("DELETE FROM user_account WHERE id = ?")) {
            helper.setValue(1, id).execute();
        }
    }

    @Override
    public String getFCMToken(String id) throws SQLException {
        try (RelationDatabaseHelper helper =
                     new RelationDatabaseHelper("SELECT fcm_token FROM user_account WHERE id = ?")) {
            RelationDatabaseHelper.Data data = helper.setValue(1, id).executeWithData();

            return (data.next()) ? (String) data.getValue(1) : null;
        }
    }

    @Override
    public boolean isEmailUsed(String email) throws SQLException {
        try (RelationDatabaseHelper helper =
                     new RelationDatabaseHelper("SELECT EXISTS (SELECT email FROM user_account WHERE email = ?)")) {
            RelationDatabaseHelper.Data data = helper.setValue(1, email).executeWithData();

            return data.next() && (boolean) data.getValue(1);
        }
    }

    @Override
    public void createUser(User user) throws SQLException {
        try (RelationDatabaseHelper helper = new RelationDatabaseHelper(
                "INSERT INTO user_account (id, username, email, pass_hash) VALUES (?, ?, ?, ?)")) {
            helper.setValue(1, user.getId())
                    .setValue(2, user.getUsername())
                    .setValue(3, user.getEmail())
                    .setValue(4, user.getPassHash()).execute();
        }
    }

}
