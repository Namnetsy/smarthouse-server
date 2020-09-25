package com.gmail.roma.teodorovich.server.email;

public class Email {

    private String email;

    private String verificationCode;

    private int timestamp;

    public String getEmail() {
        return email;
    }

    public Email setEmail(String email) {
        this.email = email;

        return this;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public Email setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;

        return this;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public Email setTimestamp(int timestamp) {
        this.timestamp = timestamp;

        return  this;
    }

    public Email(String email, String verificationCode, int timestamp) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.timestamp = timestamp;
    }

}
