/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 */
package br.com.dragonmc.core.bukkit.event.cooldown;

import br.com.dragonmc.core.bukkit.utils.cooldown.Cooldown;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class CooldownStartEvent
extends CooldownEvent
implements Cancellable {
    private boolean cancelled;

    public CooldownStartEvent(Player player, Cooldown cooldown) {
        super(player, cooldown);
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}

