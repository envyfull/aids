/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.WorldCreator
 *  org.bukkit.event.Event
 */
package br.com.dragonmc.core.bukkit.manager;

import java.util.HashMap;
import java.util.Map;

import br.com.dragonmc.core.bukkit.event.server.LocationChangeEvent;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.Event;

public class LocationManager {
    private Map<String, Location> locationMap = new HashMap<String, Location>();

    public boolean hasLocation(String locationName) {
        return this.locationMap.containsKey(locationName.toLowerCase());
    }

    public void loadLocation(String locationName, Location location) {
        this.locationMap.put(locationName.toLowerCase(), location);
    }

    public void saveLocation(String locationName, Location location) {
        BukkitCommon.getInstance().getConfig().set("location." + locationName + ".world", (Object)location.getWorld().getName());
        BukkitCommon.getInstance().getConfig().set("location." + locationName + ".x", (Object)location.getX());
        BukkitCommon.getInstance().getConfig().set("location." + locationName + ".y", (Object)location.getY());
        BukkitCommon.getInstance().getConfig().set("location." + locationName + ".z", (Object)location.getZ());
        BukkitCommon.getInstance().getConfig().set("location." + locationName + ".pitch", (Object)Float.valueOf(location.getPitch()));
        BukkitCommon.getInstance().getConfig().set("location." + locationName + ".yaw", (Object)Float.valueOf(location.getYaw()));
        BukkitCommon.getInstance().saveConfig();
        Bukkit.getPluginManager().callEvent((Event)new LocationChangeEvent(locationName, location, null));
    }

    public void saveAndLoadLocation(String locationName, Location location) {
        this.locationMap.put(locationName.toLowerCase(), location);
        this.saveLocation(locationName, location);
    }

    public Location getLocation(String locationName) {
        return this.locationMap.computeIfAbsent(locationName.toLowerCase(), v -> this.getLocationFromConfig(locationName));
    }

    public Location getLocationFromConfig(String locationName) {
        if (!BukkitCommon.getInstance().getConfig().contains("location." + locationName)) {
            return new Location((World)Bukkit.getWorlds().stream().findFirst().orElse(null), 0.0, 120.0, 0.0);
        }
        World world = Bukkit.getWorld((String)BukkitCommon.getInstance().getConfig().getString("location." + locationName + ".world").toLowerCase());
        if (world == null) {
            world = Bukkit.createWorld((WorldCreator)new WorldCreator(BukkitCommon.getInstance().getConfig().getString("location." + locationName + ".world").toLowerCase()));
        }
        Location location = new Location(world, BukkitCommon.getInstance().getConfig().getDouble("location." + locationName + ".x"), BukkitCommon.getInstance().getConfig().getDouble("location." + locationName + ".y"), BukkitCommon.getInstance().getConfig().getDouble("location." + locationName + ".z"), (float)BukkitCommon.getInstance().getConfig().getDouble("location." + locationName + ".yaw"), (float)BukkitCommon.getInstance().getConfig().getDouble("location." + locationName + ".pitch"));
        return location;
    }

    public void removeLocationInConfig(String locationName) {
        this.locationMap.remove(locationName.toLowerCase());
        BukkitCommon.getInstance().getConfig().set("location." + locationName, null);
        BukkitCommon.getInstance().saveConfig();
    }

    public String[] getLocations() {
        return (String[])this.locationMap.keySet().stream().toArray(String[]::new);
    }

    public Map<String, Location> getLocationMap() {
        return this.locationMap;
    }
}

