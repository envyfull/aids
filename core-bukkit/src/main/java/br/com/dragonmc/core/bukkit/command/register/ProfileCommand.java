/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.wrappers.WrappedSignedProperty
 *  com.google.common.base.Joiner
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.api.chat.ClickEvent
 *  net.md_5.bungee.api.chat.ClickEvent$Action
 *  net.md_5.bungee.api.chat.HoverEvent
 *  net.md_5.bungee.api.chat.HoverEvent$Action
 *  net.md_5.bungee.api.chat.TextComponent
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 *  org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.command.register;

import br.com.dragonmc.core.bukkit.command.BukkitCommandSender;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import br.com.dragonmc.core.BukkitConst;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.member.BukkitMember;
import br.com.dragonmc.core.bukkit.menu.profile.PreferencesInventory;
import br.com.dragonmc.core.bukkit.menu.profile.ProfileInventory;
import br.com.dragonmc.core.bukkit.utils.player.PlayerAPI;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.command.CommandClass;
import br.com.dragonmc.core.common.command.CommandFramework;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.member.Profile;
import br.com.dragonmc.core.common.member.configuration.LoginConfiguration;
import br.com.dragonmc.core.common.permission.Group;
import br.com.dragonmc.core.common.permission.GroupInfo;
import br.com.dragonmc.core.common.permission.Tag;
import br.com.dragonmc.core.common.punish.Punish;
import br.com.dragonmc.core.common.punish.PunishType;
import br.com.dragonmc.core.common.utils.DateUtils;
import br.com.dragonmc.core.common.utils.skin.Skin;
import br.com.dragonmc.core.common.utils.string.MessageBuilder;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ProfileCommand
implements CommandClass {
    @CommandFramework.Command(name="block", console=false)
    public void blockCommand(CommandArgs cmdArgs) {
        Member sender = cmdArgs.getSenderAsMember();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <player>\u00a7f para bloquear um jogador.");
            return;
        }
        Member target = CommonPlugin.getInstance().getMemberManager().getMemberByName(cmdArgs.getArgs()[0]);
        if (target == null && (target = CommonPlugin.getInstance().getMemberData().loadMember(cmdArgs.getArgs()[0], true)) == null) {
            sender.sendMessage(sender.getLanguage().t("account-doesnt-exist", "%player%", cmdArgs.getArgs()[0]));
            return;
        }
        if (sender.isUserBlocked(Profile.from(target))) {
            sender.sendMessage("\u00a7cO jogador " + target.getName() + " j\u00e1 est\u00e1 bloqueado.");
            return;
        }
        sender.sendMessage("\u00a7aVoc\u00ea bloqueou o jogador " + target.getName() + ".");
        sender.block(Profile.from(target));
    }

    @CommandFramework.Command(name="unblock", console=false)
    public void unblockCommand(CommandArgs cmdArgs) {
        Member sender = cmdArgs.getSenderAsMember();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <player>\u00a7f para desbloquear um jogador.");
            return;
        }
        Member target = CommonPlugin.getInstance().getMemberManager().getMemberByName(cmdArgs.getArgs()[0]);
        if (target == null && (target = CommonPlugin.getInstance().getMemberData().loadMember(cmdArgs.getArgs()[0], true)) == null) {
            sender.sendMessage(sender.getLanguage().t("account-doesnt-exist", "%player%", cmdArgs.getArgs()[0]));
            return;
        }
        if (!sender.isUserBlocked(Profile.from(target))) {
            sender.sendMessage("\u00a7cO jogador " + target.getName() + " n\u00e3o est\u00e1 bloqueado.");
            return;
        }
        sender.sendMessage("\u00a7aVoc\u00ea desbloqueou o jogador " + target.getName() + ".");
        sender.unblock(Profile.from(target));
    }

    @CommandFramework.Command(name="ping", console=false)
    public void pingCommand(CommandArgs cmdArgs) {
        BukkitMember member = cmdArgs.getSenderAsMember(BukkitMember.class);
        member.sendMessage("\u00a7aSeu ping \u00e9 " + ((CraftPlayer)member.getPlayer()).getHandle().ping + "ms.");
    }

    @CommandFramework.Command(name="tell")
    public void tellCommand(CommandArgs cmdArgs) {
        br.com.dragonmc.core.common.command.CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length <= 1) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <player> <message>\u00a7f para enviar uma mensagem para um jogador.");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "on": {
                sender.setTellEnabled(true);
                sender.sendMessage("\u00a7aVoc\u00ea agora receber\u00e1 mensagens privadas.");
                break;
            }
            case "off": {
                sender.setTellEnabled(false);
                sender.sendMessage("\u00a7cVoc\u00ea agora n\u00e3o receber\u00e1 mais mensagens privadas.");
                break;
            }
            default: {
                br.com.dragonmc.core.common.command.CommandSender target;
                Member member;
                Punish punish;
                if (sender instanceof Member && (punish = (member = (Member)sender).getPunishConfiguration().getActualPunish(PunishType.MUTE)) != null) {
                    member.sendMessage((BaseComponent)new MessageBuilder(punish.getMuteMessage(member.getLanguage())).setHoverEvent("\u00a7fPunido em: \u00a77" + CommonConst.DATE_FORMAT.format(punish.getCreatedAt()) + "\n\u00a7fExpire em: \u00a77" + (punish.isPermanent() ? "\u00a7cnunca" : DateUtils.formatDifference(member.getLanguage(), punish.getExpireAt() / 1000L))).create());
                    return;
                }
                br.com.dragonmc.core.common.command.CommandSender commandSender = target = args[0].equalsIgnoreCase("console") ? new BukkitCommandSender((CommandSender)Bukkit.getConsoleSender()) : CommonPlugin.getInstance().getMemberManager().getMemberByName(args[0]);
                if (target == null) {
                    sender.sendMessage(sender.getLanguage().t("player-is-not-online", "%player%", args[0]));
                    return;
                }
                if (target.isUserBlocked(Profile.from(sender))) {
                    sender.sendMessage("\u00a7cO jogador " + target.getName() + " bloqueou voc\u00ea.");
                    return;
                }
                if (!target.isTellEnabled() && !sender.hasPermission("command.admin")) {
                    sender.sendMessage("\u00a7cO jogador " + target.getName() + " n\u00e3o est\u00e1 recebendo mensagens privadas no momento.");
                    return;
                }
                this.sendMessage(sender, target, Joiner.on((char)' ').join((Object[])Arrays.copyOfRange(args, 1, args.length)));
                break;
            }
        }
    }

    @CommandFramework.Command(name="reply", aliases={"r"})
    public void replyCommand(CommandArgs cmdArgs) {
        br.com.dragonmc.core.common.command.CommandSender target;
        Member member;
        Punish punish;
        br.com.dragonmc.core.common.command.CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <message>\u00a7f para enviar uma mensagem para um jogador.");
            return;
        }
        if (!sender.hasReply()) {
            sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o possui mensagem para responder.");
            return;
        }
        if (sender instanceof Member && (punish = (member = (Member)sender).getPunishConfiguration().getActualPunish(PunishType.MUTE)) != null) {
            member.sendMessage((BaseComponent)new MessageBuilder(punish.getMuteMessage(member.getLanguage())).setHoverEvent("\u00a7fPunido em: \u00a77" + CommonConst.DATE_FORMAT.format(punish.getCreatedAt()) + "\n\u00a7fExpire em: \u00a77" + (punish.isPermanent() ? "\u00a7cnunca" : DateUtils.formatDifference(member.getLanguage(), punish.getExpireAt() / 1000L))).create());
            return;
        }
        br.com.dragonmc.core.common.command.CommandSender commandSender = target = sender.getReplyId() == CommonConst.CONSOLE_ID ? BukkitConst.CONSOLE_SENDER : CommonPlugin.getInstance().getMemberManager().getMember(sender.getReplyId());
        if (target == null) {
            sender.sendMessage("\u00a7cO \u00faltimo jogador que voc\u00ea te mandou mensagem n\u00e3o est\u00e1 mais online.");
            return;
        }
        if (target.isUserBlocked(Profile.from(sender))) {
            sender.sendMessage("\u00a7cO jogador " + target.getName() + " bloqueou voc\u00ea.");
            return;
        }
        this.sendMessage(sender, target, Joiner.on((char)' ').join((Object[])Arrays.copyOfRange(args, 0, args.length)));
    }

    @CommandFramework.Command(name="profile", aliases={"perfil"}, console=false)
    public void profileCommand(CommandArgs cmdArgs) {
        new ProfileInventory(cmdArgs.getSenderAsMember(BukkitMember.class).getPlayer());
    }

    @CommandFramework.Command(name="preferences", aliases={"pref", "prefs", "preferencias"}, console=false)
    public void preferencesCommand(CommandArgs cmdArgs) {
        new PreferencesInventory(cmdArgs.getSenderAsMember(BukkitMember.class).getPlayer());
    }

    @CommandFramework.Command(name="account", aliases={"acc"}, runAsync=true)
    public void accountCommand(CommandArgs cmdArgs) {
        Member member;
        br.com.dragonmc.core.common.command.CommandSender sender = cmdArgs.getSender();
        Member member2 = member = cmdArgs.isPlayer() ? cmdArgs.getSenderAsMember() : null;
        if (!cmdArgs.isPlayer() && cmdArgs.getArgs().length == 0) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <player>\u00a7f para ver o perfil de algu\u00e9m.");
            return;
        }
        if (cmdArgs.getArgs().length >= 1) {
            if (sender.hasPermission("command.admin")) {
                member = CommonPlugin.getInstance().getMemberManager().getMemberByName(cmdArgs.getArgs()[0]);
                if (member == null && (member = CommonPlugin.getInstance().getMemberData().loadMember(cmdArgs.getArgs()[0], true)) == null) {
                    sender.sendMessage(sender.getLanguage().t("account-doesnt-exist", "%player%", cmdArgs.getArgs()[0]));
                    return;
                }
            } else {
                sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o tem permiss\u00e3o para visualizar o perfil de outros jogadores.");
                return;
            }
        }
        Group actualGroup = member.getServerGroup();
        GroupInfo groupInfo = member.getServerGroup(actualGroup.getGroupName());
        sender.sendMessage(" ");
        sender.sendMessage("\u00a7a  " + (sender.getUniqueId() == member.getUniqueId() ? "Sua conta" : "Conta do " + member.getPlayerName()));
        sender.sendMessage("    \u00a7fPrimeiro login: \u00a77" + CommonPlugin.getInstance().formatTime(member.getFirstLogin()));
        sender.sendMessage("    \u00a7fUltimo login: \u00a77" + CommonPlugin.getInstance().formatTime(member.getLastLogin()) + (member.isOnline() ? "" : " (h\u00e1 " + DateUtils.formatDifference(sender.getLanguage(), (System.currentTimeMillis() - member.getLastLogin()) / 1000L) + ")"));
        sender.sendMessage("    \u00a7fTempo total de jogo: \u00a77" + DateUtils.formatDifference(sender.getLanguage(), member.getOnlineTime() / 1000L));
        sender.sendMessage("    \u00a7fTipo de conta: \u00a77" + StringFormat.formatString(member.getLoginConfiguration().getAccountType().name()));
        sender.sendMessage(" ");
        sender.sendMessage((BaseComponent)new MessageBuilder("    \u00a7fGrupo principal: \u00a77" + StringFormat.formatString(actualGroup.getGroupName())).setHoverEvent("\u00a7aGrupo principal " + StringFormat.formatString(actualGroup.getGroupName()) + "\n\n  \u00a7fAutor: \u00a77" + groupInfo.getAuthorName() + "\n  \u00a7fData: \u00a77" + CommonConst.DATE_FORMAT.format(groupInfo.getGivenDate()) + "\n  \u00a7fExpire em: \u00a77" + (groupInfo.isPermanent() ? "Nunca" : DateUtils.getTime(sender.getLanguage(), groupInfo.getExpireTime())) + "\n\n\u00a7eClique para ver informa\u00e7\u00f5es do grupo.").setClickEvent("/group info " + actualGroup.getGroupName()).create());
        List list = member.getGroups().keySet().stream().filter(groupName -> !actualGroup.getGroupName().equals(groupName)).map(groupName -> CommonPlugin.getInstance().getPluginInfo().getGroupByName((String)groupName)).collect(Collectors.toList());
        if (!list.isEmpty()) {
            MessageBuilder messageBuilder = new MessageBuilder("    \u00a7fGrupos adicionais: \u00a77");
            HashSet<Map.Entry<String, GroupInfo>> entrySet = new HashSet<Map.Entry<String, GroupInfo>>(member.getGroups().entrySet());
            entrySet.removeIf(entry -> ((String)entry.getKey()).equalsIgnoreCase(actualGroup.getGroupName()));
            int i = 1;
            for (Map.Entry entry2 : entrySet) {
                messageBuilder.extra(new MessageBuilder("\u00a77" + StringFormat.formatString((String)entry2.getKey()) + (i == entrySet.size() ? "\u00a7f." : "\u00a7f, \u00a77")).setHoverEvent("\u00a7aGrupo " + StringFormat.formatString((String)entry2.getKey()) + "\n\n  \u00a7fAutor: \u00a77" + ((GroupInfo)entry2.getValue()).getAuthorName() + "\n  \u00a7fData: \u00a77" + CommonConst.DATE_FORMAT.format(((GroupInfo)entry2.getValue()).getGivenDate()) + "\n  \u00a7fExpire em: \u00a77" + (((GroupInfo)entry2.getValue()).isPermanent() ? "Nunca" : DateUtils.getTime(sender.getLanguage(), ((GroupInfo)entry2.getValue()).getExpireTime())) + "\n\n\u00a7eClique para ver informa\u00e7\u00f5es do grupo.").setClickEvent("/group info " + (String)entry2.getKey()).create());
                ++i;
            }
            sender.sendMessage((BaseComponent)messageBuilder.create());
        }
        if (sender.isStaff() || sender.getUniqueId() == member.getUniqueId()) {
            if (!member.getPermissions().isEmpty()) {
                sender.sendMessage("    \u00a7fPermiss\u00f5es: \u00a77" + Joiner.on((String)", ").join(member.getPermissions()));
            }
            if (member.getLastIpAddress() != null && sender.getServerGroup().getId() >= member.getServerGroup().getId()) {
                sender.sendMessage("");
                sender.sendMessage("    \u00a7fEndere\u00e7o ip: \u00a77" + member.getLastIpAddress());
            }
        }
        if (member.isOnline()) {
            if (member.getLastIpAddress() == null || sender.getServerGroup().getId() < member.getServerGroup().getId()) {
                sender.sendMessage("");
            }
            sender.sendMessage((BaseComponent)new MessageBuilder("    \u00a7fServidor atual: \u00a77" + member.getActualServerId()).setClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/connect " + member.getActualServerId()).setHoverEvent("\u00a7aClique para ir ao servidor.").create());
            sender.sendMessage("    \u00a7fTempo da sess\u00e3o atual: \u00a77" + DateUtils.formatDifference(sender.getLanguage(), member.getSessionTime() / 1000L));
            sender.sendMessage("    \u00a7aO jogador est\u00e1 online no momento.");
        } else {
            sender.sendMessage("    \u00a7cO jogador est\u00e1 offline no momento.");
        }
        sender.sendMessage("");
    }

    @CommandFramework.Command(name="tag", runAsync=true, console=false)
    public void tagCommand(CommandArgs cmdArgs) {
        BukkitMember player = (BukkitMember)cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            List<Tag> tags = CommonPlugin.getInstance().getPluginInfo().getTagMap().values().stream().filter(tag -> player.hasTag((Tag)tag)).collect(Collectors.toList());
            if (tags.isEmpty()) {
                player.sendMessage("\u00a7cVoc\u00ea n\u00e3o possui nenhuma tag.");
                return;
            }
            TextComponent message = new TextComponent("\u00a7aSelecione sua tag: ");
            int max = tags.size() * 2;
            int i = max - 1;
            for (Tag t : tags) {
                if (i < max - 1) {
                    message.addExtra((BaseComponent)new TextComponent("\u00a7f, "));
                    --i;
                }
                message.addExtra((BaseComponent)new MessageBuilder(t.getStrippedColor().toUpperCase()).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (BaseComponent[])new TextComponent[]{new TextComponent("\u00a7fExemplo: " + t.getRealPrefix() + player.getPlayerName() + "\n\n\u00a7aClique para selecionar!")})).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tag " + t.getTagName())).create());
                --i;
            }
            player.sendMessage((BaseComponent)message);
            return;
        }
        if (args[0].equalsIgnoreCase("default") || args[0].equalsIgnoreCase("normal")) {
            if (((Member)player).setTag(player.getDefaultTag())) {
                player.sendMessage(" \u00a7a\u00bb \u00a7fVoc\u00ea alterou sua tag para " + player.getDefaultTag().getStrippedColor() + "\u00a7f.");
            }
            return;
        }
        Tag tag2 = CommonPlugin.getInstance().getPluginInfo().getTagByName(args[0]);
        if (tag2 == null) {
            player.sendMessage(" \u00a74\u00bb \u00a7fA tag " + args[0] + " n\u00e3o existe!");
            return;
        }
        if (player.hasTag(tag2)) {
            if (!player.getTag().equals(tag2)) {
                ((Member)player).setTag(tag2);
            }
            player.sendMessage("\u00a7aSua tag foi alterada para " + tag2.getStrippedColor() + "\u00a7a.");
        } else {
            player.sendMessage("\u00a7cVoc\u00ea n\u00e3o tem permiss\u00e3o para usar essa tag.");
        }
    }

    @CommandFramework.Command(name="fake.#", aliases={"nick.#", "nickreset", "fakereset", "fake.reset", "nick.reset"}, permission="command.fake", console=false)
    public void fakeresetCommand(CommandArgs cmdArgs) {
        BukkitMember sender = cmdArgs.getSenderAsMember(BukkitMember.class);
        if (!sender.isUsingFake()) {
            sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o est\u00e1 usando fake.");
            return;
        }
        if (!sender.hasCustomSkin()) {
            PlayerAPI.changePlayerSkin(sender.getPlayer(), sender.getName(), sender.getUniqueId(), false);
        }
        Player player = sender.getPlayer();
        PlayerAPI.changePlayerSkin(player, player.getName(), player.getUniqueId(), false);
        PlayerAPI.changePlayerName(player, sender.getName(), true);
        sender.setFakeName(sender.getPlayerName());
        sender.setTag(sender.getDefaultTag());
        sender.sendMessage("\u00a7aSeu fake foi removido com sucesso.");
    }

    @CommandFramework.Command(name="fake", aliases={"nick"}, runAsync=true, permission="command.fake", console=false)
    public void fakeCommand(CommandArgs cmdArgs) {
        String playerName;
        BukkitMember sender = cmdArgs.getSenderAsMember(BukkitMember.class);
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <player>\u00a7f para alterar sua skin.");
            return;
        }
        if (sender.hasCooldown("fake.command") && !sender.hasPermission("command.admin")) {
            sender.sendMessage("\u00a7cVoc\u00ea precisa esperar " + sender.getCooldownFormatted("fake.command") + " para usar eses comando novamente.");
            return;
        }
        String string = playerName = args[0].equals("random") ? BukkitConst.RANDOM.get(CommonConst.RANDOM.nextInt(BukkitConst.RANDOM.size())) : args[0];
        if (!PlayerAPI.validateName(playerName)) {
            sender.sendMessage("\u00a7cO nome inserido \u00e9 inv\u00e1lido.");
            return;
        }
        UUID uniqueId = CommonPlugin.getInstance().getUuidFetcher().request(playerName);
        if (uniqueId != null) {
            sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o pode usar fake de uma conta registrada na mojang, tente outro nick.");
            return;
        }
        if (CommonPlugin.getInstance().getMemberData().loadMember(playerName, true) != null) {
            sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o pode usar fake de uma conta j\u00e1 registrada no servidor, tente outro nick.");
            return;
        }
        if (Bukkit.getPlayerExact((String)playerName) != null) {
            sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o pode usar o fake do " + playerName + ". 3");
            return;
        }
        if (!sender.isCustomSkin()) {
            CommonPlugin.getInstance().getMemberManager().getMembers().stream().filter(member -> member.hasCustomSkin() || member.getLoginConfiguration().getAccountType() == LoginConfiguration.AccountType.PREMIUM).findFirst().ifPresent(member -> {
                if (member.isCustomSkin()) {
                    sender.setSkin(member.getSkin());
                    PlayerAPI.changePlayerSkin(sender.getPlayer(), member.getSkin().getValue(), member.getSkin().getSignature(), false);
                } else {
                    WrappedSignedProperty changePlayerSkin = PlayerAPI.changePlayerSkin(sender.getPlayer(), member.getName(), member.getUniqueId(), false);
                    sender.setSkin(new Skin(member.getName(), member.getUniqueId(), changePlayerSkin.getValue(), changePlayerSkin.getSignature()));
                }
            });
        }
        PlayerAPI.changePlayerName(sender.getPlayer(), playerName, true);
        sender.setFakeName(playerName);
        sender.setTag(CommonPlugin.getInstance().getPluginInfo().getDefaultTag());
        sender.sendMessage("\u00a7aSeu nick foi alterada para " + playerName + ".");
        sender.putCooldown("command.fake", 30L);
    }

    @CommandFramework.Completer(name="tag")
    public List<String> tagCompleter(CommandArgs cmdArgs) {
        if (cmdArgs.isPlayer() && cmdArgs.getArgs().length == 1) {
            ArrayList<String> tagList = new ArrayList<String>();
            BukkitMember member = (BukkitMember)CommonPlugin.getInstance().getMemberManager().getMember(cmdArgs.getSender().getUniqueId());
            if (cmdArgs.getArgs()[0].isEmpty()) {
                for (Tag tag : CommonPlugin.getInstance().getPluginInfo().getTags()) {
                    if (!member.hasTag(tag)) continue;
                    tagList.add(tag.getTagName().toLowerCase());
                }
            } else {
                for (Tag tag : CommonPlugin.getInstance().getPluginInfo().getTags()) {
                    if (!member.hasTag(tag) || !tag.getTagName().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())) continue;
                    tagList.add(tag.getTagName().toLowerCase());
                }
            }
            return tagList;
        }
        return new ArrayList<String>();
    }

    public void sendMessage(br.com.dragonmc.core.common.command.CommandSender sender, br.com.dragonmc.core.common.command.CommandSender target, String message) {
        sender.sendMessage("\u00a77[" + sender.getName() + " -> " + target.getName() + "] " + message);
        target.sendMessage("\u00a77[" + sender.getName() + " -> " + target.getName() + "] " + message);
        target.setReplyId(sender.getUniqueId());
    }
}

