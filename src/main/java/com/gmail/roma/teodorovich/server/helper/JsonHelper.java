package com.gmail.roma.teodorovich.server.helper;

import com.google.gson.*;

public class JsonHelper {

    private JsonElement jsonElement;

    public JsonHelper(String json) {
        try {
            jsonElement = JsonParser.parseString(json);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();

            jsonElement = null;
        }
    }

    public JsonPrimitive getAsPrimitive() {
        if (jsonElement == null || !jsonElement.isJsonPrimitive()) {
            return null;
        }

        return jsonElement.getAsJsonPrimitive();
    }

    public JsonObject getAsObject() {
        if (jsonElement == null || !jsonElement.isJsonObject()) {
            return null;
        }

        return jsonElement.getAsJsonObject();
    }

    public JsonArray getAsArray() {
        if (jsonElement == null || !jsonElement.isJsonArray()) {
            return null;
        }

        return jsonElement.getAsJsonArray();
    }

}
