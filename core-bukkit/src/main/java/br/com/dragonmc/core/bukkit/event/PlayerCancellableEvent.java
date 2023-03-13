/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 */
package br.com.dragonmc.core.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class PlayerCancellableEvent
extends PlayerEvent
implements Cancellable {
    private boolean cancelled;

    public PlayerCancellableEvent(Player player) {
        super(player);
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}

