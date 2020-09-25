package com.gmail.roma.teodorovich.server.member;

import com.gmail.roma.teodorovich.server.helper.ControllerHelper;
import com.gmail.roma.teodorovich.server.helper.JsonHelper;
import com.gmail.roma.teodorovich.server.hub.Hub;
import com.gmail.roma.teodorovich.server.hub.IHubDao;
import com.gmail.roma.teodorovich.server.hub.impl.HubDaoImpl;
import com.gmail.roma.teodorovich.server.member.impl.MemberDaoImpl;
import com.gmail.roma.teodorovich.server.user.impl.UserDaoImpl;
import com.google.gson.*;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberController {

    public static void addMember(Context ctx) {
        IMemberDao memberDao = MemberDaoImpl.getInstance();
        String userId = ctx.pathParam("id");
        String hubId = null;
        String password = null;

        try {
            JsonObject jsonObject = new JsonHelper(ctx.body()).getAsObject();

            hubId = jsonObject.get("hub_id").getAsString();
            password = jsonObject.get("password").getAsString();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        String finalHubId = hubId;
        new ControllerHelper(ctx)
                .checkIfAnyNull(hubId, password)
                .validateAccessToken()
                .doLogic(controllerCtx -> {
                    Member member = new Member(userId, finalHubId);

                    if (!userId.equals(controllerCtx.getUserData().get("uid"))) {
                        controllerCtx.returnForbidden();
                    } else if (HubDaoImpl.getInstance().isHubExists(finalHubId)) {
                        if (!memberDao.checkIfExists(member)) {
                            memberDao.addMember(member);
                            controllerCtx.returnOK();
                        } else {
                            controllerCtx.returnConflict();
                        }
                    } else {
                        controllerCtx.returnNotFound();
                    }
                }).end();
    }

    public static void deleteMember(Context ctx) {
        String memberId = ctx.pathParam("id");

        new ControllerHelper(ctx)
                .validateAccessToken()
                .doLogic(controllerCtx -> { // TODO: hits database 4 times, optimize or don't bother :)
                    IMemberDao memberDao = MemberDaoImpl.getInstance();
                    IHubDao hubDao = HubDaoImpl.getInstance();
                    String userId = controllerCtx.getUserData().get("uid");

                    if (hubDao.isUserOwnHub(userId)) {
                        Hub hub = hubDao.getHubByUserId(userId);

                        if (hub.getAdminId().equals(userId)) {
                            if (hub.getAdminId().equals(memberId)) {
                                controllerCtx.returnForbidden();
                            } else if (memberDao.checkIfExists(new Member(memberId, hub.getId()))) {
                                memberDao.deleteMember(memberId);

                                controllerCtx.returnOK();
                            } else {
                                controllerCtx.returnNotFound();
                            }
                        } else {
                            controllerCtx.returnForbidden();
                        }
                    } else {
                        controllerCtx.returnForbidden();
                    }
                }).end();
    }

    public static void getMembers(Context ctx) {
        String hubId = ctx.pathParam("id");

        new ControllerHelper(ctx)
                .validateAccessToken()
                .doLogic(controllerCtx -> {
                    IMemberDao memberDao = MemberDaoImpl.getInstance();
                    String userId = controllerCtx.getUserData().get("uid");

                    if (memberDao.checkIfExists(new Member(userId, hubId))) {
                        List<Member> members = MemberDaoImpl.getInstance().getMembers(hubId);
                        Map<String, Map<String, Object>> result = new HashMap<>();

                        for (Member member : members) { // TODO: optimize this, too many requests to relational db
                            Map<String, Object> data = new HashMap<>();
                            data.put("username", UserDaoImpl.getInstance().getUsername(member.getUserId()));
                            data.put("is_admin", member.isAdmin());

                            result.put(member.getUserId(), data);
                        }

                        if (result.isEmpty()) {
                            controllerCtx.returnNoContent();
                        } else {
                            controllerCtx.returnOK();
                            ctx.json(result);
                        }
                    } else {
                        controllerCtx.returnNotFound();
                    }
                }).end();
    }

}
