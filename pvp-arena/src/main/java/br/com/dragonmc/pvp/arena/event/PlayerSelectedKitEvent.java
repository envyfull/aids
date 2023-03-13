/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.pvp.arena.event;

import br.com.dragonmc.core.bukkit.event.PlayerCancellableEvent;
import br.com.dragonmc.pvp.arena.kit.Kit;
import br.com.dragonmc.pvp.arena.menu.AbilityInventory;
import org.bukkit.entity.Player;

public class PlayerSelectedKitEvent
extends PlayerCancellableEvent {
    private Kit kit;
    private AbilityInventory.InventoryType inventoryType;

    public PlayerSelectedKitEvent(Player player, Kit kit, AbilityInventory.InventoryType inventoryType) {
        super(player);
        this.kit = kit;
        this.inventoryType = inventoryType;
    }

    public Kit getKit() {
        return this.kit;
    }

    public AbilityInventory.InventoryType getInventoryType() {
        return this.inventoryType;
    }
}

