/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 */
package br.com.dragonmc.lobby.login.listener;

import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.event.member.PlayerLanguageChangeEvent;
import br.com.dragonmc.core.bukkit.event.server.PlayerChangeEvent;
import br.com.dragonmc.core.bukkit.utils.scoreboard.ScoreHelper;
import br.com.dragonmc.core.bukkit.utils.scoreboard.Scoreboard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScoreboardListener
implements Listener {
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.handleScoreboard(event.getPlayer());
        this.updateScoreboard(event.getPlayer());
        this.updatePlayers();
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.updatePlayers();
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerChange(PlayerChangeEvent event) {
        this.updatePlayers();
    }

    @EventHandler
    public void onPlayerLanguageChange(PlayerLanguageChangeEvent event) {
        ScoreHelper.getInstance().removeScoreboard(event.getPlayer());
        this.handleScoreboard(event.getPlayer());
        this.updateScoreboard(event.getPlayer());
    }

    private void handleScoreboard(Player player) {
        Scoreboard scoreboard = new Scoreboard(player, "\u00a76\u00a7lLOGIN");
        scoreboard.add(7, "\u00a7e");
        scoreboard.add(6, "\u00a77Aguardando voc\u00ea");
        scoreboard.add(5, "\u00a77se autenticar.");
        scoreboard.add(4, "\u00a7e");
        scoreboard.add(3, "\u00a7f\u00a7%scoreboard-players%\u00a7: \u00a7a" + BukkitCommon.getInstance().getServerManager().getTotalMembers());
        scoreboard.add(2, "\u00a7e");
        scoreboard.add(1, "\u00a7ewww." + CommonPlugin.getInstance().getPluginInfo().getWebsite());
        ScoreHelper.getInstance().setScoreboard(player, scoreboard);
    }

    private void updateScoreboard(Player player) {
    }

    private void updatePlayers() {
        ScoreHelper.getInstance().updateScoreboard(3, "\u00a7f\u00a7%scoreboard-players%\u00a7: \u00a7a" + BukkitCommon.getInstance().getServerManager().getTotalMembers());
    }
}

