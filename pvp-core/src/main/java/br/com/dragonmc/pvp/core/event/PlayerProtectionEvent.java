/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.pvp.core.event;

import lombok.NonNull;
import br.com.dragonmc.core.bukkit.event.PlayerEvent;
import org.bukkit.entity.Player;

public class PlayerProtectionEvent
extends PlayerEvent {
    private boolean newState;

    public PlayerProtectionEvent(@NonNull Player player, boolean newState) {
        super(player);
        if (player == null) {
            throw new NullPointerException("player is marked non-null but is null");
        }
        this.newState = newState;
    }

    public boolean getNewState() {
        return this.newState;
    }

    public boolean getOldState() {
        return !this.newState;
    }
}

