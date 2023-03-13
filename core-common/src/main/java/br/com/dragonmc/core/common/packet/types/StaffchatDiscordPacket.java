/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.packet.types;

import java.util.UUID;

import br.com.dragonmc.core.common.permission.Group;
import br.com.dragonmc.core.common.packet.Packet;
import br.com.dragonmc.core.common.packet.PacketType;

public class StaffchatDiscordPacket
extends Packet {
    private UUID playerId;
    private Group playerGroup;
    private String message;

    public StaffchatDiscordPacket(UUID playerId, Group playerGroup, String message) {
        super(PacketType.STAFFCHAT_DISCORD);
        this.playerId = playerId;
        this.playerGroup = playerGroup;
        this.message = message;
        this.discord();
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public Group getPlayerGroup() {
        return this.playerGroup;
    }

    public String getMessage() {
        return this.message;
    }
}

