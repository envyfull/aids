/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package br.com.dragonmc.core.common.packet.types.party;

import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.member.party.Party;
import br.com.dragonmc.core.common.packet.Packet;
import br.com.dragonmc.core.common.packet.PacketType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PartyCreate
extends Packet {
    private JsonObject jsonObject;

    public PartyCreate(Party party) {
        super(PacketType.PARTY_CREATE);
        this.jsonObject = CommonConst.GSON.toJsonTree((Object)party).getAsJsonObject();
    }

    @Override
    public void receive() {
        Party party = (Party)CommonConst.GSON.fromJson((JsonElement)this.jsonObject, CommonPlugin.getInstance().getPartyClass());
        if (party != null) {
            CommonPlugin.getInstance().getPartyManager().loadParty(party);
            System.out.println("pacote recebido");
        }
    }

    public JsonObject getJsonObject() {
        return this.jsonObject;
    }
}

