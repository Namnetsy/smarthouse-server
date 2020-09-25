package com.gmail.roma.teodorovich.server.revokeSession;

import com.gmail.roma.teodorovich.server.helper.ControllerHelper;
import com.gmail.roma.teodorovich.server.helper.HtmlHelper;
import com.gmail.roma.teodorovich.server.revokeSession.impl.RevokeLinkRedis;
import com.gmail.roma.teodorovich.server.session.impl.SessionDaoRedis;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class RevokeLinkController {

    private static final HtmlHelper successLetter;

    private static final HtmlHelper failureLetter;

    static {
        successLetter = new HtmlHelper().loadHtmlFromFile("html/revoke-session-success.html");
        failureLetter = new HtmlHelper().loadHtmlFromFile("html/revoke-session-failure.html");
    }

    public static void revokeSession(@NotNull Context ctx) {
        String secret = ctx.pathParam("secret");

        new ControllerHelper(ctx)
                .doLogic(controllerCtx -> {
                    IRevokeLinkDao revokeLinkDao = RevokeLinkRedis.getInstance();
                    String html;

                    if (revokeLinkDao.exists(secret)) {
                        Map<String, String> data = revokeLinkDao.getRevokeLinkData(secret);
                        String sessionId = data.get("sid");
                        revokeLinkDao.deleteLink(secret);

                        SessionDaoRedis.getInstance().removeSession(data.get("uid"), sessionId);

                        html = successLetter.fillPlaceholder("<[session_id]>", sessionId).getCompactHtml();
                        successLetter.resetPlaceholders();
                    } else {
                        html = failureLetter.getCompactHtml();
                    }

                    ctx.html(html);
                }).end();
    }

}
