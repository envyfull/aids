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

public class NormalEvent
extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}

