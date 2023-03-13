/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.plugin.Event
 */
package br.com.dragonmc.core.bungee.event.player;

import br.com.dragonmc.core.bungee.member.BungeeMember;
import net.md_5.bungee.api.plugin.Event;

public class PlayerFieldUpdateEvent
extends Event {
    private final BungeeMember player;
    private String fieldName;

    public PlayerFieldUpdateEvent(BungeeMember player, String fieldName) {
        this.player = player;
        this.fieldName = fieldName;
    }

    public BungeeMember getPlayer() {
        return this.player;
    }

    public String getFieldName() {
        return this.fieldName;
    }
}

