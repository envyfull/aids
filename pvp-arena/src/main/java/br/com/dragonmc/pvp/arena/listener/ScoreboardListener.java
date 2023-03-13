/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 */
package br.com.dragonmc.pvp.arena.listener;

import br.com.dragonmc.core.bukkit.utils.scoreboard.ScoreHelper;
import br.com.dragonmc.core.bukkit.utils.scoreboard.Scoreboard;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.pvp.arena.GameMain;
import br.com.dragonmc.pvp.arena.event.PlayerSelectedKitEvent;
import br.com.dragonmc.pvp.arena.gamer.Gamer;
import br.com.dragonmc.pvp.core.event.PlayerProtectionEvent;
import br.com.dragonmc.pvp.core.event.StatusChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class ScoreboardListener
implements Listener {
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onStatusChange(StatusChangeEvent event) {
        this.updateScoreboard(event.getPlayer());
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.handleScoreboard(event.getPlayer());
        this.updateScoreboard(event.getPlayer());
    }

    @EventHandler
    public void onGamerChange(PlayerSelectedKitEvent event) {
        this.updateScoreboard(event.getPlayer());
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        this.updateScoreboard(event.getEntity());
        if (event.getEntity().getKiller() != null) {
            this.updateScoreboard(event.getEntity().getKiller());
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerProtection(PlayerProtectionEvent event) {
        this.updateScoreboard(event.getPlayer());
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerSelectedKit(PlayerSelectedKitEvent event) {
        this.updateScoreboard(event.getPlayer());
    }

    private void handleScoreboard(Player player) {
        Scoreboard scoreboard = new Scoreboard(player, "\u00a76\u00a7lARENA");
        scoreboard.add(9, "\u00a7e");
        scoreboard.add(8, "\u00a7fKills: \u00a770");
        scoreboard.add(7, "\u00a7fDeaths: \u00a770");
        scoreboard.add(6, "\u00a7fKillstreak: \u00a770");
        scoreboard.add(5, "\u00a7e");
        scoreboard.add(4, "\u00a7fKit 1: \u00a7aNenhum");
        scoreboard.add(3, "\u00a7fKit 2: \u00a7aNenhum");
        scoreboard.add(2, "\u00a7e");
        scoreboard.add(1, "\u00a7e" + CommonPlugin.getInstance().getPluginInfo().getWebsite());
        ScoreHelper.getInstance().setScoreboard(player, scoreboard);
    }

    private void updateScoreboard(Player player) {
        ScoreHelper.getInstance().updateScoreboard(player, 8, "\u00a7fKills: \u00a770");
        ScoreHelper.getInstance().updateScoreboard(player, 7, "\u00a7fDeaths: \u00a770");
        ScoreHelper.getInstance().updateScoreboard(player, 6, "\u00a7fKillstreak: \u00a770");
        Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);
        ScoreHelper.getInstance().updateScoreboard(player, 4, "\u00a7fKit 1: \u00a7a" + gamer.getPrimary());
        ScoreHelper.getInstance().updateScoreboard(player, 3, "\u00a7fKit 2: \u00a7a" + gamer.getSecondary());
    }
}

