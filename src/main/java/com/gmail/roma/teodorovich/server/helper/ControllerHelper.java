package com.gmail.roma.teodorovich.server.helper;

import com.gmail.roma.teodorovich.server.App;
import io.javalin.http.Context;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Map;

public class ControllerHelper {

    public interface Logic {

        void logic(ControllerContext controllerCtx) throws SQLException, JedisException;

    }

    public static class ControllerContext {

        ControllerHelper helper;

        public ControllerContext(ControllerHelper helper) {
            this.helper = helper;
        }

        public Map<String, String> getUserData() {
            return helper.userData;
        }

        public void returnInternalError() {
            helper.status = 500;
        }

        public void returnAccepted() {
            helper.status = 202;
        }

        public void returnCreated() {
            helper.status = 201;
        }

        public void returnOK() {
            helper.status = 200;
        }

        public void returnUnauthorized() {
            helper.status = 401;
        }

        public void returnServiceUnavailable() {
            helper.status = 503;
        }

        public void returnTimeout() {
            helper.status = 408;
        }

        public void returnNoContent() {
            helper.status = 204;
        }

        public void returnConflict() {
            helper.status = 409;
        }

        public void returnNotFound() {
            helper.status = 404;
        }

        public void returnBadRequest() {
            helper.status = 400;
        }

        public void returnForbidden() {
            helper.status = 403;
        }

    }

    private final Context ctx;

    private int status;

    private Map<String, String> userData;

    public ControllerHelper(Context ctx) {
        this.ctx = ctx;
        this.status = 200;
        this.userData = null;
    }

    public ControllerHelper validateAccessToken() {
        if (status != 200) {
            return this;
        }

        String authHeader = ctx.header("Authorization");
        String accessToken = (authHeader != null) ? AccessTokenHelper.parseAuthHeader(authHeader) : null;

        if (authHeader == null || accessToken == null) {
            status = 400;
        } else {
            userData = AccessTokenHelper.getUserData(accessToken);

            if (userData.isEmpty()) {
                status = 401;
            }
        }

        return this;
    }

    public ControllerHelper doLogic(Logic logic) {
        if (status != 200) {
            return this;
        }

        try {
            logic.logic(new ControllerContext(this));
        } catch (SQLException | JedisException e) {
            e.printStackTrace();
            status = 500;

            if (e instanceof SQLException) {
                System.out.println(((SQLException)e).getErrorCode());
            }
        }

        return this;
    }

    public ControllerHelper checkIfAnyNull(Object... fields) {
        if (status != 200) {
            return this;
        }

        for (Object field : fields) {
            if (field == null) {
                status = 400;

                return this;
            }
        }

        return this;
    }

    public void end() {
        ctx.status(status);
    }

}
