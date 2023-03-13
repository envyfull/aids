/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package br.com.dragonmc.game.bedwars.listener;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.event.UpdateEvent;
import br.com.dragonmc.core.bukkit.event.member.PlayerLanguageChangeEvent;
import br.com.dragonmc.core.bukkit.event.player.PlayerAdminEvent;
import br.com.dragonmc.game.bedwars.GameMain;
import br.com.dragonmc.game.bedwars.event.PlayerSpectateEvent;
import br.com.dragonmc.game.bedwars.event.island.IslandBedBreakEvent;
import br.com.dragonmc.game.bedwars.event.island.IslandLoseEvent;
import br.com.dragonmc.game.bedwars.gamer.Gamer;
import br.com.dragonmc.game.bedwars.island.Island;
import br.com.dragonmc.game.engine.GameAPI;
import br.com.dragonmc.game.engine.event.GameStateChangeEvent;
import br.com.dragonmc.core.bukkit.utils.scoreboard.ScoreHelper;
import br.com.dragonmc.core.bukkit.utils.scoreboard.Scoreboard;
import br.com.dragonmc.core.common.server.loadbalancer.server.MinigameState;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardListener
implements Listener {
    private String text = "";

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.handleScoreboard(event.getPlayer());
        new BukkitRunnable(){

            public void run() {
                ScoreboardListener.this.updatePlayers(GameMain.getInstance().getAlivePlayers().size());
                if (GameAPI.getInstance().getState() == MinigameState.GAMETIME) {
                    ScoreboardListener.this.updateIsland(null);
                }
            }
        }.runTaskLater((Plugin)GameAPI.getInstance(), 7L);
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        new BukkitRunnable(){

            public void run() {
                ScoreboardListener.this.updatePlayers(GameMain.getInstance().getAlivePlayers().size());
                if (GameAPI.getInstance().getState() == MinigameState.GAMETIME) {
                    ScoreboardListener.this.updateIsland(null);
                }
            }
        }.runTaskLater((Plugin)GameAPI.getInstance(), 7L);
    }

    @EventHandler
    public void onPlayerLanguageChange(PlayerLanguageChangeEvent event) {
        this.handleScoreboard(event.getPlayer());
    }

    @EventHandler
    public void onPlayerSpectate(PlayerSpectateEvent event) {
        new BukkitRunnable(){

            public void run() {
                ScoreboardListener.this.updateIsland(null);
            }
        }.runTaskLater((Plugin)GameAPI.getInstance(), 7L);
    }

    @EventHandler
    public void onPlayerAdmin(PlayerAdminEvent event) {
        new BukkitRunnable(){

            public void run() {
                ScoreboardListener.this.updatePlayers(GameMain.getInstance().getAlivePlayers().size());
            }
        }.runTaskLater((Plugin)GameAPI.getInstance(), 7L);
    }

    @EventHandler
    public void onGameStateChange(GameStateChangeEvent event) {
        if (event.getState().isGametime()) {
            new BukkitRunnable(){

                public void run() {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if (player.getPlayer() != null) {
                            ScoreboardListener.this.handleScoreboard(player.getPlayer());
                        }
                    });
                }
            }.runTaskLater((Plugin)GameAPI.getInstance(), 7L);
        }
    }

    @EventHandler
    public void onIslandBreakEvent(IslandBedBreakEvent event) {
        this.updateScoreboard();
    }

    @EventHandler
    public void onIslandLoseEvent(IslandLoseEvent event) {
        this.updateScoreboard();
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (event.getType() == UpdateEvent.UpdateType.TICK && event.getCurrentTick() % 10L == 0L) {
            this.text = this.text.length() >= 3 ? "" : this.text + ".";
            if (!GameAPI.getInstance().isTimer()) {
                this.updateTimer();
                return;
            }
        } else if (event.getType() == UpdateEvent.UpdateType.SECOND && GameAPI.getInstance().isTimer()) {
            this.updateTimer();
        }
    }

    private void handleScoreboard(Player player) {
        Scoreboard scoreboard = new Scoreboard(player, "\u00a76\u00a7lBED WARS");
        if (GameAPI.getInstance().getState().isPregame()) {
            scoreboard.add(8, "\u00a7e");
            scoreboard.add(7, "\u00a7%scoreboard-map%\u00a7: \u00a77" + GameAPI.getInstance().getMapName());
            scoreboard.add(6, "\u00a7%scoreboard-players%\u00a7: \u00a77" + GameMain.getInstance().getAlivePlayers().size() + "/" + Bukkit.getMaxPlayers());
            scoreboard.add(5, "");
            scoreboard.add(4, "\u00a7%scoreboard-starting%\u00a7: \u00a77" + StringFormat.formatTime(GameAPI.getInstance().getTime(), StringFormat.TimeFormat.DOUBLE_DOT));
            scoreboard.add(3, "\u00a7%scoreboard-mode%\u00a7: \u00a77" + StringFormat.formatString(CommonPlugin.getInstance().getServerType().name().split("_")[1]));
            scoreboard.add(2, "");
            scoreboard.add(1, "\u00a7e" + CommonPlugin.getInstance().getPluginInfo().getWebsite());
        } else {
            if (GameMain.getInstance().getGeneratorUpgrade() != null && GameMain.getInstance().getGeneratorUpgrade().getTimer() - GameAPI.getInstance().getTime() > 0) {
                scoreboard.add(14, "\u00a7e");
                scoreboard.add(13, "\u00a7%scoreboard-" + GameMain.getInstance().getGeneratorUpgrade().getName().toLowerCase() + "-upgrade%\u00a7: \u00a77" + StringFormat.formatTime(GameMain.getInstance().getGeneratorUpgrade().getTimer() - GameAPI.getInstance().getTime(), StringFormat.TimeFormat.DOUBLE_DOT));
            }
            scoreboard.add(12, "");
            this.updateIsland(scoreboard);
            scoreboard.add(2, "");
            scoreboard.add(1, "\u00a7ewww." + CommonPlugin.getInstance().getPluginInfo().getWebsite());
        }
        ScoreHelper.getInstance().setScoreboard(player, scoreboard);
    }

    private void updateScoreboard() {
        this.updateIsland(null);
    }

    private void updateIsland(Scoreboard scoreboard) {
        List islandList = GameMain.getInstance().getIslandManager().values().stream().sorted((i1, i2) -> Character.valueOf(GameMain.CHARS[i1.getIslandColor().getColor().ordinal()]).compareTo(Character.valueOf(GameMain.CHARS[i2.getIslandColor().getColor().ordinal()]))).collect(Collectors.toList());
        for (int i = 0; i < islandList.size(); ++i) {
            Island island = (Island)islandList.get(i);
            String status = island.getIslandStatus() == Island.IslandStatus.ALIVE ? (island.stream(false).count() >= 1L ? "\u00a7a" : "\u00a7e") + "\u2714" : (island.getIslandStatus() == Island.IslandStatus.BED_BROKEN ? "\u00a7a" + island.getTeam().getPlayerSet().stream().filter(uuid -> !GameAPI.getInstance().getGamerManager().getGamer((UUID)uuid, Gamer.class).isSpectator()).count() : "\u00a7c\u2716");
            int index = this.getInitialIslandIndex() - i;
            String text = "" + island.getIslandColor().getColor() + ChatColor.BOLD + "\u00a7%" + island.getIslandColor().name().toLowerCase() + "-symbol%\u00a7 \u00a7f\u00a7%" + island.getIslandColor().name().toLowerCase() + "-name%\u00a7 " + status;
            if (scoreboard == null) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Island playerIsland = GameMain.getInstance().getIslandManager().getIsland(player.getUniqueId());
                    boolean sameIsland = playerIsland == island;
                    ScoreHelper.getInstance().addScoreboard(player, index, text + (sameIsland ? " \u00a77\u00a7%scoreboard-you%\u00a7" : ""));
                }
                continue;
            }
            boolean sameIsland = GameMain.getInstance().getIslandManager().getIsland(scoreboard.getPlayer().getUniqueId()) == island;
            scoreboard.add(index, text + (sameIsland ? " \u00a77\u00a7%scoreboard-you%\u00a7" : ""));
        }
    }

    private void updateTimer() {
        if (GameAPI.getInstance().getState().isPregame()) {
            ScoreHelper.getInstance().updateScoreboard(4, "\u00a7%scoreboard-starting%\u00a7: \u00a77" + StringFormat.formatTime(GameAPI.getInstance().getTime(), StringFormat.TimeFormat.SHORT));
        } else if (GameMain.getInstance().getGeneratorUpgrade() != null && GameMain.getInstance().getGeneratorUpgrade().getTimer() - GameAPI.getInstance().getTime() > 0) {
            ScoreHelper.getInstance().updateScoreboard(13, "\u00a7%scoreboard-" + GameMain.getInstance().getGeneratorUpgrade().getName().toLowerCase() + "-upgrade%\u00a7: \u00a77" + StringFormat.formatTime(GameMain.getInstance().getGeneratorUpgrade().getTimer() - GameAPI.getInstance().getTime(), StringFormat.TimeFormat.DOUBLE_DOT));
        } else {
            ScoreHelper.getInstance().removeScoreboard(14);
            ScoreHelper.getInstance().removeScoreboard(13);
        }
    }

    private void updatePlayers(int players) {
        if (GameAPI.getInstance().getState().isPregame()) {
            ScoreHelper.getInstance().updateScoreboard(6, "\u00a7%scoreboard-players%\u00a7: \u00a77" + players + "/" + Bukkit.getMaxPlayers());
        }
    }

    public int getInitialIslandIndex() {
        return 11;
    }
}

