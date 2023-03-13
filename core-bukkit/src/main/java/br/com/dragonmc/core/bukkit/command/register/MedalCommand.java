/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  net.md_5.bungee.api.ChatColor
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.api.chat.ClickEvent$Action
 */
package br.com.dragonmc.core.bukkit.command.register;

import br.com.dragonmc.core.bukkit.manager.ChatManager;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.member.BukkitMember;
import br.com.dragonmc.core.bukkit.utils.menu.confirm.ConfirmInventory;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.command.CommandClass;
import br.com.dragonmc.core.common.command.CommandFramework;
import br.com.dragonmc.core.common.command.CommandSender;
import br.com.dragonmc.core.common.medal.Medal;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.permission.Tag;
import br.com.dragonmc.core.common.utils.string.MessageBuilder;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;

public class MedalCommand
implements CommandClass {
    @CommandFramework.Command(name="medalmanager", permission="command.medalmanager")
    public void medalManagerCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            this.handleMedalUsage(sender, cmdArgs.getLabel());
            return;
        }
        block5 : switch (args[0].toLowerCase()) {
            case "deletar": {
                if (args.length == 1) {
                    sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " create <medalName>\u00a7f para deletar uma medalha");
                    return;
                }
                Medal medal2 = CommonPlugin.getInstance().getPluginInfo().getMedalByName(args[0]);
                if (medal2 == null) {
                    sender.sendMessage("\u00a7cA medalha \"" + args[0] + "\" n\u00e3o existe.");
                    return;
                }
                if (sender.isPlayer()) {
                    new ConfirmInventory(cmdArgs.getSenderAsMember(BukkitMember.class).getPlayer(), "Confirme a a\u00e7\u00e3o.", bool -> {
                        if (bool) {
                            CommonPlugin.getInstance().getPluginInfo().getMedalMap().remove(medal2.getMedalName().toLowerCase());
                            sender.sendMessage("\u00a7aMedalha deletada com sucesso.");
                        }
                    }, null);
                    break;
                }
                CommonPlugin.getInstance().getPluginInfo().getMedalMap().remove(medal2.getMedalName().toLowerCase());
                sender.sendMessage("\u00a7aMedalha deletada com sucesso.");
                break;
            }
            case "list": {
                sender.sendMessage("  \u00a7aLista de medalha: ");
                for (Medal medal3 : CommonPlugin.getInstance().getPluginInfo().getMedalMap().values()) {
                    sender.sendMessage((BaseComponent)new MessageBuilder("    \u00a7f- " + medal3.getChatColor() + medal3.getMedalName() + "").setHoverEvent("" + medal3.getChatColor() + medal3.getMedalName() + "\n\n\u00a7eInfo:\n  \u00a7fS\u00edmbolo: \u00a77" + medal3.getChatColor() + medal3.getSymbol() + "\n  \u00a7fCor: \u00a77" + medal3.getChatColor() + StringFormat.formatString(medal3.getChatColor().name())).create());
                }
                break;
            }
            case "create": {
                if (args.length == 1) {
                    sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " create <medalName>\u00a7f para criar uma medalha.");
                    return;
                }
                String medalName = args[1];
                ChatManager.Callback confirm = (cancel, answers) -> {
                    String[] aliases;
                    if (cancel) {
                        sender.sendMessage("\u00a7cOperation cancelled.");
                        return;
                    }
                    String color = answers[0];
                    ChatColor chatColor = ChatColor.getByChar((char)(color.contains("&") ? color.charAt(1) : color.toCharArray()[0]));
                    if (chatColor == null) {
                        chatColor = ChatColor.valueOf((String)color.toUpperCase());
                    }
                    String symbol = "" + answers[1].toCharArray()[0];
                    String[] stringArray = aliases = answers[2].contains(", ") ? answers[2].split(", ") : answers[2].split(",");
                    if (CommonPlugin.getInstance().getPluginInfo().getMedalByName(medalName) == null) {
                        Medal medal = new Medal(medalName, symbol, chatColor.name().toUpperCase(), Arrays.asList(aliases));
                        CommonPlugin.getInstance().getPluginInfo().loadMedal(medal);
                        sender.sendMessage("\u00a7aA medalha " + medalName + " \u00a77(" + chatColor + symbol + "\u00a77) foi criada com sucesso.");
                    } else {
                        sender.sendMessage("\u00a7cA medalha inserida j\u00e1 existe.");
                    }
                };
                ChatManager.Validator validator = (message, index) -> {
                    switch (index) {
                        case 0: {
                            try {
                                return ChatColor.valueOf((String)message) != null;
                            }
                            catch (Exception ex) {
                                return (message.contains("&") ? ChatColor.getByChar((char)message.toCharArray()[1]) : ChatColor.getByChar((char)message.toCharArray()[0])) != null;
                            }
                        }
                        case 1: {
                            return true;
                        }
                        case 2: {
                            return !message.isEmpty() && message.length() > 3;
                        }
                    }
                    return true;
                };
                BukkitCommon.getInstance().getChatManager().loadChat(sender, confirm, validator, "\u00a7aPara criar uma medalha, digite qual ser\u00e1 a cor usada\n\u00a7aPaleta de cores dispon\u00edveis: " + Joiner.on((char)' ').join((Iterable)Arrays.asList(ChatColor.values()).stream().map(chatColor -> "" + chatColor + chatColor.getName()).collect(Collectors.toList())), "\u00a7aInsira qual ser\u00e1 o simbolo da medalha:", "\u00a7aInsira quais ser\u00e3o as aliases da medalha, utilizando v\u00edrgula para separar");
                break;
            }
            default: {
                Member member = CommonPlugin.getInstance().getMemberManager().getMemberByName(args[0]);
                if (member == null && (member = CommonPlugin.getInstance().getMemberData().loadMember(args[0], true)) == null) {
                    sender.sendMessage(sender.getLanguage().t("account-doesnt-exist", "%player%", args[0]));
                    return;
                }
                if (args.length == 1) {
                    if (member.getMedals().isEmpty()) {
                        sender.sendMessage("\u00a7cO jogador " + member.getPlayerName() + " n\u00e3o possui medalhas.");
                    } else {
                        sender.sendMessage("  \u00a7aMembro " + member.getPlayerName());
                    }
                    Member m = member;
                    for (Medal medal4 : CommonPlugin.getInstance().getPluginInfo().getMedalMap().values().stream().filter(medal -> m.hasMedal((Medal)medal)).collect(Collectors.toList())) {
                        sender.sendMessage((BaseComponent)new MessageBuilder("    \u00a7f- " + medal4.getChatColor() + medal4.getMedalName() + "").setHoverEvent("" + medal4.getChatColor() + medal4.getMedalName() + "\n\n  \u00a7eInfo:\n  \u00a7fS\u00edmbolo: \u00a77" + medal4.getChatColor() + medal4.getSymbol() + "\n  \u00a7fCor: \u00a77" + medal4.getChatColor() + StringFormat.formatString(medal4.getChatColor().name())).create());
                    }
                    return;
                }
                switch (args[1].toLowerCase()) {
                    case "add": {
                        if (args.length == 2) {
                            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <player> add <medal>\u00a7f para adicionar uma medalha a um jogador.");
                            return;
                        }
                        Medal medal5 = CommonPlugin.getInstance().getPluginInfo().getMedalByName(args[2]);
                        if (medal5 == null) {
                            sender.sendMessage("\u00a7cA medalha \"" + args[2] + "\" n\u00e3o existe.");
                            return;
                        }
                        if (member.addMedal(medal5)) {
                            sender.sendMessage("\u00a7aVoc\u00ea deu a medalha " + medal5.getChatColor() + medal5.getMedalName() + "\u00a7a para o " + member.getPlayerName() + ".");
                            break block5;
                        }
                        sender.sendMessage("\u00a7cO jogador " + member.getPlayerName() + " j\u00e1 possui a medalha " + medal5.getChatColor() + medal5.getMedalName() + "\u00a7c.");
                        break block5;
                    }
                    case "remove": {
                        if (args.length == 2) {
                            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <player> remove <medal>\u00a7f para remover uma medalha de um jogador.");
                            return;
                        }
                        Medal medal6 = CommonPlugin.getInstance().getPluginInfo().getMedalByName(args[2]);
                        if (medal6 == null) {
                            sender.sendMessage("\u00a7cA medalha \"" + args[2] + "\" n\u00e3o existe.");
                            return;
                        }
                        if (member.removeMedal(medal6)) {
                            sender.sendMessage("\u00a7aVoc\u00ea removeu a medalha " + medal6.getChatColor() + medal6.getMedalName() + "\u00a7a do " + member.getPlayerName() + ".");
                            break block5;
                        }
                        sender.sendMessage("\u00a7cO jogador " + member.getPlayerName() + " n\u00e3o possui a medalha " + medal6.getChatColor() + medal6.getMedalName() + "\u00a7c.");
                        break block5;
                    }
                }
                sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <player> remove <medal>\u00a7f para adicionar uma medalha a um jogador.");
                sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <player> add <medal>\u00a7f para remover uma medalha de um jogador.");
            }
        }
    }

    @CommandFramework.Command(name="medal", aliases={"medalhas", "medals", "emblemas"}, console=false)
    public void medalCommand(CommandArgs cmdArgs) {
        Member sender = cmdArgs.getSenderAsMember();
        Object[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            if (sender.getMedals().isEmpty()) {
                sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o possui nenhuma medalha.");
            } else {
                MessageBuilder messageBuilder = new MessageBuilder("\u00a7aSuas medalhas: ");
                messageBuilder.extra(new MessageBuilder("\u00a77Nenhum\u00a7f, ").setHoverEvent("\u00a7eRemover sua medalha.\n\n\u00a7aClique para selecionar.").setClickEvent(ClickEvent.Action.RUN_COMMAND, "/medal nenhum").create());
                List medals = CommonPlugin.getInstance().getPluginInfo().getMedalMap().values().stream().filter(medal -> sender.hasMedal((Medal)medal)).collect(Collectors.toList());
                int size = medals.size();
                for (int i = 0; i < size; ++i) {
                    Medal medal2 = (Medal)medals.get(i);
                    messageBuilder.extra(new MessageBuilder("" + medal2.getChatColor() + medal2.getSymbol() + (i == size - 1 ? "\u00a7f." : "\u00a7f,")).setHoverEvent("" + medal2.getChatColor() + medal2.getMedalName() + "\n\n\u00a7aClique para selecionar.").setClickEvent(ClickEvent.Action.RUN_COMMAND, "/medal " + medal2.getMedalName()).create());
                }
                sender.sendMessage((BaseComponent)messageBuilder.create());
            }
            return;
        }
        String medalName = Joiner.on((char)' ').join(args);
        if (medalName.equalsIgnoreCase("nenhum") || medalName.equalsIgnoreCase("nenhuma") || medalName.equalsIgnoreCase("remover")) {
            sender.setMedal(null);
            sender.sendMessage("\u00a7aSua medalha foi removida.");
            return;
        }
        Medal medal3 = CommonPlugin.getInstance().getPluginInfo().getMedalByName(medalName);
        if (medal3 == null) {
            sender.sendMessage("\u00a7cA medalha \"" + medalName + "\" n\u00e3o existe.");
            return;
        }
        if (!sender.hasMedal(medal3)) {
            sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o possui a medalha " + medal3.getChatColor() + medal3.getMedalName() + "\u00a7c.");
            return;
        }
        sender.setMedal(medal3);
        sender.sendMessage("\u00a7aSua medalha foi alterada para " + medal3.getChatColor() + medal3.getMedalName() + "\u00a7a.");
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

    private void handleMedalUsage(CommandSender sender, String label) {
        sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + label + " create <medalName>\u00a7f para criar uma medalha.");
        sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + label + " create <medalName>\u00a7f para deletar uma medalha.");
        sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + label + " list\u00a7f para listar as medalhas.");
        sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + label + " <player>\u00a7f para listar as medalhas de um jogador.");
        sender.sendMessage("");
        sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + label + " <player> add <medal>\u00a7f para adicionar uma medalha a um jogador.");
        sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + label + " <player> remove <medal>\u00a7f para adicionar uma medalha a um jogador.");
    }
}

