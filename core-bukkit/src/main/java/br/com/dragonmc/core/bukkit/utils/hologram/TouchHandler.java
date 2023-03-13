/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.utils.hologram;

import org.bukkit.entity.Player;

public interface TouchHandler {
    public void onTouch(Hologram var1, Player var2, TouchType var3);

    public static enum TouchType {
        LEFT,
        RIGHT;

    }
}

