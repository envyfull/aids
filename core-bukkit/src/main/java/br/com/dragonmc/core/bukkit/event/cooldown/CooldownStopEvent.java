/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.event.cooldown;

import br.com.dragonmc.core.bukkit.utils.cooldown.Cooldown;
import org.bukkit.entity.Player;

public class CooldownStopEvent
extends CooldownEvent {
    public CooldownStopEvent(Player player, Cooldown cooldown) {
        super(player, cooldown);
    }
}

