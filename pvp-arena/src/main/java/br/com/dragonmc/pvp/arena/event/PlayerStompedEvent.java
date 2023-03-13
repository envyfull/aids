/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.pvp.arena.event;

import br.com.dragonmc.core.bukkit.event.PlayerCancellableEvent;
import org.bukkit.entity.Player;

public class PlayerStompedEvent
extends PlayerCancellableEvent {
    private Player stomper;

    public PlayerStompedEvent(Player stomper, Player stomped) {
        super(stomped);
        this.stomper = stomper;
    }

    public Player getStomper() {
        return this.stomper;
    }
}

