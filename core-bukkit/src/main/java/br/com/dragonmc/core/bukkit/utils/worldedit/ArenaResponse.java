/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.block.BlockState
 */
package br.com.dragonmc.core.bukkit.utils.worldedit;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.block.BlockState;

public class ArenaResponse {
    private Map<Location, BlockState> map;
    private int blocks;

    public ArenaResponse(int blocks) {
        this.blocks = -1;
        this.map = new HashMap<Location, BlockState>();
    }

    public ArenaResponse() {
        this.map = new HashMap<Location, BlockState>();
    }

    public void addMap(Location location, BlockState blockState) {
        this.map.put(location, blockState);
        ++this.blocks;
    }

    public void addBlock() {
        ++this.blocks;
    }

    public Map<Location, BlockState> getMap() {
        return this.map;
    }

    public int getBlocks() {
        return this.blocks;
    }
}

