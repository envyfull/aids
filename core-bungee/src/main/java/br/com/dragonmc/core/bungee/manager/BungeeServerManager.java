/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ProxyServer
 *  net.md_5.bungee.api.config.ServerInfo
 */
package br.com.dragonmc.core.bungee.manager;

import java.net.InetSocketAddress;
import br.com.dragonmc.core.common.server.ServerManager;
import br.com.dragonmc.core.common.server.ServerType;
import br.com.dragonmc.core.common.server.loadbalancer.server.ProxiedServer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class BungeeServerManager
extends ServerManager {
    @Override
    public ProxiedServer addActiveServer(String serverAddress, String serverIp, ServerType type, int maxPlayers, long startTime) {
        ProxiedServer server = super.addActiveServer(serverAddress, serverIp, type, maxPlayers, startTime);
        if (!ProxyServer.getInstance().getServers().containsKey(serverIp.toLowerCase())) {
            String ipAddress = serverAddress.split(":")[0];
            int port = Integer.valueOf(serverAddress.split(":")[1]);
            ServerInfo localServerInfo = ProxyServer.getInstance().constructServerInfo(serverIp.toLowerCase(), new InetSocketAddress(ipAddress, port), "Restarting", false);
            ProxyServer.getInstance().getServers().put(serverIp.toLowerCase(), localServerInfo);
        }
        return server;
    }

    @Override
    public void removeActiveServer(String str) {
        super.removeActiveServer(str);
        if (ProxyServer.getInstance().getServers().containsKey(str.toLowerCase())) {
            ProxyServer.getInstance().getServers().remove(str.toLowerCase());
        }
    }
}

