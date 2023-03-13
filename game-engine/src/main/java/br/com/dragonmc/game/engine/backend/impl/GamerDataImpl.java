/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package br.com.dragonmc.game.engine.backend.impl;

import br.com.dragonmc.game.engine.GameAPI;
import br.com.dragonmc.game.engine.backend.GamerData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Optional;
import java.util.UUID;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.backend.mongodb.MongoQuery;
import br.com.dragonmc.game.engine.gamer.Gamer;
import br.com.dragonmc.core.common.utils.json.JsonBuilder;
import br.com.dragonmc.core.common.utils.json.JsonUtils;

public class GamerDataImpl
implements GamerData {
    private MongoQuery query = MongoQuery.createDefault(CommonPlugin.getInstance().getMongoConnection(), GameAPI.getInstance().getCollectionName());

    @Override
    public <T extends Gamer> Optional<T> loadGamer(UUID uniqueId) {
        JsonElement jsonElement = this.query.findOne("uniqueId", uniqueId.toString());
        return jsonElement == null ? Optional.empty() : Optional.of((T)CommonConst.GSON.fromJson(jsonElement, GameAPI.getInstance().getGamerClass()));
    }

    @Override
    public void createGamer(Gamer gamer) {
        JsonElement jsonElement = this.query.findOne("uniqueId", gamer.getUniqueId().toString());
        if (jsonElement == null) {
            this.query.create(new String[]{CommonConst.GSON.toJson((Object)gamer)});
        }
    }

    @Override
    public void saveGamer(final Gamer gamer, final String fieldName) {
        CommonPlugin.getInstance().getPluginPlatform().runAsync(new Runnable(){

            @Override
            public void run() {
                JsonObject tree = JsonUtils.jsonTree(gamer);
                GamerDataImpl.this.query.updateOne("uniqueId", gamer.getUniqueId().toString(), (JsonElement)new JsonBuilder().addProperty("fieldName", fieldName).add("value", tree.get(fieldName)).build());
            }
        });
    }
}

