/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.util.Vector
 */
package br.com.dragonmc.pvp.arena.kit.register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.pvp.arena.kit.Kit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class KangarooKit
extends Kit {
    private final List<Player> kangarooMap = new ArrayList<Player>();

    public KangarooKit() {
        super("Kangaroo", "Use o seu foguete para movimentar-se mais rapidamente pelo mapa", Material.FIREWORK, 18000, Arrays.asList(new ItemBuilder().name("\u00a7aKangaroo").type(Material.FIREWORK).build()));
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getEntity();
        if (this.hasAbility(player)) {
            this.addCooldown(player, 8L);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (this.hasAbility(player) && event.getAction() != Action.PHYSICAL && this.isAbilityItem(player.getItemInHand())) {
            event.setCancelled(true);
            if (this.isCooldown(player) || this.kangarooMap.contains(player)) {
                return;
            }
            Vector vector = player.getEyeLocation().getDirection().multiply(player.isSneaking() ? 1.8f : 0.6f).setY(player.isSneaking() ? 0.6 : (double)0.9f);
            player.setFallDistance(-1.0f);
            player.setVelocity(vector);
            this.kangarooMap.add(player);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityDamage(EntityDamageEvent event) {
        Player player;
        if (event.getEntity() instanceof Player && this.hasAbility(player = (Player)event.getEntity()) && event.getCause().name().contains("FALL")) {
            if (event.getDamage() > 7.0) {
                event.setDamage(player.getHealth() - 5.0 > 0.5 ? 5.0 : 0.0);
            } else if (event.getDamage() < 2.0) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Block block;
        Player player = event.getPlayer();
        if (this.hasAbility(player) && this.kangarooMap.contains(player) && (block = player.getLocation().clone().add(0.0, -1.0, 0.0).getBlock()).getType() != Material.AIR) {
            this.kangarooMap.remove(player);
        }
    }
}

