package com.gmail.roma.teodorovich.server.ws;

import org.eclipse.jetty.websocket.api.Session;

public class ConnectedHub {

    private String hubId;

    private String accessToken;

    private Session session;

    public ConnectedHub(String hubId, String accessToken, Session session) {
        this.hubId = hubId;
        this.accessToken = accessToken;
        this.session = session;
    }

    public String getHubId() {
        return hubId;
    }

    public void setHubId(String hubId) {
        this.hubId = hubId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
