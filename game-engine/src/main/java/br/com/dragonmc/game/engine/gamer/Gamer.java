/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.game.engine.gamer;

import java.util.UUID;

import br.com.dragonmc.game.engine.GameAPI;
import org.bukkit.entity.Player;

public abstract class Gamer {
    private String playerName;
    private final UUID uniqueId;
    private transient Player player;
    private boolean online;

    public Gamer(String playerName, UUID uniqueId) {
        this.playerName = playerName;
        this.uniqueId = uniqueId;
    }

    public void loadGamer() {
    }

    public void setPlayer(Player player) {
        this.player = player;
        if (player != null) {
            this.playerName = player.getName();
        }
    }

    public void save(String fieldName) {
        GameAPI.getInstance().getGamerData().saveGamer(this, fieldName);
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean isOnline() {
        return this.online;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

}

