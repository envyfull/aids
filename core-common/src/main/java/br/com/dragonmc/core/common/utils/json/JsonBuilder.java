/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package br.com.dragonmc.core.common.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.UUID;

public class JsonBuilder {
    private JsonObject jsonObject = new JsonObject();

    public JsonBuilder add(String key, JsonElement value) {
        this.jsonObject.add(key, value);
        return this;
    }

    public JsonBuilder addProperty(String key, String value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public JsonBuilder addProperty(String key, Boolean value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public JsonBuilder addProperty(String key, Number value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public JsonBuilder addProperty(String key, Character value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public JsonObject build() {
        return this.jsonObject;
    }

    public static JsonBuilder createObjectBuilder() {
        return new JsonBuilder();
    }

    public static JsonBuilder createPlayer(String playerName, UUID uniqueId) {
        return JsonBuilder.createObjectBuilder().addProperty("playerName", playerName).addProperty("uniqueId", uniqueId.toString());
    }
}

