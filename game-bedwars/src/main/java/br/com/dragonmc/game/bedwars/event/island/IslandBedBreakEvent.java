/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.game.bedwars.event.island;

import br.com.dragonmc.core.bukkit.event.NormalEvent;
import br.com.dragonmc.game.bedwars.island.Island;
import org.bukkit.entity.Player;

public class IslandBedBreakEvent
extends NormalEvent {
    private Player player;
    private Island island;

    public Player getPlayer() {
        return this.player;
    }

    public Island getIsland() {
        return this.island;
    }

    public IslandBedBreakEvent(Player player, Island island) {
        this.player = player;
        this.island = island;
    }
}

