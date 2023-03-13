/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.event.player;

import br.com.dragonmc.core.bukkit.event.PlayerCancellableEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class PlayerAdminEvent
extends PlayerCancellableEvent {
    private AdminMode adminMode;
    private GameMode gameMode;

    public PlayerAdminEvent(Player player, AdminMode adminMode, GameMode mode) {
        super(player);
        this.adminMode = adminMode;
        this.gameMode = mode;
    }

    public AdminMode getAdminMode() {
        return this.adminMode;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public static enum AdminMode {
        ADMIN,
        PLAYER;

    }
}

