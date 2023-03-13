/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.plugin.Listener
 *  net.md_5.bungee.event.EventHandler
 */
package br.com.dragonmc.core.bungee.listener;

import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bungee.event.player.PlayerPardonedEvent;
import br.com.dragonmc.core.bungee.event.player.PlayerPunishEvent;
import br.com.dragonmc.core.common.command.CommandSender;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.punish.Punish;
import br.com.dragonmc.core.common.utils.DateUtils;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class LogListener
implements Listener {
    @EventHandler
    public void onPlayerPunish(PlayerPunishEvent event) {
        Member target = event.getPunished();
        CommandSender sender = event.getSender();
        Punish punish = event.getPunish();
        if (punish.getPunisherId().equals(CommonConst.CONSOLE_ID)) {
            CommonPlugin.getInstance().getMemberManager().getMembers().stream().filter(Member::isStaff).forEach(member -> member.sendMessage("\u00a7cO jogador " + target.getName() + " foi " + punish.getPunishType().getDescriminator() + " do servidor por " + punish.getPunishReason() + " pelo CONSOLE."));
            return;
        }
        switch (punish.getPunishType()) {
            case KICK: {
                CommonPlugin.getInstance().getMemberManager().staffLog("\u00a7cO jogador " + target.getPlayerName() + " foi kickado do servidor por " + punish.getPunishReason() + " pelo " + sender.getName() + ".", false);
                break;
            }
            default: {
                CommonPlugin.getInstance().getMemberManager().getMembers().stream().forEach(member -> {
                    if (member.isStaff()) {
                        member.sendMessage("\u00a7cO jogador " + target.getPlayerName() + " foi " + punish.getPunishType().getDescriminator().toLowerCase() + " " + (punish.isPermanent() ? "permanentemente" : " temporariamente com dura\u00e7\u00e3o de " + DateUtils.formatDifference(CommonPlugin.getInstance().getPluginInfo().getDefaultLanguage(), punish.getExpireTime() / 1000L)) + " por " + punish.getPunishReason() + " pelo " + sender.getName() + ".");
                    } else {
                        member.sendMessage("\u00a7cO jogador " + target.getName() + " foi banido " + (punish.isPermanent() ? "permanentemente" : "temporariamente") + " do servidor.");
                    }
                });
            }
        }
    }

    @EventHandler
    public void onPlayerPardoned(PlayerPardonedEvent event) {
        Member target = event.getPunished();
        CommandSender sender = event.getSender();
        Punish punish = event.getPunish();
        CommonPlugin.getInstance().getMemberManager().staffLog("\u00a7eO jogador " + target.getPlayerName() + " foi des" + punish.getPunishType().getDescriminator().toLowerCase() + " pelo " + sender.getName() + ".", false);
    }
}

