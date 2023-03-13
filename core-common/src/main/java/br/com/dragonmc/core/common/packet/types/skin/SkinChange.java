/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.packet.types.skin;

import java.util.UUID;

import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.packet.Packet;
import br.com.dragonmc.core.common.packet.PacketType;
import br.com.dragonmc.core.common.utils.skin.Skin;

public class SkinChange
extends Packet {
    private UUID playerId;
    private Skin skin;

    public SkinChange(UUID playerId, Skin skin) {
        super(PacketType.SKIN_CHANGE);
        this.bungeecord();
        this.playerId = playerId;
        this.skin = skin;
    }

    public SkinChange(Member member) {
        super(PacketType.SKIN_CHANGE);
        this.bungeecord();
        this.playerId = member.getUniqueId();
        this.skin = member.getSkin();
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public Skin getSkin() {
        return this.skin;
    }
}

