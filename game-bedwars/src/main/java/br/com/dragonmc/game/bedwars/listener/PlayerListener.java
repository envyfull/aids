/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.entity.CreatureSpawnEvent
 *  org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason
 *  org.bukkit.event.entity.EntityExplodeEvent
 *  org.bukkit.event.entity.FoodLevelChangeEvent
 *  org.bukkit.event.inventory.CraftItemEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerTeleportEvent
 *  org.bukkit.event.player.PlayerTeleportEvent$TeleportCause
 */
package br.com.dragonmc.game.bedwars.listener;

import br.com.dragonmc.core.bukkit.event.player.PlayerAdminEvent;
import br.com.dragonmc.game.bedwars.event.PlayerLevelUpEvent;
import br.com.dragonmc.game.bedwars.gamer.Gamer;
import br.com.dragonmc.game.engine.GameAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerListener
implements Listener {
    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlayerAdmin(PlayerAdminEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);
        if (event.getAdminMode() == PlayerAdminEvent.AdminMode.ADMIN) {
            gamer.setSpectator(true);
        } else {
            gamer.setSpectator(false);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerLevelUp(PlayerLevelUpEvent event) {
        Player player = event.getPlayer();
        if (player != null) {
            player.setExp(0.0f);
            player.setTotalExperience(0);
            player.setLevel(event.getLevel());
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            event.setCancelled(true);
            event.getPlayer().teleport(event.getTo());
            event.getPlayer().setFallDistance(-1.0f);
        }
    }

    @EventHandler
    public void onBlockList(EntityExplodeEvent event) {
        event.blockList().removeIf(b -> b.getType() == Material.GLASS);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            event.setCancelled(true);
        }
    }
}

