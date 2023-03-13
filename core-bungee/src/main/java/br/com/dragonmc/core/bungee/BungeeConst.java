/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ProxyServer
 */
package br.com.dragonmc.core.bungee;

import br.com.dragonmc.core.bungee.command.BungeeCommandSender;
import br.com.dragonmc.core.common.command.CommandSender;
import net.md_5.bungee.api.ProxyServer;

public class BungeeConst {
    public static final int MAX_PLAYERS = 3000;
    public static final CommandSender CONSOLE_SENDER = new BungeeCommandSender(ProxyServer.getInstance().getConsole());
    public static final String BROADCAST_PREFIX = "\u00a76\u00a7lDRAGON \u00a78\u00bb ";
}

