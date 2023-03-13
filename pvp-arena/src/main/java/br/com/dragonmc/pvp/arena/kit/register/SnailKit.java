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

import java.util.ArrayList;
import java.util.Random;
import br.com.dragonmc.pvp.arena.kit.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SnailKit
extends Kit {
    public SnailKit() {
        super("Snail", "Deixe seus inimigos mais lentos ao encosta-los", Material.WEB, 12500, new ArrayList<ItemStack>());
    }

    @EventHandler
    public void onSnail(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player damager = (Player)event.getDamager();
        if (!this.hasAbility(damager)) {
            return;
        }
        Random r = new Random();
        Player damaged = (Player)event.getEntity();
        if (damaged instanceof Player && r.nextInt(4) == 0) {
            damaged.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 1));
        }
    }
}

