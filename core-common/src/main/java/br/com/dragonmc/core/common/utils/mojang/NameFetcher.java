/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 */
package br.com.dragonmc.core.common.utils.mojang;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import br.com.dragonmc.core.common.CommonPlugin;

public class NameFetcher {
    private List<String> apis = new ArrayList<String>();
    private LoadingCache<UUID, String> cache = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.DAYS).build((CacheLoader)new CacheLoader<UUID, String>(){

        public String load(UUID uuid) throws Exception {
            String name = CommonPlugin.getInstance().getPluginPlatform().getName(uuid);
            return name == null ? NameFetcher.this.request(uuid) : name;
        }
    });

    public NameFetcher() {
        this.apis.add("https://api.mojang.com/user/profiles/%s/names");
        this.apis.add("https://sessionserver.mojang.com/session/minecraft/profile/%s");
        this.apis.add("https://api.mcuuid.com/json/name/%s");
        this.apis.add("https://api.minetools.eu/uuid/%s");
    }

    private String request(UUID uuid) {
        return this.request(0, this.apis.get(0), uuid);
    }

    private String request(int idx, String api, UUID uuid) {
        block8: {
            try {
                URLConnection con = new URL(String.format(api, uuid.toString().replace("-", ""))).openConnection();
                JsonElement element = JsonParser.parseReader((Reader)new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8)));
                if (element instanceof JsonArray) {
                    JsonArray names = (JsonArray)element;
                    JsonObject name = (JsonObject)names.get(names.size() - 1);
                    if (name.has("name")) {
                        return name.get("name").getAsString();
                    }
                } else if (element instanceof JsonObject) {
                    JsonObject object = (JsonObject)element;
                    if (object.has("error") && object.has("errorMessage")) {
                        throw new Exception(object.get("errorMessage").getAsString());
                    }
                    if (object.has("name")) {
                        return object.get("name").getAsString();
                    }
                }
            }
            catch (Exception e) {
                if (++idx >= this.apis.size()) break block8;
                api = this.apis.get(idx);
                return this.request(idx, api, uuid);
            }
        }
        return null;
    }

    public String getName(UUID uuid) {
        try {
            return (String)this.cache.get(uuid);
        }
        catch (Exception e) {
            return null;
        }
    }
}

