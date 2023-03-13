/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.utils.hologram;

import java.util.Collection;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface Hologram {
    public void spawn();

    public void remove();

    public boolean isSpawned();

    public Hologram setDisplayName(String var1);

    public boolean hasDisplayName();

    public boolean isCustomNameVisible();

    public String getDisplayName();

    public Hologram line(String var1);

    public Hologram line(Hologram var1);

    public List<Hologram> getLines();

    public void teleport(Location var1);

    default public void teleport(World world, int x, int y, int z) {
        this.teleport(new Location(world, (double)x, (double)y, (double)z));
    }

    default public void teleport(int x, int y, int z) {
        this.teleport(new Location(this.getLocation().getWorld(), (double)x, (double)y, (double)z));
    }

    public Location getLocation();

    public void show(Player var1);

    public void hide(Player var1);

    public Collection<? extends Player> getViewers();

    public boolean isVisibleTo(Player var1);

    public void setTouchHandler(TouchHandler var1);

    public boolean hasTouchHandler();

    public TouchHandler getTouchHandler();

    public void setViewHandler(ViewHandler var1);

    public boolean hasViewHandler();

    public ViewHandler getViewHandler();

    public boolean compareEntity(Entity var1);

    public boolean isEntityOrLine(int var1);
}

