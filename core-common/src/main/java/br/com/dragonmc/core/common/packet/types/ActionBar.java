/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.packet.types;

import java.util.UUID;
import br.com.dragonmc.core.common.packet.Packet;
import br.com.dragonmc.core.common.packet.PacketType;

public class ActionBar
extends Packet {
    private UUID uniqueId;
    private String text;

    public ActionBar(UUID uniqueId, String text) {
        super(PacketType.ACTION_BAR);
        this.uniqueId = uniqueId;
        this.text = text;
    }

    @Override
    public void receive() {
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public String getText() {
        return this.text;
    }
}

