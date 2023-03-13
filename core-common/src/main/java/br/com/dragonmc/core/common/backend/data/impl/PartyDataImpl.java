/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package br.com.dragonmc.core.common.backend.data.impl;

import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.backend.data.PartyData;
import br.com.dragonmc.core.common.backend.mongodb.MongoConnection;
import br.com.dragonmc.core.common.backend.mongodb.MongoQuery;
import br.com.dragonmc.core.common.member.party.Party;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.UUID;

import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.packet.types.party.PartyCreate;
import br.com.dragonmc.core.common.packet.types.party.PartyDelete;
import br.com.dragonmc.core.common.packet.types.party.PartyField;
import br.com.dragonmc.core.common.utils.json.JsonBuilder;
import br.com.dragonmc.core.common.utils.json.JsonUtils;

public class PartyDataImpl
implements PartyData {
    private MongoQuery query;

    public PartyDataImpl(MongoConnection mongoConnection) {
        this.query = MongoQuery.createDefault(mongoConnection, mongoConnection.getDataBase(), "party");
    }

    @Override
    public <T extends Party> T loadParty(UUID partyId, Class<T> clazz) {
        JsonElement jsonElement = this.query.findOne("partyId", partyId.toString());
        return (T)(jsonElement == null ? null : (Party)CommonConst.GSON.fromJson(jsonElement, clazz));
    }

    @Override
    public void createParty(Party party) {
        JsonElement jsonElement = this.query.findOne("partyId", party.getPartyId().toString());
        if (jsonElement == null) {
            this.query.create(new String[]{CommonConst.GSON.toJson((Object)party)});
        }
        CommonPlugin.getInstance().getPluginPlatform().runAsync(() -> CommonPlugin.getInstance().getServerData().sendPacket(new PartyCreate(party).server((String[])party.getMembers().stream().map(id -> CommonPlugin.getInstance().getMemberManager().getMember((UUID)id).getActualServerId()).toArray(String[]::new))));
    }

    @Override
    public void deleteParty(Party party) {
        JsonElement jsonElement = this.query.findOne("partyId", party.getPartyId().toString());
        if (jsonElement != null) {
            this.query.deleteOne("partyId", party.getPartyId().toString());
        }
        CommonPlugin.getInstance().getPluginPlatform().runAsync(() -> CommonPlugin.getInstance().getServerData().sendPacket(new PartyDelete(party.getPartyId()).server((String[])party.getMembers().stream().filter(id -> CommonPlugin.getInstance().getMemberManager().getMember((UUID)id) != null).map(id -> CommonPlugin.getInstance().getMemberManager().getMember((UUID)id).getActualServerId()).distinct().toArray(String[]::new))));
    }

    @Override
    public void updateParty(final Party party, final String fieldName) {
        CommonPlugin.getInstance().getPluginPlatform().runAsync(new Runnable(){

            @Override
            public void run() {
                JsonObject tree = JsonUtils.jsonTree(party);
                CommonPlugin.getInstance().getServerData().sendPacket(new PartyField(party, fieldName).server((String[])party.getMembers().stream().filter(id -> CommonPlugin.getInstance().getMemberManager().getMember((UUID)id) != null).map(id -> CommonPlugin.getInstance().getMemberManager().getMember((UUID)id).getActualServerId()).distinct().toArray(String[]::new)));
                PartyDataImpl.this.query.updateOne("partyId", party.getPartyId().toString(), (JsonElement)new JsonBuilder().addProperty("fieldName", fieldName).add("value", tree.get(fieldName)).build());
            }
        });
    }

    @Override
    public UUID getPartyId() {
        UUID id;
        while (this.query.findOne("partyId", (id = UUID.randomUUID()).toString()) != null) {
        }
        return id;
    }

    @Override
    public MongoQuery getQuery() {
        return this.query;
    }
}

