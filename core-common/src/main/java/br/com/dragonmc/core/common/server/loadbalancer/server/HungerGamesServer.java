/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.server.loadbalancer.server;

import java.util.Set;
import java.util.UUID;
import br.com.dragonmc.core.common.server.ServerType;

public class HungerGamesServer
extends MinigameServer {
    public HungerGamesServer(String serverId, ServerType type, Set<UUID> players, int maxPlayers, boolean joinEnabled) {
        super(serverId, type, players, maxPlayers, joinEnabled);
        this.setState(MinigameState.WAITING);
    }

    @Override
    public boolean canBeSelected() {
        return super.canBeSelected() && !this.isInProgress() && (this.getState() == MinigameState.PREGAME && this.getTime() >= 15 || this.getState() == MinigameState.WAITING);
    }

    @Override
    public boolean isInProgress() {
        return this.getState() == MinigameState.GAMETIME || this.getState() == MinigameState.INVINCIBILITY;
    }
}

