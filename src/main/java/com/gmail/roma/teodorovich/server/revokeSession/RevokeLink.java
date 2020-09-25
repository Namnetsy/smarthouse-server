package com.gmail.roma.teodorovich.server.revokeSession;

import com.gmail.roma.teodorovich.server.helper.SecretHelper;
import com.gmail.roma.teodorovich.server.session.Session;

public class RevokeLink {

    private Session session;

    private String secret;

    public RevokeLink(Session session) {
        this.session = session;
        this.secret = SecretHelper.generateSecret(16);
    }

    public RevokeLink(Session session, String secret) {
        this.session = session;
        this.secret = secret;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

}
