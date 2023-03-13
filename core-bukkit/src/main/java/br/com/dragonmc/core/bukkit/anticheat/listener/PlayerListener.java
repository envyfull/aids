/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerLoginEvent
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.event.player.PlayerPortalEvent
 *  org.bukkit.event.player.PlayerRespawnEvent
 *  org.bukkit.event.player.PlayerTeleportEvent
 */
package br.com.dragonmc.core.bukkit.anticheat.listener;

import br.com.dragonmc.core.bukkit.member.BukkitMember;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.anticheat.StormCore;
import br.com.dragonmc.core.bukkit.anticheat.gamer.UserData;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerListener
implements Listener {
    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        StormCore.getInstance().ignore(event.getEntity(), 4.0);
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        StormCore.getInstance().ignore(event.getPlayer(), 4.0);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        StormCore.getInstance().ignore(event.getPlayer(), 4.0);
    }

    @EventHandler
    public void onPlayerVelocity(Player event) {
        StormCore.getInstance().ignore(event.getPlayer(), 0.5);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        StormCore.getInstance().ignore(event.getPlayer(), 4.0);
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        StormCore.getInstance().ignore(event.getPlayer(), 4.0);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        StormCore.getInstance().ignore(event.getPlayer(), 4.0);
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onPlayerMove(PlayerMoveEvent event) {
        UserData userData = this.getUserData(event.getPlayer());
        Location lastLocation = userData.getLastLocation() == null ? event.getFrom() : userData.getLastLocation();
        userData.setDistanceY(lastLocation.getY() - event.getTo().getY());
        if (userData.getDistanceY() > 0.05) {
            userData.setGoingUp(true);
            userData.setFalling(false);
        } else if (userData.getDistanceY() < -0.05) {
            userData.setGoingUp(false);
            userData.setFalling(true);
        } else {
            userData.setGoingUp(false);
            userData.setFalling(false);
        }
        userData.setPing(((CraftPlayer)event.getPlayer()).getHandle().ping);
        userData.setLastLocation(event.getTo().clone());
    }

    public UserData getUserData(Player player) {
        return CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId(), BukkitMember.class).getUserData();
    }
}

