package com.gmail.roma.teodorovich.server.session;

import com.gmail.roma.teodorovich.server.helper.SecretHelper;

public class Session {

    private String userId;

    private String sessionId;

    private String ip;

    private String country;

    private String city;

    private String accessToken;

    public static class Builder {

        private String userId;

        private String sessionId;

        private String ip;

        private String country;

        private String city;

        private String accessToken;

        public Builder() {
            sessionId = SecretHelper.generateSecret(7);
        }

        public Builder withUserId(String userId) {
            this.userId = userId;

            return this;
        }

        public Builder withSessionId(String sessionId) {
            this.sessionId = sessionId;

            return this;
        }

        public Builder withIp(String ip) {
            this.ip = ip;

            return this;
        }

        public Builder withCountry(String country) {
            this.country = country;

            return this;
        }

        public Builder withCity(String city) {
            this.city = city;

            return this;
        }

        public Builder withAccessToken() {
            accessToken = SecretHelper.generateSecret(16);

            return this;
        }

        public Builder withAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Session build() {
            return new Session(this);
        }

    }

    public Session(Builder builder) {
        this.userId = builder.userId;
        this.sessionId = builder.sessionId;
        this.ip = builder.ip;
        this.country = builder.country;
        this.city = builder.city;
        this.accessToken = builder.accessToken;
    }

    public String getUserId() {
        return userId;
    }

    public Session setUserId(String userId) {
        this.userId = userId;

        return this;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Session setSessionId(String sessionId) {
        this.sessionId = sessionId;

        return this;
    }

    public String getIp() {
        return ip;
    }

    public Session setIp(String ip) {
        this.ip = ip;

        return this;
    }

    public String getCountry() {
        return country;
    }

    public Session setCountry(String country) {
        this.country = country;

        return this;
    }

    public String getCity() {
        return city;
    }

    public Session setCity(String city) {
        this.city = city;

        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Session setAccessToken(String accessToken) {
        this.accessToken = accessToken;

        return this;
    }
}
