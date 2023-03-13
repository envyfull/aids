/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.pvp.arena.menu;

import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.pvp.arena.GameMain;
import br.com.dragonmc.pvp.arena.kit.Kit;
import org.bukkit.entity.Player;

public class AbilityInventory {
    public AbilityInventory(Player player, InventoryType selectClass) {
        MenuInventory menuInventory = new MenuInventory("\u00a77\u00a7nSelecionar kit", 6);
        int slot = 10;
        for (Kit kit : GameMain.getInstance().getKitManager().getKitList()) {
            menuInventory.setItem(slot, new ItemBuilder().name("\u00a7a" + kit.getKitName()).lore("\u00a77" + kit.getKitDescription()).type(kit.getKitType()).build(), (p, inv, type, stack, s) -> player.performCommand("kit " + kit.getName() + " " + (selectClass == InventoryType.PRIMARY ? "1" : "2")));
            if (slot % 9 == 7) {
                slot += 3;
                continue;
            }
            ++slot;
        }
        menuInventory.open(player);
    }

    public static enum InventoryType {
        PRIMARY,
        SECONDARY;

    }
}

