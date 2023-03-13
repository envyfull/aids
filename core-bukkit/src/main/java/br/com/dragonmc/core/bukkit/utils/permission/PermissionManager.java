/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Server
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package br.com.dragonmc.core.bukkit.utils.permission;

import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.utils.permission.injector.PermissionMatcher;
import br.com.dragonmc.core.bukkit.utils.permission.injector.RegExpMatcher;
import br.com.dragonmc.core.bukkit.utils.permission.injector.regexperms.RegexPermissions;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class PermissionManager {
    private BukkitCommon plugin;
    private RegexPermissions regexPerms;
    protected PermissionMatcher matcher = new RegExpMatcher();

    public PermissionManager(BukkitCommon plugin) {
        this.plugin = plugin;
        this.regexPerms = new RegexPermissions(this);
    }

    public void onDisable() {
        if (this.regexPerms != null) {
            this.regexPerms.onDisable();
            this.regexPerms = null;
        }
    }

    public Server getServer() {
        return this.plugin.getServer();
    }

    public void registerListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, (Plugin)this.plugin);
    }

    public RegexPermissions getRegexPerms() {
        return this.regexPerms;
    }

    public PermissionMatcher getPermissionMatcher() {
        return this.matcher;
    }

    public BukkitCommon getPlugin() {
        return this.plugin;
    }

    public PermissionMatcher getMatcher() {
        return this.matcher;
    }
}

