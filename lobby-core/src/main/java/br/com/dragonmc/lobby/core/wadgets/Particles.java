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

public enum Particles {
    HEART("\u00a76Part\u00edculas de Cora\u00e7\u00f5es", EnumParticle.HEART, new ItemBuilder().type(Material.INK_SACK).durability(10).name("\u00a76Part\u00edculas de Cora\u00e7\u00f5es")),
    FOGUETE("\u00a76Part\u00edculas de Anjo", EnumParticle.FIREWORKS_SPARK, new ItemBuilder().type(Material.INK_SACK).durability(10).name("\u00a76Part\u00edculas de Anjo")),
    FOGO("\u00a76Part\u00edculas de Fogo", EnumParticle.FLAME, new ItemBuilder().type(Material.INK_SACK).durability(10).name("\u00a76Part\u00edculas de Fogo"));

    private String name;
    private EnumParticle particle;
    private ItemBuilder item;

    public static Particles getParticleByName(String nameOfParticle) {
        for (Particles p : Particles.values()) {
            if (!p.getName().equalsIgnoreCase(nameOfParticle)) continue;
            return p;
        }
        return null;
    }

    private Particles(String name, EnumParticle particle, ItemBuilder item) {
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

