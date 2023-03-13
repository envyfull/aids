/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.ProtocolLibrary
 *  com.comphenix.protocol.events.ListenerPriority
 *  com.comphenix.protocol.events.PacketAdapter
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.events.PacketEvent
 *  com.comphenix.protocol.events.PacketListener
 *  com.comphenix.protocol.wrappers.WrappedWatchableObject
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package br.com.dragonmc.core.bukkit.manager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.utils.hologram.Hologram;
import br.com.dragonmc.core.bukkit.utils.hologram.HologramBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class HologramManager {
    private JavaPlugin javaPlugin;
    private List<Hologram> hologramList = new ArrayList<Hologram>();

    public HologramManager() {
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter((Plugin)BukkitCommon.getInstance(), ListenerPriority.HIGHEST, new PacketType[]{PacketType.Play.Server.SPAWN_ENTITY_LIVING, PacketType.Play.Server.ENTITY_METADATA}){

            public void onPacketSending(PacketEvent event) {
                Entity entity = (Entity)event.getPacket().getEntityModifier(event.getPlayer().getWorld()).read(0);
                Player player = event.getPlayer();
                if (entity != null && entity.getType() == EntityType.ARMOR_STAND) {
                    Hologram hologram = HologramManager.this.hologramList.stream().filter(h -> h.isEntityOrLine(entity.getEntityId())).findFirst().orElse(null);
                    if (hologram == null) {
                        return;
                    }
                    if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
                        if (hologram.hasViewHandler()) {
                            PacketContainer packet = event.getPacket().deepClone();
                            List<WrappedWatchableObject> objects = (List)packet.getWatchableCollectionModifier().read(0);
                            for (WrappedWatchableObject obj : objects) {
                                if (obj.getIndex() != 2) continue;
                                obj.setValue((Object)hologram.getViewHandler().onView(hologram, player, (String)obj.getRawValue()));
                                break;
                            }
                            event.setPacket(packet);
                        }
                    } else if (!hologram.isVisibleTo(player)) {
                        event.setCancelled(true);
                    }
                }
            }
        });
        Bukkit.getWorlds().forEach(world -> world.getEntities().forEach(entity -> entity.remove()));
    }

    public void registerHologram(Hologram hologram) {
        if (!this.hologramList.contains(hologram)) {
            this.hologramList.add(hologram);
        }
    }

    public void unregisterHologram(Hologram hologram) {
        if (this.hologramList.contains(hologram)) {
            this.hologramList.remove(hologram);
        }
    }

    public Hologram createHologram(String displayName, Location location, Class<? extends Hologram> clazz) {
        Hologram hologram = null;
        try {
            hologram = clazz.getConstructor(String.class, Location.class).newInstance(displayName, location);
            hologram.spawn();
            this.registerHologram(hologram);
        }
        catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return hologram;
    }

    public Hologram createHologram(HologramBuilder hologramBuilder) {
        Hologram hologram = hologramBuilder.build();
        hologram.spawn();
        this.registerHologram(hologram);
        return hologram;
    }

    public Hologram createHologram(Hologram hologram) {
        hologram.spawn();
        this.registerHologram(hologram);
        return hologram;
    }

    public JavaPlugin getJavaPlugin() {
        return this.javaPlugin;
    }

    public List<Hologram> getHologramList() {
        return this.hologramList;
    }
}

