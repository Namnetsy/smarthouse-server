package com.gmail.roma.teodorovich.server.user;

import java.security.SecureRandom;

public class User {

    private final String id;

    private String username;

    private String email;

    private String passHash;

    private String fcmToken;

    public static class Builder {

        private String id;

        private String username;

        private String email;

        private String passHash;

        private String fcmToken;

        public User build() {
            return new User(this);
        }

        public  Builder withId(String id) {
            this.id = id;

            return this;
        }

        public Builder withUsername(String username) {
            this.username = username;

            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;

            return this;
        }

        public Builder withPassHash(String passHash) {
            this.passHash = passHash;

            return this;
        }

        public Builder withFcmToken(String fcmToken) {
            this.fcmToken = fcmToken;

            return this;
        }
    }

    private User(Builder builder) {
        if (builder.id == null) {
            char[] allowedChars = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890".toCharArray();
            SecureRandom random = new SecureRandom();
            StringBuilder secretBuilder = new StringBuilder();

            for (int i = 0; i < 7; i++) {
                secretBuilder.append(allowedChars[random.nextInt(allowedChars.length)]);
            }

            id = secretBuilder.toString();
        } else {
            id = builder.id;
        }

        username = builder.username;
        email = builder.email;
        passHash = builder.passHash;
        fcmToken = builder.fcmToken;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassHash() {
        return passHash;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public User setUsername(String username) {
        this.username = username;

        return this;
    }

    public User setEmail(String email) {
        this.email = email;

        return this;
    }

    public User setPassHash(String passHash) {
        this.passHash = passHash;

        return this;
    }

    public User setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;

        return this;
    }

}
