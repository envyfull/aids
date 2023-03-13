/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.UnmodifiableIterator
 *  org.bukkit.Bukkit
 *  org.bukkit.event.Event
 */
package br.com.dragonmc.game.engine.manager;

import br.com.dragonmc.game.engine.GameAPI;
import br.com.dragonmc.game.engine.event.SchedulePulseEvent;
import br.com.dragonmc.game.engine.scheduler.Scheduler;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public class SchedulerManager {
    private Map<String, Scheduler> schedulerMap = new HashMap<String, Scheduler>();

    public void loadScheduler(Scheduler scheduler) {
        this.schedulerMap.put(scheduler.toString().toLowerCase(), scheduler);
    }

    public void loadScheduler(String identifier, Scheduler scheduler) {
        this.schedulerMap.put(identifier, scheduler);
    }

    public void unloadSchedulers() {
        this.schedulerMap.clear();
    }

    public Collection<Scheduler> getSchedulers() {
        return ImmutableList.copyOf(this.schedulerMap.values());
    }

    public void unloadScheduler(Scheduler scheduler) {
        String identifier = null;
        for (Map.Entry<String, Scheduler> entry : this.schedulerMap.entrySet()) {
            if (entry.getValue() != scheduler) continue;
            identifier = entry.getKey();
            break;
        }
        if (identifier != null) {
            this.schedulerMap.remove(identifier);
        }
    }

    public void unloadScheduler(String identifier) {
        this.schedulerMap.remove(identifier);
    }

    public void pulse() {
        boolean pulse = false;
        UnmodifiableIterator iterator = ImmutableMap.copyOf(this.schedulerMap).values().iterator();
        while (iterator.hasNext()) {
            ((Scheduler)iterator.next()).pulse();
            pulse = true;
        }
        if (pulse) {
            Bukkit.getPluginManager().callEvent((Event)new SchedulePulseEvent());
        }
        if (GameAPI.getInstance().isTimer()) {
            GameAPI.getInstance().setTime(GameAPI.getInstance().getTime() + (GameAPI.getInstance().getState().isDecrementTime() ? -1 : 1));
        }
    }
}

