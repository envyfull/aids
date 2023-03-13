/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit;

import br.com.dragonmc.core.bukkit.utils.character.handler.ActionHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class BukkitMain
extends BukkitCommon {
    @Override
    public void onEnable() {
        super.onEnable();
        this.createCharacter(new Location((World)Bukkit.getWorlds().stream().findFirst().orElse(null), 0.0, 120.0, 0.0), "Kotcka", new ActionHandler(){

            @Override
            public boolean onInteract(Player player, boolean right) {
                player.sendMessage("viado");
                return false;
            }
        });
    }
}

