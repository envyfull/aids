/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 */
package br.com.dragonmc.lobby.core.wadgets;

import org.bukkit.Material;

public enum Wadget {
    HEADS("Cabe\u00e7as", Material.GOLD_HELMET),
    CAPES("Capas", Material.ENCHANTMENT_TABLE),
    PARTICLES("Part\u00edculas", Material.NETHER_STAR);

    private String name;
    private Material type;

    public String getName() {
        return this.name;
    }

    public Material getType() {
        return this.type;
    }

    private Wadget(String name, Material type) {
        this.name = name;
        this.type = type;
    }
}

