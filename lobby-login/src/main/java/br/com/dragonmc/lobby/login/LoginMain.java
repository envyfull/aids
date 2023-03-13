/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package br.com.dragonmc.lobby.login;

import br.com.dragonmc.lobby.login.listener.PlayerListener;
import br.com.dragonmc.lobby.login.listener.ScoreboardListener;
import br.com.dragonmc.lobby.core.CoreMain;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class LoginMain
extends CoreMain {
    @Override
    public void onEnable() {
        super.onEnable();
        Bukkit.getPluginManager().registerEvents((Listener)new PlayerListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new ScoreboardListener(), (Plugin)this);
        this.setMaxPlayers(6);
        this.setPlayerInventory(player -> {});
    }
}

