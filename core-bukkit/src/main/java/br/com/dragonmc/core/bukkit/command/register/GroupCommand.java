/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  net.md_5.bungee.api.chat.BaseComponent
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package br.com.dragonmc.core.bukkit.command.register;

import br.com.dragonmc.core.bukkit.manager.ChatManager;
import com.google.common.base.Joiner;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.OptionalInt;

import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.member.BukkitMember;
import br.com.dragonmc.core.bukkit.menu.group.MemberGroupListInventory;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.command.CommandClass;
import br.com.dragonmc.core.common.command.CommandFramework;
import br.com.dragonmc.core.common.command.CommandSender;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.permission.Group;
import br.com.dragonmc.core.common.permission.GroupInfo;
import br.com.dragonmc.core.common.permission.Tag;
import br.com.dragonmc.core.common.utils.DateUtils;
import br.com.dragonmc.core.common.utils.string.MessageBuilder;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import br.com.dragonmc.core.common.utils.supertype.OptionalBoolean;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class GroupCommand
implements CommandClass {
    @CommandFramework.Command(name="group", permission="command.group")
    public void groupCommand(final CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/group info\u00a7f para ver as informa\u00e7\u00f5es do seu grupo.");
            sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/group info <group>\u00a7f para ver as informa\u00e7\u00f5es do grupo.");
            sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/group playerlist <group>\u00a7f para ver as informa\u00e7\u00f5es do grupo.");
            sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/group list\u00a7f para listar os grupos.");
            sender.sendMessage("");
            sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/group <player> add <group>\u00a7f para adicionar um grupo a algu\u00e9m.");
            sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/group <player> remove <group>\u00a7f para remover um grupo de algu\u00e9m.");
            sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/group <player> set <group>\u00a7f para setar um grupo a algu\u00e9m.");
            sender.sendMessage("");
            sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/group create <groupName>\u00a7f para criar um grupo.");
            sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/group manager <groupName>\u00a7f para gerenciar um grupo.");
            sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/group delete <groupName>\u00a7f para deletar um grupo.");
            return;
        }
        block10 : switch (args[0].toString().toLowerCase()) {
            case "list": {
                Collection<Group> groupList = CommonPlugin.getInstance().getPluginInfo().getGroupMap().values();
                sender.sendMessage("  \u00a7aGrupos dispon\u00edveis:");
                for (Group group : groupList) {
                    sender.sendMessage((BaseComponent)new MessageBuilder("    \u00a7f- " + StringFormat.formatString(group.getGroupName())).setHoverEvent("\u00a7fName: \u00a77" + StringFormat.formatString(group.getGroupName()) + "\n" + (group.getPermissions().isEmpty() ? "" : "\n\u00a7fPermissions:\n  - \u00a77" + Joiner.on((String)"\n  - \u00a77").join(group.getPermissions()))).create());
                }
                break;
            }
            case "playerlist": {
                if (!sender.isPlayer()) {
                    sender.sendMessage("\u00a7cSomente jogadores podem executar esse comando.");
                    return;
                }
                if (!sender.hasPermission("command.group.playerlist")) {
                    sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o tem permiss\u00e3o para executar esse argumento.");
                    return;
                }
                Group g = null;
                if (args.length == 1) {
                    g = cmdArgs.getSenderAsMember().getServerGroup();
                } else {
                    g = CommonPlugin.getInstance().getPluginInfo().getGroupByName(args[1]);
                    if (g == null) {
                        sender.sendMessage(sender.getLanguage().t("group-not-found", new String[]{"%group%", args[1]}));
                        return;
                    }
                }
                final Group group = g;
                sender.sendMessage("\u00a7aAguarde, isso pode demorar um pouco.");
                new BukkitRunnable(){

                    public void run() {
                        final List<Member> memberList = CommonPlugin.getInstance().getMemberData().getMembersByGroup(group);
                        new BukkitRunnable(){

                            public void run() {
                                new MemberGroupListInventory(cmdArgs.getSenderAsMember(BukkitMember.class).getPlayer(), group, memberList);
                            }
                        }.runTask((Plugin)BukkitCommon.getInstance());
                    }
                }.runTaskAsynchronously((Plugin)BukkitCommon.getInstance());
                break;
            }
            case "info": {
                Group group = null;
                if (args.length == 1) {
                    group = cmdArgs.getSenderAsMember().getServerGroup();
                } else {
                    group = CommonPlugin.getInstance().getPluginInfo().getGroupByName(args[1]);
                    if (group == null) {
                        sender.sendMessage(sender.getLanguage().t("group-not-found", new String[]{"%group%", args[1]}));
                        return;
                    }
                }
                sender.sendMessage("  \u00a7aGrupo " + StringFormat.formatString(group.getGroupName()));
                sender.sendMessage("    \u00a7fID: \u00a77" + group.getId());
                sender.sendMessage("    \u00a7fPermiss\u00f5es:");
                for (String permission : group.getPermissions()) {
                    sender.sendMessage("      \u00a7f- \u00a77" + permission);
                }
                break;
            }
            case "permission": {
                if (!sender.hasPermission("command.group.create")) {
                    sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o tem permiss\u00e3o para alterar as permiss\u00f5es de um grupo.");
                    return;
                }
                Group group = null;
                if (args.length <= 2) {
                    group = cmdArgs.getSenderAsMember().getServerGroup();
                    sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " " + Joiner.on((char)' ').join(args) + " <add:remove> <permission>\u00a7f para adicionar ou remove uma permiss\u00e3o do grupo.");
                    sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " " + Joiner.on((char)' ').join(args) + " list\u00a7f para listar as permiss\u00f5es do grupo.");
                    return;
                }
                group = CommonPlugin.getInstance().getPluginInfo().getGroupByName(args[1]);
                if (group == null) {
                    sender.sendMessage(sender.getLanguage().t("group-not-found", new String[]{"%group%", args[1]}));
                    return;
                }
                if (args.length <= 3) {
                    if (args[2].equalsIgnoreCase("list")) {
                        sender.sendMessage("  \u00a7aGrupo " + StringFormat.formatString(group.getGroupName()));
                        sender.sendMessage("    \u00a7fPermiss\u00f5es:");
                        for (String permission : group.getPermissions()) {
                            sender.sendMessage("      \u00a7f- \u00a77" + permission);
                        }
                        return;
                    }
                    group = cmdArgs.getSenderAsMember().getServerGroup();
                    sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " " + Joiner.on((char)' ').join(args) + " <permission>\u00a7f para adicionar ou remove r");
                    return;
                }
                String permission = args[3].toLowerCase();
                boolean add = ((String)args[2]).equalsIgnoreCase("add");
                if (add) {
                    if (group.getPermissions().contains(permission)) {
                        sender.sendMessage("\u00a7cO grupo " + StringFormat.formatString(group.getGroupName()) + " j\u00e1 tem a permiss\u00e3o \"" + permission + "\".");
                        break;
                    }
                    group.getPermissions().add(permission);
                    CommonPlugin.getInstance().saveConfig("groupMap");
                    sender.sendMessage("\u00a7aPermiss\u00e3o \"" + permission + "\" foi adicionada ao grupo " + StringFormat.formatString(group.getGroupName()) + ".");
                    this.staffLog("O grupo " + StringFormat.formatString(group.getGroupName()) + " teve a permiss\u00e3o " + permission + " adicionadada.", true);
                    break;
                }
                if (!group.getPermissions().contains(permission)) {
                    sender.sendMessage("\u00a7cO grupo " + StringFormat.formatString(group.getGroupName()) + " n\u00e3o tem a permiss\u00e3o \"" + permission + "\".");
                    break;
                }
                group.getPermissions().remove(permission);
                sender.sendMessage("\u00a7aPermiss\u00e3o \"" + permission + "\" foi removida do grupo " + StringFormat.formatString(group.getGroupName()) + ".");
                CommonPlugin.getInstance().saveConfig("groupMap");
                this.staffLog("O grupo " + StringFormat.formatString(group.getGroupName()) + " teve a permiss\u00e3o " + permission + " removida.", true);
                break;
            }
            case "create": {
                if (!sender.hasPermission("command.group.create")) {
                    sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o tem permiss\u00e3o para criar grupo.");
                    return;
                }
                if (args.length == 1) {
                    sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/group create <group>\u00a7f para criar um grupo.");
                    break;
                }
                String groupName = args[1];
                ChatManager.Callback confirm = (cancel, answers) -> {
                    if (cancel) {
                        sender.sendMessage("\u00a7cOperation cancelled.");
                        return;
                    }
                    int id = StringFormat.parseInt(answers[0]).getAsInt();
                    boolean defaultGroup = StringFormat.parseBoolean(sender.getLanguage(), answers[1]).getAsBoolean();
                    boolean isStaff = StringFormat.parseBoolean(sender.getLanguage(), answers[2]).getAsBoolean();
                    Group group = new Group(id, groupName, new ArrayList<String>(), defaultGroup, isStaff);
                    if (defaultGroup) {
                        // empty if block
                    }
                    boolean sort = false;
                    if (CommonPlugin.getInstance().getPluginInfo().getGroupById(id) != null) {
                        CommonPlugin.getInstance().getPluginInfo().getGroupMap().values().stream().filter(g -> g.getId() >= id).forEach(g -> g.setId(g.getId() + 1));
                        sort = true;
                    }
                    CommonPlugin.getInstance().getPluginInfo().loadGroup(group);
                    CommonPlugin.getInstance().saveConfig();
                    sender.sendMessage("\u00a7aO grupo " + groupName + " foi criada.");
                    this.staffLog("O grupo " + StringFormat.formatString(group.getGroupName()) + " foi criado.", true);
                    if (sort) {
                        CommonPlugin.getInstance().getPluginInfo().sortGroup();
                    }
                };
                ChatManager.Validator validator = (message, index) -> {
                    switch (index) {
                        case 0: {
                            OptionalInt optionalInt = StringFormat.parseInt(message);
                            if (!optionalInt.isPresent()) {
                                sender.sendMessage(sender.getLanguage().t("number-format-invalid", "%number%", message));
                                return false;
                            }
                            return true;
                        }
                        case 1: 
                        case 2: {
                            OptionalBoolean optionalBool = StringFormat.parseBoolean(sender.getLanguage(), message);
                            if (optionalBool.isPresent()) break;
                            sender.sendMessage(sender.getLanguage().t("format-invalid", "%object%", message));
                            return false;
                        }
                    }
                    return true;
                };
                BukkitCommon.getInstance().getChatManager().loadChat(sender, confirm, validator, "\u00a7aInsira o id do grupo (n\u00famero).", "\u00a7aInsira se o grupo \u00e9 \"default\" (true ou false).", "\u00a7aInsira se o grupo \u00e9 \"staff\" (true ou false).");
                break;
            }
            case "createtag": {
                if (args.length == 1) {
                    sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/group createtag <group>\u00a7f para criar um grupo.");
                    break;
                }
                String tagName = args[1];
                ChatManager.Callback confirm = (cancel, answers) -> {
                    if (cancel) {
                        sender.sendMessage("\u00a7cOperation cancelled.");
                        return;
                    }
                    int id = StringFormat.parseInt(answers[0]).getAsInt();
                    String tagPrefix = answers[1].replace('&', '\u00a7');
                    ArrayList<String> aliases = answers[2].equalsIgnoreCase("nenhum") ? new ArrayList<String>() : (ArrayList<String>) Arrays.asList(answers[2].contains(", ") ? answers[2].split(", ") : answers[2].split(","));
                    boolean exclusive = StringFormat.parseBoolean(sender.getLanguage(), answers[3]).getAsBoolean();
                    boolean defaultTag = StringFormat.parseBoolean(sender.getLanguage(), answers[4]).getAsBoolean();
                    Tag tag = new Tag(id, tagName, tagPrefix, aliases, exclusive, defaultTag);
                    boolean sort = false;
                    if (CommonPlugin.getInstance().getPluginInfo().getTagById(id) != null) {
                        CommonPlugin.getInstance().getPluginInfo().getTagMap().values().stream().filter(t -> t.getTagId() >= id).forEach(t -> t.setTagId(t.getTagId() + 1));
                        sort = true;
                    }
                    CommonPlugin.getInstance().getPluginInfo().loadTag(tag);
                    CommonPlugin.getInstance().saveConfig();
                    sender.sendMessage("\u00a7aO tag " + tagName + " foi criada.");
                    this.staffLog("A tag " + StringFormat.formatString(tag.getTagName()) + " foi criada.", true);
                    if (sort) {
                        CommonPlugin.getInstance().getPluginInfo().sortGroup();
                    }
                };
                ChatManager.Validator validator = (message, index) -> {
                    switch (index) {
                        case 0: {
                            OptionalInt optionalInt = StringFormat.parseInt(message);
                            if (!optionalInt.isPresent()) {
                                sender.sendMessage(sender.getLanguage().t("number-format-invalid", "%number%", message));
                                return false;
                            }
                            return true;
                        }
                        case 3: 
                        case 4: {
                            OptionalBoolean optionalBool = StringFormat.parseBoolean(sender.getLanguage(), message);
                            if (optionalBool.isPresent()) break;
                            sender.sendMessage(sender.getLanguage().t("format-invalid", "%object%", message));
                            return false;
                        }
                    }
                    return true;
                };
                BukkitCommon.getInstance().getChatManager().loadChat(sender, confirm, validator, "\u00a7aInsira o id do tag (n\u00famero).", "\u00a7aInsira a tag do grupo sem espa\u00e7o e com cor usando o s\u00edmbolo &.", "\u00a7aInsira as aliases usando \",\" para separar.", "\u00a7aInsira se a tag \u00e9 exclusiva ou n\u00e3o (true ou false).", "\u00a7aInsira se a tag \u00e9 \"default\" ou n\u00e3o (true ou false).");
                break;
            }
            case "delete": 
            case "remove": {
                Group group = CommonPlugin.getInstance().getPluginInfo().getGroupByName(args[1]);
                if (group == null) {
                    sender.sendMessage(sender.getLanguage().t("group-not-found", "%group%", args[1]));
                    return;
                }
                CommonPlugin.getInstance().getPluginInfo().getGroupMap().remove(group.getGroupName().toLowerCase());
                sender.sendMessage(sender.getLanguage().t("command.group.deleted-group", "%groupName%", StringFormat.formatString(group.getGroupName())));
                this.staffLog("O grupo " + StringFormat.formatString(group.getGroupName()) + " foi deletado.", true);
                break;
            }
            default: {
                Member member = CommonPlugin.getInstance().getMemberManager().getMemberByName(args[0]);
                if (member == null && (member = CommonPlugin.getInstance().getMemberData().loadMember(args[0], true)) == null) {
                    sender.sendMessage(sender.getLanguage().t("account-doesnt-exist", new String[]{"%player%", args[0]}));
                    return;
                }
                Group actualGroup = member.getServerGroup();
                if (args.length <= 1) {
                    GroupInfo groupInfo = member.getServerGroup(actualGroup.getGroupName());
                    sender.sendMessage("  \u00a7aMembro " + member.getPlayerName());
                    sender.sendMessage((BaseComponent)new MessageBuilder("    \u00a7fGrupo: \u00a77" + StringFormat.formatString(actualGroup.getGroupName())).setHoverEvent("\u00a7aClique para ver informa\u00e7\u00f5es do grupo.").setClickEvent("/group info " + actualGroup.getGroupName().toLowerCase()).create());
                    sender.sendMessage("    \u00a7fExpire em: \u00a77" + (groupInfo.isPermanent() ? "Nunca" : DateUtils.getTime(sender.getLanguage(), groupInfo.getExpireTime())));
                    return;
                }
                Group group = CommonPlugin.getInstance().getPluginInfo().getGroupByName(args[2]);
                if (group == null) {
                    sender.sendMessage(sender.getLanguage().t("group-not-found", new String[]{"%group%", args[2]}));
                    return;
                }
                if (args.length == 2) {
                    sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/group " + member.getPlayerName() + " add <group>\u00a7f para adicionar um grupo a algu\u00e9m.");
                    sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/group " + member.getPlayerName() + " remove <group>\u00a7f para remover um grupo de algu\u00e9m.");
                    sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/group " + member.getPlayerName() + " set <group>\u00a7f para setar um grupo a algu\u00e9m.");
                    return;
                }
                switch (((String)args[1]).toLowerCase()) {
                    case "add": {
                        boolean temp = args.length >= 4;
                        long expireTime = temp ? DateUtils.getTime((String)args[3]) : -1L;
                        member.addServerGroup(group.getGroupName(), new GroupInfo(sender, expireTime));
                        member.setTag(member.getDefaultTag());
                        member.getMemberConfiguration().setStaffChat(false);
                        sender.sendMessage("\u00a7aVoc\u00ea adicionou o cargo " + group.getGroupName() + " ao jogador " + member.getPlayerName() + " por tempo " + (temp ? DateUtils.getTime(sender.getLanguage(), expireTime) : "indeterminado") + ".");
                        this.staffLog("O jogador " + member.getPlayerName() + " recebeu cargo " + group.getRealPrefix() + " \u00a77por " + (temp ? DateUtils.getTime(sender.getLanguage(), expireTime) : "indeterminado") + " do " + sender.getName(), true);
                        break block10;
                    }
                    case "remove": {
                        if (member.hasGroup(group.getGroupName())) {
                            member.removeServerGroup(group.getGroupName());
                            member.setTag(member.getDefaultTag());
                            member.getMemberConfiguration().setStaffChat(false);
                            sender.sendMessage("\u00a7aVoc\u00ea removeu o cargo " + group.getGroupName() + " do jogador " + member.getPlayerName() + ".");
                            this.staffLog("O jogador " + member.getPlayerName() + " teve o seu cargo " + group.getRealPrefix() + " \u00a77removido pelo " + sender.getName(), true);
                            break block10;
                        }
                        sender.sendMessage("\u00a7cO player " + member.getPlayerName() + " n\u00e3o tem o grupo " + group.getGroupName() + ".");
                        break block10;
                    }
                    case "set": {
                        member.setServerGroup(group.getGroupName(), new GroupInfo(sender, -1L));
                        member.setTag(member.getDefaultTag());
                        member.getMemberConfiguration().setStaffChat(false);
                        sender.sendMessage("\u00a7aVoc\u00ea adicionou o cargo " + group.getGroupName() + " ao jogador " + member.getPlayerName() + ".");
                        this.staffLog("O jogador " + member.getPlayerName() + " teve o cargo alterado para " + group.getRealPrefix() + " \u00a77pelo " + sender.getName(), true);
                        break block10;
                    }
                }
                sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/group <player> add <group>\u00a7f para adicionar um grupo a algu\u00e9m.");
                sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/group <player> remove <group>\u00a7f para remover um grupo de algu\u00e9m.");
                sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/group <player> set <group>\u00a7f para setar um grupo a algu\u00e9m.");
                break;
            }
        }
    }

    @CommandFramework.Completer(name="group")
    public List<String> groupCompleter(CommandArgs cmdArgs) {
        ArrayList<String> returnList;
        block20: {
            List<String> arguments;
            Player player;
            block21: {
                block19: {
                    returnList = new ArrayList<String>();
                    if (cmdArgs.getArgs().length != 1) break block19;
                    List<String> arguments2 = Arrays.asList("info", "list", "create", "manager", "delete");
                    if (cmdArgs.getArgs()[0].isEmpty()) {
                        for (String argument : arguments2) {
                            returnList.add(argument);
                        }
                    } else {
                        for (String argument : arguments2) {
                            if (!argument.toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())) continue;
                            returnList.add(argument);
                        }
                        for (Player player2 : Bukkit.getOnlinePlayers()) {
                            if (!player2.getName().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())) continue;
                            returnList.add(player2.getName());
                        }
                    }
                    break block20;
                }
                if (cmdArgs.getArgs().length != 2) break block21;
                if (cmdArgs.getArgs()[0].equalsIgnoreCase("info")) {
                    if (cmdArgs.getArgs()[1].isEmpty()) {
                        for (Group group : CommonPlugin.getInstance().getPluginInfo().getGroupMap().values()) {
                            returnList.add(group.getGroupName());
                        }
                    } else {
                        for (Group group : CommonPlugin.getInstance().getPluginInfo().getGroupMap().values()) {
                            if (!group.getGroupName().toLowerCase().startsWith(cmdArgs.getArgs()[1].toLowerCase())) continue;
                            returnList.add(group.getGroupName());
                        }
                    }
                } else {
                    List<String> arguments3 = Arrays.asList("add", "set", "remove");
                    if (cmdArgs.getArgs()[1].isEmpty()) {
                        for (String argument : arguments3) {
                            returnList.add(argument);
                        }
                    } else {
                        for (String argument : arguments3) {
                            if (!argument.toLowerCase().startsWith(cmdArgs.getArgs()[1].toLowerCase())) continue;
                            returnList.add(argument);
                        }
                    }
                }
                break block20;
            }
            if (cmdArgs.getArgs().length != 3 || (player = Bukkit.getPlayer((String)cmdArgs.getArgs()[0])) == null || !(arguments = Arrays.asList("add", "set", "remove")).contains(cmdArgs.getArgs()[1])) break block20;
            if (cmdArgs.getArgs()[2].isEmpty()) {
                for (Group group : CommonPlugin.getInstance().getPluginInfo().getGroupMap().values()) {
                    returnList.add(group.getGroupName());
                }
            } else {
                for (Group group : CommonPlugin.getInstance().getPluginInfo().getGroupMap().values()) {
                    if (!group.getGroupName().toLowerCase().startsWith(cmdArgs.getArgs()[2].toLowerCase())) continue;
                    returnList.add(group.getGroupName());
                }
            }
        }
        return returnList;
    }

    public void set(Group group, int id) throws Exception {
        Field field = Group.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(group, id);
    }
}

