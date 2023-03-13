/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 *  net.md_5.bungee.api.plugin.Event
 */
package br.com.dragonmc.core.bungee.event.packet;

import lombok.NonNull;
import br.com.dragonmc.core.common.packet.Packet;
import br.com.dragonmc.core.common.packet.PacketType;
import net.md_5.bungee.api.plugin.Event;

public class PacketReceiveEvent
extends Event {
    @NonNull
    private final Packet packet;

    public PacketType getPacketType() {
        return this.packet.getPacketType();
    }

    public PacketReceiveEvent(@NonNull Packet packet) {
        if (packet == null) {
            throw new NullPointerException("packet is marked non-null but is null");
        }
        this.packet = packet;
    }

    @NonNull
    public Packet getPacket() {
        return this.packet;
    }
}

