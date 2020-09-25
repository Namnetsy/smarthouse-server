package com.gmail.roma.teodorovich.server.member;

public class Member {

    private String userId;

    private String hubId;

    private boolean isAdmin;

    public Member(String userId, String hubId) {
        this.userId = userId;
        this.hubId = hubId;
        isAdmin = false;
    }

    public Member(String userId, String hubId, boolean isAdmin) {
        this.userId = userId;
        this.hubId = hubId;
        this.isAdmin = isAdmin;
    }

    public String getUserId() {
        return userId;
    }

    public Member setUserId(String userId) {
        this.userId = userId;

        return this;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public Member setAdmin(boolean admin) {
        isAdmin = admin;

        return this;
    }

    public String getHubId() {
        return hubId;
    }

    public Member setHubId(String hubId) {
        this.hubId = hubId;

        return this;
    }
}
