/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  redis.clients.jedis.Jedis
 */
package br.com.dragonmc.core.common.backend.data.impl;

import br.com.dragonmc.core.common.backend.data.SkinData;
import br.com.dragonmc.core.common.backend.redis.RedisConnection;
import br.com.dragonmc.core.common.utils.skin.Skin;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.utils.json.JsonUtils;
import redis.clients.jedis.Jedis;

public class SkinDataImpl
implements SkinData {
    private static final String BASE_PATH = "skin-data:";
    private static final int TIME_TO_EXPIRE = 7200;
    private RedisConnection redisConnection;

    public SkinDataImpl(RedisConnection redisConnection) {
        this.redisConnection = redisConnection;
    }

    @Override
    public Optional<Skin> loadData(String playerName) {
        try (Jedis jedis = this.redisConnection.getPool().getResource();){
            Map fields;
            boolean exists;
            boolean bl = exists = jedis.ttl(BASE_PATH + playerName.toLowerCase()) >= 0L;
            if (exists && (fields = jedis.hgetAll(BASE_PATH + playerName.toLowerCase())) != null && !fields.isEmpty()) {
                Optional<Skin> optional = Optional.of(JsonUtils.mapToObject(fields, Skin.class));
                return optional;
            }
        }
        UUID uniqueId = CommonPlugin.getInstance().getUniqueId(playerName);
        if (uniqueId == null) {
            return null;
        }
        String[] skin = this.loadSkinById(uniqueId);
        if (skin == null) {
            return Optional.empty();
        }
        Skin skinData = new Skin(playerName, uniqueId, skin[0], skin[1]);
        this.save(skinData, 7200);
        return Optional.of(skinData);
    }

    @Override
    public void save(Skin skin, int seconds) {
        try (Jedis jedis = this.redisConnection.getPool().getResource();){
            jedis.hmset(BASE_PATH + skin.getPlayerName().toLowerCase(), JsonUtils.objectToMap(skin));
            jedis.expire(BASE_PATH + skin.getPlayerName().toLowerCase(), 259200);
            jedis.save();
        }
    }

    @Override
    public String[] loadSkinById(UUID uuid) {
        try {
            JsonObject object;
            URLConnection con = new URL(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", uuid.toString())).openConnection();
            JsonElement element = JsonParser.parseReader((Reader)new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8)));
            if (element instanceof JsonObject && (object = element.getAsJsonObject()).has("properties")) {
                JsonArray jsonArray = object.get("properties").getAsJsonArray();
                JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                String value = jsonObject.get("value").getAsString();
                String signature = jsonObject.has("signature") ? jsonObject.get("signature").getAsString() : "";
                return new String[]{value, signature};
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

