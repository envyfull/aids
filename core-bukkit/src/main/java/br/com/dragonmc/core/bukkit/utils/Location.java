/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 */
package br.com.dragonmc.core.bukkit.utils;

import java.util.OptionalInt;
import br.com.dragonmc.core.common.CommonConst;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class Location {
    private String worldName;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public Location() {
        this(((World)Bukkit.getWorlds().stream().findFirst().orElse(null)).getName(), 0.0, 0.0, 0.0, 0.0f, 0.0f);
    }

    public Location(String worldName) {
        this(worldName, 0.0, 0.0, 0.0, 0.0f, 0.0f);
    }

    public Location(String worldName, double x, double y, double z) {
        this(worldName, x, y, z, 0.0f, 0.0f);
    }

    public World getWorld() {
        return Bukkit.getWorld((String)this.worldName);
    }

    public void set(org.bukkit.Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public org.bukkit.Location getAsLocation() {
        return new org.bukkit.Location(this.getWorld(), this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public static Location fromLocation(org.bukkit.Location location) {
        return new Location(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public String toString() {
        return CommonConst.GSON.toJson((Object)this);
    }

    public static Location valueOf(String value) {
        if (value.startsWith("{") && value.endsWith("}")) {
            return (Location)CommonConst.GSON.fromJson(value, Location.class);
        }
        if (value.contains(",")) {
            boolean space = value.contains(", ");
            String[] split = value.split(space ? ", " : ",");
            String worldName = split[0];
            OptionalInt optionalX = OptionalInt.of(Integer.valueOf(split[1]));
            OptionalInt optionalY = OptionalInt.of(Integer.valueOf(split[2]));
            OptionalInt optionalZ = OptionalInt.of(Integer.valueOf(split[3]));
            return new Location(worldName, optionalX.getAsInt(), optionalY.getAsInt(), optionalZ.getAsInt(), 0.0f, 0.0f);
        }
        return null;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public Location(String worldName, double x, double y, double z, float yaw, float pitch) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }
}

