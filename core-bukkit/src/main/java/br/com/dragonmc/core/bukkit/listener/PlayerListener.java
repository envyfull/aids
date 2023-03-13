/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Achievement
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scoreboard.Objective
 *  org.bukkit.scoreboard.Scoreboard
 *  org.bukkit.scoreboard.Team
 */
package br.com.dragonmc.core.bukkit.listener;

import java.io.File;
import java.util.UUID;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.event.member.PlayerGroupChangeEvent;
import br.com.dragonmc.core.bukkit.event.server.ServerPacketReceiveEvent;
import br.com.dragonmc.core.bukkit.utils.player.PlayerHelper;
import br.com.dragonmc.core.bukkit.utils.scoreboard.ScoreHelper;
import br.com.dragonmc.core.common.packet.types.ActionBar;
import br.com.dragonmc.core.common.permission.Group;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PlayerListener
implements Listener {
    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        event.getPlayer().awardAchievement(Achievement.OPEN_INVENTORY);
        event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    @EventHandler
    public void onServerPacket(ServerPacketReceiveEvent event) {
        ActionBar actionBar;
        Player player;
        if (event.getPacket() instanceof ActionBar && (player = Bukkit.getPlayer((UUID)(actionBar = (ActionBar)event.getPacket()).getUniqueId())) != null) {
            PlayerHelper.actionbar(player, actionBar.getText());
        }
    }

    @EventHandler
    public void onPlayerGroupChange(PlayerGroupChangeEvent event) {
        Group group = event.getGroup();
        if (group == null) {
            PlayerHelper.title(event.getPlayer(), event.getMember().getDefaultTag().getRealPrefix(), "\u00a7fseu grupo foi atualizado.", 15, 60, 15);
            return;
        }
        String strippedColor = CommonPlugin.getInstance().getPluginInfo().getTagByGroup(group).getStrippedColor();
        switch (event.getAction()) {
            case ADD: {
                PlayerHelper.title(event.getPlayer(), strippedColor, "\u00a7ffoi adicionado a voc\u00ea.", 15, 60, 15);
                break;
            }
            case REMOVE: {
                PlayerHelper.title(event.getPlayer(), strippedColor, "\u00a7ffoi removido de voc\u00ea.", 15, 60, 15);
                break;
            }
            case SET: {
                PlayerHelper.title(event.getPlayer(), strippedColor, "\u00a7fvoc\u00ea se tornou.", 15, 60, 15);
                break;
            }
            default: {
                PlayerHelper.title(event.getPlayer(), strippedColor, "\u00a7fseu grupo foi atualizado", 15, 60, 15);
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerQuitListener(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        ScoreHelper.getInstance().removeScoreboard(event.getPlayer());
        Scoreboard board = event.getPlayer().getScoreboard();
        if (board != null) {
            for (Team t : board.getTeams()) {
                t.unregister();
            }
            for (Objective ob : board.getObjectives()) {
                ob.unregister();
            }
        }
        event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        this.removePlayerFile(event.getPlayer().getUniqueId());
    }

    private void removePlayerFile(UUID uuid) {
        World world;
        File folder;
        if (BukkitCommon.getInstance().isRemovePlayerDat() && (folder = new File((world = (World)Bukkit.getWorlds().get(0)).getWorldFolder(), "playerdata")).exists() && folder.isDirectory()) {
            File file = new File(folder, uuid.toString() + ".dat");
            Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin)BukkitCommon.getInstance(), () -> {
                if (file.exists() && !file.delete()) {
                    this.removePlayerFile(uuid);
                }
            }, 2L);
        }
    }
}

