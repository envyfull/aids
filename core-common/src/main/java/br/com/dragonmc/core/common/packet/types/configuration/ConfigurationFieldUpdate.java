/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package br.com.dragonmc.core.common.packet.types.configuration;

import br.com.dragonmc.core.common.packet.Packet;
import br.com.dragonmc.core.common.packet.PacketType;
import br.com.dragonmc.core.common.utils.reflection.Reflection;
import com.google.gson.JsonObject;
import java.lang.reflect.Field;

import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.PluginInfo;
import br.com.dragonmc.core.common.utils.json.JsonBuilder;
import br.com.dragonmc.core.common.utils.json.JsonUtils;

public class ConfigurationFieldUpdate
extends Packet {
    private JsonObject jsonObject;

    public ConfigurationFieldUpdate(String fieldName) {
        super(PacketType.CONFIGURATION_FIELD_UPDATE);
        JsonObject tree = JsonUtils.jsonTree(CommonPlugin.getInstance().getPluginInfo());
        this.jsonObject = new JsonBuilder().addProperty("fieldName", fieldName).add("value", tree.get(fieldName)).build();
    }

    @Override
    public void receive() {
        try {
            Field f = Reflection.getField(PluginInfo.class, this.jsonObject.get("fieldName").getAsString());
            Object object = CommonConst.GSON.fromJson(this.jsonObject.get("value"), f.getGenericType());
            f.setAccessible(true);
            f.set(CommonPlugin.getInstance().getPluginInfo(), object);
        }
        catch (IllegalAccessException | IllegalArgumentException | SecurityException e) {
            e.printStackTrace();
        }
    }
}

