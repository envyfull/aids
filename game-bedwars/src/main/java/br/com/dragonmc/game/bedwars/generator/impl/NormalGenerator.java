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

public class NormalGenerator
extends Generator {
    public NormalGenerator(Location location, Material material) {
        super(location, GeneratorType.NORMAL, new ItemStack(material));
    }

    @Override
    public Location getDropLocation() {
        if (this.getDropsLocation().isEmpty()) {
            return this.getLocation();
        }
        if (this.getDropsLocation().size() <= 1) {
            return this.getDropsLocation().stream().findFirst().orElse(null);
        }
        return this.getDropsLocation().get(++this.dropIndex >= this.getDropsLocation().size() ? (this.dropIndex = 0) : this.dropIndex);
    }
}

