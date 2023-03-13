/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package br.com.dragonmc.game.bedwars.generator;

import org.bukkit.ChatColor;

public enum GeneratorType {
    EMERALD(60, ChatColor.DARK_GREEN),
    DIAMOND(30, ChatColor.AQUA),
    NORMAL;

    private int timer = 1;
    private ChatColor color;

    public GeneratorType getNextUpgrade() {
        if (this.ordinal() - 1 < 0) {
            return GeneratorType.values()[GeneratorType.values().length - 2];
        }
        return GeneratorType.values()[this.ordinal() - 1];
    }

    public String getConfigFieldName() {
        return this.name().toLowerCase() + "Generator";
    }

    public int getTimer() {
        return this.timer;
    }

    public ChatColor getColor() {
        return this.color;
    }

    private GeneratorType(int timer, ChatColor color) {
        this.timer = timer;
        this.color = color;
    }

    private GeneratorType() {
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }
}

