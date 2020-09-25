package com.gmail.roma.teodorovich.server.tests;

import com.gmail.roma.teodorovich.server.PostgreSqlUnitTest;
import com.gmail.roma.teodorovich.server.UserHelper;
import com.gmail.roma.teodorovich.server.user.User;
import com.gmail.roma.teodorovich.server.user.impl.UserDaoImpl;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("[DAO:PostgreSQL] User Tests")
public class UserDaoTest extends PostgreSqlUnitTest {

    @Test
    @DisplayName("Create a user")
    public void createUser() throws SQLException {
        User user = new User.Builder()
                .withId("Y29DB2Y")
                .withEmail("john.smith@gmail.com")
                .withUsername("John")
                .withPassHash("hashedPassword").build();

        UserDaoImpl.getInstance().createUser(user);

        Assertions.assertTrue(UserDaoImpl.getInstance().isEmailUsed(user.getEmail()));

        UserDaoImpl.getInstance().deleteUser(user.getId());
    }

    @Test
    @DisplayName("Get a user by ID")
    public void getUserById() throws SQLException {
        try (UserHelper helper = new UserHelper()) {
            Assertions.assertEquals(helper.getUser().getId(),
                    UserDaoImpl.getInstance().getUserById(helper.getUser().getId()).getId());
        }
    }

    @Test
    @DisplayName("Change password")
    public void setPassword() throws SQLException {
        try (UserHelper helper = new UserHelper()) {
            UserDaoImpl.getInstance().setPassword(helper.getUser().getId(), "newPassword");

            Assertions.assertEquals("newPassword",
                    UserDaoImpl.getInstance().getUserById(helper.getUser().getId()).getPassHash());
        }
    }

    @Test
    @DisplayName("Get password")
    public void getPassword() throws SQLException {
        try (UserHelper helper = new UserHelper()) {
            Assertions.assertEquals(helper.getUser().getPassHash(),
                    UserDaoImpl.getInstance().getPassword(helper.getUser().getId()));
        }
    }

    @Test
    @DisplayName("Delete a user")
    public void deleteUser() throws SQLException {
        try (UserHelper helper = new UserHelper()) {
            UserDaoImpl.getInstance().deleteUser(helper.getUser().getId());

            Assertions.assertFalse(UserDaoImpl.getInstance().isEmailUsed(helper.getUser().getEmail()));
        }
    }

    @Test
    @DisplayName("Set FCM token")
    public void setFCMtoken() throws SQLException {
        try (UserHelper helper = new UserHelper()) {
            String newToken = "NewToken";
            UserDaoImpl.getInstance().setFCMToken(helper.getUser().getId(), newToken);

            Assertions.assertEquals(newToken, UserDaoImpl.getInstance().getFCMToken(helper.getUser().getId()));
        }
    }

    @Test
    @DisplayName("Get FCM token")
    public void getFCMtoken() throws SQLException {
        try (UserHelper helper = new UserHelper()) {
            String token = UserDaoImpl.getInstance().getFCMToken(helper.getUser().getId());

            Assertions.assertEquals(helper.getUser().getFcmToken(), token);
        }
    }

    @Test
    @DisplayName("Change username")
    public void setUsername() throws SQLException {
        try (UserHelper helper = new UserHelper()) {
            String newUsername = "NewUsername";
            UserDaoImpl.getInstance().setUsername(helper.getUser().getId(), newUsername);

            Assertions.assertEquals(newUsername,
                    UserDaoImpl.getInstance().getUsername(helper.getUser().getId()));
        }
    }

    @Test
    @DisplayName("Get username")
    public void getUsername() throws SQLException {
        try (UserHelper helper = new UserHelper()) {
            Assertions.assertEquals(helper.getUser().getUsername(),
                    UserDaoImpl.getInstance().getUsername(helper.getUser().getId()));
        }
    }

    @Test
    @DisplayName("Get a user by email")
    public void getUserByEmail() throws SQLException {
        try (UserHelper helper = new UserHelper()) {
            Assertions.assertEquals(helper.getUser().getId(),
                    UserDaoImpl.getInstance().getUserById(helper.getUser().getId()).getId());
        }
    }

    @Test
    @DisplayName("Change email")
    public void setEmail() throws SQLException {
        try (UserHelper helper = new UserHelper()) {
            String newEmail = "new@email.com";
            UserDaoImpl.getInstance().setEmail(helper.getUser().getId(), newEmail);

            Assertions.assertEquals(newEmail,
                    UserDaoImpl.getInstance().getUserById(helper.getUser().getId()).getEmail());
        }
    }

}
