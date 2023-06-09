/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package br.com.dragonmc.game.engine;

import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.game.engine.event.GameStateChangeEvent;
import br.com.dragonmc.game.engine.scheduler.Scheduler;
import br.com.dragonmc.game.engine.backend.GamerData;
import br.com.dragonmc.game.engine.backend.impl.GamerDataImpl;
import br.com.dragonmc.game.engine.gamer.Gamer;
import br.com.dragonmc.game.engine.listener.GamerListener;
import br.com.dragonmc.game.engine.listener.SchedulerListener;
import br.com.dragonmc.game.engine.manager.GamerManager;
import br.com.dragonmc.game.engine.manager.SchedulerManager;
import br.com.dragonmc.core.common.server.loadbalancer.server.MinigameState;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class GameAPI
extends BukkitCommon {
    private static GameAPI instance;
    private GamerManager gamerManager;
    private SchedulerManager schedulerManager;
    private boolean unloadGamer = true;
    private Class<? extends Gamer> gamerClass;
    private String collectionName;
    private boolean timer;
    private boolean consoleControl = true;
    private GamerData gamerData;

    @Override
    public void onLoad() {
        super.onLoad();
        this.setServerLog(false);
        this.setRemovePlayerDat(false);
        instance = this;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.gamerManager = new GamerManager();
        this.schedulerManager = new SchedulerManager();
        this.gamerData = new GamerDataImpl();
        Bukkit.getPluginManager().registerEvents((Listener)new GamerListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new SchedulerListener(), (Plugin)this);
    }

    public void startScheduler(Scheduler scheduler) {
        this.getSchedulerManager().loadScheduler(scheduler);
        if (Listener.class.isAssignableFrom(scheduler.getClass())) {
            Bukkit.getPluginManager().registerEvents((Listener)scheduler, (Plugin)this);
        }
    }

    public void stopScheduler(Scheduler scheduler) {
        this.getSchedulerManager().unloadScheduler(scheduler);
        if (Listener.class.isAssignableFrom(scheduler.getClass())) {
            HandlerList.unregisterAll((Listener)((Listener)scheduler));
        }
    }

    public void setMap(String mapName) {
        CommonPlugin.getInstance().setMap(mapName);
        CommonPlugin.getInstance().getServerData().updateStatus(this.getState(), this.getMapName(), this.getTime());
    }

    public void setTime(int time) {
        CommonPlugin.getInstance().setServerTime(time);
        CommonPlugin.getInstance().getServerData().updateStatus(this.getState(), this.getMapName(), this.getTime());
    }

    public void setState(MinigameState state) {
        MinigameState oldState = CommonPlugin.getInstance().getMinigameState();
        if (oldState != state) {
            CommonPlugin.getInstance().setMinigameState(state);
            CommonPlugin.getInstance().getServerData().updateStatus(this.getState(), this.getMapName(), this.getTime());
            System.out.println((Object)((Object)oldState) + " > " + (Object)((Object)state));
            Bukkit.getPluginManager().callEvent((Event)new GameStateChangeEvent(oldState, state));
        }
    }

    public String getMapName() {
        return CommonPlugin.getInstance().getMap();
    }

    public int getTime() {
        return CommonPlugin.getInstance().getServerTime();
    }

    public MinigameState getState() {
        return CommonPlugin.getInstance().getMinigameState();
    }

    public GamerManager getGamerManager() {
        return this.gamerManager;
    }

    public SchedulerManager getSchedulerManager() {
        return this.schedulerManager;
    }

    public boolean isUnloadGamer() {
        return this.unloadGamer;
    }

    public Class<? extends Gamer> getGamerClass() {
        return this.gamerClass;
    }

    public String getCollectionName() {
        return this.collectionName;
    }

    public boolean isTimer() {
        return this.timer;
    }

    public boolean isConsoleControl() {
        return this.consoleControl;
    }

    public GamerData getGamerData() {
        return this.gamerData;
    }

    public static GameAPI getInstance() {
        return instance;
    }

    public void setUnloadGamer(boolean unloadGamer) {
        this.unloadGamer = unloadGamer;
    }

    public void setGamerClass(Class<? extends Gamer> gamerClass) {
        this.gamerClass = gamerClass;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public void setTimer(boolean timer) {
        this.timer = timer;
    }

    public void setConsoleControl(boolean consoleControl) {
        this.consoleControl = consoleControl;
    }
}

