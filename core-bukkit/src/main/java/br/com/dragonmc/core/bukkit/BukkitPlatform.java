/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package br.com.dragonmc.core.bukkit;

import java.util.UUID;
import java.util.logging.Logger;
import br.com.dragonmc.core.common.PluginPlatform;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BukkitPlatform
implements PluginPlatform {
    @Override
    public UUID getUniqueId(String playerName) {
        Player player = Bukkit.getPlayerExact((String)playerName);
        return player == null ? null : player.getUniqueId();
    }

    @Override
    public String getName(UUID uuid) {
        Player player = Bukkit.getPlayer((UUID)uuid);
        return player == null ? null : player.getName();
    }

    @Override
    public void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)BukkitCommon.getPlugin(BukkitCommon.class), runnable);
    }

    @Override
    public void runAsync(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin)BukkitCommon.getPlugin(BukkitCommon.class), runnable, delay);
    }

    @Override
    public void runAsync(Runnable runnable, long delay, long repeat) {
        Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)BukkitCommon.getPlugin(BukkitCommon.class), runnable, delay, repeat);
    }

    @Override
    public void run(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLater((Plugin)BukkitCommon.getPlugin(BukkitCommon.class), runnable, delay);
    }

    @Override
    public void run(Runnable runnable, long delay, long repeat) {
        Bukkit.getScheduler().runTaskTimer((Plugin)BukkitCommon.getPlugin(BukkitCommon.class), runnable, delay, repeat);
    }

    @Override
    public void shutdown(String message) {
        Bukkit.getConsoleSender().sendMessage("\u00a74" + message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("\u00a7cO servidor foi fechado!");
        }
        Bukkit.shutdown();
    }

    @Override
    public Logger getLogger() {
        return Bukkit.getLogger();
    }

    @Override
    public void dispatchCommand(String command) {
        Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), (String)command);
    }

    @Override
    public void broadcast(String string) {
        Bukkit.broadcastMessage((String)string);
    }

    @Override
    public void broadcast(String string, String permission) {
        Bukkit.broadcast((String)string, (String)permission);
    }
}

