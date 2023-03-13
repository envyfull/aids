/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 */
package br.com.dragonmc.core.common.utils.mojang;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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

public class UUIDFetcher {
    private List<String> apis = new ArrayList<String>();
    private LoadingCache<String, UUID> cache = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.DAYS).build((CacheLoader)new CacheLoader<String, UUID>(){

        public UUID load(String name) throws Exception {
            UUID uuid = CommonPlugin.getInstance().getPluginPlatform().getUniqueId(name);
            return uuid == null ? UUIDFetcher.this.request(name) : uuid;
        }
    });

    public UUIDFetcher() {
        this.apis.add("https://api.mojang.com/users/profiles/minecraft/%s");
        this.apis.add("https://api.mcuuid.com/json/uuid/%s");
        this.apis.add("https://api.minetools.eu/uuid/%s");
    }

    public UUID request(String name) {
        return this.request(0, this.apis.get(0), name);
    }

    public UUID request(int idx, String api, String name) {
        block6: {
            try {
                URLConnection con = new URL(String.format(api, name)).openConnection();
                JsonElement element = JsonParser.parseReader((Reader)new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8)));
                if (element instanceof JsonObject) {
                    JsonObject uuid;
                    JsonObject object = (JsonObject)element;
                    if (object.has("error") && object.has("errorMessage")) {
                        throw new Exception(object.get("errorMessage").getAsString());
                    }
                    if (object.has("id")) {
                        return UUIDParser.parse(object.get("id"));
                    }
                    if (object.has("uuid") && (uuid = object.getAsJsonObject("uuid")).has("formatted")) {
                        return UUIDParser.parse(object.get("formatted"));
                    }
                }
            }
            catch (Exception e) {
                if (++idx >= this.apis.size()) break block6;
                api = this.apis.get(idx);
                return this.request(idx, api, name);
            }
        }
        return null;
    }

    public UUID getUUID(String name) {
        if (name != null && !name.isEmpty()) {
            if (name.matches("[a-zA-Z0-9_]{3,16}")) {
                try {
                    return (UUID)this.cache.get(name);
                }
                catch (Exception exception) {
                }
            } else {
                return UUIDParser.parse(name);
            }
        }
        return null;
    }
}

