/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.server;

import br.com.dragonmc.core.common.utils.string.StringFormat;

public enum ServerType {
    BUILD,
    LOGIN,
    LOBBY,
    BW_LOBBY,
    SW_LOBBY,
    HG_LOBBY,
    PVP_LOBBY,
    DUELS,
    FPS,
    LAVA,
    ARENA,
    HG,
    MINIHIGH,
    EVENTO,
    RANKUP,
    SW_SOLO,
    SW_DUOS,
    SW_TRIO,
    SW_SQUAD,
    BW_SOLO,
    BW_DUOS,
    BW_TRIO,
    BW_SQUAD,
    BW_1X1,
    BW_2X2,
    BW_3X3,
    BW_4X4,
    BUNGEECORD,
    DISCORD;


    public int getPlayersPerTeam() {
        if (this.name().contains("SOLO") || this.name().contains("1X1")) {
            return 1;
        }
        if (this.name().contains("DUO") || this.name().contains("2X2")) {
            return 2;
        }
        if (this.name().contains("TRIO") || this.name().contains("3X3")) {
            return 3;
        }
        return 4;
    }

    public ServerType getServerLobby() {
        if (this.name().contains("LOBBY")) {
            return LOBBY;
        }
        if (this.name().contains("BW")) {
            return BW_LOBBY;
        }
        if (this.name().contains("SW")) {
            return SW_LOBBY;
        }
        switch (this) {
            case HG: {
                return HG_LOBBY;
            }
            case ARENA: 
            case FPS: 
            case LAVA: {
                return PVP_LOBBY;
            }
        }
        return LOBBY;
    }

    public boolean isPvP() {
        switch (this) {
            case ARENA: 
            case FPS: 
            case LAVA: 
            case PVP_LOBBY: {
                return true;
            }
        }
        return false;
    }

    public boolean isHG() {
        switch (this) {
            case HG: 
            case EVENTO: 
            case MINIHIGH: {
                return true;
            }
        }
        return false;
    }

    public boolean isLobby() {
        return this.name().contains("LOBBY") || this == LOGIN || this == BUILD;
    }

    public String getName() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String name : this.name().split("_")) {
            stringBuilder.append(StringFormat.formatString(name.replace("BW", "Bedwars").replace("SW", "Skywars"))).append(" ");
        }
        return stringBuilder.toString().trim();
    }

    public static ServerType getTypeByName(String string) {
        try {
            return ServerType.valueOf(string.toUpperCase());
        }
        catch (Exception ex) {
            return null;
        }
    }
}

