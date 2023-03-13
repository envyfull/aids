/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.lobby.core.server;

import java.util.HashSet;
import java.util.Set;
import br.com.dragonmc.core.common.backend.data.DataServerMessage;
import br.com.dragonmc.core.common.server.ServerType;
import br.com.dragonmc.core.common.server.loadbalancer.server.ProxiedServer;

public abstract class ServerWatcher {
    private Set<String> serverIds = new HashSet<String>();
    private Set<ServerType> serverTypes = new HashSet<ServerType>();

    public ServerWatcher server(ServerType serverType) {
        this.serverTypes.add(serverType);
        return this;
    }

    public ServerWatcher server(String serverId) {
        this.serverIds.add(serverId.toLowerCase());
        return this;
    }

    public void pulse(ProxiedServer server, DataServerMessage<?> data) {
        if (this.serverIds.contains(data.getSource()) || this.serverTypes.contains((Object)data.getServerType())) {
            this.onServerUpdate(server, data);
        }
    }

    public abstract void onServerUpdate(ProxiedServer var1, DataServerMessage<?> var2);

    public Set<String> getServerIds() {
        return this.serverIds;
    }

    public Set<ServerType> getServerTypes() {
        return this.serverTypes;
    }
}

