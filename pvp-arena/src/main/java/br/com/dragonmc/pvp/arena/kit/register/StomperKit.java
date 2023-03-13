/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_8_R3.EnumParticle
 *  net.minecraft.server.v1_8_R3.Packet
 *  net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 *  org.bukkit.inventory.ItemStack
 */
package br.com.dragonmc.pvp.arena.kit.register;

import java.util.ArrayList;

import br.com.dragonmc.pvp.arena.event.PlayerStompedEvent;
import br.com.dragonmc.pvp.arena.kit.Kit;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class StomperKit
extends Kit {
    public StomperKit() {
        super("Stomper", "Pise em cima de seus inimigos", Material.IRON_BOOTS, 21000, new ArrayList<ItemStack>());
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }
        Player stomper = (Player)event.getEntity();
        if (!this.hasAbility(stomper)) {
            return;
        }
        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause != EntityDamageEvent.DamageCause.FALL) {
            return;
        }
        double dmg = event.getDamage();
        for (Player stomped : Bukkit.getOnlinePlayers()) {
            if (stomped.getUniqueId() == stomper.getUniqueId() || stomped.isDead() || stomped.getLocation().distance(stomper.getLocation()) > 5.0) continue;
            if (stomped.isSneaking() && dmg > 8.0) {
                dmg = 8.0;
            }
            PlayerStompedEvent playerStomperEvent = new PlayerStompedEvent(stomped, stomper);
            Bukkit.getPluginManager().callEvent((Event)playerStomperEvent);
            if (playerStomperEvent.isCancelled()) continue;
            stomped.damage(0.1, (Entity)stomper);
            stomped.damage(dmg);
        }
        for (int x = -3; x <= 3; ++x) {
            for (int z = -3; z <= 3; ++z) {
                Location effect = stomper.getLocation().clone().add((double)x, 0.0, (double)z);
                if (effect.distance(stomper.getLocation()) > 3.0) continue;
                PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.SPELL_WITCH, true, (float)effect.getX(), (float)effect.getY(), (float)effect.getZ(), 0.1f, 0.1f, 0.1f, 1.0f, 30, new int[0]);
                Bukkit.getOnlinePlayers().stream().filter(viewer -> viewer.canSee(stomper)).forEach(viewer -> ((CraftPlayer)viewer).getHandle().playerConnection.sendPacket((Packet)packet));
            }
        }
        stomper.getWorld().playSound(stomper.getLocation(), Sound.ANVIL_LAND, 1.0f, 1.0f);
        if (event.getDamage() > 4.0) {
            event.setDamage(4.0);
        }
    }
}

