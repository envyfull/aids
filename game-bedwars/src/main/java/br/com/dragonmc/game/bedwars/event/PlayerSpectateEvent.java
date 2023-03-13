/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.game.bedwars.event;

import lombok.NonNull;
import br.com.dragonmc.core.bukkit.event.PlayerEvent;
import org.bukkit.entity.Player;

public class PlayerSpectateEvent
extends PlayerEvent {
    public PlayerSpectateEvent(@NonNull Player player) {
        super(player);
        if (player == null) {
            throw new NullPointerException("player is marked non-null but is null");
        }
    }
}

