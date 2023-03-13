/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.event.player.PlayerMoveEvent
 */
package br.com.dragonmc.core.bukkit.event.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveUpdateEvent
extends PlayerMoveEvent {
    public PlayerMoveUpdateEvent(Player player, Location from, Location to) {
        super(player, from, to);
    }
}

