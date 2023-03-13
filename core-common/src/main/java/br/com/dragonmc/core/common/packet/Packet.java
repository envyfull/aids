/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.packet;

import java.util.ArrayList;
import java.util.List;

import br.com.dragonmc.core.common.server.ServerType;
import br.com.dragonmc.core.common.CommonPlugin;

public abstract class Packet {
    private final String source;
    private final ServerType serverType;
    private final PacketType packetType;
    private boolean exclusiveServers;
    private List<String> serverList = new ArrayList<String>();

    public Packet(PacketType packetType) {
        this.source = CommonPlugin.getInstance().getServerId();
        this.serverType = CommonPlugin.getInstance().getServerType();
        this.packetType = packetType;
    }

    public Packet server(String ... servers) {
        this.exclusiveServers = true;
        for (String server : servers) {
            this.serverList.add(server);
        }
        return this;
    }

    public Packet bungeecord() {
        this.server("bungeecord.dragonmc.com.br");
        return this;
    }

    public Packet discord() {
        this.server("discord.dragonmc.com.br");
        return this;
    }

    public void receive() {
    }

    public void send() {
    }

    public String getSource() {
        return this.source;
    }

    public ServerType getServerType() {
        return this.serverType;
    }

    public PacketType getPacketType() {
        return this.packetType;
    }

    public boolean isExclusiveServers() {
        return this.exclusiveServers;
    }

    public List<String> getServerList() {
        return this.serverList;
    }

    public Packet(String source, ServerType serverType, PacketType packetType, boolean exclusiveServers, List<String> serverList) {
        this.source = source;
        this.serverType = serverType;
        this.packetType = packetType;
        this.exclusiveServers = exclusiveServers;
        this.serverList = serverList;
    }
}

