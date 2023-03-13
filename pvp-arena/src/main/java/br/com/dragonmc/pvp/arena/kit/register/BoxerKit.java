/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.inventory.ItemStack
 */
package br.com.dragonmc.pvp.arena.kit.register;

import java.util.ArrayList;
import br.com.dragonmc.pvp.arena.kit.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class BoxerKit
extends Kit {
    public BoxerKit() {
        super("Boxer", "Vire um boxeador e esteja acustumado a receber pancadas e a revida-las", Material.STONE_SWORD, 12000, new ArrayList<ItemStack>());
    }

    @EventHandler
    public void onBoxer(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player damager = (Player)event.getDamager();
        if (!this.hasAbility(damager)) {
            return;
        }
        if (damager.getItemInHand().getType() == Material.AIR) {
            event.setDamage(event.getDamage() + 2.0);
        }
    }

    public void onSnail(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player damaged = (Player)event.getEntity();
        if (!this.hasAbility(damaged)) {
            return;
        }
        if (event.getDamage() - 1.0 >= 1.0) {
            event.setDamage(event.getDamage() - 1.0);
        } else {
            event.setDamage(1.0);
        }
    }
}

