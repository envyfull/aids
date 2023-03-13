/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ChatColor
 *  net.md_5.bungee.api.ProxyServer
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.api.connection.ProxiedPlayer
 *  net.md_5.bungee.api.event.ChatEvent
 *  net.md_5.bungee.api.plugin.Event
 *  net.md_5.bungee.api.plugin.Listener
 *  net.md_5.bungee.event.EventHandler
 */
package br.com.dragonmc.core.bungee.listener;

import java.util.Arrays;
import java.util.UUID;

import br.com.dragonmc.core.bungee.BungeeMain;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bungee.event.player.PlayerCommandEvent;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.packet.types.StaffchatDiscordPacket;
import br.com.dragonmc.core.common.punish.Punish;
import br.com.dragonmc.core.common.punish.PunishType;
import br.com.dragonmc.core.common.utils.DateUtils;
import br.com.dragonmc.core.common.utils.mojang.UUIDParser;
import br.com.dragonmc.core.common.utils.string.MessageBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class MessageListener
implements Listener {
    @EventHandler(priority=127)
    public void onChatTeleport(ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer)) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer)event.getSender();
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(proxiedPlayer.getUniqueId());
        if (member == null) {
            event.setCancelled(true);
            proxiedPlayer.disconnect("\u00a7cSua conta n\u00e3o foi carregada. [BungeeCord: 01]");
            return;
        }
        boolean isCommand = event.isCommand();
        String message = event.getMessage();
        String[] split = message.trim().split(" ");
        String[] args = Arrays.copyOfRange(split, isCommand ? 1 : 0, split.length);
        String command = split[0].replace("/", "").toLowerCase();
        if (isCommand) {
            PlayerCommandEvent callEvent = (PlayerCommandEvent)ProxyServer.getInstance().getPluginManager().callEvent((Event)new PlayerCommandEvent(proxiedPlayer, command, args));
            event.setCancelled(callEvent.isCancelled());
            return;
        }
        if (member.getMemberConfiguration().isStaffChat()) {
            if (!member.getMemberConfiguration().isSeeingStaffChat()) {
                event.setCancelled(true);
                member.sendMessage("\u00a7cAtiva a visualiza\u00e7\u00e3o do staffchat para poder falar no staffchat.");
                return;
            }
            String staffMessage = this.getStaffchatMessage(member, ChatColor.translateAlternateColorCodes((char)'&', (String)message));
            CommonPlugin.getInstance().getMemberManager().getMembers().stream().filter(m -> m.isStaff() && m.getMemberConfiguration().isSeeingStaffChat()).forEach(m -> m.sendMessage(staffMessage));
            CommonPlugin.getInstance().getServerData().sendPacket(new StaffchatDiscordPacket(member.getUniqueId(), member.getServerGroup(), message));
            event.setCancelled(true);
            return;
        }
        if (event.getMessage().length() > 1) {
            if (event.getMessage().startsWith("%") && member.hasPermission("command.staffchat")) {
                if (!member.getMemberConfiguration().isSeeingStaffChat()) {
                    event.setCancelled(true);
                    member.sendMessage("\u00a7cAtiva a visualiza\u00e7\u00e3o do staffchat para poder falar no staffchat.");
                    return;
                }
                String staffMessage = this.getStaffchatMessage(member, event.getMessage().substring(1));
                CommonPlugin.getInstance().getMemberManager().getMembers().stream().filter(m -> m.isStaff() && m.getMemberConfiguration().isSeeingStaffChat()).forEach(m -> m.sendMessage(staffMessage));
                CommonPlugin.getInstance().getServerData().sendPacket(new StaffchatDiscordPacket(member.getUniqueId(), member.getServerGroup(), message));
                event.setCancelled(true);
            } else if (event.getMessage().startsWith("@") && member.getParty() != null) {
                Punish punish = member.getPunishConfiguration().getActualPunish(PunishType.MUTE);
                if (punish != null) {
                    member.sendMessage((BaseComponent)new MessageBuilder(punish.getMuteMessage(member.getLanguage())).setHoverEvent("\u00a7fPunido em: \u00a77" + CommonConst.DATE_FORMAT.format(punish.getCreatedAt()) + "\n\u00a7fExpire em: \u00a77" + (punish.isPermanent() ? "\u00a7cnunca" : DateUtils.getTime(member.getLanguage(), punish.getExpireAt()))).create());
                    event.setCancelled(true);
                    return;
                }
                member.getParty().chat(member, event.getMessage().substring(1));
                event.setCancelled(true);
            }
        }
    }

    private String getStaffchatMessage(Member member, String message) {
        return "\u00a7e[STAFF] " + CommonPlugin.getInstance().getPluginInfo().getTagByGroup(member.getServerGroup()).getStrippedColor() + " " + member.getPlayerName() + "\u00a77: \u00a7f" + ChatColor.translateAlternateColorCodes((char)'&', (String)message);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandEvent event) {
        ProxiedPlayer player = event.getPlayer();
        String label = event.getCommand();
        String[] args = event.getArgs();
        if (label.startsWith("teleport") || label.startsWith("tp")) {
            if (args.length == 1) {
                ProxiedPlayer target;
                UUID uniqueId = UUIDParser.parse(args[0]);
                ProxiedPlayer proxiedPlayer = target = uniqueId == null ? ProxyServer.getInstance().getPlayer(args[0]) : ProxyServer.getInstance().getPlayer(uniqueId);
                if (target == null || target.getServer() == null || target.getServer().getInfo() == null || target.getServer().getInfo().getName().equals(player.getServer().getInfo().getName())) {
                    return;
                }
                event.setCancelled(true);
                BungeeMain.getInstance().teleport(player, target);
                return;
            }
        } else {
            Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
            if (!member.getLoginConfiguration().isLogged() && !CommonConst.ALLOWED_COMMAND_LOGIN.contains(label)) {
                event.setCancelled(true);
                player.sendMessage(member.getLanguage().t("login.message.not-allowed", new String[0]));
            }
        }
    }
}

