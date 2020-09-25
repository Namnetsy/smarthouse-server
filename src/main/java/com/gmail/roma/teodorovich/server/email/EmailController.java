package com.gmail.roma.teodorovich.server.email;

import com.gmail.roma.teodorovich.server.email.impl.EmailDaoRedis;
import com.gmail.roma.teodorovich.server.helper.ControllerHelper;
import com.gmail.roma.teodorovich.server.helper.EmailHelper;
import com.gmail.roma.teodorovich.server.helper.HtmlHelper;
import com.gmail.roma.teodorovich.server.helper.JsonHelper;
import com.gmail.roma.teodorovich.server.user.impl.UserDaoImpl;
import com.google.gson.*;
import io.javalin.http.Context;

public class EmailController {

    private static final HtmlHelper htmlHelper;

    static {
        htmlHelper = new HtmlHelper().loadHtmlFromFile("html/verification-code.html");
    }

    public static void sendVerificationCode(Context ctx) {
        IEmailDao emailDao = EmailDaoRedis.getInstance();
        String email = null;

        try {
            email = new JsonHelper(ctx.body()).getAsObject().get("email").getAsString();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        String finalEmail = email;
        new ControllerHelper(ctx)
                .checkIfAnyNull(email)
                .doLogic(controllerCtx -> {
                    if (emailDao.checkEmailRecord(finalEmail) || UserDaoImpl.getInstance().isEmailUsed(finalEmail)) {
                        controllerCtx.returnConflict();
                    } else {
                        String code = EmailHelper.generateVerificationCode();
                        String htmlBody = htmlHelper.fillPlaceholder("<[verification_code]>", code)
                                .getCompactHtml();
                        htmlHelper.resetPlaceholders();

                        if (EmailDaoRedis.getInstance().checkEmailRecord(finalEmail)) {
                            controllerCtx.returnConflict();
                        } else if (EmailHelper.sendEmail(finalEmail, "Підтвердження електронної пошти", htmlBody)) {
                            emailDao.saveVerificationRecord(finalEmail, code, 5 * 60);
                            controllerCtx.returnAccepted();
                        } else {
                            controllerCtx.returnServiceUnavailable();
                        }
                    }
                }).end();
    }

    public static void checkVerificationCode(Context ctx) {
        String email = null;
        String code = null;

        try {
            JsonObject jsonObject = new JsonHelper(ctx.body()).getAsObject();

            email = jsonObject.get("email").getAsString();
            code = jsonObject.get("code").getAsString();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        String finalEmail = email;
        String finalCode = code;
        new ControllerHelper(ctx)
                .checkIfAnyNull(email, code)
                .doLogic(controllerCtx -> {
                    if (EmailDaoRedis.getInstance().checkVerificationCode(finalEmail, finalCode)) {
                        controllerCtx.returnOK();
                    } else {
                        controllerCtx.returnNotFound();
                    }
                }).end();
    }

}
