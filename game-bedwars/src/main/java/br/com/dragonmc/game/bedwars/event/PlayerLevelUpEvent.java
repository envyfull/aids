/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.game.bedwars.event;

import br.com.dragonmc.game.bedwars.gamer.Gamer;
import lombok.NonNull;
import br.com.dragonmc.core.bukkit.event.PlayerEvent;
import org.bukkit.entity.Player;

public class PlayerLevelUpEvent
extends PlayerEvent {
    private Gamer gamer;
    private int level;

    public PlayerLevelUpEvent(@NonNull Player player, Gamer gamer, int level) {
        super(player);
        if (player == null) {
            throw new NullPointerException("player is marked non-null but is null");
        }
        this.gamer = gamer;
        this.level = level;
    }

    public Gamer getGamer() {
        return this.gamer;
    }

    public int getLevel() {
        return this.level;
    }
}

