/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.bukkit.event.server;

import br.com.dragonmc.core.bukkit.event.NormalEvent;
import br.com.dragonmc.core.common.packet.Packet;
import br.com.dragonmc.core.common.packet.PacketType;

public class ServerPacketReceiveEvent
extends NormalEvent {
    private PacketType packetType;
    private Packet packet;

    public ServerPacketReceiveEvent(PacketType packetType, Packet packet) {
        this.packetType = packetType;
        this.packet = packet;
    }

    public PacketType getPacketType() {
        return this.packetType;
    }

    public Packet getPacket() {
        return this.packet;
    }
}

