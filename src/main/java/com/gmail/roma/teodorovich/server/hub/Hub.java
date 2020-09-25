package com.gmail.roma.teodorovich.server.hub;

import java.security.SecureRandom;

public class Hub {

    private String id;

    private String adminId;

    private String passHash;

    private String accessToken;

    public Hub(String adminId, String passHash) {
        this.passHash = passHash;
        this.adminId = adminId;

        char[] allowedChars = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890".toCharArray();
        SecureRandom random = new SecureRandom();
        StringBuilder secretBuilder = new StringBuilder();

        for (int i = 0; i < 16; i++) {
            secretBuilder.append(allowedChars[random.nextInt(allowedChars.length)]);
        }

        this.accessToken = secretBuilder.toString();
        secretBuilder.setLength(0);

        allowedChars = "QWERTYUPAFHJKLXCVNM1234567890".toCharArray();

        for (int i = 0; i < 7; i++) {
            secretBuilder.append(allowedChars[random.nextInt(allowedChars.length)]);
        }

        this.id = secretBuilder.toString();
    }

    public String getId() {
        return id;
    }

    public Hub setId(String id) {
        this.id = id;

        return this;
    }


    public String getAdminId() {
        return adminId;
    }

    public Hub setAdminId(String adminId) {
        this.adminId = adminId;

        return this;
    }

    public String getPassHash() {
        return passHash;
    }

    public Hub setPassHash(String passHash) {
        this.passHash = passHash;

        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Hub setAccessToken(String accessToken) {
        this.accessToken = accessToken;

        return this;
    }
}
