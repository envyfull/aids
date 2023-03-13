/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package br.com.dragonmc.core.common.packet.types.configuration;

import br.com.dragonmc.core.common.packet.Packet;
import br.com.dragonmc.core.common.packet.PacketType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.PluginInfo;

public class ConfigurationUpdate
extends Packet {
    private JsonObject jsonObject = CommonConst.GSON.toJsonTree((Object)CommonPlugin.getInstance().getPluginInfo()).getAsJsonObject();

    public ConfigurationUpdate() {
        super(PacketType.CONFIGURATION_UPDATE);
    }

    @Override
    public void receive() {
        CommonPlugin.getInstance().setPluginInfo((PluginInfo)CommonConst.GSON.fromJson((JsonElement)this.jsonObject, PluginInfo.class));
    }
}

