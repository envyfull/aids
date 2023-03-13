/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.ProtocolLibrary
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.reflect.FieldAccessException
 *  com.comphenix.protocol.utility.MinecraftReflection
 *  com.comphenix.protocol.wrappers.EnumWrappers$Difficulty
 *  com.comphenix.protocol.wrappers.EnumWrappers$NativeGameMode
 *  com.comphenix.protocol.wrappers.EnumWrappers$PlayerInfoAction
 *  com.comphenix.protocol.wrappers.PlayerInfoData
 *  com.comphenix.protocol.wrappers.WrappedChatComponent
 *  com.comphenix.protocol.wrappers.WrappedGameProfile
 *  com.comphenix.protocol.wrappers.WrappedSignedProperty
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package br.com.dragonmc.core.bukkit.utils.player;

import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.BukkitMain;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import br.com.dragonmc.core.common.CommonConst;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerAPI {
    public static void changePlayerName(Player player, String name) {
        PlayerAPI.changePlayerName(player, name, true);
    }

    public static void changePlayerName(Player player, String name, boolean respawn) {
        if (respawn) {
            PlayerAPI.removeFromTab(player);
        }
        try {
            Object minecraftServer = MinecraftReflection.getMinecraftServerClass().getMethod("getServer", new Class[0]).invoke(null, new Object[0]);
            Object playerList = minecraftServer.getClass().getMethod("getPlayerList", new Class[0]).invoke(minecraftServer, new Object[0]);
            Field f = playerList.getClass().getSuperclass().getDeclaredField("playersByName");
            f.setAccessible(true);
            Map playersByName = (Map)f.get(playerList);
            playersByName.remove(player.getName());
            WrappedGameProfile profile = WrappedGameProfile.fromPlayer((Player)player);
            Field field = profile.getHandle().getClass().getDeclaredField("name");
            field.setAccessible(true);
            field.set(profile.getHandle(), name);
            field.setAccessible(false);
            playersByName.put(name, MinecraftReflection.getCraftPlayerClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]));
            f.setAccessible(false);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (respawn) {
            PlayerAPI.respawnPlayer(player);
        }
    }

    public void addToTab(Player player, Collection<? extends Player> players) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        if (player.getGameMode() != null) {
            try {
                Object entityPlayer = MinecraftReflection.getCraftPlayerClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
                Object object = MinecraftReflection.getEntityPlayerClass().getMethod("getPlayerListName", new Class[0]).invoke(entityPlayer, new Object[0]);
                packet.getPlayerInfoDataLists().write(0, Arrays.asList(new PlayerInfoData(WrappedGameProfile.fromPlayer((Player)player), 0, EnumWrappers.NativeGameMode.fromBukkit((GameMode)player.getGameMode()), object != null ? WrappedChatComponent.fromHandle((Object)object) : null)));
            }
            catch (FieldAccessException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e1) {
                e1.printStackTrace();
            }
        }
        for (Player player2 : players) {
            if (!player2.canSee(player)) continue;
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player2, packet);
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static void removeFromTab(Player player) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        if (player.getGameMode() != null) {
            try {
                Object entityPlayer = MinecraftReflection.getCraftPlayerClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
                Object getDisplayName = MinecraftReflection.getEntityPlayerClass().getMethod("getPlayerListName", new Class[0]).invoke(entityPlayer, new Object[0]);
                packet.getPlayerInfoDataLists().write(0, Arrays.asList(new PlayerInfoData(WrappedGameProfile.fromPlayer((Player)player), 0, EnumWrappers.NativeGameMode.fromBukkit((GameMode)player.getGameMode()), getDisplayName != null ? WrappedChatComponent.fromHandle((Object)getDisplayName) : null)));
            }
            catch (FieldAccessException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e1) {
                e1.printStackTrace();
            }
        }
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!online.canSee(player)) continue;
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(online, packet);
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static void respawnPlayer(final Player player) {
        PlayerAPI.respawnSelf(player);
        new BukkitRunnable(){

            public void run() {
                Bukkit.getOnlinePlayers().stream().filter(onlinePlayer -> !onlinePlayer.equals(player)).filter(onlinePlayer -> onlinePlayer.canSee(player)).forEach(onlinePlayer -> {
                    onlinePlayer.hidePlayer(player);
                    onlinePlayer.showPlayer(player);
                });
            }
        }.runTaskLater((Plugin) BukkitCommon.getInstance(), 2L);
    }

    public static void respawnSelf(final Player player) {
        ArrayList<PlayerInfoData> data = new ArrayList<PlayerInfoData>();
        if (player.getGameMode() != null) {
            try {
                Object entityPlayer = MinecraftReflection.getCraftPlayerClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
                Object getDisplayName = MinecraftReflection.getEntityPlayerClass().getMethod("getPlayerListName", new Class[0]).invoke(entityPlayer, new Object[0]);
                int ping = (Integer)MinecraftReflection.getEntityPlayerClass().getField("ping").get(entityPlayer);
                data.add(new PlayerInfoData(WrappedGameProfile.fromPlayer((Player)player), ping, EnumWrappers.NativeGameMode.fromBukkit((GameMode)player.getGameMode()), getDisplayName != null ? WrappedChatComponent.fromHandle((Object)getDisplayName) : null));
            }
            catch (FieldAccessException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException | NoSuchMethodException | SecurityException | InvocationTargetException e1) {
                e1.printStackTrace();
            }
        }
        final PacketContainer addPlayerInfo = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        addPlayerInfo.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        addPlayerInfo.getPlayerInfoDataLists().write(0, data);
        final PacketContainer removePlayerInfo = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        removePlayerInfo.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        removePlayerInfo.getPlayerInfoDataLists().write(0, data);
        final PacketContainer respawnPlayer = new PacketContainer(PacketType.Play.Server.RESPAWN);
        respawnPlayer.getIntegers().write(0, player.getWorld().getEnvironment().getId());
        respawnPlayer.getDifficulties().write(0, EnumWrappers.Difficulty.valueOf((String)player.getWorld().getDifficulty().name()));
        if (player.getGameMode() != null) {
            respawnPlayer.getGameModes().write(0, EnumWrappers.NativeGameMode.fromBukkit((GameMode)player.getGameMode()));
        }
        respawnPlayer.getWorldTypeModifier().write(0, player.getWorld().getWorldType());
        final boolean flying = player.isFlying();
        new BukkitRunnable(){

            public void run() {
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, removePlayerInfo);
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, addPlayerInfo);
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, respawnPlayer);
                    player.getInventory().setHeldItemSlot(player.getInventory().getHeldItemSlot());
                    player.teleport(player.getLocation());
                    player.setFlying(flying);
                    player.setWalkSpeed(player.getWalkSpeed());
                    player.setMaxHealth(player.getMaxHealth());
                    player.setHealthScale(player.getHealthScale());
                    player.setExp(player.getExp());
                    player.setLevel(player.getLevel());
                    player.updateInventory();
                }
                catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }.runTask((Plugin) BukkitMain.getInstance());
    }

    public static WrappedSignedProperty changePlayerSkin(Player player, String value, String signature, boolean respawn) {
        return PlayerAPI.changePlayerSkin(player, new WrappedSignedProperty("textures", value, signature));
    }

    public static WrappedSignedProperty changePlayerSkin(Player player, String name, UUID uuid, boolean respawn) {
        WrappedSignedProperty property = null;
        WrappedGameProfile gameProfile = WrappedGameProfile.fromPlayer((Player)player);
        gameProfile.getProperties().clear();
        property = TextureFetcher.loadTexture(new WrappedGameProfile(uuid, name));
        gameProfile.getProperties().put("textures", property);
        if (respawn) {
            PlayerAPI.respawnPlayer(player);
        }
        return property;
    }

    public static WrappedSignedProperty changePlayerSkin(Player player, WrappedSignedProperty wrappedSignedProperty) {
        WrappedGameProfile gameProfile = WrappedGameProfile.fromPlayer((Player)player);
        gameProfile.getProperties().clear();
        gameProfile.getProperties().put("textures", wrappedSignedProperty);
        PlayerAPI.respawnPlayer(player);
        return wrappedSignedProperty;
    }

    public static void changePlayerSkin(Player player, WrappedSignedProperty property, boolean respawn) {
        WrappedGameProfile gameProfile = WrappedGameProfile.fromPlayer((Player)player);
        gameProfile.getProperties().clear();
        gameProfile.getProperties().put("textures", property);
        if (respawn) {
            PlayerAPI.respawnPlayer(player);
        }
    }

    public static void removePlayerSkin(Player player) {
        PlayerAPI.removePlayerSkin(player, true);
    }

    public static void removePlayerSkin(Player player, boolean respawn) {
        WrappedGameProfile gameProfile = WrappedGameProfile.fromPlayer((Player)player);
        gameProfile.getProperties().clear();
        if (respawn) {
            PlayerAPI.respawnPlayer(player);
        }
    }

    public static boolean validateName(String username) {
        return CommonConst.NAME_PATTERN.matcher(username).matches();
    }
}

