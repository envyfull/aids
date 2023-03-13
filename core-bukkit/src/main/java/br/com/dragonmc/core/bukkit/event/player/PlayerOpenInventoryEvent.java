/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 */
package br.com.dragonmc.core.bukkit.event.player;

import br.com.dragonmc.core.bukkit.event.PlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PlayerOpenInventoryEvent
extends PlayerEvent {
    private Inventory inventory;

    public PlayerOpenInventoryEvent(Player player, Inventory inventory) {
        super(player);
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}

