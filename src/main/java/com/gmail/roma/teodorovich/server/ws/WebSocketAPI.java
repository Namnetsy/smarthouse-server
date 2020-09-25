package com.gmail.roma.teodorovich.server.ws;

import com.gmail.roma.teodorovich.server.helper.AccessTokenHelper;
import com.gmail.roma.teodorovich.server.hub.impl.HubDaoImpl;
import io.javalin.websocket.WsHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebSocketAPI {

    private static final ConnectedHubList hubs;

    private static final List<String> waitingForResponse;

    private static final Map<String, String> responses;

    static {
        hubs = new ConnectedHubList();
        waitingForResponse = new ArrayList<>();
        responses = new HashMap<>();
    }

    public static boolean isSomeoneWaiting(String hubId) {
        synchronized (waitingForResponse) {
            return waitingForResponse.contains(hubId);
        }
    }

    public static synchronized String sendMessageToHub(String hubId, String message)
            throws IOException, InterruptedException {
        ConnectedHub hub = hubs.getHubById(hubId);

        if (hub == null) {
            return null;
        } else if (hub.getSession() == null) {
            return null;
        } else {
            hub.getSession().getRemote().sendString(message);

            waitingForResponse.add(hubId);

            synchronized (responses) {
                responses.wait(10_000);
            }

            String response = responses.getOrDefault(hubId, null);

            waitingForResponse.remove(hubId);
            responses.remove(hubId);

            return response;
        }
    }

    public static void endpoint(WsHandler ws) {
        ws.onConnect(ctx -> {
            String token = AccessTokenHelper.parseAuthHeader(ctx.header("Authorization"));
            String hubId = ctx.pathParam("id");

            if (token == null) {
                ctx.session.close();
            } else {
                String actualHubId = HubDaoImpl.getInstance().getHubIdByAccessToken(token);

                if (actualHubId != null && actualHubId.equals(hubId)) {
                    hubs.addHub(hubId, token, ctx.session);
                    ctx.session.setIdleTimeout(16 * (60 * 1000)); // 16 minutes
                } else {
                    ctx.session.close();
                }
            }
        });

        ws.onMessage(ctx -> {
            String hubId = ctx.pathParam("id");

            if (waitingForResponse.contains(hubId)) { // If someone's waiting for a response then notify that user
                waitingForResponse.remove(hubId);

                synchronized (responses) {
                    responses.put(hubId, ctx.message());
                    responses.notifyAll();
                }
            } else { // if no one is waiting for this response then remove the entry
                waitingForResponse.remove(hubId);
            }
        });

        ws.onBinaryMessage(ctx -> {
            ctx.session.close();
        });

        ws.onError(ctx -> {
            ctx.session.close();
        });

        ws.onClose(ctx -> {
            hubs.removeHub(ctx.pathParam("id"));
        });
    };

}
