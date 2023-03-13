/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 *  org.bukkit.inventory.ItemStack
 */
package br.com.dragonmc.pvp.arena.kit.register;

import java.util.ArrayList;

import br.com.dragonmc.pvp.arena.event.PlayerStompedEvent;
import br.com.dragonmc.pvp.arena.kit.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class AntitowerKit
extends Kit {
    public AntitowerKit() {
        super("Antitower", "N\u00e3o seja stompado", Material.GOLD_HELMET, 22000, new ArrayList<ItemStack>());
    }

    @EventHandler
    public void onPlayerStomped(PlayerStompedEvent event) {
        if (this.hasAbility(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().damage(4.0);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL && this.hasAbility((Player)event.getEntity())) {
            event.setCancelled(true);
        }
    }
}

