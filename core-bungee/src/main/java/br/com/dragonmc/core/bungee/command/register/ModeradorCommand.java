/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  net.md_5.bungee.BungeeCord
 *  net.md_5.bungee.api.CommandSender
 *  net.md_5.bungee.api.ProxyServer
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.api.chat.ClickEvent$Action
 *  net.md_5.bungee.api.chat.HoverEvent$Action
 *  net.md_5.bungee.api.chat.TextComponent
 *  net.md_5.bungee.api.connection.ProxiedPlayer
 */
package br.com.dragonmc.core.bungee.command.register;

import br.com.dragonmc.core.bungee.BungeeMain;
import com.google.common.base.Joiner;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bungee.member.BungeeMember;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.command.CommandClass;
import br.com.dragonmc.core.common.command.CommandFramework;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.server.ServerType;
import br.com.dragonmc.core.common.server.loadbalancer.server.ProxiedServer;
import br.com.dragonmc.core.common.utils.DateUtils;
import br.com.dragonmc.core.common.utils.ProtocolVersion;
import br.com.dragonmc.core.common.utils.string.MessageBuilder;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ModeradorCommand
implements CommandClass {
    @CommandFramework.Command(name="fakelist", aliases={"nicklist"}, permission="command.fakelist")
    public void fakelistCommand(CommandArgs cmdArgs) {
        List<Member> list = CommonPlugin.getInstance().getMemberManager().getMembers().stream().filter(member -> member.isUsingFake()).collect(Collectors.toList());
        if (list.isEmpty()) {
            cmdArgs.getSender().sendMessage("\u00a7cNingu\u00e9m est\u00e1 usando fake.");
        } else {
            list.forEach(member -> cmdArgs.getSender().sendMessage("\u00a7a" + member.getPlayerName() + " est\u00e1 usando o fake " + member.getFakeName() + "."));
        }
    }

    @CommandFramework.Command(name="glist", aliases={"globallist", "serverinfo"}, permission="command.glist")
    public void glistChatCommand(CommandArgs cmdArgs) {
        String[] args = cmdArgs.getArgs();
        br.com.dragonmc.core.common.command.CommandSender sender = cmdArgs.getSender();
        if (args.length == 0) {
            ProxiedServer[] servers = (ProxiedServer[]) BungeeMain.getInstance().getServerManager().getServers().stream().sorted((o1, o2) -> o1.getServerId().compareTo(o2.getServerId())).toArray(ProxiedServer[]::new);
            sender.sendMessage(" \u00a7aServidor global: ");
            this.handleInfo(sender, ProxyServer.getInstance().getPlayers(), (int)((System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime()) / 1000L), BungeeMain.getInstance().getPlayersRecord(), ServerType.BUNGEECORD);
            MessageBuilder messageBuilder = new MessageBuilder("    \u00a7fServidores: \u00a77");
            for (int i = 0; i < Math.max(servers.length, 24); ++i) {
                ProxiedServer server = servers[i];
                messageBuilder.extra(new MessageBuilder("\u00a77" + server.getServerId() + (i == servers.length - 1 ? "." : ", ")).setHoverEvent("\u00a7a" + server.getServerId() + "\n\n  \u00a7fPlayers: \u00a77" + server.getOnlinePlayers() + " jogadores\n  \u00a7fM\u00e1ximo de players: \u00a77" + server.getMaxPlayers() + " jogadores\n  \u00a7fRecord de players: \u00a77" + server.getPlayersRecord() + " jogadores\n  \u00a7fTipo de servidor: \u00a77" + server.getServerType().getName() + "\n  \u00a7fLigado h\u00e1: \u00a77" + StringFormat.formatTime((int)((System.currentTimeMillis() - server.getStartTime()) / 1000L), StringFormat.TimeFormat.NORMAL) + "\n\n\u00a7eClique para saber mais.").setClickEvent("/glist " + server.getServerId()).create());
            }
            sender.sendMessage((BaseComponent)messageBuilder.create());
            return;
        }
        ProxiedServer proxiedServer = BungeeMain.getInstance().getServerManager().getServerByName(args[0]);
        if (proxiedServer == null || proxiedServer.getServerInfo() == null) {
            sender.sendMessage(sender.getLanguage().t("server-not-found", "%server%", args[0]));
            return;
        }
        sender.sendMessage((BaseComponent)new MessageBuilder(" \u00a7aServidor " + proxiedServer.getServerId() + ":").setHoverEvent("\u00a7eClique para se conectar.").setClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/connect " + proxiedServer.getServerId()).create());
        MessageBuilder messageBuilder = new MessageBuilder("    \u00a7f" + proxiedServer.getServerInfo().getPlayers().size() + " players: \u00a77");
        int max = proxiedServer.getServerInfo().getPlayers().size() * 2;
        int i = max - 1;
        for (ProxiedPlayer player : proxiedServer.getServerInfo().getPlayers()) {
            if (i < max - 1) {
                messageBuilder.extra(new TextComponent("\u00a7f, "));
                --i;
            }
            messageBuilder.extra(new MessageBuilder("\u00a77" + player.getName()).setClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + player.getName()).setHoverEvent(HoverEvent.Action.SHOW_TEXT, "\u00a7eClique para teletransportar").create());
            --i;
        }
        this.handleInfo(sender, proxiedServer.getServerInfo().getPlayers(), (int)((System.currentTimeMillis() - proxiedServer.getStartTime()) / 1000L), proxiedServer.getPlayersRecord(), proxiedServer.getServerType());
    }

    @CommandFramework.Command(name="messagebroadcast.remove", permission="staff.super")
    public void messagebroadcastremove(CommandArgs cmdArgs) {
        br.com.dragonmc.core.common.command.CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " remove <index> \u00a7fpara remover.");
            return;
        }
        OptionalInt integer = StringFormat.parseInt(args[0]);
        if (integer.isPresent()) {
            if (integer.getAsInt() >= 0 && integer.getAsInt() < BungeeMain.getInstance().getMessages().size()) {
                BungeeMain.getInstance().removeMessage(integer.getAsInt());
                sender.sendMessage("\u00a7aRemovido com sucesso.");
            } else {
                sender.sendMessage("\u00a7cN\u00e3o existe.");
            }
        } else {
            sender.sendMessage(sender.getLanguage().t("invalid-format-integer", "%value%", args[0]));
        }
    }

    @CommandFramework.Command(name="messagebroadcast", permission="staff.super")
    public void messagebroadcast(CommandArgs cmdArgs) {
        br.com.dragonmc.core.common.command.CommandSender sender = cmdArgs.getSender();
        Object[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <message>\u00a7f ");
            for (int i = 1; i <= BungeeMain.getInstance().getMessages().size(); ++i) {
                sender.sendMessage("  \u00a7a" + i + "\u00b0 \u00a7f" + BungeeMain.getInstance().getMessages().get(i - 1));
            }
            return;
        }
        String message = Joiner.on((char)' ').join(args);
        BungeeMain.getInstance().addMessage(message.replace('&', '\u00a7'));
        sender.sendMessage("\u00a7a" + message.replace('&', '\u00a7') + " \u00a7aadicionada com sucesso.");
    }

    @CommandFramework.Command(name="mojang", permission="command.mojang")
    public void mojangCommand(CommandArgs cmdArgs) {
        br.com.dragonmc.core.common.command.CommandSender sender = cmdArgs.getSender();
        int pirateCount = 0;
        int premiumCount = 0;
        int onlineCount = BungeeCord.getInstance().getPlayers().size();
        for (ProxiedPlayer player : BungeeCord.getInstance().getPlayers()) {
            if (player.getPendingConnection().isOnlineMode()) {
                ++premiumCount;
                continue;
            }
            ++pirateCount;
        }
        sender.sendMessage("  \u00a7aEstat\u00edsticas dos jogadores:");
        sender.sendMessage("    \u00a7fOriginais: \u00a77" + premiumCount + " jogadores.");
        sender.sendMessage("    \u00a7fPiratas: \u00a77" + pirateCount + " jogadores.");
        sender.sendMessage("    \u00a7fTotal online: \u00a77" + onlineCount + " jogadores.");
    }

    @CommandFramework.Command(name="staffchat", aliases={"sc"}, permission="command.staff", console=false)
    public void staffChatCommand(CommandArgs cmdArgs) {
        Member member = cmdArgs.getSenderAsMember();
        member.getMemberConfiguration().setStaffChat(!member.getMemberConfiguration().isStaffChat());
        member.sendMessage("\u00a7%command.staffchat." + (member.getMemberConfiguration().isStaffChat() ? "enabled" : "disabled") + "%\u00a7");
    }

    @CommandFramework.Command(name="stafflist", runAsync=true, permission="command.staff", usage="/<command> <player> <server>")
    public void stafflistCommand(CommandArgs cmdArgs) {
        BungeeMember[] array;
        br.com.dragonmc.core.common.command.CommandSender sender = cmdArgs.getSender();
        int groupId = sender.isPlayer() ? cmdArgs.getSenderAsMember().getServerGroup().getId() : Integer.MAX_VALUE;
        int ping = 0;
        long time = 0L;
        HashMap<ProtocolVersion, Integer> map = new HashMap<ProtocolVersion, Integer>();
        for (BungeeMember member2 : array = (BungeeMember[])CommonPlugin.getInstance().getMemberManager().getMembers(BungeeMember.class).stream().sorted((o1, o2) -> (o1.getServerGroup().getId() - o2.getServerGroup().getId()) * -1).filter(member -> member.getServerGroup().isStaff() && groupId >= member.getServerGroup().getId()).toArray(BungeeMember[]::new)) {
            ping += member2.getProxiedPlayer().getPing();
            time += member2.getSessionTime();
            ProtocolVersion version = ProtocolVersion.getById(member2.getProxiedPlayer().getPendingConnection().getVersion());
            map.putIfAbsent(version, 0);
            map.put(version, (Integer)map.get((Object)version) + 1);
        }
        sender.sendMessage("  \u00a7aEquipe online:");
        sender.sendMessage("    \u00a7fTempo m\u00e9dio: \u00a77" + StringFormat.formatTime((int)((time /= (long)Math.max(array.length, 1)) / 1000L), StringFormat.TimeFormat.NORMAL));
        sender.sendMessage("    \u00a7fPing m\u00e9dio: \u00a77" + (ping /= Math.max(array.length, 1)) + "ms");
        sender.sendMessage("    \u00a7fEquipe: \u00a77" + array.length + " online");
        MessageBuilder messageBuilder = new MessageBuilder("    \u00a7fPlayers: \u00a77");
        for (int i = 0; i < array.length; ++i) {
            BungeeMember member3 = array[i];
            messageBuilder.extra(new MessageBuilder("\u00a77" + member3.getDefaultTag().getRealPrefix() + member3.getPlayerName() + (i == array.length - 1 ? "." : ", ")).setHoverEvent("\u00a7fTempo online: \u00a77" + StringFormat.formatTime((int)(member3.getSessionTime() / 1000L), StringFormat.TimeFormat.NORMAL) + "\n\u00a7fPing: \u00a77" + member3.getProxiedPlayer().getPing() + "ms\n\u00a7fServidor: \u00a77" + member3.getActualServerId() + "").setClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teleport " + member3.getProxiedPlayer().getName()).create());
        }
        sender.sendMessage((BaseComponent)messageBuilder.create());
    }

    @CommandFramework.Command(name="broadcast", aliases={"bc"}, permission="command.broadcast")
    public void broadcastCommand(CommandArgs cmdArgs) {
        br.com.dragonmc.core.common.command.CommandSender sender = cmdArgs.getSender();
        Object[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <message>\u00a7f para enviar uma mensagem no servidor.");
            return;
        }
        String message = Joiner.on((char)' ').join(args).replace('&', '\u00a7');
        ProxyServer.getInstance().broadcast("");
        ProxyServer.getInstance().broadcast("\u00a76\u00a7lDRAGON \u00a78\u00bb \u00a7f" + message);
        ProxyServer.getInstance().broadcast("");
        this.staffLog("O " + sender.getName() + " mandou uma mensagem global na proxy.");
    }

    @CommandFramework.Command(name="bungeewhitelist", aliases={"globalwhitelist", "gwhitelist", "bwhitelist"}, permission="command.globalwhitelist")
    public void whitelistCommand(CommandArgs cmdArgs) {
        br.com.dragonmc.core.common.command.CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage("\u00a7%command.bungeecord.whitelist.usage%\u00a7");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "off": 
            case "on": {
                boolean enabled = args[0].equalsIgnoreCase("on");
                long time = -1L;
                if (args.length >= 2) {
                    time = DateUtils.getTime(args[1]);
                }
                BungeeMain.getInstance().setWhitelistEnabled(enabled, time);
                sender.sendMessage(sender.getLanguage().t("command.bungeecord.whitelist." + (enabled ? "enabled" + (time == -1L ? "" : "-temporary") + "-success" : "disabled-success"), "%time%", DateUtils.getTime(sender.getLanguage(), time)));
                break;
            }
            case "add": {
                if (args.length < 2) break;
                String playerName = args[1];
                if (BungeeMain.getInstance().isMemberInWhiteList(playerName)) {
                    sender.sendMessage("j\u00e1 est\u00e1");
                    return;
                }
                BungeeMain.getInstance().addMemberToWhiteList(playerName);
                sender.sendMessage("adicionado");
                break;
            }
            case "remove": {
                if (args.length < 2) break;
                String playerName = args[1];
                if (!BungeeMain.getInstance().isMemberInWhiteList(playerName)) {
                    sender.sendMessage("n\u00e3o est\u00e1");
                    return;
                }
                BungeeMain.getInstance().addMemberToWhiteList(playerName);
                sender.sendMessage("removido");
                break;
            }
            default: {
                sender.sendMessage("\u00a7%command.bungeecord.whitelist.usage%\u00a7");
            }
        }
    }

    @CommandFramework.Command(name="bungee", permission="command.bungee")
    public void bungeeCommand(CommandArgs cmdArgs) {
        br.com.dragonmc.core.common.command.CommandSender sender = cmdArgs.getSender();
        Language language = sender.getLanguage();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage("\u00a7%command-server-usage%\u00a7");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "reload-config": {
                try {
                    CommonPlugin.getInstance().loadConfig();
                    sender.sendMessage(language.t("command-server-reload-config-successfully", new String[0]));
                }
                catch (Exception ex) {
                    sender.sendMessage(language.t("command-server-reload-config-error", new String[0]));
                    sender.sendMessage("\u00a7c" + ex.getLocalizedMessage());
                    ex.printStackTrace();
                }
                break;
            }
            case "save-config": {
                try {
                    CommonPlugin.getInstance().saveConfig();
                    sender.sendMessage(language.t("command-server-save-config-successfully", new String[0]));
                }
                catch (Exception ex) {
                    sender.sendMessage(language.t("command-server-save-config-error", new String[0]));
                    sender.sendMessage("\u00a7c" + ex.getLocalizedMessage());
                    ex.printStackTrace();
                }
                break;
            }
            default: {
                sender.sendMessage("\u00a7%command-server-usage%\u00a7");
            }
        }
    }

    @CommandFramework.Command(name="find", permission="command.find")
    public void findCommand(CommandArgs cmdArgs) {
        br.com.dragonmc.core.common.command.CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <player>\u00a7f para saber aonde um jogador est\u00e1.");
            return;
        }
        String playerName = args[0];
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(sender.getLanguage().t("player-not-found", "%player%", playerName));
            return;
        }
        sender.sendMessage((BaseComponent)new MessageBuilder("\u00a7aO jogador " + target.getName() + " est\u00e1 na sala " + target.getServer().getInfo().getName() + ".").setHoverEvent("\u00a7aClique para se conectar.").setClickEvent("/tp " + target.getName()).create());
    }

    @CommandFramework.Command(name="go", permission="command.go", console=false)
    public void goCommand(CommandArgs cmdArgs) {
        br.com.dragonmc.core.common.command.CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <player>\u00a7f para ir at\u00e9 um jogador.");
            return;
        }
        String playerName = args[0];
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(sender.getLanguage().t("player-not-found", "%player%", playerName));
            return;
        }
        cmdArgs.getSenderAsMember(BungeeMember.class).getProxiedPlayer().connect(target.getServer().getInfo());
    }

    @CommandFramework.Command(name="top", permission="command.top")
    public void topCommand(CommandArgs cmdArgs) {
        br.com.dragonmc.core.common.command.CommandSender sender = cmdArgs.getSender();
        if (cmdArgs.getArgs().length > 0 && cmdArgs.getArgs()[0].equalsIgnoreCase("gc")) {
            Runtime.getRuntime().gc();
            sender.sendMessage("\u00a7aVoc\u00ea passou o Garbage Collector do java no BungeeCord.");
            return;
        }
        long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 2L / 0x100000L;
        long allocatedMemory = Runtime.getRuntime().totalMemory() / 0x100000L;
        sender.sendMessage(" ");
        sender.sendMessage("  \u00a7aBungeeCord Usage Info:");
        sender.sendMessage("    \u00a7fMem\u00f3ria usada: \u00a77" + usedMemory + "MB (" + usedMemory * 100L / allocatedMemory + "%)");
        sender.sendMessage("    \u00a7fMem\u00f3ria livre: \u00a77" + (allocatedMemory - usedMemory) + "MB (" + (allocatedMemory - usedMemory) * 100L / allocatedMemory + "%)");
        sender.sendMessage("    \u00a7fMem\u00f3ria m\u00e1xima: \u00a77" + allocatedMemory + "MB");
        sender.sendMessage("    \u00a7fCPU: \u00a77" + CommonConst.DECIMAL_FORMAT.format(CommonConst.getCpuUse()) + "%");
        sender.sendMessage("    \u00a7fPing m\u00e9dio: \u00a77" + BungeeMain.getInstance().getAveragePing(ProxyServer.getInstance().getPlayers()) + "ms.");
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @CommandFramework.Command(name="send", permission="command.send")
    public void sendCommand(CommandArgs cmdArgs) {
        br.com.dragonmc.core.common.command.CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length <= 1) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <player:current:all> <serverId:serverType>\u00a7f para enviar um jogador para uma sala.");
            return;
        }
        ArrayList<ProxiedPlayer> playerList = new ArrayList<ProxiedPlayer>();
        if (args[0].equalsIgnoreCase("all")) {
            playerList.addAll(ProxyServer.getInstance().getPlayers());
        } else if (args[0].equalsIgnoreCase("current")) {
            if (!cmdArgs.isPlayer()) {
                sender.sendMessage("\u00a7%command-only-for-players%\u00a7");
                return;
            }
            playerList.addAll(cmdArgs.getSenderAsMember(BungeeMember.class).getProxiedPlayer().getServer().getInfo().getPlayers());
        } else if (!args[0].contains(",")) {
            ProxiedPlayer player2 = ProxyServer.getInstance().getPlayer(args[0]);
            if (player2 == null) {
                sender.sendMessage(sender.getLanguage().t("player-not-found", "%player%", args[0]));
                return;
            }
            playerList.add(player2);
        } else {
            String[] split;
            for (String playerName : split = args[0].split(",")) {
                ProxiedPlayer player3 = ProxyServer.getInstance().getPlayer(playerName);
                if (player3 == null) {
                    sender.sendMessage(sender.getLanguage().t("player-not-found", "%player%", playerName));
                    return;
                }
                playerList.add(player3);
            }
        }
        ServerType serverType = null;
        try {
            serverType = ServerType.valueOf(args[1].toUpperCase());
        }
        catch (Exception ex) {
            serverType = null;
        }
        if (serverType == null) {
            ProxiedServer proxiedServer = BungeeMain.getInstance().getServerManager().getServerByName(args[1]);
            if (proxiedServer == null || proxiedServer.getServerInfo() == null) {
                sender.sendMessage(sender.getLanguage().t("server-not-found", "%server%", args[1]));
                return;
            }
            playerList.forEach(player -> player.connect(proxiedServer.getServerInfo()));
            if (args[0].equalsIgnoreCase("current")) {
                sender.sendMessage("\u00a7aTodos os jogadores da sala foram enviados para o servidor " + proxiedServer.getServerId() + ".");
                return;
            }
            if (args[0].equalsIgnoreCase("all")) {
                sender.sendMessage("\u00a7aTodos os jogadores foram enviados para o servidor " + proxiedServer.getServerId() + ".");
                return;
            }
            sender.sendMessage("\u00a7aOs jogadores " + playerList.stream().map(CommandSender::getName).collect(Collectors.joining(", ")) + " foram enviados para o servidor " + proxiedServer.getServerId() + ".");
            return;
        }
        ServerType type = serverType;
        List<ProxiedServer> servers = BungeeMain.getInstance().getServerManager().getBalancer(type).getList();
        if (servers.isEmpty()) {
            sender.sendMessage(sender.getLanguage().t("server-not-found", "%server%", type.name()));
            return;
        }
        int index = 0;
        for (ProxiedPlayer player3 : playerList) {
            player3.connect(servers.get(index).getServerInfo());
            if (++index < servers.size()) continue;
            index = 0;
        }
        if (args[0].equalsIgnoreCase("current")) {
            sender.sendMessage("\u00a7aTodos os jogadores da sala foram enviados para o servidor " + type.name() + ".");
            return;
        }
        if (args[0].equalsIgnoreCase("all")) {
            sender.sendMessage("\u00a7aTodos os jogadores foram enviados para o servidor " + type.name() + ".");
            return;
        }
        sender.sendMessage("\u00a7aOs jogadores " + playerList.stream().map(CommandSender::getName).collect(Collectors.joining(", ")) + " foram enviados para o servidor " + type.name() + ".");
    }

    private void handleInfo(br.com.dragonmc.core.common.command.CommandSender sender, Collection<ProxiedPlayer> players, int onlineTime, int playersRecord, ServerType serverType) {
        sender.sendMessage("    \u00a7fPlayers: \u00a77" + players.size() + " jogadores");
        sender.sendMessage("    \u00a7fRecord de players: \u00a77" + playersRecord);
        int ping = 0;
        HashMap<ProtocolVersion, Integer> map = new HashMap<ProtocolVersion, Integer>();
        for (ProxiedPlayer proxiedPlayer : players) {
            ping += proxiedPlayer.getPing();
            ProtocolVersion version = ProtocolVersion.getById(proxiedPlayer.getPendingConnection().getVersion());
            map.putIfAbsent(version, 0);
            map.put(version, (Integer)map.get((Object)version) + 1);
        }
        sender.sendMessage("    \u00a7fPing m\u00e9dio: \u00a77" + (ping /= Math.max(players.size(), 1)) + "ms");
        if (!players.isEmpty()) {
            sender.sendMessage("    \u00a7fVers\u00e3o: \u00a77");
            for (Map.Entry entry : map.entrySet()) {
                sender.sendMessage("      \u00a7f- " + ((ProtocolVersion)((Object)entry.getKey())).name().replace("MINECRAFT_", "").replace("_", ".") + ": \u00a77" + entry.getValue() + " jogadores");
            }
        }
        if (serverType == ServerType.BUNGEECORD) {
            sender.sendMessage("    \u00a7fTipo de servidor: \u00a77" + serverType.getName());
        }
        sender.sendMessage("    \u00a7fLigado h\u00e1: \u00a77" + StringFormat.formatTime(onlineTime, StringFormat.TimeFormat.NORMAL));
    }

    @CommandFramework.Completer(name="teleport", aliases={"tp"})
    public List<String> teleportCompleter(CommandArgs cmdArgs) {
        if (cmdArgs.getArgs().length == 1) {
            return ProxyServer.getInstance().getPlayers().stream().filter(player -> player.getName().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())).map(CommandSender::getName).collect(Collectors.toList());
        }
        return new ArrayList<String>();
    }

    @CommandFramework.Completer(name="send")
    public List<String> serverCompleter(CommandArgs cmdArgs) {
        ArrayList<String> stringList = new ArrayList<String>();
        if (cmdArgs.getArgs().length == 1) {
            for (String possibilities : Arrays.asList("all", "current")) {
                if (!possibilities.startsWith(cmdArgs.getArgs()[0].toLowerCase())) continue;
                stringList.add(possibilities);
            }
            stringList.addAll(ProxyServer.getInstance().getPlayers().stream().filter(player -> player.getName().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())).map(CommandSender::getName).collect(Collectors.toList()));
        } else if (cmdArgs.getArgs().length == 2) {
            stringList.addAll(BungeeMain.getInstance().getServerManager().getActiveServers().values().stream().filter(server -> server.getServerId().toLowerCase().startsWith(cmdArgs.getArgs()[1].toLowerCase())).map(ProxiedServer::getServerId).collect(Collectors.toList()));
            stringList.addAll(Arrays.asList(ServerType.values()).stream().filter(server -> server.name().toLowerCase().startsWith(cmdArgs.getArgs()[1].toLowerCase())).map(Enum::name).collect(Collectors.toList()));
        }
        return stringList;
    }

    @CommandFramework.Completer(name="find")
    public List<String> findCompleter(CommandArgs cmdArgs) {
        if (cmdArgs.getArgs().length == 1) {
            return ProxyServer.getInstance().getPlayers().stream().filter(player -> player.getName().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())).map(CommandSender::getName).collect(Collectors.toList());
        }
        return new ArrayList<String>();
    }
}

