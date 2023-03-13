/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.inventory.ItemStack
 */
package br.com.dragonmc.core.bukkit.utils.floatingitem;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public interface CustomItem {
    public CustomItem spawn();

    public CustomItem remove();

    public void teleport(Location var1);

    public Location getLocation();

    public ItemStack getItemStack();
}

