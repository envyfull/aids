/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 */
package br.com.dragonmc.core.bukkit.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import br.com.dragonmc.core.bukkit.event.UpdateEvent;
import br.com.dragonmc.core.bukkit.event.player.PlayerMoveUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class MoveListener
implements Listener {
    private Map<UUID, Location> locationMap = new HashMap<UUID, Location>();

    @EventHandler(priority=EventPriority.LOWEST)
    public void onUpdate(UpdateEvent event) {
        if (event.getCurrentTick() % 5L == 0L) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Location location;
                if (this.locationMap.containsKey(player.getUniqueId()) && ((location = this.locationMap.get(player.getUniqueId())).getX() != player.getLocation().getX() || location.getZ() != player.getLocation().getZ() || location.getY() != player.getLocation().getY())) {
                    PlayerMoveUpdateEvent playerMoveUpdateEvent = new PlayerMoveUpdateEvent(player, location, player.getLocation());
                    Bukkit.getPluginManager().callEvent((Event)playerMoveUpdateEvent);
                    if (playerMoveUpdateEvent.isCancelled()) {
                        player.teleport(new Location(location.getWorld(), location.getX(), player.getLocation().getY(), location.getZ(), location.getYaw(), location.getPitch()));
                    }
                }
                this.locationMap.put(player.getUniqueId(), player.getLocation());
            }
        }
    }
}

