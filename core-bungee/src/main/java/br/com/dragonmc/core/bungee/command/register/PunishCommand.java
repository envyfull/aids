/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  net.md_5.bungee.api.CommandSender
 *  net.md_5.bungee.api.ProxyServer
 *  net.md_5.bungee.api.connection.ProxiedPlayer
 *  net.md_5.bungee.api.plugin.Event
 */
package br.com.dragonmc.core.bungee.command.register;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.PluginInfo;
import br.com.dragonmc.core.bungee.event.player.PlayerPardonedEvent;
import br.com.dragonmc.core.bungee.event.player.PlayerPunishEvent;
import br.com.dragonmc.core.bungee.member.BungeeMember;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.command.CommandClass;
import br.com.dragonmc.core.common.command.CommandFramework;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.punish.Punish;
import br.com.dragonmc.core.common.punish.PunishType;
import br.com.dragonmc.core.common.utils.DateUtils;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class PunishCommand
implements CommandClass {
    @CommandFramework.Command(name="pardon", aliases={"unpunish"}, permission="command.pardon", runAsync=true)
    public void pardonCommand(CommandArgs cmdArgs) {
        br.com.dragonmc.core.common.command.CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length <= 1) {
            sender.sendMessage(PluginInfo.t(sender, "command.pardon.usage", "%label%", cmdArgs.getLabel()));
            return;
        }
        this.pardon(sender, args, null);
    }

    @CommandFramework.Command(name="unban", permission="command.pardon")
    public void unbanCommand(CommandArgs cmdArgs) {
        br.com.dragonmc.core.common.command.CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <player>\u00a7f para desbanir o jogador.");
            return;
        }
        this.pardon(sender, args, PunishType.BAN);
    }

    @CommandFramework.Command(name="unmute", permission="command.unmute")
    public void unmuteCommand(CommandArgs cmdArgs) {
        br.com.dragonmc.core.common.command.CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <player>\u00a7f para desmutar o jogador.");
            return;
        }
        this.pardon(sender, args, PunishType.MUTE);
    }

    @CommandFramework.Command(name="punish", aliases={"punir"}, permission="command.punish", runAsync=true)
    public void punishCommand(CommandArgs cmdArgs) {
        br.com.dragonmc.core.common.command.CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length <= 2) {
            sender.sendMessage(PluginInfo.t(sender, "command.punish.usage", "%label%", cmdArgs.getLabel()));
            return;
        }
        PunishType punishType = null;
        try {
            punishType = PunishType.valueOf(args[1].toUpperCase());
        }
        catch (Exception ex) {
            sender.sendMessage(PluginInfo.t(sender, "command.punish.type-not-exist", "%type%", args[1]));
            return;
        }
        String reason = args.length == 3 ? sender.getLanguage().t(punishType == PunishType.KICK ? "no-reason" : "defaut-ban", new String[0]) : Joiner.on((char)' ').join((Object[])Arrays.copyOfRange(args, 3, args.length));
        this.punish(sender, args[0], punishType, reason, args[2].equals("0") || args[2].equals("never") ? -1L : DateUtils.getTime(args[2]));
    }

    @CommandFramework.Command(name="mute", aliases={"ban", "tempban", "tempmute", "kick"}, permission="command.mute", runAsync=true)
    public void banMuteCommand(CommandArgs cmdArgs) {
        boolean temp;
        br.com.dragonmc.core.common.command.CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length <= ((temp = cmdArgs.getLabel().toLowerCase().contains("temp")) ? 1 : 0)) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <player> " + (temp ? "<tempo> " : "") + " <motivo>\u00a7f para punir algu\u00e9m.");
            return;
        }
        this.punish(sender, args[0], PunishType.valueOf(cmdArgs.getLabel().toUpperCase().replace("TEMP", "")), args.length == (temp ? 2 : 1) ? sender.getLanguage().t(cmdArgs.getLabel().equalsIgnoreCase("kick") ? "no-reason" : "defaut-ban", new String[0]) : Joiner.on((char)' ').join((Object[])Arrays.copyOfRange(args, temp ? 2 : 1, args.length)), temp ? DateUtils.getTime(args[1]) : -1L);
    }

    public void punish(br.com.dragonmc.core.common.command.CommandSender sender, String playerName, PunishType punishType, String reason, long expireTime) {
        Member target = CommonPlugin.getInstance().getMemberManager().getMemberByName(playerName);
        if (target == null && (target = CommonPlugin.getInstance().getMemberData().loadMember(playerName, true)) == null) {
            sender.sendMessage(sender.getLanguage().t("player-not-found", "%player%", playerName));
            return;
        }
        if (target.getServerGroup().getId() >= sender.getServerGroup().getId() && sender.isPlayer()) {
            sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o pode banir cargo superior.");
            return;
        }
        if (punishType != PunishType.KICK && target.getPunishConfiguration().getActualPunish(punishType) != null) {
            sender.sendMessage(PluginInfo.t(sender, "command.punish.already-punished", "%type%", StringFormat.formatString(punishType.name()), "%player%", target.getName()));
            return;
        }
        boolean permanent = expireTime == -1L;
        Punish punish = new Punish(target, sender, reason, expireTime, punishType);
        switch (punish.getPunishType()) {
            case BAN: {
                this.banPlayer(target, punish);
                break;
            }
            case KICK: {
                if (!target.isOnline()) {
                    sender.sendMessage(PluginInfo.t(sender, "player-is-not-online", "%player%", target.getPlayerName()));
                    return;
                }
                this.kickPlayer(target, punish);
                break;
            }
            case MUTE: {
                target.sendMessage(PluginInfo.t(sender, "mute-message", "%reason%", punish.getPunishReason(), "%expireAt%", DateUtils.getTime(target.getLanguage(), punish.getExpireAt()), "%punisher%", punish.getPunisherName(), "%website%", CommonPlugin.getInstance().getPluginInfo().getWebsite(), "%store%", CommonPlugin.getInstance().getPluginInfo().getStore(), "%discord%", CommonPlugin.getInstance().getPluginInfo().getDiscord()));
            }
        }
        target.getPunishConfiguration().punish(punish);
        target.saveConfig();
        sender.sendMessage(PluginInfo.t(sender, "command.punish.success-" + punishType.name().toLowerCase() + "-" + (permanent ? "permanent" : "temporary"), "%player%", target.getName(), "%reason%", reason, "%time%", DateUtils.getTime(sender.getLanguage(), expireTime)));
        ProxyServer.getInstance().getPluginManager().callEvent((Event)new PlayerPunishEvent(target, punish, sender));
    }

    @CommandFramework.Completer(name="ban", aliases={"mute", "tempban", "tempmute", "tempbanir", "tempmutar", "punish", "pardon", "punir", "perdoar"})
    public List<String> punishCompleter(CommandArgs cmdArgs) {
        switch (cmdArgs.getArgs().length) {
            case 1: {
                return ProxyServer.getInstance().getPlayers().stream().filter(player -> player.getName().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())).map(CommandSender::getName).collect(Collectors.toList());
            }
            case 2: {
                if (!cmdArgs.getLabel().equalsIgnoreCase("punish")) break;
                return Arrays.asList(PunishType.values()).stream().filter(type -> type.name().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())).map(Enum::name).collect(Collectors.toList());
            }
        }
        return new ArrayList<String>();
    }

    private void kickPlayer(Member target, Punish punish) {
        if (!(target instanceof BungeeMember)) {
            return;
        }
        BungeeMember bungeeMember = (BungeeMember)target;
        ProxiedPlayer player = bungeeMember.getProxiedPlayer();
        if (player != null) {
            player.disconnect(PluginInfo.t(bungeeMember, "kick-message", "%reason%", punish.getPunishReason(), "%punisher%", punish.getPunisherName(), "%website%", CommonPlugin.getInstance().getPluginInfo().getWebsite(), "%store%", CommonPlugin.getInstance().getPluginInfo().getStore(), "%discord%", CommonPlugin.getInstance().getPluginInfo().getDiscord()));
        }
    }

    public void banPlayer(Member target, Punish punish) {
        CommonPlugin.getInstance().getReportManager().notify(target.getUniqueId());
        if (!target.isOnline()) {
            return;
        }
        BungeeMember bungeeMember = (BungeeMember)target;
        ProxiedPlayer player = bungeeMember.getProxiedPlayer();
        if (player != null) {
            player.disconnect(PluginInfo.t(bungeeMember, "ban-" + (punish.isPermanent() ? "permanent" : "temporary") + "-kick-message", "%reason%", punish.getPunishReason(), "%expireAt%", DateUtils.getTime(bungeeMember.getLanguage(), punish.getExpireAt()), "%punisher%", punish.getPunisherName(), "%website%", CommonPlugin.getInstance().getPluginInfo().getWebsite(), "%store%", CommonPlugin.getInstance().getPluginInfo().getStore(), "%discord%", CommonPlugin.getInstance().getPluginInfo().getDiscord()));
        }
    }

    private void pardon(br.com.dragonmc.core.common.command.CommandSender sender, String[] args, PunishType punishType) {
        Member target = CommonPlugin.getInstance().getMemberManager().getMemberByName(args[0]);
        if (target == null && (target = CommonPlugin.getInstance().getMemberData().loadMember(args[0], true)) == null) {
            sender.sendMessage(sender.getLanguage().t("player-not-found", "%player%", args[0]));
            return;
        }
        if (punishType == null) {
            try {
                punishType = PunishType.valueOf(args[1].toUpperCase());
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (punishType == null || punishType == PunishType.KICK) {
            sender.sendMessage(PluginInfo.t(sender, "command.punish.type-not-exist", "%type%", args[1]));
            return;
        }
        Punish actualPunish = target.getPunishConfiguration().getActualPunish(punishType);
        if (actualPunish == null) {
            sender.sendMessage(PluginInfo.t(sender, "command.pardon.no-punish-found", "%type%", StringFormat.formatString(punishType.name()), "%player%", target.getName()));
            return;
        }
        if (target.getPunishConfiguration().pardon(actualPunish, sender)) {
            sender.sendMessage(PluginInfo.t(sender, "command.pardon.success", "%type%", StringFormat.formatString(punishType.name()), "%player%", target.getName()));
            target.saveConfig();
            ProxyServer.getInstance().getPluginManager().callEvent((Event)new PlayerPardonedEvent(target, actualPunish, sender));
            return;
        }
        sender.sendMessage(PluginInfo.t(sender, "command.pardon.failed", "%type%", StringFormat.formatString(punishType.name()), "%player%", target.getName()));
    }
}

