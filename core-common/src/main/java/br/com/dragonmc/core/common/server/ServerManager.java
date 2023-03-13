/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import br.com.dragonmc.core.common.server.loadbalancer.BaseBalancer;
import br.com.dragonmc.core.common.server.loadbalancer.type.LeastConnection;
import br.com.dragonmc.core.common.server.loadbalancer.type.MostConnection;
import br.com.dragonmc.core.common.server.loadbalancer.server.BedwarsServer;
import br.com.dragonmc.core.common.server.loadbalancer.server.HungerGamesServer;
import br.com.dragonmc.core.common.server.loadbalancer.server.MinigameServer;
import br.com.dragonmc.core.common.server.loadbalancer.server.MinigameState;
import br.com.dragonmc.core.common.server.loadbalancer.server.ProxiedServer;
import br.com.dragonmc.core.common.server.loadbalancer.server.SkywarsServer;

public class ServerManager {
    private Map<String, ProxiedServer> activeServers;
    private Map<ServerType, BaseBalancer<ProxiedServer>> balancers = new HashMap<ServerType, BaseBalancer<ProxiedServer>>();
    private int totalMembers;

    public ServerManager() {
        this.activeServers = new HashMap<String, ProxiedServer>();
        for (ServerType serverType : ServerType.values()) {
            if (serverType == ServerType.BUNGEECORD) continue;
            this.balancers.put(serverType, serverType.name().contains("LOBBY") ? new LeastConnection() : new MostConnection());
        }
    }

    public BaseBalancer<ProxiedServer> getBalancer(ServerType type) {
        return this.balancers.get((Object)type);
    }

    public void putBalancer(ServerType type, BaseBalancer<ProxiedServer> balancer) {
        this.balancers.put(type, balancer);
    }

    public ProxiedServer addActiveServer(String serverAddress, String serverIp, ServerType type, int maxPlayers, long startTime) {
        return this.updateActiveServer(serverIp, type, new HashSet<UUID>(), maxPlayers, true, startTime);
    }

    public ProxiedServer updateActiveServer(String serverId, ServerType type, Set<UUID> onlinePlayers, int maxPlayers, boolean canJoin, long startTime) {
        return this.updateActiveServer(serverId, type, onlinePlayers, maxPlayers, canJoin, 0, "Unknown", null, startTime);
    }

    public ProxiedServer updateActiveServer(String serverId, ServerType type, Set<UUID> onlinePlayers, int maxPlayers, boolean canJoin, int tempo, String map, MinigameState state, long startTime) {
        ProxiedServer server = this.activeServers.get(serverId);
        if (server == null) {
            server = type.isLobby() ? new ProxiedServer(serverId, type, onlinePlayers, maxPlayers, true) : (type.isHG() ? new HungerGamesServer(serverId, type, onlinePlayers, maxPlayers, true) : (type.name().startsWith("SW") ? new SkywarsServer(serverId, type, onlinePlayers, maxPlayers, true) : (type.name().startsWith("BW") ? new BedwarsServer(serverId, type, onlinePlayers, maxPlayers, true) : new ProxiedServer(serverId, type, onlinePlayers, maxPlayers, true))));
            this.activeServers.put(serverId.toLowerCase(), server);
        }
        server.setOnlinePlayers(onlinePlayers);
        server.setJoinEnabled(canJoin);
        server.setStartTime(startTime);
        if (state != null && server instanceof MinigameServer) {
            ((MinigameServer)server).setState(state);
            ((MinigameServer)server).setTime(tempo);
            ((MinigameServer)server).setMap(map);
        }
        this.addToBalancers(serverId, server);
        return server;
    }

    public ProxiedServer getServer(String serverName) {
        return this.activeServers.get(serverName.toLowerCase());
    }

    public ProxiedServer getServerByName(String serverName) {
        for (ProxiedServer proxiedServer : this.activeServers.values()) {
            if (!proxiedServer.getServerId().toLowerCase().startsWith(serverName.toLowerCase())) continue;
            return proxiedServer;
        }
        return this.activeServers.get(serverName.toLowerCase());
    }

    public Collection<ProxiedServer> getServers() {
        return this.activeServers.values();
    }

    public void removeActiveServer(String str) {
        if (this.getServer(str) != null) {
            this.removeFromBalancers(this.getServer(str));
        }
        this.activeServers.remove(str.toLowerCase());
    }

    public void addToBalancers(String serverId, ProxiedServer server) {
        BaseBalancer<ProxiedServer> balancer = this.getBalancer(server.getServerType());
        if (balancer == null) {
            return;
        }
        balancer.add(serverId.toLowerCase(), server);
    }

    public void removeFromBalancers(ProxiedServer serverId) {
        BaseBalancer<ProxiedServer> balancer = this.getBalancer(serverId.getServerType());
        if (balancer != null) {
            balancer.remove(serverId.getServerId().toLowerCase());
        }
    }

    public void setTotalMembers(int totalMembers) {
        this.totalMembers = totalMembers;
    }

    public int getTotalNumber() {
        return this.totalMembers;
    }

    public int getTotalNumber(ServerType ... serverTypes) {
        int number = 0;
        for (ServerType serverType : serverTypes) {
            number += this.getBalancer(serverType).getTotalNumber();
        }
        return number;
    }

    public int getTotalNumber(List<ServerType> types) {
        int players = 0;
        for (ServerType type : types) {
            players += this.getBalancer(type).getTotalNumber();
        }
        return players;
    }

    public Map<String, ProxiedServer> getActiveServers() {
        return this.activeServers;
    }

    public Map<ServerType, BaseBalancer<ProxiedServer>> getBalancers() {
        return this.balancers;
    }

    public int getTotalMembers() {
        return this.totalMembers;
    }
}

