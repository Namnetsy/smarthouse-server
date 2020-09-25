package com.gmail.roma.teodorovich.server.hub;

import java.sql.SQLException;

public interface IHubDao {

    void addHub(Hub hub) throws SQLException;

    void deleteHub(String id) throws SQLException;

    void setHubOwner(String hubId, String userId) throws SQLException;

    void setPassword(String hubId, String passHash) throws SQLException;

    boolean isHubIdBelongsToUser(String hubId, String userId) throws SQLException;

    boolean isUserOwnHub(String userId) throws SQLException;

    boolean isHubExists(String hubId) throws SQLException;

    String getHubIdByAccessToken(String accessToken) throws SQLException;

    String getPassword(String hubId) throws SQLException;

    Hub getHubByUserId(String userId) throws SQLException;

}
