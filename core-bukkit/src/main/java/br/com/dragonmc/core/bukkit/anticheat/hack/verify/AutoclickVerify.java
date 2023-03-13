/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Client
 *  com.comphenix.protocol.ProtocolLibrary
 *  com.comphenix.protocol.events.PacketAdapter
 *  com.comphenix.protocol.events.PacketEvent
 *  com.comphenix.protocol.events.PacketListener
 *  com.google.common.collect.ImmutableList
 *  org.bukkit.GameMode
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.block.BlockDamageEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.plugin.Plugin
 */
package br.com.dragonmc.core.bukkit.anticheat.hack.verify;

import br.com.dragonmc.core.bukkit.BukkitMain;
import br.com.dragonmc.core.bukkit.anticheat.hack.HackType;
import br.com.dragonmc.core.bukkit.anticheat.hack.Verify;
import br.com.dragonmc.core.bukkit.event.UpdateEvent;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import br.com.dragonmc.core.bukkit.anticheat.hack.Clicks;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class AutoclickVerify
implements Verify {
    private Map<Player, Clicks> clicksPerSecond = new HashMap<Player, Clicks>();
    private Map<Player, Long> cooldownMap = new HashMap<Player, Long>();

    public AutoclickVerify() {
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter((Plugin) BukkitMain.getInstance(), new PacketType[]{PacketType.Play.Client.ARM_ANIMATION}){

            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (player == null) {
                    return;
                }
                if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.ADVENTURE) {
                    return;
                }
                if (AutoclickVerify.this.cooldownMap.containsKey(player) && (Long)AutoclickVerify.this.cooldownMap.get(player) > System.currentTimeMillis()) {
                    return;
                }
                try {
                    if (player.getTargetBlock((Set)null, 4).getType() != Material.AIR) {
                        return;
                    }
                }
                catch (IllegalStateException ex) {
                    return;
                }
                AutoclickVerify.this.handle(player);
            }
        });
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerInteract(BlockDamageEvent event) {
        this.clicksPerSecond.remove(event.getPlayer());
        this.cooldownMap.put(event.getPlayer(), System.currentTimeMillis() + 1000L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.clicksPerSecond.remove(event.getPlayer());
        this.cooldownMap.remove(event.getPlayer());
    }

    public void handle(Player player) {
        this.clicksPerSecond.computeIfAbsent(player, v -> new Clicks()).addClick();
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        ImmutableList.copyOf(this.clicksPerSecond.entrySet()).stream().filter(entry -> ((Clicks)entry.getValue()).getExpireTime() < System.currentTimeMillis()).forEach(entry -> {
            if (((Clicks)entry.getValue()).getClicks() >= 25) {
                this.alert((Player)entry.getKey(), "(" + ((Clicks)entry.getValue()).getClicks() + " cps)");
            }
            this.clicksPerSecond.remove(entry.getKey());
        });
    }

    @Override
    public HackType getHackType() {
        return HackType.AUTOCLICK;
    }
}

