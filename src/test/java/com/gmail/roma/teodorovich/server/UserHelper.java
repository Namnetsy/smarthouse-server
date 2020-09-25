package com.gmail.roma.teodorovich.server;

import com.gmail.roma.teodorovich.server.hub.Hub;
import com.gmail.roma.teodorovich.server.hub.impl.HubDaoImpl;
import com.gmail.roma.teodorovich.server.member.Member;
import com.gmail.roma.teodorovich.server.member.impl.MemberDaoImpl;
import com.gmail.roma.teodorovich.server.session.Session;
import com.gmail.roma.teodorovich.server.session.impl.SessionDaoRedis;
import com.gmail.roma.teodorovich.server.user.User;
import com.gmail.roma.teodorovich.server.user.impl.UserDaoImpl;
import redis.clients.jedis.exceptions.JedisException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserHelper implements AutoCloseable {

    private final User user;

    private Hub hub;

    private Member member;

    private final List<Session> sessions;

    private int lastSessionIdDigit;

    public UserHelper() throws SQLException {
        sessions = new ArrayList<>();
        lastSessionIdDigit = 0;
        hub = null;
        user = new User.Builder()
                .withId("Y29DB2Y")
                .withEmail("john.smith@gmail.com")
                .withUsername("John")
                .withPassHash("1a1dc91c907325c69271ddf0c944bc72") // password: pass
                .build();

        UserDaoImpl.getInstance().createUser(user);
        addSession();
    }

    public UserHelper addSession() throws JedisException {
        Session session = new Session.Builder()
                .withCountry("Country")
                .withCity("City")
                .withIp("123.312.132.123")
                .withAccessToken("217WqwEvqwKehqoI")
                .withSessionId("UEH231" + lastSessionIdDigit)
                .withUserId(user.getId()).build();
        sessions.add(session);
        lastSessionIdDigit++;

        SessionDaoRedis.getInstance().createSession(session);

        return this;
    }

    public UserHelper addToHub() throws SQLException {
        hub = new Hub(user.getId(), "1a1dc91c907325c69271ddf0c944bc72"); // password: pass
        hub.setAccessToken("AccessToken").setId("1234567");

        HubDaoImpl.getInstance().addHub(hub);

        return this;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public User getUser() {
        return user;
    }

    @Override
    public void close() throws SQLException, JedisException {
        if (hub != null) {
            MemberDaoImpl.getInstance().deleteAllMembers(hub.getId());

            HubDaoImpl.getInstance().deleteHub(hub.getId());
        }

        SessionDaoRedis.getInstance().removeAllSessions(user.getId());

        UserDaoImpl.getInstance().deleteUser(user.getId());
    }

    public Hub getHub() {
        return hub;
    }

    public UserHelper setHub(Hub hub) {
        this.hub = hub;

        return this;
    }

    public Member getMember() {
        return member;
    }

}
