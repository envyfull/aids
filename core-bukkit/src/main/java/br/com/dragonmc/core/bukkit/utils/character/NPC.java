/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  com.mojang.authlib.properties.PropertyMap
 *  net.minecraft.server.v1_8_R3.DataWatcher
 *  net.minecraft.server.v1_8_R3.Entity
 *  net.minecraft.server.v1_8_R3.EntityBat
 *  net.minecraft.server.v1_8_R3.EntityHuman
 *  net.minecraft.server.v1_8_R3.EntityLiving
 *  net.minecraft.server.v1_8_R3.EntityPlayer
 *  net.minecraft.server.v1_8_R3.MinecraftServer
 *  net.minecraft.server.v1_8_R3.Packet
 *  net.minecraft.server.v1_8_R3.PacketPlayOutAttachEntity
 *  net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy
 *  net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation
 *  net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata
 *  net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn
 *  net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo
 *  net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo$EnumPlayerInfoAction
 *  net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving
 *  net.minecraft.server.v1_8_R3.PlayerConnection
 *  net.minecraft.server.v1_8_R3.PlayerInteractManager
 *  net.minecraft.server.v1_8_R3.World
 *  net.minecraft.server.v1_8_R3.WorldServer
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.craftbukkit.v1_8_R3.CraftServer
 *  org.bukkit.craftbukkit.v1_8_R3.CraftWorld
 *  org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package br.com.dragonmc.core.bukkit.utils.character;

import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.common.utils.skin.Skin;
import br.com.dragonmc.core.common.utils.string.CodeCreator;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityBat;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.World;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class NPC {
    public static final CodeCreator CODE_CREATOR = new CodeCreator(10).setSpecialCharacters(false).setUpperCase(false).setNumbers(true);
    private GameProfile gameProfile;
    private Location location;
    private EntityPlayer entityPlayer;
    private EntityBat entityBat;
    private Set<UUID> showing = new HashSet<UUID>();

    public NPC(Location location, String skinName) {
        this(location, (Skin)CommonPlugin.getInstance().getSkinData().loadData(skinName).orElse(null));
    }

    public NPC(Location location, Skin skin) {
        this.location = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        MinecraftServer server = ((CraftServer)Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld)location.getWorld()).getHandle();
        this.gameProfile = new GameProfile(UUID.randomUUID(), "\u00a78[" + CODE_CREATOR.random() + "]");
        this.entityPlayer = new EntityPlayer(server, world, this.gameProfile, new PlayerInteractManager((World)world));
        if (skin != null) {
            this.gameProfile.getProperties().clear();
            PropertyMap propertyMap = new PropertyMap();
            propertyMap.put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
            this.gameProfile.getProperties().putAll((Multimap)propertyMap);
        }
        this.entityPlayer.getBukkitEntity().setRemoveWhenFarAway(false);
        this.entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.entityPlayer.setInvisible(false);
        this.entityBat = new EntityBat((World)world);
        this.entityBat.setLocation(location.getX() * 32.0, location.getY() * 32.0, location.getZ() * 32.0, 0.0f, 0.0f);
        this.entityBat.setInvisible(true);
        Bukkit.getOnlinePlayers().forEach(player -> this.show((Player)player));
    }

    public void teleport(Location location) {
        this.location = location;
        this.entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.entityBat.setLocation(location.getX() * 32.0, location.getY() * 32.0, location.getZ() * 32.0, 0.0f, 0.0f);
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.hide(player);
            this.show(player);
        }
    }

    public void show(Player player) {
        if (this.showing.contains(player.getUniqueId())) {
            return;
        }
        this.showing.add(player.getUniqueId());
        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
        connection.sendPacket((Packet)new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[]{this.entityPlayer}));
        connection.sendPacket((Packet)new PacketPlayOutNamedEntitySpawn((EntityHuman)this.entityPlayer));
        connection.sendPacket((Packet)new PacketPlayOutEntityMetadata(this.entityPlayer.getId(), this.entityPlayer.getDataWatcher(), true));
        connection.sendPacket((Packet)new PacketPlayOutEntityHeadRotation((Entity)this.entityPlayer, (byte)(this.location.getYaw() * 256.0f / 360.0f)));
        connection.sendPacket((Packet)new PacketPlayOutSpawnEntityLiving((EntityLiving)this.entityBat));
        connection.sendPacket((Packet)new PacketPlayOutAttachEntity(0, (Entity)this.entityBat, (Entity)this.entityPlayer));
        DataWatcher watcher = this.entityPlayer.getDataWatcher();
        watcher.watch(10, (Object)127);
        connection.sendPacket((Packet)new PacketPlayOutEntityMetadata(this.entityPlayer.getId(), watcher, true));
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)BukkitCommon.getInstance(), () -> connection.sendPacket((Packet)new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[]{this.entityPlayer})), 85L);
    }

    public void hide(Player player) {
        if (!this.showing.contains(player.getUniqueId())) {
            return;
        }
        this.showing.remove(player.getUniqueId());
        PlayerConnection playerConnection = ((CraftPlayer)player).getHandle().playerConnection;
        playerConnection.sendPacket((Packet)new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[]{this.entityPlayer}));
        playerConnection.sendPacket((Packet)new PacketPlayOutEntityDestroy(new int[]{this.entityPlayer.getId()}));
        playerConnection.sendPacket((Packet)new PacketPlayOutEntityDestroy(new int[]{this.entityBat.getId()}));
    }

    public void remove() {
        Bukkit.getOnlinePlayers().forEach(player -> this.hide((Player)player));
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public Location getLocation() {
        return this.location;
    }

    public EntityPlayer getEntityPlayer() {
        return this.entityPlayer;
    }

    public EntityBat getEntityBat() {
        return this.entityBat;
    }

    public Set<UUID> getShowing() {
        return this.showing;
    }
}

