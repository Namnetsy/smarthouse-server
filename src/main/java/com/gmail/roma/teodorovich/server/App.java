package com.gmail.roma.teodorovich.server;

import com.gmail.roma.teodorovich.server.db.SqlScriptRunner;
import com.gmail.roma.teodorovich.server.email.EmailController;
import com.gmail.roma.teodorovich.server.helper.EmailHelper;
import com.gmail.roma.teodorovich.server.helper.IpHelper;
import com.gmail.roma.teodorovich.server.hub.HubController;
import com.gmail.roma.teodorovich.server.member.MemberController;
import com.gmail.roma.teodorovich.server.revokeSession.RevokeLinkController;
import com.gmail.roma.teodorovich.server.session.SessionController;
import com.gmail.roma.teodorovich.server.user.UserController;
import com.gmail.roma.teodorovich.server.ws.WebSocketAPI;
import com.google.gson.Gson;
import io.javalin.Javalin;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class App {

    public static final String SERVER_URL;

    private static Logger logger;

    private static Javalin app;

    static {
        SERVER_URL = "http://" + (((Config.getIsOnline()) ? IpHelper.getMyGlobalIP() : "localhost") + ":" + Config.getPort());
    }

    public static void main(String[] args) {
        startServer();
    }

    public static void startServer() {
        try {
            SqlScriptRunner.execute("create_tables.sql");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        logger = Configurator.initialize("App", "log4j2.xml").getLogger("rollingFile");

        app = Javalin.create();
        app.config.requestLogger((ctx, executionTimeMs) -> {
            Map<String, Object> data = new HashMap<>();
            data.put("method", ctx.method());
            data.put("http-status-code", ctx.status());
            data.put("auth-header", ctx.headerMap().getOrDefault("Authorization", null));
            data.put("content-length", ctx.contentLength());
            data.put("content-type", ctx.contentType());
            data.put("full-url", ctx.fullUrl());
            data.put("execution-time-ms", executionTimeMs);
            data.put("body", ctx.body());

            if (ctx.status() == 500) {
                String htmlBody = "<b>Method</b>: " + ctx.method() + "<br>"
                        + "<b>Full URL</b>: " + ctx.fullUrl() + "<br>"
                        + "<b>Authorization Header</b>: "
                        + ctx.headerMap().getOrDefault("Authorization", null) + "<br>"
                        + "<b>Content-Type</b>: " + ctx.contentType() + "<br>"
                        + "<b>Content-Length</b>: " + ctx.contentLength() + "<br>"
                        + "<b>Execution time in ms</b>: " + executionTimeMs + "<br>"
                        + "<b>Body</b>: " + ctx.body();
                EmailHelper.sendEmail(Config.getAdminEmail(),
                        "Internal Server Error Occurred", htmlBody);
            }

            logger.debug(new Gson().toJson(data));
        });
        app.config.wsLogger(wsHandler -> {
            wsHandler.onConnect(ctx -> {
                Map<String, String> data = new HashMap<>();
                data.put("event", "on_connect");
                data.put("auth-header", ctx.headerMap().getOrDefault("Authorization", null));
                data.put("hub-id", ctx.pathParam("id"));

                logger.debug(new Gson().toJson(data));
            });

            wsHandler.onMessage(ctx -> {
                Map<String, String> data = new HashMap<>();
                data.put("event", "on_message");
                data.put("auth-header", ctx.headerMap().getOrDefault("Authorization", null));
                data.put("hub-id", ctx.pathParam("id"));
                data.put("message", ctx.message());

                logger.debug(new Gson().toJson(data));
            });

            wsHandler.onBinaryMessage(ctx -> {
                Map<String, String> data = new HashMap<>();
                data.put("event", "on_binary_message");
                data.put("auth-header", ctx.headerMap().getOrDefault("Authorization", null));
                data.put("hub-id", ctx.pathParam("id"));

                logger.debug(new Gson().toJson(data));
            });

            wsHandler.onError(ctx -> {
                Map<String, String> data = new HashMap<>();
                data.put("event", "on_error");
                data.put("auth-header", ctx.headerMap().getOrDefault("Authorization", null));
                data.put("hub-id", ctx.pathParam("id"));
                data.put("error", Objects.requireNonNull(ctx.error()).getMessage());

                logger.debug(new Gson().toJson(data));
            });

            wsHandler.onClose(ctx -> {
                Map<String, String> data = new HashMap<>();
                data.put("event", "on_close");
                data.put("auth-header", ctx.headerMap().getOrDefault("Authorization", null));
                data.put("hub-id", ctx.pathParam("id"));
                data.put("reason", ctx.reason());

                logger.debug(new Gson().toJson(data));
            });
        });
        app.start(Integer.parseInt(Config.getPort()));

        app.post("/users", UserController::addNewUser);
        app.post("/users/auth", UserController::authUser);
        app.post("/users/sendVerificationCode", EmailController::sendVerificationCode);
        app.post("/users/checkVerificationCode", EmailController::checkVerificationCode);
        app.post("/hubs/:id", HubController::sendCommand);
        app.post("/members/:id", MemberController::addMember);
        app.post("/hubs/:id/leave", HubController::leaveHub);

        app.put("/users/:id/email", UserController::updateEmail);
        app.put("/users/:id/password", UserController::updatePassword);
        app.put("/users/:id/username", UserController::updateUsername);
        app.put("/users/:id/fcmToken", UserController::updateFCMToken);
        app.put("/users/:id/hub", HubController::addHub);
        app.put("/hubs/:id/password", HubController::changePassword);

        app.get("/hubs/:id/members", MemberController::getMembers);
        app.get("/users/:id/fcmToken", UserController::getFCMToken);
        app.get("/users/:id/sessions", SessionController::getSessions);
        app.get("/revoke-link/:secret", RevokeLinkController::revokeSession);

        app.delete("/users/:id", UserController::deleteUser);
        app.delete("/hubs/:id", HubController::deleteHub);
        app.delete("/users/:userId/sessions/:sessionId", SessionController::removeSession);
        app.delete("/members/:id", MemberController::deleteMember);

        app.ws("/hubs/:id", WebSocketAPI::endpoint);
    }

    public static void stopServer() {
        app.stop();
    }

}
