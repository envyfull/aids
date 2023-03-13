/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package br.com.dragonmc.pvp.arena.kit.register;

import java.util.Arrays;
import java.util.Random;

import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.pvp.arena.kit.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ReaperKit
extends Kit {
    public ReaperKit() {
        super("Reaper", "Ceife a alma de seus inimigos por alguns segundos com a sua enxada", Material.WOOD_HOE, 11250, Arrays.asList(new ItemBuilder().name("\u00a7aReaper").type(Material.WOOD_HOE).build()));
    }

    @EventHandler
    public void onSnail(EntityDamageByEntityEvent event) {
        ItemStack item;
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player damager = (Player)event.getDamager();
        if (this.hasAbility(damager) && this.isAbilityItem(item = damager.getItemInHand())) {
            event.setCancelled(true);
            damager.updateInventory();
            Random r = new Random();
            Player damaged = (Player)event.getEntity();
            if (damaged instanceof Player && r.nextInt(4) == 0) {
                damaged.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 4));
            }
        }
    }
}

