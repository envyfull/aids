/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package br.com.dragonmc.game.bedwars.generator.impl;

import br.com.dragonmc.game.bedwars.generator.Generator;
import br.com.dragonmc.game.bedwars.generator.GeneratorType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DiamondGenerator
extends Generator {
    public DiamondGenerator(Location location) {
        super(location, GeneratorType.DIAMOND, new ItemStack(Material.DIAMOND));
    }
}

