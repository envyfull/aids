/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.pvp.core.event;

import lombok.NonNull;
import br.com.dragonmc.core.bukkit.event.PlayerEvent;
import br.com.dragonmc.core.common.member.status.Status;
import org.bukkit.entity.Player;

public class StatusChangeEvent
extends PlayerEvent {
    private Status status;

    public StatusChangeEvent(@NonNull Player player, Status status) {
        super(player);
        if (player == null) {
            throw new NullPointerException("player is marked non-null but is null");
        }
        this.status = status;
    }

    public Status getStatus() {
        return this.status;
    }
}

