/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package br.com.dragonmc.pvp.arena;

import br.com.dragonmc.pvp.arena.gamer.Gamer;
import br.com.dragonmc.pvp.arena.manager.KitManager;
import br.com.dragonmc.pvp.core.GameAPI;
import br.com.dragonmc.pvp.arena.listener.LauncherListener;
import br.com.dragonmc.pvp.arena.listener.PlayerListener;
import br.com.dragonmc.pvp.arena.listener.ScoreboardListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class GameMain
extends GameAPI {
    private static GameMain instance;
    private KitManager kitManager;

    @Override
    public void onLoad() {
        super.onLoad();
        instance = this;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.setGamerClass(Gamer.class);
        this.setDropItems(true);
        this.kitManager = new KitManager();
        Bukkit.getPluginManager().registerEvents((Listener)new LauncherListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new PlayerListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new ScoreboardListener(), (Plugin)this);
    }

    public KitManager getKitManager() {
        return this.kitManager;
    }

    public static GameMain getInstance() {
        return instance;
    }
}

