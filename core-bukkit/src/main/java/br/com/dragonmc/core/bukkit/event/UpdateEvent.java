/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package br.com.dragonmc.core.bukkit.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UpdateEvent
extends Event {
    public static final HandlerList handlers = new HandlerList();
    private UpdateType type;
    private long currentTick;

    public UpdateEvent(UpdateType type) {
        this(type, -1L);
    }

    public UpdateEvent(UpdateType type, long currentTick) {
        this.type = type;
        this.currentTick = currentTick;
    }

    public UpdateType getType() {
        return this.type;
    }

    public long getCurrentTick() {
        return this.currentTick;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public static enum UpdateType {
        TICK,
        SECOND,
        MINUTE;

    }
}

