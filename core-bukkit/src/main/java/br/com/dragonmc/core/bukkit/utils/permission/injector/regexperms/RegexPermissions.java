/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerKickEvent
 *  org.bukkit.event.player.PlayerLoginEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.permissions.Permissible
 *  org.bukkit.plugin.Plugin
 */
package br.com.dragonmc.core.bukkit.utils.permission.injector.regexperms;

import java.util.logging.Level;

import br.com.dragonmc.core.bukkit.utils.permission.PermissionManager;
import br.com.dragonmc.core.bukkit.utils.permission.injector.CraftBukkitInterface;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;

public class RegexPermissions {
    private final PermissionManager plugin;
    private PermissionList permsList;
    private PEXPermissionSubscriptionMap subscriptionHandler;

    public RegexPermissions(PermissionManager plugin) {
        this.plugin = plugin;
        this.subscriptionHandler = PEXPermissionSubscriptionMap.inject((Plugin)plugin.getPlugin(), plugin.getServer().getPluginManager());
        this.permsList = PermissionList.inject(plugin.getServer().getPluginManager());
        plugin.getServer().getPluginManager().registerEvents((Listener)new EventListener(), (Plugin)plugin.getPlugin());
        this.injectAllPermissibles();
    }

    public void onDisable() {
        this.subscriptionHandler.uninject();
        this.uninjectAllPermissibles();
    }

    public PermissionList getPermissionList() {
        return this.permsList;
    }

    public void injectPermissible(Player player) {
        try {
            Permissible oldPerm;
            PermissiblePEX permissible = new PermissiblePEX(player, this.plugin);
            PermissibleInjector.ClassPresencePermissibleInjector injector = new PermissibleInjector.ClassPresencePermissibleInjector(CraftBukkitInterface.getCBClassName("entity.CraftHumanEntity"), "perm", true);
            boolean success = false;
            if (((PermissibleInjector)injector).isApplicable(player) && (oldPerm = injector.inject(player, (Permissible)permissible)) != null) {
                permissible.setPreviousPermissible(oldPerm);
                success = true;
            }
            if (!success) {
                this.plugin.getPlugin().getLogger().warning("Unable to inject PEX's permissible for " + player.getName());
            }
            permissible.recalculatePermissions();
        }
        catch (Throwable e) {
            this.plugin.getPlugin().getLogger().log(Level.SEVERE, "Unable to inject permissible for " + player.getName(), e);
        }
    }

    private void injectAllPermissibles() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.injectPermissible(player);
        }
    }

    private void uninjectPermissible(Player player) {
        try {
            boolean success = false;
            PermissibleInjector.ClassPresencePermissibleInjector injector = new PermissibleInjector.ClassPresencePermissibleInjector(CraftBukkitInterface.getCBClassName("entity.CraftHumanEntity"), "perm", true);
            if (((PermissibleInjector)injector).isApplicable(player)) {
                Permissible pexPerm = injector.getPermissible(player);
                if (pexPerm instanceof PermissiblePEX) {
                    if (injector.inject(player, ((PermissiblePEX)pexPerm).getPreviousPermissible()) != null) {
                        success = true;
                    }
                } else {
                    success = true;
                }
            }
            if (!success) {
                this.plugin.getPlugin().getLogger().warning("No Permissible injector found for your server implementation (while uninjecting for " + player.getName() + "!");
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void uninjectAllPermissibles() {
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            this.uninjectPermissible(player);
        }
    }

    private class EventListener
    implements Listener {
        private EventListener() {
        }

        @EventHandler(priority=EventPriority.LOWEST)
        public void onPlayerLogin(PlayerLoginEvent event) {
            RegexPermissions.this.injectPermissible(event.getPlayer());
        }

        @EventHandler(priority=EventPriority.MONITOR)
        public void onPlayerQuit(PlayerQuitEvent event) {
            RegexPermissions.this.uninjectPermissible(event.getPlayer());
        }

        @EventHandler(priority=EventPriority.MONITOR)
        public void onPlayerKick(PlayerKickEvent event) {
            RegexPermissions.this.uninjectPermissible(event.getPlayer());
        }
    }
}

