/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 */
package br.com.dragonmc.core.bukkit.utils.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MenuHolder
implements InventoryHolder {
    private MenuInventory menu;

    public MenuHolder(MenuInventory menuInventory) {
        this.menu = menuInventory;
    }

    public MenuInventory getMenu() {
        return this.menu;
    }

    public void setMenu(MenuInventory menu) {
        this.menu = menu;
    }

    public void onClose(Player player) {
        this.menu.onClose(player);
    }

    public boolean isOnePerPlayer() {
        return this.menu.isOnePerPlayer();
    }

    public void destroy() {
        this.menu = null;
    }

    public Inventory getInventory() {
        if (this.isOnePerPlayer()) {
            return null;
        }
        return this.menu.getInventory();
    }
}

