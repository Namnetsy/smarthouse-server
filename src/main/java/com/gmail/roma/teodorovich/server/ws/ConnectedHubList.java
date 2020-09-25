package com.gmail.roma.teodorovich.server.ws;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectedHubList {

    private final List<ConnectedHub> hubs;

    public ConnectedHubList() {
        hubs = new ArrayList<>();
    }

    public void addHub(String hubId, String accessToken, Session endpoint) {
        hubs.add(new ConnectedHub(hubId, accessToken, endpoint));
    }

    public ConnectedHub getHubById(String hubId) {
        for (ConnectedHub hub : hubs) {
            if (hub.getHubId().equals(hubId)) {
                return hub;
            }
        }

        return null;
    }

    public void removeHub(String hubId) {
        hubs.removeIf(hub -> hub.getHubId().equals(hubId));
    }
}
