/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package br.com.dragonmc.pvp.arena.kit.register;

import java.util.ArrayList;
import br.com.dragonmc.pvp.arena.kit.Kit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PvpKit
extends Kit {
    public PvpKit() {
        super("PvP", "Kit padr\u00e3o sem nenhuma habilidade!", Material.DIAMOND_SWORD, 0, new ArrayList<ItemStack>());
    }
}

