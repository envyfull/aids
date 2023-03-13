/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.pvp.core.event;

import lombok.NonNull;
import br.com.dragonmc.core.bukkit.event.PlayerEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerRealRespawnEvent
extends PlayerEvent {
    private Location respawnLocation;

    public PlayerRealRespawnEvent(@NonNull Player player, Location respawnLocation) {
        super(player);
        if (player == null) {
            throw new NullPointerException("player is marked non-null but is null");
        }
        this.respawnLocation = respawnLocation;
    }

    public void setRespawnLocation(Location respawnLocation) {
        this.respawnLocation = respawnLocation;
    }

    public Location getRespawnLocation() {
        return this.respawnLocation;
    }
}

