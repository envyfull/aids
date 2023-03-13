/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.util.Vector
 */
package br.com.dragonmc.pvp.arena.kit.register;

import java.util.ArrayList;

import br.com.dragonmc.core.bukkit.event.player.PlayerDamagePlayerEvent;
import br.com.dragonmc.pvp.arena.GameMain;
import br.com.dragonmc.pvp.arena.kit.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class HulkKit
extends Kit {
    public HulkKit() {
        super("Hulk", "Pegue seus inimigos em suas costas e lan\u00e7e-os para longe", Material.SADDLE, 14000, new ArrayList<ItemStack>());
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (this.hasAbility(player) && event.getRightClicked() instanceof Player) {
            Player clicked = (Player)event.getRightClicked();
            if (!(GameMain.getInstance().getGamerManager().getGamer(clicked.getUniqueId()).isSpawnProtection() || player.isInsideVehicle() || clicked.isInsideVehicle() || player.getItemInHand().getType() != Material.AIR)) {
                if (this.isCooldown(player)) {
                    return;
                }
                this.addCooldown(player, 12L);
                player.setPassenger((Entity)clicked);
            }
        }
    }

    @EventHandler
    public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
        final Player player = event.getPlayer();
        Player hulk = event.getDamager();
        if (hulk.getPassenger() != null && hulk.getPassenger() == player && this.hasAbility(hulk) && hulk.getPassenger() == player) {
            event.setCancelled(true);
            player.setSneaking(true);
            Vector v = hulk.getEyeLocation().getDirection().multiply(1.6f);
            v.setY(0.6);
            player.setVelocity(v);
            new BukkitRunnable(){

                public void run() {
                    player.setSneaking(false);
                }
            }.runTaskLater((Plugin)GameMain.getInstance(), 10L);
        }
    }
}

