/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.pvp.arena.event.gladiator;

import br.com.dragonmc.core.bukkit.event.PlayerCancellableEvent;
import org.bukkit.entity.Player;

public class GladiatorScapeEvent
extends PlayerCancellableEvent {
    private Player target;

    public GladiatorScapeEvent(Player player, Player target) {
        super(player);
        this.target = target;
    }

    public Player getTarget() {
        return this.target;
    }
}

