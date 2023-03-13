/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package br.com.dragonmc.lobby.core.manager;

import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.Set;
import br.com.dragonmc.core.common.backend.data.DataServerMessage;
import br.com.dragonmc.lobby.core.server.ServerWatcher;
import br.com.dragonmc.core.common.server.loadbalancer.server.ProxiedServer;

public class ServerWatcherManager {
    private Set<ServerWatcher> serverWatcherSet = new HashSet<ServerWatcher>();

    public void watch(ServerWatcher serverWatcher) {
        this.serverWatcherSet.add(serverWatcher);
    }

    public void pulse(ProxiedServer server, DataServerMessage<?> data) {
        ImmutableSet.copyOf(this.serverWatcherSet).forEach(serverWatcher -> serverWatcher.pulse(server, data));
    }
}

