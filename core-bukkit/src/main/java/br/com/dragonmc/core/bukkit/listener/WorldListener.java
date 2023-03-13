/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.weather.WeatherChangeEvent
 */
package br.com.dragonmc.core.bukkit.listener;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldListener
implements Listener {
    @EventHandler
    public void onWheater(WeatherChangeEvent event) {
        for (World w : Bukkit.getWorlds()) {
            w.setWeatherDuration(0);
        }
        event.setCancelled(true);
    }
}

