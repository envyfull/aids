/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.plugin.Event
 */
package br.com.dragonmc.core.bungee.event;

import br.com.dragonmc.core.common.server.loadbalancer.server.ProxiedServer;
import net.md_5.bungee.api.plugin.Event;

public class ServerEvent
extends Event {
    private ProxiedServer proxiedServer;

    public ProxiedServer getProxiedServer() {
        return this.proxiedServer;
    }

    public ServerEvent(ProxiedServer proxiedServer) {
        this.proxiedServer = proxiedServer;
    }
}

