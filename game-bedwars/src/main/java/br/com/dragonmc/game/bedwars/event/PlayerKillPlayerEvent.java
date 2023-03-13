/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.game.bedwars.event;

import lombok.NonNull;
import br.com.dragonmc.core.bukkit.event.PlayerEvent;
import org.bukkit.entity.Player;

public class PlayerKillPlayerEvent
extends PlayerEvent {
    private Player killer;
    private boolean finalKill;

    public PlayerKillPlayerEvent(@NonNull Player player, Player killer, boolean finalKill) {
        super(player);
        if (player == null) {
            throw new NullPointerException("player is marked non-null but is null");
        }
        this.killer = killer;
        this.finalKill = finalKill;
    }

    public Player getKiller() {
        return this.killer;
    }

    public boolean isFinalKill() {
        return this.finalKill;
    }
}

