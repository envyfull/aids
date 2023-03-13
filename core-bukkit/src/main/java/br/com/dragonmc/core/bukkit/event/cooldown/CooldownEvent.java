/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.event.cooldown;

import br.com.dragonmc.core.bukkit.event.PlayerEvent;
import lombok.NonNull;
import br.com.dragonmc.core.bukkit.utils.cooldown.Cooldown;
import org.bukkit.entity.Player;

public abstract class CooldownEvent
extends PlayerEvent {
    @NonNull
    private Cooldown cooldown;

    public CooldownEvent(Player player, Cooldown cooldown) {
        super(player);
        this.cooldown = cooldown;
    }

    @NonNull
    public Cooldown getCooldown() {
        return this.cooldown;
    }
}

