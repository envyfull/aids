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
 *  com.comphenix.protocol.wrappers.EnumWrappers$EntityUseAction
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerRespawnEvent
 *  org.bukkit.event.player.PlayerTeleportEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package br.com.dragonmc.core.bukkit.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.EnumWrappers;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.BukkitMain;
import br.com.dragonmc.core.bukkit.event.player.PlayerMoveUpdateEvent;
import br.com.dragonmc.core.bukkit.utils.character.Character;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CharacterListener
implements Listener {
    private static final double MAX_DISTANCE = 128.0;

    public CharacterListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter((Plugin)BukkitCommon.getInstance(), new PacketType[]{PacketType.Play.Client.USE_ENTITY}){

            public void onPacketReceiving(PacketEvent event) {
                int entityId;
                Character character;
                if (event.isCancelled()) {
                    return;
                }
                Player player = event.getPlayer();
                if ((event.getPacket().getEntityUseActions().read(0) == EnumWrappers.EntityUseAction.INTERACT || event.getPacket().getEntityUseActions().read(0) == EnumWrappers.EntityUseAction.ATTACK) && (character = Character.getCharacter(entityId = ((Integer)event.getPacket().getIntegers().read(0)).intValue())) != null) {
                    character.getInteractHandler().onInteract(player, event.getPacket().getEntityUseActions().read(0) == EnumWrappers.EntityUseAction.INTERACT);
                }
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Character.getCharacters().forEach(character -> {
            if (character.getNpc().getLocation().getWorld() == event.getPlayer().getLocation().getWorld() && character.getNpc().getLocation().distance(event.getPlayer().getLocation()) < 128.0) {
                character.show(event.getPlayer());
            }
        });
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        new BukkitRunnable(){

            public void run() {
                Character.getCharacters().forEach(character -> {
                    if (character.getNpc().getLocation().getWorld() == event.getPlayer().getLocation().getWorld() && character.isShowing(event.getPlayer().getUniqueId()) && character.getNpc().getLocation().distance(event.getPlayer().getLocation()) < 128.0) {
                        character.hide(event.getPlayer());
                        character.show(event.getPlayer());
                    }
                });
            }
        }.runTaskLater((Plugin)BukkitMain.getInstance(), 5L);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Character.getCharacters().forEach(character -> {
            if (character.isShowing(event.getPlayer().getUniqueId())) {
                if (character.getNpc().getLocation().getWorld() != event.getPlayer().getLocation().getWorld()) {
                    character.hide(event.getPlayer());
                } else if (character.getNpc().getLocation().distance(event.getPlayer().getLocation()) > 128.0) {
                    character.hide(event.getPlayer());
                }
            } else if (character.getNpc().getLocation().getWorld() == event.getPlayer().getLocation().getWorld() && character.getNpc().getLocation().distance(event.getPlayer().getLocation()) < 128.0) {
                character.show(event.getPlayer());
            }
        });
    }

    @EventHandler
    public void onPlayerMoveUpdate(PlayerMoveUpdateEvent event) {
        Character.getCharacters().forEach(character -> {
            if (character.isShowing(event.getPlayer().getUniqueId())) {
                if (character.getNpc().getLocation().getWorld() != event.getPlayer().getLocation().getWorld()) {
                    character.hide(event.getPlayer());
                } else if (character.getNpc().getLocation().distance(event.getPlayer().getLocation()) > 128.0) {
                    character.hide(event.getPlayer());
                }
            } else if (character.getNpc().getLocation().getWorld() == event.getPlayer().getLocation().getWorld() && character.getNpc().getLocation().distance(event.getPlayer().getLocation()) < 128.0) {
                character.show(event.getPlayer());
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerQuitEvent event) {
        Character.getCharacters().forEach(character -> character.hide(event.getPlayer()));
    }
}

