/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.bukkit.anticheat.hack;

public enum HackType {
    AUTOSOUP(10),
    KILLAURA(15),
    FLY(10),
    SPEED(10),
    AUTOCLICK(20),
    MACRO(30),
    REACH(100);

    private int maxAlerts;

    private HackType(int maxAlerts) {
        this.maxAlerts = maxAlerts;
    }

    public int getMaxAlerts() {
        return this.maxAlerts;
    }
}

