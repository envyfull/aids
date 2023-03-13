/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Snowball
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 */
package br.com.dragonmc.pvp.arena.kit.register;

import java.util.Arrays;

import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.pvp.arena.GameMain;
import br.com.dragonmc.pvp.arena.kit.Kit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class SwitcherKit
extends Kit {
    public SwitcherKit() {
        super("Switcher", "Troque de lugar com seus inimigos com sua bola de neve", Material.SNOW_BALL, 12500, Arrays.asList(new ItemBuilder().name("\u00a7aSwitcher").type(Material.SNOW_BALL).build()));
    }

    @EventHandler
    public void onProjectileLaunch(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && this.hasAbility(player) && this.isAbilityItem(player.getItemInHand())) {
            event.setCancelled(true);
            player.updateInventory();
            if (this.isCooldown(player)) {
                return;
            }
            Snowball ball = (Snowball)player.launchProjectile(Snowball.class);
            ball.setMetadata("switch", (MetadataValue)new FixedMetadataValue((Plugin)GameMain.getInstance(), (Object)player));
            this.addCooldown(player, 7L);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager().hasMetadata("switch")) {
            Player player = (Player)((MetadataValue)event.getDamager().getMetadata("switch").get(0)).value();
            if (player == null) {
                return;
            }
            Location loc = event.getEntity().getLocation().clone();
            event.getEntity().teleport(player.getLocation().clone());
            player.teleport(loc);
        }
    }
}

