/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ProxyServer
 *  net.md_5.bungee.api.plugin.Event
 *  redis.clients.jedis.JedisPubSub
 */
package br.com.dragonmc.core.bungee.networking;

import br.com.dragonmc.core.bungee.event.RedisMessageEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Event;
import redis.clients.jedis.JedisPubSub;

public class BungeePubSubHandler
extends JedisPubSub {
    public void onMessage(String channel, String message) {
        ProxyServer.getInstance().getPluginManager().callEvent((Event)new RedisMessageEvent(channel, message));
    }
}

