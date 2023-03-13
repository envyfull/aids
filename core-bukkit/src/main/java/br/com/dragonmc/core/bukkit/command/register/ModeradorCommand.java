/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 */
package br.com.dragonmc.core.bukkit.command.register;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.member.BukkitMember;
import br.com.dragonmc.core.bukkit.menu.staff.AdminInventory;
import br.com.dragonmc.core.bukkit.menu.staff.punish.PunishInfoInventory;
import br.com.dragonmc.core.bukkit.menu.staff.server.ServerListInventory;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.command.CommandClass;
import br.com.dragonmc.core.common.command.CommandFramework;
import br.com.dragonmc.core.common.command.CommandSender;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.permission.Group;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ModeradorCommand
implements CommandClass {
    @CommandFramework.Command(name="punishinfo", aliases={"cc"}, console=false, permission="command.punish")
    public void punishinfoCommand(CommandArgs cmdArgs) {
        BukkitMember sender = cmdArgs.getSenderAsMember(BukkitMember.class);
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <player>\u00a7f para ver as puni\u00e7\u00f5es de um jogador.");
            return;
        }
        Member target = CommonPlugin.getInstance().getMemberManager().getMemberByName(cmdArgs.getArgs()[0]);
        if (target == null && (target = CommonPlugin.getInstance().getMemberData().loadMember(cmdArgs.getArgs()[0], true)) == null) {
            sender.sendMessage(sender.getLanguage().t("account-doesnt-exist", "%player%", cmdArgs.getArgs()[0]));
            return;
        }
        new PunishInfoInventory(sender.getPlayer(), target);
    }

    @CommandFramework.Command(name="serverlist", console=false, permission="command.server")
    public void serverlistCommand(CommandArgs cmdArgs) {
        new ServerListInventory(cmdArgs.getSenderAsMember(BukkitMember.class).getPlayer(), 1);
    }

    @CommandFramework.Command(name="clearchat", aliases={"cc"}, permission="command.clearchat")
    public void clearchatCommand(CommandArgs cmdArgs) {
        for (int i = 0; i < 128; ++i) {
            Bukkit.broadcastMessage((String)" ");
        }
        this.staffLog("O chat foi limpo pelo " + cmdArgs.getSender().getName());
    }

    @CommandFramework.Command(name="chat", permission="command.chat")
    public void chatCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <on:off>\u00a7f para desativar ou ativar o chat.");
            return;
        }
        BukkitCommon.ChatState chatState = null;
        switch (args[0].toLowerCase()) {
            case "on": {
                chatState = BukkitCommon.ChatState.ENABLED;
                break;
            }
            case "off": {
                chatState = BukkitCommon.ChatState.DISABLED;
                break;
            }
            case "vips": {
                chatState = BukkitCommon.ChatState.PAYMENT;
                break;
            }
            default: {
                try {
                    chatState = BukkitCommon.ChatState.valueOf(cmdArgs.getArgs()[0].toUpperCase());
                }
                catch (Exception ex) {
                    sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <on:off>\u00a7f para desativar ou ativar o chat.");
                }
                break;
            }
        }
        BukkitCommon.getInstance().setChatState(chatState);
        sender.sendMessage("\u00a7aO chat do servidor foi alterado para " + chatState.name() + ".");
        this.staffLog("O " + sender.getName() + " alterou o estado do chat para " + chatState.name() + "");
    }

    @CommandFramework.Command(name="inventorysee", aliases={"invsee", "inv"}, console=false, permission="command.invsee")
    public void invseeCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <player>\u00a7f para abrir o invent\u00e1rio do player.");
            return;
        }
        Player player = Bukkit.getPlayer((String)args[0]);
        if (player == null) {
            sender.sendMessage(sender.getLanguage().t("player-is-not-online", "%player%", args[0]));
            return;
        }
        cmdArgs.getSenderAsMember(BukkitMember.class).getPlayer().openInventory((Inventory)player.getInventory());
        this.staffLog("O " + sender.getName() + " abriu o invent\u00e1rio de " + player.getName());
    }

    @CommandFramework.Command(name="say", permission="command.say")
    public void sayCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        Object[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <message>\u00a7f para enviar uma mensagem no servidor.");
            return;
        }
        String message = Joiner.on((char)' ').join(args).replace('&', '\u00a7');
        Bukkit.broadcastMessage((String)"");
        Bukkit.broadcastMessage((String)("\u00a7dServer> \u00a7f" + message));
        Bukkit.broadcastMessage((String)"");
        this.staffLog("O " + sender.getName() + " mandou uma mensagem global.");
    }

    @CommandFramework.Command(name="admin", aliases={"adm"}, console=false, permission="command.admin")
    public void adminCommand(CommandArgs cmdArgs) {
        Player player = cmdArgs.getSenderAsMember(BukkitMember.class).getPlayer();
        if (cmdArgs.getArgs().length >= 1 && cmdArgs.getArgs()[0].equals("config")) {
            new AdminInventory(player, 0L);
            return;
        }
        if (BukkitCommon.getInstance().getVanishManager().isPlayerInAdmin(player)) {
            BukkitCommon.getInstance().getVanishManager().setPlayer(player);
            this.staffLog("O " + player.getName() + " saiu do modo admin");
        } else {
            BukkitCommon.getInstance().getVanishManager().setPlayerInAdmin(player);
            this.staffLog("O " + player.getName() + " entrou no modo admin");
        }
    }

    @CommandFramework.Command(name="anticheatbypass", permission="staff.super")
    public void anticheatbypassCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <player>\u00a7f para remover o autoban de um player.");
            return;
        }
        BukkitMember member = CommonPlugin.getInstance().getMemberManager().getMemberByName(args[0], BukkitMember.class);
        if (member == null) {
            sender.sendMessage(sender.getLanguage().t("player-is-not-online", "%player%", args[0]));
            return;
        }
        member.setAnticheatBypass(!member.isAnticheatBypass());
        sender.sendMessage("\u00a7aO modo anticheat bypass do player " + member.getName() + " foi alterado para " + member.isAnticheatBypass() + ".");
    }

    @CommandFramework.Command(name="autoban", permission="command.anticheat")
    public void autobanCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <player>\u00a7f para remover o autoban de um player.");
            return;
        }
        BukkitMember member = CommonPlugin.getInstance().getMemberManager().getMemberByName(args[0], BukkitMember.class);
        if (member == null) {
            sender.sendMessage(sender.getLanguage().t("player-is-not-online", "%player%", args[0]));
            return;
        }
        if (!BukkitCommon.getInstance().getStormCore().getBanPlayerMap().containsKey(member.getUniqueId())) {
            sender.sendMessage("\u00a7cO jogador n\u00e3o est\u00e1 na lista de pr\u00e9 banimento.");
            return;
        }
        sender.sendMessage("\u00a7aO jogador foi removido da lista de pr\u00e9 banimento e seus alertas foram limpos.");
        member.getUserData().getHackMap().clear();
        BukkitCommon.getInstance().getStormCore().getBanPlayerMap().remove(member.getUniqueId());
    }

    @CommandFramework.Command(name="vanish", aliases={"v"}, permission="command.vanish", console=false)
    public void vanishCommand(CommandArgs cmdArgs) {
        Group hidePlayer;
        Player player = cmdArgs.getSenderAsMember(BukkitMember.class).getPlayer();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            if (BukkitCommon.getInstance().getVanishManager().isPlayerVanished(player.getUniqueId())) {
                BukkitCommon.getInstance().getVanishManager().showPlayer(player);
                player.sendMessage("\u00a7dVoc\u00ea est\u00e1 vis\u00edvel para todos os jogadores.");
                this.staffLog("O " + player.getName() + " n\u00e3o est\u00e1 mais invis\u00edvel");
                return;
            }
            hidePlayer = BukkitCommon.getInstance().getVanishManager().hidePlayer(player);
        } else {
            Group group = CommonPlugin.getInstance().getPluginInfo().getGroupByName(args[0]);
            if (group == null) {
                player.sendMessage("\u00a7cO grupo " + StringFormat.formatString(args[0]) + " n\u00e3o existe.");
                return;
            }
            hidePlayer = group;
        }
        if (hidePlayer.getId() > cmdArgs.getSender().getServerGroup().getId()) {
            player.sendMessage("\u00a7cVoc\u00ea n\u00e3o pode ficar invis\u00edvel para um grupo superior ao seu.");
            return;
        }
        BukkitCommon.getInstance().getVanishManager().setPlayerVanishToGroup(player, hidePlayer);
        player.sendMessage(Language.getLanguage(player.getUniqueId()).t("vanish.player-group-hided", "%group%", StringFormat.formatString(hidePlayer.getGroupName())));
        this.staffLog("O " + player.getName() + " ficou invis\u00edvel para " + StringFormat.formatString(hidePlayer.getGroupName()));
    }

    @CommandFramework.Completer(name="chat")
    public List<String> chatCompleter(CommandArgs cmdArgs) {
        return cmdArgs.getArgs().length == 1 ? Arrays.asList(BukkitCommon.ChatState.values()).stream().filter(state -> state.name().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())).map(Enum::name).collect(Collectors.toList()) : new ArrayList<String>();
    }
}

