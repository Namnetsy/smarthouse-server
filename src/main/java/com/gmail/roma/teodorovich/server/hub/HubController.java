package com.gmail.roma.teodorovich.server.hub;

import com.gmail.roma.teodorovich.server.helper.ControllerHelper;
import com.gmail.roma.teodorovich.server.helper.EmailHelper;
import com.gmail.roma.teodorovich.server.helper.JsonHelper;
import com.gmail.roma.teodorovich.server.helper.HashHelper;
import com.gmail.roma.teodorovich.server.hub.impl.HubDaoImpl;
import com.gmail.roma.teodorovich.server.member.IMemberDao;
import com.gmail.roma.teodorovich.server.member.Member;
import com.gmail.roma.teodorovich.server.member.impl.MemberDaoImpl;
import com.gmail.roma.teodorovich.server.user.IUserDao;
import com.gmail.roma.teodorovich.server.user.User;
import com.gmail.roma.teodorovich.server.user.impl.UserDaoImpl;
import com.gmail.roma.teodorovich.server.ws.WebSocketAPI;
import com.google.gson.*;
import io.javalin.http.Context;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HubController {

    public static void addHub(Context ctx) {
        String userId = ctx.pathParam("id");
        String membersPassword = null;

        try {
            membersPassword = new JsonHelper(ctx.body()).getAsObject().get("password_for_members").getAsString();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        String finalMembersPassword = membersPassword;
        new ControllerHelper(ctx)
                .checkIfAnyNull(membersPassword)
                .validateAccessToken()
                .doLogic(controllerContext -> {
                    if (HubDaoImpl.getInstance().isUserOwnHub(userId)) {
                        controllerContext.returnConflict();
                    } else {
                        assert finalMembersPassword != null;
                        Hub hub = new Hub(userId, HashHelper.hashString(finalMembersPassword));
                        HubDaoImpl.getInstance().addHub(hub);
                        MemberDaoImpl.getInstance().addMember(new Member(userId, hub.getId(), true));

                        Map<String, String> data = new HashMap<>();
                        data.put("id", hub.getId());
                        data.put("access_token", hub.getAccessToken());

                        ctx.json(data);
                        controllerContext.returnCreated();
                    }
                }).end();
    }

    public static void sendCommand(Context ctx) {
        String hubId = ctx.pathParam("id");

        JsonPrimitive jsonPrimitive = new JsonHelper(ctx.body()).getAsPrimitive();
        String command = (jsonPrimitive != null) ? jsonPrimitive.getAsString() : null;

        new ControllerHelper(ctx)
                .checkIfAnyNull(command)
                .validateAccessToken()
                .doLogic(controllerContext -> {
                    String userId = controllerContext.getUserData().get("uid");

                    if (!MemberDaoImpl.getInstance().checkIfExists(new Member(userId, hubId))) {
                        controllerContext.returnForbidden();
                    } else if (WebSocketAPI.isSomeoneWaiting(hubId)) {
                        controllerContext.returnServiceUnavailable();
                    } else {
                        try {
                            String response = WebSocketAPI.sendMessageToHub(hubId, command);

                            if (response == null) {
                                controllerContext.returnTimeout();
                            } else {
                                controllerContext.returnOK();
                                ctx.json(response);
                            }
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                            controllerContext.returnInternalError();
                        }
                    }
                }).end();
    }

    public static void changePassword(Context ctx) {
        String hubId = ctx.pathParam("id");
        String oldPassword = null, newPassword = null;

        try {
            JsonObject jsonObject = new JsonHelper(ctx.body()).getAsObject();

            oldPassword = jsonObject.get("old_password").getAsString();
            newPassword = jsonObject.get("new_password").getAsString();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        String finalNewPassword = newPassword;
        String finalOldPassword = oldPassword;
        new ControllerHelper(ctx)
                .checkIfAnyNull(oldPassword, newPassword)
                .validateAccessToken()
                .doLogic(controllerCtx -> {
                    IHubDao hubDao = HubDaoImpl.getInstance();
                    String userId = controllerCtx.getUserData().get("uid");

                    if (hubDao.isHubIdBelongsToUser(hubId, userId)) {
                        String oldPassHash = hubDao.getPassword(hubId);

                        if (oldPassHash != null && oldPassHash.equals(HashHelper.hashString(finalOldPassword))) {
                            hubDao.setPassword(hubId, HashHelper.hashString(finalNewPassword));

                            controllerCtx.returnCreated();
                        } else {
                            controllerCtx.returnUnauthorized();
                        }
                    } else {
                        controllerCtx.returnNotFound();
                    }
                }).end();
    }

    public static void deleteHub(Context ctx) {
        IHubDao hubDao = HubDaoImpl.getInstance();
        String hubId = ctx.pathParam("id");

        new ControllerHelper(ctx)
                .validateAccessToken()
                .doLogic(controllerContext -> {
                    boolean isHubIdBelongs = hubDao.isHubIdBelongsToUser(hubId, controllerContext.getUserData().get("uid"));

                    if (isHubIdBelongs) {
                        MemberDaoImpl.getInstance().deleteAllMembers(hubId);
                        hubDao.deleteHub(hubId);

                        controllerContext.returnOK();
                    } else {
                        controllerContext.returnUnauthorized();
                    }
                }).end();
    }

    public static void leaveHub(Context ctx) {
        IHubDao hubDao = HubDaoImpl.getInstance();
        String hubId = ctx.pathParam("id");

        JsonPrimitive primitive = new JsonHelper(ctx.body()).getAsPrimitive();
        String memberId = (primitive != null) ? primitive.getAsString() : null;

        new ControllerHelper(ctx)
                .checkIfAnyNull(memberId)
                .validateAccessToken()
                .doLogic(controllerContext -> {
                    Member member = new Member(memberId, hubId);
                    String userId = controllerContext.getUserData().get("uid");
                    IMemberDao memberDao = MemberDaoImpl.getInstance();
                    IUserDao userDao = UserDaoImpl.getInstance();

                    boolean isHubBelongsToUser = hubDao.isHubIdBelongsToUser(hubId, userId);
                    boolean isMemberBelongsToHub = memberDao.checkIfExists(member);

                    if (isHubBelongsToUser) {
                        if (!isMemberBelongsToHub) {
                            controllerContext.returnForbidden();
                        } else {
                            if (userId.equals(memberId)) {
                                controllerContext.returnForbidden();
                            } else {
                                hubDao.setHubOwner(hubId, memberId);
                                memberDao.setAdmin(member, true);
                                memberDao.deleteMember(userId);

                                User oldAdmin = userDao.getUserById(userId);
                                User newAdmin = userDao.getUserById(memberId);
                                EmailHelper.sendEmail(newAdmin.getEmail(),
                                        "You're a hub owner!",
                                        "Hello " + newAdmin.getUsername()
                                                + "!<br>" + oldAdmin.getUsername() + " has left it's hub and made You it's new hub owner!");

                                controllerContext.returnOK();
                            }
                        }
                    } else {
                        controllerContext.returnForbidden();
                    }
                }).end();
    }

}
