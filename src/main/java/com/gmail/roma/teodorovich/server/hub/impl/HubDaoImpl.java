package com.gmail.roma.teodorovich.server.hub.impl;

import com.gmail.roma.teodorovich.server.helper.RelationDatabaseHelper;
import com.gmail.roma.teodorovich.server.hub.Hub;
import com.gmail.roma.teodorovich.server.hub.IHubDao;

import java.sql.SQLException;

public class HubDaoImpl implements IHubDao {

    private static IHubDao hubDao = null;

    private HubDaoImpl() {}

    public static synchronized IHubDao getInstance() {
        return (hubDao == null) ? hubDao = new HubDaoImpl() : hubDao;
    }

    @Override
    public void addHub(Hub hub) throws SQLException {
        try (RelationDatabaseHelper helper = new RelationDatabaseHelper("INSERT INTO hub VALUES (?, ?, ?, ?)")) {
            helper.setValue(1, hub.getId())
                    .setValue(2,hub.getAdminId())
                    .setValue(3, hub.getPassHash())
                    .setValue(4, hub.getAccessToken())
                    .execute();
        }
    }

    @Override
    public Hub getHubByUserId(String userId) throws SQLException {
        try (RelationDatabaseHelper helper = new RelationDatabaseHelper("SELECT * FROM hub WHERE user_id = ?")) {
            Hub hub = null;

            RelationDatabaseHelper.Data data = helper
                    .setValue(1, userId)
                    .executeWithData();

            while (data.next()) {
                hub = new Hub((String)data.getValue(2), (String)data.getValue(3));
                hub.setId((String)data.getValue(1));
                hub.setAccessToken((String)data.getValue(4));
            }

            return hub;
        }
    }

    @Override
    public String getHubIdByAccessToken(String accessToken) throws SQLException {
        try (RelationDatabaseHelper helper =
                     new RelationDatabaseHelper("SELECT id FROM hub WHERE access_token = ?")) {
            RelationDatabaseHelper.Data data = helper.setValue(1, accessToken)
                    .executeWithData();

            return data.next() ? (String) data.getValue(1) : null;
        }
    }

    @Override
    public String getPassword(String hubId) throws SQLException {
        try (RelationDatabaseHelper helper =
                     new RelationDatabaseHelper("SELECT pass_hash FROM hub WHERE id = ?")) {
            RelationDatabaseHelper.Data data = helper.setValue(1, hubId).executeWithData();

            return (data.next()) ? (String) data.getValue(1) : null;
        }
    }

    @Override
    public boolean isHubIdBelongsToUser(String hubId, String userId) throws SQLException {
        try (RelationDatabaseHelper helper = new RelationDatabaseHelper(
                "SELECT EXISTS (SELECT id FROM hub WHERE user_id = ? AND id = ?)")) {
            RelationDatabaseHelper.Data data = helper
                    .setValue(1, userId)
                    .setValue(2, hubId)
                    .executeWithData();

            return data.next() && (boolean) data.getValue(1);
        }
    }

    @Override
    public void deleteHub(String id) throws SQLException {
        try (RelationDatabaseHelper helper = new RelationDatabaseHelper("DELETE FROM hub WHERE id = ?")) {
            helper.setValue(1, id).execute();
        }
    }

    @Override
    public void setHubOwner(String hubId, String userId) throws SQLException {
        try (RelationDatabaseHelper helper =
                     new RelationDatabaseHelper("UPDATE hub SET user_id = ? WHERE id = ?")) {
            helper.setValue(1, userId).setValue(2, hubId).execute();
        }
    }

    @Override
    public boolean isUserOwnHub(String userId) throws SQLException {
        try (RelationDatabaseHelper helper = new RelationDatabaseHelper(
                "SELECT EXISTS (SELECT user_id FROM hub WHERE user_id = ?)")) {
            RelationDatabaseHelper.Data data = helper.setValue(1, userId).executeWithData();

            return data.next() && (boolean) data.getValue(1);
        }
    }

    @Override
    public boolean isHubExists(String hubId) throws SQLException {
        try (RelationDatabaseHelper helper = new RelationDatabaseHelper(
                "SELECT EXISTS (SELECT id FROM hub WHERE id = ?)")) {
            RelationDatabaseHelper.Data data = helper.setValue(1, hubId).executeWithData();

            return data.next() && (boolean) data.getValue(1);
        }
    }

    @Override
    public void setPassword(String hubId, String passHash) throws SQLException {
        try (RelationDatabaseHelper helper =
                     new RelationDatabaseHelper("UPDATE hub SET pass_hash = ? WHERE id = ?")) {
            helper.setValue(1, passHash).setValue(2, hubId).execute();
        }
    }

}
