/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.game.bedwars.event;

import br.com.dragonmc.core.bukkit.event.NormalEvent;
import br.com.dragonmc.game.bedwars.island.Island;

public class IslandWinEvent
extends NormalEvent {
    private Island island;

    public IslandWinEvent(Island island) {
        this.island = island;
    }

    public Island getIsland() {
        return this.island;
    }
}

