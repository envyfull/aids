/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package br.com.dragonmc.game.bedwars.event;

import lombok.NonNull;
import br.com.dragonmc.core.bukkit.event.PlayerCancellableEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerBoughtItemEvent
extends PlayerCancellableEvent {
    private ItemStack itemStack;

    public PlayerBoughtItemEvent(@NonNull Player player, ItemStack itemStack) {
        super(player);
        if (player == null) {
            throw new NullPointerException("player is marked non-null but is null");
        }
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
}

