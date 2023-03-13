/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.server.PluginDisableEvent
 */
package br.com.dragonmc.core.bukkit.listener.member;

import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.event.member.PlayerChangedTagEvent;
import br.com.dragonmc.core.bukkit.event.member.PlayerLanguageChangeEvent;
import br.com.dragonmc.core.bukkit.member.BukkitMember;
import br.com.dragonmc.core.bukkit.utils.player.PlayerHelper;
import br.com.dragonmc.core.bukkit.utils.scoreboard.ScoreboardAPI;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.permission.Tag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

public class TagListener
implements Listener {
    private static char[] chars = "abcdefghijklmnopqrstuv".toCharArray();

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (BukkitCommon.getInstance().isTagControl()) {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                ScoreboardAPI.leaveCurrentTeamForOnlinePlayers(p);
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        ScoreboardAPI.leaveCurrentTeamForOnlinePlayers(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLanguageChange(PlayerLanguageChangeEvent event) {
        if (!BukkitCommon.getInstance().isTagControl()) {
            return;
        }
        Player player = event.getPlayer();
        for (Player o : Bukkit.getOnlinePlayers()) {
            Member bp = CommonPlugin.getInstance().getMemberManager().getMember(o.getUniqueId());
            Tag tag = bp.getTag();
            String id = TagListener.getTeamName(tag);
            String prefix = PlayerHelper.translate(Language.getLanguage(player.getUniqueId()), tag.getRealPrefix());
            ScoreboardAPI.setTeamPrefixAndSuffix(ScoreboardAPI.createTeamIfNotExistsToPlayer(player, id, prefix, ""), prefix, "");
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerChangeTag(PlayerChangedTagEvent event) {
        if (!BukkitCommon.getInstance().isTagControl()) {
            return;
        }
        Player p = event.getPlayer();
        BukkitMember player = (BukkitMember)event.getMember();
        if (player == null) {
            return;
        }
        String id = TagListener.getTeamName(event.getNewTag());
        String oldId = TagListener.getTeamName(event.getOldTag());
        String tag = PlayerHelper.translate(Language.getLanguage(player.getUniqueId()), event.getNewTag().getRealPrefix());
        for (Player o : Bukkit.getOnlinePlayers()) {
            ScoreboardAPI.leaveTeamToPlayer(o, oldId, p);
            ScoreboardAPI.joinTeam(ScoreboardAPI.createTeamIfNotExistsToPlayer(o, id, tag, ""), p);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!BukkitCommon.getInstance().isTagControl()) {
            return;
        }
        Player p = e.getPlayer();
        BukkitMember player = CommonPlugin.getInstance().getMemberManager().getMember(e.getPlayer().getUniqueId(), BukkitMember.class);
        Tag tag = player.getTag();
        String id = TagListener.getTeamName(tag);
        for (Player o : Bukkit.getOnlinePlayers()) {
            ScoreboardAPI.joinTeam(ScoreboardAPI.createTeamIfNotExistsToPlayer(o, id, PlayerHelper.translate(Language.getLanguage(o.getUniqueId()), tag.getRealPrefix()), ""), p);
        }
        for (Player o : Bukkit.getOnlinePlayers()) {
            if (o.getUniqueId().equals(p.getUniqueId())) continue;
            BukkitMember bp = (BukkitMember)CommonPlugin.getInstance().getMemberManager().getMember(o.getUniqueId());
            if (bp == null) {
                o.kickPlayer("\u00a7cSua conta n\u00e3o foi carregada.");
                return;
            }
            tag = bp.getTag();
            id = TagListener.getTeamName(tag);
            ScoreboardAPI.joinTeam(ScoreboardAPI.createTeamIfNotExistsToPlayer(p, id, PlayerHelper.translate(player.getLanguage(), tag.getRealPrefix()), ""), o);
        }
    }

    public static String getTeamName(Tag tag) {
        String p = chars[tag.getTagId()] + "";
        return p;
    }
}

