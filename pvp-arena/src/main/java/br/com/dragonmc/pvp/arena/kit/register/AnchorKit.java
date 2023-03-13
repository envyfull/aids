/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
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
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class AnchorKit
extends Kit {
    public AnchorKit() {
        super("Anchor", "Se prenda ao ch\u00e3o e n\u00e3o saia dele", Material.ANVIL, 9000, new ArrayList<ItemStack>());
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
        Player player = event.getPlayer();
        Player damager = event.getDamager();
        if (this.hasAbility(player) || this.hasAbility(damager)) {
            player.getWorld().playSound(player.getLocation(), Sound.ANVIL_LAND, 0.15f, 1.0f);
            this.velocityPlayer(player);
            this.velocityPlayer(damager);
        }
    }

    private void velocityPlayer(final Player player) {
        player.setVelocity(new Vector(0, 0, 0));
        new BukkitRunnable(){

            public void run() {
                player.setVelocity(new Vector(0, 0, 0));
            }
        }.runTaskLater((Plugin)GameMain.getInstance(), 1L);
    }
}

