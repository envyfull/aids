/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_8_R3.EnumParticle
 *  org.bukkit.Material
 */
package br.com.dragonmc.lobby.core.wadgets;

import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Material;

public enum Wings {
    FOGUETE("\u00a76Asas de Anjo", EnumParticle.FIREWORKS_SPARK, new ItemBuilder().type(Material.FIREWORK).name("\u00a76Asas de Anjo")),
    FOGO("\u00a76Asas de Fogo", EnumParticle.FLAME, new ItemBuilder().type(Material.LAVA_BUCKET).name("\u00a76Asas de Fogo"));

    private String name;
    private EnumParticle particle;
    private ItemBuilder item;

    public static Wings getWingsByName(String nameOfParticle) {
        for (Wings p : Wings.values()) {
            if (!p.getName().equalsIgnoreCase(nameOfParticle)) continue;
            return p;
        }
        return null;
    }

    private Wings(String name, EnumParticle particle, ItemBuilder item) {
        this.name = name;
        this.particle = particle;
        this.item = item;
    }

    public String getName() {
        return this.name;
    }

    public EnumParticle getParticle() {
        return this.particle;
    }

    public ItemBuilder getItem() {
        return this.item;
    }
}

