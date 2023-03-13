/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 */
package br.com.dragonmc.lobby.core.listener;

import br.com.dragonmc.core.bukkit.event.server.ServerEvent;
import br.com.dragonmc.lobby.core.CoreMain;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ServerListener
implements Listener {
    @EventHandler
    public void onServer(ServerEvent event) {
        CoreMain.getInstance().getServerWatcherManager().pulse(event.getProxiedServer(), event.getData());
    }
}

