/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.game.bedwars.menu.creator;

import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import org.bukkit.entity.Player;

public class GeneratorCreatorInventory {
    public GeneratorCreatorInventory(Player player) {
        MenuInventory menuInventory = new MenuInventory("\u00a77Criar geradores", 3);
        menuInventory.open(player);
    }
}

