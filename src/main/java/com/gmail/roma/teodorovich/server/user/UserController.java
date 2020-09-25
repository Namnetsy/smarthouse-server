package com.gmail.roma.teodorovich.server.user;

import com.gmail.roma.teodorovich.server.App;
import com.gmail.roma.teodorovich.server.Config;
import com.gmail.roma.teodorovich.server.email.IEmailDao;
import com.gmail.roma.teodorovich.server.email.impl.EmailDaoRedis;
import com.gmail.roma.teodorovich.server.helper.*;
import com.gmail.roma.teodorovich.server.hub.Hub;
import com.gmail.roma.teodorovich.server.hub.IHubDao;
import com.gmail.roma.teodorovich.server.hub.impl.HubDaoImpl;
import com.gmail.roma.teodorovich.server.member.IMemberDao;
import com.gmail.roma.teodorovich.server.member.impl.MemberDaoImpl;
import com.gmail.roma.teodorovich.server.revokeSession.RevokeLink;
import com.gmail.roma.teodorovich.server.revokeSession.impl.RevokeLinkRedis;
import com.gmail.roma.teodorovich.server.session.ISessionDao;
import com.gmail.roma.teodorovich.server.session.Session;
import com.gmail.roma.teodorovich.server.session.impl.SessionDaoRedis;
import com.gmail.roma.teodorovich.server.user.impl.UserDaoImpl;
import com.google.gson.*;
import io.javalin.http.Context;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class UserController {

    private static final HtmlHelper htmlHelper;

    static {
        htmlHelper = new HtmlHelper().loadHtmlFromFile("html/new-log-in.html");
    }

    public static void addNewUser(Context ctx) {
        IUserDao userDao = UserDaoImpl.getInstance();
        IEmailDao emailDao = EmailDaoRedis.getInstance();

        String email = null, code = null,
                password = null, username = null;

        try {
            JsonObject jsonObject = new JsonHelper(ctx.body()).getAsObject();

            email = jsonObject.get("email").getAsString();
            code = jsonObject.get("verification_code").getAsString();
            password = jsonObject.get("password").getAsString();
            username = jsonObject.get("username").getAsString();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        String finalCode = code;
        String finalEmail = email;
        String finalUsername = username;
        String finalPassword = password;
        new ControllerHelper(ctx)
                .checkIfAnyNull(email, code, password, username)
                .doLogic(controllerCtx -> {
                    // Check if email's confirmed
                    if (!emailDao.checkVerificationCode(finalEmail, finalCode)) { // If the verification code is wrong
                        controllerCtx.returnUnauthorized();
                    } else { // Everything's fine so we can create the user
                        userDao.createUser(new User.Builder()
                                .withUsername(finalUsername)
                                .withEmail(finalEmail)
                                .withPassHash(HashHelper.hashString(finalPassword))
                                .build());

                        // Removing the verification code from db
                        emailDao.removeVerificationRecord(finalEmail);

                        controllerCtx.returnCreated();
                    }
                }).end();
    }

    public static void updateEmail(Context ctx) {
        IEmailDao emailDao = EmailDaoRedis.getInstance();
        String userId = ctx.pathParam("id");
        String email = null;
        String code = null;

        try {
            JsonObject object = new JsonHelper(ctx.body()).getAsObject();

            email = object.get("email").getAsString();
            code = object.get("verification_code").getAsString();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        String finalEmail = email;
        String finalCode = code;
        String finalEmail1 = email;
        new ControllerHelper(ctx)
                .checkIfAnyNull(email, code)
                .validateAccessToken()
                .doLogic(controllerCtx -> {
                    if (!userId.equals(controllerCtx.getUserData().get("uid"))) {
                        controllerCtx.returnForbidden();
                    } else if (!emailDao.checkVerificationCode(finalEmail1, finalCode)) {
                        controllerCtx.returnNotFound();
                    } else {
                        emailDao.removeVerificationRecord(finalEmail);
                        UserDaoImpl.getInstance().setEmail(userId, finalEmail);

                        controllerCtx.returnCreated();
                    }
                }).end();
    }

    public static void deleteUser(Context ctx) {
        String userId = ctx.pathParam("id");

        new ControllerHelper(ctx)
                .validateAccessToken()
                .doLogic(controllerCtx -> {
                    IHubDao hubDao = HubDaoImpl.getInstance();
                    IMemberDao memberDao = MemberDaoImpl.getInstance();

                    if (!controllerCtx.getUserData().get("uid").equals(userId)) {
                        controllerCtx.returnForbidden();
                    } else if (hubDao.isUserOwnHub(userId)) {
                        Hub hub = hubDao.getHubByUserId(userId);
                        memberDao.deleteAllMembers(hub.getId());
                        hubDao.deleteHub(hub.getId());
                    } else {
                        SessionDaoRedis.getInstance().removeAllSessions(userId);

                        if (memberDao.isUserMember(userId)) {
                            memberDao.deleteMember(userId);
                        }

                        UserDaoImpl.getInstance().deleteUser(userId);

                        controllerCtx.returnOK();
                    }
                }).end();
    }

    public static void updateUsername(Context ctx) {
        String userId = ctx.pathParam("id");
        JsonPrimitive jsonPrimitive = new JsonHelper(ctx.body()).getAsPrimitive();
        String newUsername = (jsonPrimitive != null) ? jsonPrimitive.getAsString() : null;

        new ControllerHelper(ctx)
                .checkIfAnyNull(newUsername)
                .validateAccessToken()
                .doLogic(controllerCtx -> {
                    if (!userId.equals(controllerCtx.getUserData().get("uid"))) {
                        controllerCtx.returnForbidden();
                    } else {
                        UserDaoImpl.getInstance().setUsername(userId, newUsername);

                        controllerCtx.returnCreated();
                    }
                }).end();
    }

    public static void updateFCMToken(Context ctx) {
        String userId = ctx.pathParam("id");
        JsonPrimitive jsonPrimitive = new JsonHelper(ctx.body()).getAsPrimitive();
        String fcmToken = (jsonPrimitive != null) ? jsonPrimitive.getAsString() : null;

        new ControllerHelper(ctx)
                .checkIfAnyNull(fcmToken)
                .validateAccessToken()
                .doLogic(controllerCtx -> {
                    if (!userId.equals(controllerCtx.getUserData().get("uid"))) {
                        controllerCtx.returnForbidden();
                    } else {
                        UserDaoImpl.getInstance().setFCMToken(userId, fcmToken);

                        controllerCtx.returnCreated();
                    }
                }).end();
    }

    public static void getFCMToken(Context ctx) {
        String userId = ctx.pathParam("id");

        new ControllerHelper(ctx)
                .validateAccessToken()
                .doLogic(controllerCtx -> {
                    if (!userId.equals(controllerCtx.getUserData().get("uid"))) {
                        controllerCtx.returnForbidden();
                    } else {
                        String token = UserDaoImpl.getInstance().getFCMToken(userId);

                        if (token != null) {
                            controllerCtx.returnOK();
                            ctx.json(token);
                        } else {
                            controllerCtx.returnNoContent();
                        }
                    }
                }).end();
    }

    public static void updatePassword(Context ctx) {
        IUserDao userDao = UserDaoImpl.getInstance();

        String userId = ctx.pathParam("id");
        String oldPassword = null;
        String newPassword = null;

        try {
            JsonObject jsonObject = new JsonHelper(ctx.body()).getAsObject();

            oldPassword = jsonObject.get("old_password").getAsString();
            newPassword = jsonObject.get("new_password").getAsString();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        String finalOldPassword = oldPassword;
        String finalNewPassword = newPassword;
        new ControllerHelper(ctx)
                .checkIfAnyNull(oldPassword, newPassword)
                .validateAccessToken()
                .doLogic(controllerCtx -> {
                    if (!userId.equals(controllerCtx.getUserData().get("uid"))) {
                        controllerCtx.returnForbidden();
                    } else if (HashHelper.hashString(finalOldPassword).equals(userDao.getPassword(userId))) {
                        userDao.setPassword(userId, HashHelper.hashString(finalNewPassword));

                        controllerCtx.returnCreated();
                    } else {
                        controllerCtx.returnBadRequest();
                    }
                }).end();
    }

    public static void authUser(Context ctx) {
        IUserDao userDao = UserDaoImpl.getInstance();
        String email = null;
        String password = null;

        try {
            JsonObject jsonObject = new JsonHelper(ctx.body()).getAsObject();

            email = jsonObject.get("email").getAsString();
            password = jsonObject.get("password").getAsString();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        String finalEmail = email;
        String finalPassword = password;
        new ControllerHelper(ctx)
                .checkIfAnyNull(email, password)
                .doLogic(controllerCtx -> {
                    User user = userDao.getUser(finalEmail);

                    if (user == null) {
                        controllerCtx.returnNotFound();
                    } else if (user.getPassHash().equals(HashHelper.hashString(finalPassword))) {
                        // Create a new session
                        IpHelper.IpData ipData = IpHelper.getIpData(ctx.ip());
                        ISessionDao sessionDao = SessionDaoRedis.getInstance();
                        Session session = new Session.Builder()
                                .withUserId(user.getId())
                                .withAccessToken()
                                .withIp(ctx.ip())
                                .withCountry(ipData.getCountry())
                                .withCity(ipData.getCity())
                                .build();

                        sessionDao.createSession(session);

                        Hub hub = HubDaoImpl.getInstance().getHubByUserId(user.getId());
                        Map<String, String> data = new HashMap<>();
                        data.put("id", user.getId());
                        data.put("username", user.getUsername());
                        data.put("email", user.getEmail());
                        data.put("accessToken", session.getAccessToken());
                        data.put("hub_id", ((hub != null) ? hub.getId() : null));
                        data.put("fcmToken", user.getFcmToken());

                        RevokeLink revokeLink = new RevokeLink(session);
                        RevokeLinkRedis.getInstance().createLink(revokeLink);

                        String htmlBody = htmlHelper.fillPlaceholder("<[session_id]>", session.getSessionId())
                                .fillPlaceholder("<[ip]>", session.getIp())
                                .fillPlaceholder("<[country]>", session.getCountry())
                                .fillPlaceholder("<[city]>", session.getCity())
                                .fillPlaceholder("<[time]>", LocalDateTime.now(ZoneId.of("UTC+03:00")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                                .fillPlaceholder("<[url]>", App.SERVER_URL + "/revoke-link/" + revokeLink.getSecret()  )
                                .getCompactHtml();
                        htmlHelper.resetPlaceholders();

                        EmailHelper.sendEmail(user.getEmail(), "Був здійснений вхід в ваш акаунт", htmlBody);

                        controllerCtx.returnOK();
                        ctx.json(data);
                    } else {
                        controllerCtx.returnUnauthorized();
                    }
                }).end();
    }

}
