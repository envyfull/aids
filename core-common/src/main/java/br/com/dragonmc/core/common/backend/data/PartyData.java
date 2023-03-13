/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.backend.data;

import java.util.UUID;

import br.com.dragonmc.core.common.backend.mongodb.MongoQuery;
import br.com.dragonmc.core.common.member.party.Party;

public interface PartyData
extends Data<MongoQuery> {
    public <T extends Party> T loadParty(UUID var1, Class<T> var2);

    public void createParty(Party var1);

    public void deleteParty(Party var1);

    public void updateParty(Party var1, String var2);

    public UUID getPartyId();
}

