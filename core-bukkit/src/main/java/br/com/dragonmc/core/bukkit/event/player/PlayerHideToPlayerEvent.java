/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.event.player;

import br.com.dragonmc.core.bukkit.event.PlayerCancellableEvent;
import org.bukkit.entity.Player;

public class PlayerHideToPlayerEvent
extends PlayerCancellableEvent {
    private Player toPlayer;

    public PlayerHideToPlayerEvent(Player player, Player toPlayer) {
        super(player);
        this.toPlayer = toPlayer;
    }

    public Player getToPlayer() {
        return this.toPlayer;
    }
}

