/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.game.bedwars.event.island;

import br.com.dragonmc.core.bukkit.event.NormalEvent;
import br.com.dragonmc.game.bedwars.island.Island;

public class IslandLoseEvent
extends NormalEvent {
    private Island island;

    public Island getIsland() {
        return this.island;
    }

    public IslandLoseEvent(Island island) {
        this.island = island;
    }
}

