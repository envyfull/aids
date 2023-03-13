/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.Callback
 *  net.md_5.bungee.api.ProxyServer
 *  net.md_5.bungee.api.ServerPing
 *  net.md_5.bungee.api.ServerPing$PlayerInfo
 *  net.md_5.bungee.api.connection.PendingConnection
 *  net.md_5.bungee.api.connection.ProxiedPlayer
 *  net.md_5.bungee.api.event.ProxyPingEvent
 *  net.md_5.bungee.api.event.SearchServerEvent
 *  net.md_5.bungee.api.event.ServerConnectEvent
 *  net.md_5.bungee.api.event.ServerConnectedEvent
 *  net.md_5.bungee.api.event.ServerKickEvent
 *  net.md_5.bungee.api.plugin.Listener
 *  net.md_5.bungee.api.plugin.Plugin
 *  net.md_5.bungee.event.EventHandler
 */
package br.com.dragonmc.core.bungee.listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import br.com.dragonmc.core.bungee.BungeeMain;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bungee.member.BungeeMember;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.member.party.Party;
import br.com.dragonmc.core.common.member.party.PartyRole;
import br.com.dragonmc.core.common.server.ServerType;
import br.com.dragonmc.core.common.server.loadbalancer.server.ProxiedServer;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class ServerListener
implements Listener {
    private static final String MOTD_HEADER = StringFormat.centerString("\u00a7d\u00a7l\u00a7oHIGH\u00a7f\u00a7l\u00a7oMC \u00a7b\u00bb \u00a7a[1.7 - 1.19]", 127);
    private static final String MOTD_FOOTER = StringFormat.centerString("\u00a76\u00a7lNOVIDADES: \u00a7evenha conferir!", 127);
    private static final String MAINTENANCE_FOOTER = StringFormat.centerString("\u00a7cO servidor est\u00e1 em manuten\u00e7\u00e3o.", 127);
    private static final String SERVER_NOT_FOUND = StringFormat.centerString("\u00a7cServidor inserido n\u00e3o existe.");
    private Set<UUID> playerUpdateSet = new HashSet<UUID>();

  /*  @EventHandler
    public void onSearchServer(SearchServerEvent event) {
        BungeeMember member = CommonPlugin.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId(), BungeeMember.class);
        member.setProxiedPlayer(event.getPlayer());
        member.loadConfiguration();
        boolean logged = member.getLoginConfiguration().isLogged();
        boolean refreshLogin = logged ? false : member.getLoginConfiguration().reloadSession();
        logged = logged || refreshLogin;
        ProxiedServer server = BungeeMain.getInstance().getServerManager().getServer(this.getServerIp(event.getPlayer().getPendingConnection()));
        if (logged && server != null && server.getServerInfo() != null) {
            event.setServer(server.getServerInfo());
            return;
        }
        if (logged && System.currentTimeMillis() - member.getLastLogin() <= 15000L && member.getActualServerId() != null && !member.getActualServerType().name().contains("LOBBY") && (server = BungeeMain.getInstance().getServerManager().getServer(member.getActualServerId())) != null && server.getServerInfo() != null) {
            event.setServer(server.getServerInfo());
            return;
        }
        server = BungeeMain.getInstance().getServerManager().getBalancer(logged ? ServerType.LOBBY : ServerType.LOGIN).getList().stream().findFirst().orElse(null);
        if (server == null || server.getServerInfo() == null) {
            event.setCancelled(true);
            event.setCancelMessage(Language.getLanguage(event.getPlayer().getUniqueId()).t(logged ? "server-fallback-not-found" : "login.kick.login-not-available", new String[0]));
            return;
        }
        if (refreshLogin) {
            this.playerUpdateSet.add(member.getUniqueId());
        }
        event.setServer(server.getServerInfo());
    } */

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        BungeeMember member = CommonPlugin.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId(), BungeeMember.class);
        if (member == null) {
            event.setCancelled(true);
            event.getPlayer().disconnect("\u00a7cHouve um problema com a sua conta!");
            return;
        }
        boolean logged = member.getLoginConfiguration().isLogged();
        if (!logged && BungeeMain.getInstance().getServerManager().getServer(event.getTarget().getName()).getServerType() != ServerType.LOGIN) {
            boolean disconnect = event.getPlayer().getServer() == null || event.getPlayer().getServer().getInfo() == null;
            String message = member.getLanguage().t(disconnect ? "login.kick.not-logged" : "login.message.not-logged", "%website%", CommonPlugin.getInstance().getPluginInfo().getWebsite());
            if (disconnect) {
                event.getPlayer().disconnect(message);
            } else {
                event.setCancelled(true);
                event.getPlayer().sendMessage(message);
            }
        }
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        ProxiedServer server;
        BungeeMember member = CommonPlugin.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId(), BungeeMember.class);
        if (member == null) {
            return;
        }
        if (this.playerUpdateSet.contains(member.getUniqueId())) {
            ProxyServer.getInstance().getScheduler().schedule((Plugin)BungeeMain.getInstance(), () -> member.saveConfig(), 3L, TimeUnit.SECONDS);
        }
        if ((server = BungeeMain.getInstance().getServerManager().getServer(event.getServer().getInfo().getName())).getServerType().isLobby()) {
            return;
        }
        Party party = member.getParty();
        if (party != null && party.hasRole(member.getUniqueId(), PartyRole.OWNER)) {
            if (server.getOnlinePlayers() + party.getMembers().size() > server.getMaxPlayers()) {
                member.sendMessage("\u00a7eO servidor n\u00e3o suporta todos os membros da sua party, talvez alguns dos jogadores n\u00e3o sejam teletransportados.");
            }
            party.getMembers().stream().map(id -> CommonPlugin.getInstance().getMemberManager().getMember((UUID)id, BungeeMember.class)).forEach(partyMember -> {
                if (partyMember != null && !partyMember.getActualServerId().equals(event.getServer().getInfo().getName())) {
                    partyMember.getProxiedPlayer().connect(event.getServer().getInfo());
                }
            });
        }
        member.handleCheckGroup();
    }

    @EventHandler
    public void onServerKick(ServerKickEvent event) {
        ProxiedServer server;
        if (event.getKickReason().toLowerCase().contains("kick") || event.getKickReason().toLowerCase().contains("expulso")) {
            return;
        }
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());
        if (member != null && !member.getLoginConfiguration().isLogged()) {
            return;
        }
        ProxiedPlayer player = event.getPlayer();
        ProxiedServer kickedFrom = BungeeMain.getInstance().getServerManager().getServer(event.getKickedFrom().getName());
        ProxiedServer proxiedServer = server = kickedFrom == null ? (ProxiedServer)BungeeMain.getInstance().getServerManager().getBalancer(ServerType.LOBBY).next() : (ProxiedServer)this.getFirstIfNull(BungeeMain.getInstance().getServerManager().getBalancer(kickedFrom.getServerType().getServerLobby()).next(), BungeeMain.getInstance().getServerManager().getBalancer(ServerType.LOBBY).next());
        if (server == null || server.getServerInfo() == null || server == kickedFrom) {
            player.disconnect(event.getKickReasonComponent());
            return;
        }
        event.setCancelled(true);
        event.setCancelServer(server.getServerInfo());
        player.sendMessage(event.getKickReasonComponent());
    }

    @EventHandler(priority=127)
    public void onProxyPing(final ProxyPingEvent event) {
        final ServerPing serverPing = event.getResponse();
        String serverIp = this.getServerIp(event.getConnection());
        ProxiedServer server = BungeeMain.getInstance().getServerManager().getServer(serverIp);
        serverPing.getPlayers().setMax(ProxyServer.getInstance().getOnlineCount() + 1);
        serverPing.getPlayers().setOnline(ProxyServer.getInstance().getOnlineCount());
        if (server == null) {
            serverPing.getPlayers().setSample(new ServerPing.PlayerInfo[]{new ServerPing.PlayerInfo("\u00a7e" + CommonPlugin.getInstance().getPluginInfo().getWebsite(), UUID.randomUUID())});
            serverPing.setDescription(MOTD_HEADER + "\n" + (BungeeMain.getInstance().isMaintenance() ? MAINTENANCE_FOOTER : MOTD_FOOTER));
        } else {
            event.registerIntent((Plugin)BungeeMain.getInstance());
            server.getServerInfo().ping((Callback)new Callback<ServerPing>(){

                public void done(ServerPing realPing, Throwable throwable) {
                    if (throwable == null) {
                        serverPing.getPlayers().setMax(realPing.getPlayers().getMax());
                        serverPing.getPlayers().setOnline(realPing.getPlayers().getOnline());
                        serverPing.setDescription(realPing.getDescription());
                    } else {
                        serverPing.getPlayers().setSample(new ServerPing.PlayerInfo[]{new ServerPing.PlayerInfo("\u00a7e" + CommonPlugin.getInstance().getPluginInfo().getWebsite(), UUID.randomUUID())});
                        serverPing.setDescription(MOTD_HEADER + "\n" + SERVER_NOT_FOUND);
                    }
                    event.completeIntent((Plugin)BungeeMain.getInstance());
                }
            });
        }
    }

    private String getServerIp(PendingConnection con) {
        if (con == null || con.getVirtualHost() == null) {
            return "";
        }
        return con.getVirtualHost().getHostName().toLowerCase();
    }

    public <T> T getFirstIfNull(T first, T second) {
        return second == null ? first : second;
    }
}

