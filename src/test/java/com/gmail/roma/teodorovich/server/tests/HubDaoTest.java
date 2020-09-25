package com.gmail.roma.teodorovich.server.tests;

import com.gmail.roma.teodorovich.server.PostgreSqlUnitTest;
import com.gmail.roma.teodorovich.server.UserHelper;
import com.gmail.roma.teodorovich.server.hub.Hub;
import com.gmail.roma.teodorovich.server.hub.impl.HubDaoImpl;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("[DAO:PostgreSQL] Hub Tests")
public class HubDaoTest extends PostgreSqlUnitTest {

    @Test
    @DisplayName("Add Hub")
    public void addHub() throws SQLException {
        try (UserHelper helper = new UserHelper()) {
            Hub hub = new Hub(helper.getUser()
                    .getId(), "1a1dc91c907325c69271ddf0c944bc72"); // password: pass
            hub.setId("1234567");
            helper.setHub(hub);

            HubDaoImpl.getInstance().addHub(hub);

            HubDaoImpl.getInstance().isHubExists(hub.getId());
        }
    }

    @Test
    @DisplayName("Delete Hub")
    public void deleteHub() throws SQLException {
        try (UserHelper helper = new UserHelper().addToHub()) {
            HubDaoImpl.getInstance().deleteHub(helper.getHub().getId());

            Assertions.assertFalse(HubDaoImpl.getInstance().isHubExists(helper.getHub().getId()));
        }
    }

    @Test
    @DisplayName("Get Hub By User ID")
    public void getHubByUserId() throws SQLException {
        try (UserHelper helper = new UserHelper().addToHub()) {
            Hub hub = HubDaoImpl.getInstance().getHubByUserId(helper.getUser().getId());

            Assertions.assertNotNull(hub);
            Assertions.assertEquals(hub.getId(), hub.getId());
            Assertions.assertEquals(hub.getAccessToken(), hub.getAccessToken());
            Assertions.assertEquals(hub.getPassHash(), hub.getPassHash());
            Assertions.assertEquals(hub.getAdminId(), hub.getAdminId());
        }
    }

    @Test
    @DisplayName("Get Hub Id By Access Token")
    public void getHubIdByAccessToken() throws SQLException {
        try (UserHelper helper = new UserHelper().addToHub()) {
            String hubId = HubDaoImpl.getInstance()
                    .getHubIdByAccessToken(helper.getHub().getAccessToken());

            Assertions.assertNotNull(hubId);
            Assertions.assertEquals(helper.getHub().getId(), hubId);
        }
    }

    @Test
    @DisplayName("Check If Hub Belongs To Useer")
    public void checkIfHubBelongsToUser() throws SQLException {
        try (UserHelper helper = new UserHelper().addToHub()) {
            Assertions.assertTrue(HubDaoImpl.getInstance()
                    .isHubIdBelongsToUser(helper.getHub().getId(), helper.getUser().getId()));
        }
    }

    @Test
    @DisplayName("Check If Hub Not Belongs To User")
    public void checkIfHubNotBelongsToUser() throws SQLException {
        try (UserHelper helper = new UserHelper().addToHub()) {
            Assertions.assertFalse(HubDaoImpl.getInstance().isHubIdBelongsToUser(helper.getHub().getId(), "1234567"));
        }
    }

    @Test
    @DisplayName("Check If User Owns Hub")
    public void checkIfUserOwnsHub() throws SQLException {
        try (UserHelper helper = new UserHelper().addToHub()) {
            Assertions.assertTrue(HubDaoImpl.getInstance()
                    .isUserOwnHub(helper.getUser().getId()));
        }
    }

    @Test
    @DisplayName("Check If User Doesn't Own Hub")
    public void checkIfUserDoesntOwnHub() throws SQLException {
        try (UserHelper helper = new UserHelper()) {
            Assertions.assertFalse(HubDaoImpl.getInstance().isUserOwnHub("1234567"));
        }
    }

    @Test
    @DisplayName("Check If Hub Exists")
    public void checkIfHubExists() throws SQLException {
        try (UserHelper helper = new UserHelper().addToHub()) {
            Assertions.assertTrue(HubDaoImpl.getInstance().isHubExists(helper.getHub().getId()));
        }
    }

    @Test
    @DisplayName("Check If Hub Doesn't Exist")
    public void checkIfHubDoesntExist() throws SQLException {
        boolean hubExists = HubDaoImpl.getInstance().isHubExists("1234567");

        Assertions.assertFalse(hubExists);
    }

}
