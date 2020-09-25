package com.gmail.roma.teodorovich.server.session;

import com.gmail.roma.teodorovich.server.helper.ControllerHelper;
import com.gmail.roma.teodorovich.server.session.impl.SessionDaoRedis;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class SessionController {

    public static void getSessions(@NotNull Context ctx) {
        String userId = ctx.pathParam("id");

        new ControllerHelper(ctx)
                .validateAccessToken()
                .doLogic(controllerCtx -> {
                    if (!userId.equals(controllerCtx.getUserData().get("uid"))) {
                        controllerCtx.returnForbidden();
                    } else {
                        Map<String, Map<String, String>> sessions = SessionDaoRedis.getInstance().getSessions(userId);

                        if (sessions.isEmpty()) {
                            controllerCtx.returnNoContent(); // It is impossible!!!
                        } else {
                            controllerCtx.returnOK();
                            ctx.json(sessions);
                        }
                    }
                }).end();
    }

    public static void removeSession(@NotNull Context ctx) {
        String userId = ctx.pathParam("userId");
        String sessionId = ctx.pathParam("sessionId");

        new ControllerHelper(ctx)
                .validateAccessToken()
                .doLogic(controllerCtx -> {
                    if (!userId.equals(controllerCtx.getUserData().get("uid"))) {
                        controllerCtx.returnForbidden();
                    } else {
                        SessionDaoRedis.getInstance().removeSession(userId, sessionId);

                        controllerCtx.returnOK();
                    }
                }).end();
    }

}
