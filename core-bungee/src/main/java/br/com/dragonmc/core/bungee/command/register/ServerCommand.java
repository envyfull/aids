/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  net.md_5.bungee.api.CommandSender
 *  net.md_5.bungee.api.ProxyServer
 *  net.md_5.bungee.api.connection.ProxiedPlayer
 */
package br.com.dragonmc.core.bungee.command.register;

import br.com.dragonmc.core.bungee.BungeeMain;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bungee.member.BungeeMember;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.command.CommandClass;
import br.com.dragonmc.core.common.command.CommandFramework;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.report.Report;
import br.com.dragonmc.core.common.server.ServerType;
import br.com.dragonmc.core.common.server.loadbalancer.server.ProxiedServer;
import br.com.dragonmc.core.common.utils.DateUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ServerCommand
implements CommandClass {
    @CommandFramework.Command(name="ping", aliases={"latency"})
    public void pingCommand(CommandArgs cmdArgs) {
        br.com.dragonmc.core.common.command.CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            if (sender.isPlayer()) {
                sender.sendMessage(sender.getLanguage().t("command-ping-your-latency", "%ping%", String.valueOf(cmdArgs.getSenderAsMember(BungeeMember.class).getProxiedPlayer().getPing())));
            } else {
                sender.sendMessage("\u00a7aO ping m\u00e9dio do servidor \u00e9 de " + BungeeMain.getInstance().getAveragePing(ProxyServer.getInstance().getPlayers()) + "ms.");
            }
        } else {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(sender.getLanguage().t("player-not-found", "%player%", args[0]));
            } else {
                sender.sendMessage(sender.getLanguage().t("command-ping-player-latency", "%player%", player.getName(), "%ping%", String.valueOf(player.getPing())));
            }
        }
    }

    @CommandFramework.Command(name="report", aliases={"rp"})
    public void reportCommand(CommandArgs cmdArgs) {
        br.com.dragonmc.core.common.command.CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length <= 1) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <player> <reason>\u00a7f para reportar um jogador.");
            return;
        }
        Member target = CommonPlugin.getInstance().getMemberManager().getMemberByName(args[0]);
        if (target == null) {
            sender.sendMessage(sender.getLanguage().t("player-is-not-online", "%player%", args[0]));
            return;
        }
        String reason = Joiner.on((char)' ').join((Object[])Arrays.copyOfRange(args, 1, args.length));
        Report report = CommonPlugin.getInstance().getReportManager().getReportById(target.getUniqueId());
        if (report == null) {
            report = new Report(target, sender, reason, target.isOnline());
            CommonPlugin.getInstance().getReportManager().createReport(report);
        } else {
            if (report.getReportMap().containsKey(sender.getUniqueId()) && report.getReportMap().get(sender.getUniqueId()).getCreatedAt() + 300000L > System.currentTimeMillis()) {
                sender.sendMessage("\u00a7cVoc\u00ea precisa esperar para reportar esse usu\u00e1rio novamente.");
                return;
            }
            report.addReport(sender, reason);
        }
        sender.sendMessage("\u00a7aSua den\u00fancia sobre o jogador " + target.getPlayerName() + " foi enviada ao servidor.");
        CommonPlugin.getInstance().getMemberManager().actionbar("\u00a7aUma nova den\u00fancia foi registrada.", "command.report");
        CommonPlugin.getInstance().getMemberManager().getMembers().stream().filter(m -> m.isStaff() && m.getMemberConfiguration().isReportsEnabled()).forEach(m -> m.sendMessage("\u00a7eO jogador " + sender.getName() + " denunciou o jogador " + target.getName() + " por " + reason));
    }

    @CommandFramework.Command(name="server", aliases={"connect"}, console=false)
    public void serverCommand(CommandArgs cmdArgs) {
        BungeeMember member = cmdArgs.getSenderAsMember(BungeeMember.class);
        ProxiedPlayer player = member.getProxiedPlayer();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            player.sendMessage(member.getLanguage().t("command-server-usage", new String[0]));
            return;
        }
        String serverId = args[0];
        ProxiedServer server = BungeeMain.getInstance().getServerManager().getServerByName(serverId);
        if (server == null || server.getServerInfo() == null) {
            player.sendMessage(member.getLanguage().t("server-not-available", new String[0]));
            return;
        }
        if (server.isFull() && !member.hasPermission("server.full")) {
            player.sendMessage(member.getLanguage().t("server-is-full", new String[0]));
            return;
        }
        player.connect(server.getServerInfo());
    }

    @CommandFramework.Command(name="evento", console=false)
    public void eventoCommand(CommandArgs cmdArgs) {
        BungeeMember member = cmdArgs.getSenderAsMember(BungeeMember.class);
        ProxiedPlayer player = member.getProxiedPlayer();
        ProxiedServer server = (ProxiedServer)BungeeMain.getInstance().getServerManager().getBalancer(ServerType.EVENTO).next();
        if (server == null || server.getServerInfo() == null) {
            player.sendMessage(member.getLanguage().t("server-not-available", new String[0]));
            return;
        }
        if (server.isFull() && !member.hasPermission("server.full")) {
            player.sendMessage(member.getLanguage().t("server-is-full", new String[0]));
            return;
        }
        player.connect(server.getServerInfo());
    }

    @CommandFramework.Command(name="lobby", aliases={"hub", "l"}, console=false)
    public void lobbyCommand(CommandArgs cmdArgs) {
        BungeeMember member = cmdArgs.getSenderAsMember(BungeeMember.class);
        ProxiedPlayer player = member.getProxiedPlayer();
        ProxiedServer server = (ProxiedServer)BungeeMain.getInstance().getServerManager().getBalancer(BungeeMain.getInstance().getServerManager().getServer(player.getServer().getInfo().getName()).getServerType().getServerLobby()).next();
        if (server == null || server.getServerInfo() == null) {
            player.sendMessage(member.getLanguage().t("server-not-available", new String[0]));
            return;
        }
        if (server.isFull() && !member.hasPermission("server.full")) {
            player.sendMessage(member.getLanguage().t("server-is-full", new String[0]));
            return;
        }
        player.connect(server.getServerInfo());
    }

    @CommandFramework.Command(name="play", aliases={"jogar"}, console=false)
    public void playCommand(CommandArgs cmdArgs) {
        ProxiedPlayer player = cmdArgs.getSenderAsMember(BungeeMember.class).getProxiedPlayer();
        Object[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            player.sendMessage(cmdArgs.getSender().getLanguage().t("command.play.usage", new String[0]));
            return;
        }
        ServerType serverType = ServerType.getTypeByName(Joiner.on((char)' ').join(args).replace(" ", "_"));
        if (serverType == null) {
            player.sendMessage("\u00a7cEsse servidor n\u00e3o existe.");
            return;
        }
        ProxiedServer server = (ProxiedServer)BungeeMain.getInstance().getServerManager().getBalancer(serverType).next();
        Language language = cmdArgs.getSender().getLanguage();
        if (server == null || server.getServerInfo() == null) {
            player.sendMessage("\u00a7cNenhum servidor desse modo est\u00e1 dispon\u00edvel no momento.");
            return;
        }
        if (server.isFull() && !player.hasPermission("server.full")) {
            player.sendMessage(language.t("server-is-full", new String[0]));
            return;
        }
        player.connect(server.getServerInfo());
    }

    @CommandFramework.Command(name="bwhitelist", aliases={"bungeewhitelist", "gwhitelist", "maintenance", "manutencao"}, permission="command.whitelist")
    public void bwhitelistChatCommand(CommandArgs cmdArgs) {
        String[] args = cmdArgs.getArgs();
        br.com.dragonmc.core.common.command.CommandSender sender = cmdArgs.getSender();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7e\u00bb \u00a7fUse /" + cmdArgs.getLabel() + " <on:off> <time> para ativar ou desativar a whitelist global.");
            sender.sendMessage(" \u00a7e\u00bb \u00a7fUse /" + cmdArgs.getLabel() + " add <player> para adicionar alguem a whitelist.");
            sender.sendMessage(" \u00a7e\u00bb \u00a7fUse /" + cmdArgs.getLabel() + " remove <player> para remover alguem da whitelist.");
            sender.sendMessage(" \u00a7e\u00bb \u00a7fUse /" + cmdArgs.getLabel() + " group <beta:staff:group> para definir qual grupo ir\u00e1 entrar no servidor.");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "on": {
                Long time = 0L;
                if (args.length > 1) {
                    time = DateUtils.getTime(args[1]);
                }
                BungeeMain.getInstance().setWhitelistEnabled(true, time);
                sender.sendMessage(" \u00a7a\u00bb \u00a7fVoc\u00ea \u00a7aativou\u00a7f a whitelist!");
                CommonPlugin.getInstance().getMemberManager().getMembers(BungeeMember.class).stream().filter(bungee -> !bungee.hasPermission("command.admin")).forEach(bungee -> bungee.getProxiedPlayer().disconnect("\u00a7cO servidor entrou em manuten\u00e7\u00e3o."));
                break;
            }
            case "off": {
                BungeeMain.getInstance().setWhitelistEnabled(false, 0L);
                sender.sendMessage(" \u00a7a\u00bb \u00a7fVoc\u00ea \u00a7cdesativou\u00a7f a whitelist!");
                break;
            }
            case "add": {
                if (args.length == 1) {
                    sender.sendMessage(" \u00a7e\u00bb \u00a7fUse /" + cmdArgs.getLabel() + " add <player> para adicionar alguem a whitelist.");
                    break;
                }
                Member member = CommonPlugin.getInstance().getMemberManager().getMemberByName(args[1]);
                if (member == null && (member = CommonPlugin.getInstance().getMemberData().loadMember(args[1], true)) == null) {
                    sender.sendMessage(" \u00a74\u00bb \u00a7fO jogador " + args[1] + " n\u00e3o existe!");
                    break;
                }
                BungeeMain.getInstance().addMemberToWhiteList(member.getPlayerName());
                sender.sendMessage(" \u00a7a\u00bb \u00a7fO jogador \u00a7a" + member.getPlayerName() + "\u00a7f foi adicionado a whitelist!");
                break;
            }
            case "remove": {
                if (args.length == 1) {
                    sender.sendMessage(" \u00a7e\u00bb \u00a7fUse /" + cmdArgs.getLabel() + " remove <player> para remover alguem da whitelist.");
                    break;
                }
                Member member = CommonPlugin.getInstance().getMemberManager().getMemberByName(args[1]);
                if (member == null && (member = CommonPlugin.getInstance().getMemberData().loadMember(args[1], true)) == null) {
                    sender.sendMessage(" \u00a74\u00bb \u00a7fO jogador " + args[1] + " n\u00e3o existe!");
                    break;
                }
                BungeeMain.getInstance().removeMemberFromWhiteList(member.getPlayerName());
                sender.sendMessage(" \u00a7a\u00bb \u00a7fO jogador \u00a7a" + member.getPlayerName() + "\u00a7f foi removido da whitelist!");
                break;
            }
            case "list": {
                sender.sendMessage(" \u00a7a\u00bb \u00a7fLista de jogadores na whitelist: \u00a7a" + Joiner.on((String)", ").join(BungeeMain.getInstance().getWhiteList()));
            }
        }
    }

    @CommandFramework.Completer(name="report", aliases={"rp"})
    public List<String> teleportCompleter(CommandArgs cmdArgs) {
        if (cmdArgs.getArgs().length == 1) {
            return ProxyServer.getInstance().getPlayers().stream().filter(player -> player.getName().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())).map(CommandSender::getName).collect(Collectors.toList());
        }
        return new ArrayList<String>();
    }

    @CommandFramework.Completer(name="server", aliases={"connect"})
    public List<String> serverCompleter(CommandArgs cmdArgs) {
        if (cmdArgs.getArgs().length == 1) {
            return BungeeMain.getInstance().getServerManager().getActiveServers().values().stream().filter(server -> server.getServerId().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())).map(ProxiedServer::getServerId).collect(Collectors.toList());
        }
        return new ArrayList<String>();
    }

    @CommandFramework.Completer(name="play", aliases={"jogar"})
    public List<String> playCompleter(CommandArgs cmdArgs) {
        if (cmdArgs.getArgs().length == 1) {
            return Arrays.asList(ServerType.values()).stream().filter(type -> type.name().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())).map(Enum::name).collect(Collectors.toList());
        }
        return new ArrayList<String>();
    }
}

