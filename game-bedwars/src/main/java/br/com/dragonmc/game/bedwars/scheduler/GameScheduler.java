/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.player.PlayerItemConsumeEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.inventory.meta.PotionMeta
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scoreboard.NameTagVisibility
 *  org.bukkit.scoreboard.Team
 */
package br.com.dragonmc.game.bedwars.scheduler;

import br.com.dragonmc.game.bedwars.GameMain;
import br.com.dragonmc.game.bedwars.island.Island;
import br.com.dragonmc.game.bedwars.utils.GamerHelper;
import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.event.UpdateEvent;
import br.com.dragonmc.game.engine.GameAPI;
import br.com.dragonmc.game.bedwars.event.GameStartEvent;
import br.com.dragonmc.game.bedwars.event.PlayerSpectateEvent;
import br.com.dragonmc.game.bedwars.listener.CombatListener;
import br.com.dragonmc.game.bedwars.listener.DefenserListener;
import br.com.dragonmc.game.bedwars.listener.FireballListener;
import br.com.dragonmc.game.bedwars.listener.GameListener;
import br.com.dragonmc.game.bedwars.listener.IslandListener;
import br.com.dragonmc.game.bedwars.listener.SpectatorListener;
import br.com.dragonmc.game.bedwars.listener.StatusListener;
import br.com.dragonmc.game.bedwars.listener.UpgradeListener;
import br.com.dragonmc.game.engine.scheduler.Scheduler;
import br.com.dragonmc.core.bukkit.utils.scoreboard.ScoreboardAPI;
import br.com.dragonmc.core.common.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

public class GameScheduler
implements Listener,
Scheduler {
    private Map<UUID, Long> playerInvisibleMap;

    public GameScheduler() {
        GameAPI.getInstance().setUnloadGamer(false);
        GameAPI.getInstance().setTagControl(false);
        GameAPI.getInstance().setTime(0);
        this.playerInvisibleMap = new HashMap<UUID, Long>();
        Bukkit.getOnlinePlayers().forEach(player -> ScoreboardAPI.leaveCurrentTeamForOnlinePlayers(player));
        for (Island island : GameMain.getInstance().getIslandManager().loadIsland()) {
            island.startIsland();
            for (UUID uuid : island.getTeam().getPlayerSet()) {
                Player player2 = Bukkit.getPlayer((UUID)uuid);
                for (Player o : Bukkit.getOnlinePlayers()) {
                    ScoreboardAPI.joinTeam(ScoreboardAPI.createTeamIfNotExistsToPlayer(o, GameMain.getInstance().getId(island), GameMain.getInstance().getTag(island, Language.getLanguage(o.getUniqueId())), ""), player2);
                }
                GamerHelper.setPlayerProtection(player2, 5);
            }
        }
        GameAPI.getInstance().getVanishManager().getPlayersInAdmin().forEach(id -> {
            Player player = Bukkit.getPlayer((UUID)id);
            if (player != null) {
                this.loadTags(player);
            }
        });
        Bukkit.getPluginManager().registerEvents((Listener)new CombatListener(), (Plugin)GameAPI.getInstance());
        Bukkit.getPluginManager().registerEvents((Listener)new GameListener(), (Plugin)GameAPI.getInstance());
        Bukkit.getPluginManager().registerEvents((Listener)new FireballListener(), (Plugin)GameAPI.getInstance());
        Bukkit.getPluginManager().registerEvents((Listener)new IslandListener(), (Plugin)GameAPI.getInstance());
        Bukkit.getPluginManager().registerEvents((Listener)new DefenserListener(), (Plugin)GameAPI.getInstance());
        Bukkit.getPluginManager().registerEvents((Listener)new SpectatorListener(), (Plugin)GameAPI.getInstance());
        Bukkit.getPluginManager().registerEvents((Listener)new StatusListener(), (Plugin)GameAPI.getInstance());
        Bukkit.getPluginManager().registerEvents((Listener)new UpgradeListener(), (Plugin)GameAPI.getInstance());
        Bukkit.getPluginManager().callEvent((Event)new GameStartEvent());
        Bukkit.getWorlds().stream().filter(world -> world.getName().equals("spawn")).findFirst().ifPresent(world -> Bukkit.unloadWorld((World)world, (boolean)false));
        GameMain.getInstance().getGeneratorManager().startGenerators();
    }

    @EventHandler
    public void onPlayerSpectate(PlayerSpectateEvent event) {
        Player player = event.getPlayer();
        this.loadTags(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.loadTags(player);
    }

    private void loadTags(Player player) {
        Island playerIsland = GameMain.getInstance().getIslandManager().getIsland(player.getUniqueId());
        for (Player online : Bukkit.getOnlinePlayers()) {
            Island island = GameMain.getInstance().getIslandManager().getIsland(online.getUniqueId());
            ScoreboardAPI.joinTeam(ScoreboardAPI.createTeamIfNotExistsToPlayer(player, island == null || island.getIslandStatus() == Island.IslandStatus.LOSER ? "z" : GameMain.getInstance().getId(island), island == null || island.getIslandStatus() == Island.IslandStatus.LOSER ? "\u00a78" : GameMain.getInstance().getTag(island, Language.getLanguage(player.getUniqueId())), ""), online);
            ScoreboardAPI.joinTeam(ScoreboardAPI.createTeamIfNotExistsToPlayer(online, playerIsland == null || playerIsland.getIslandStatus() == Island.IslandStatus.LOSER ? "z" : GameMain.getInstance().getId(playerIsland), playerIsland == null || playerIsland.getIslandStatus() == Island.IslandStatus.LOSER ? "\u00a78" : GameMain.getInstance().getTag(playerIsland, Language.getLanguage(player.getUniqueId())), ""), player);
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.POTION) {
            final Player player = event.getPlayer();
            PotionMeta potionMeta = (PotionMeta)event.getItem().getItemMeta();
            Island island = GameMain.getInstance().getIslandManager().getIsland(player.getUniqueId());
            new BukkitRunnable(){

                public void run() {
                    player.getInventory().remove(Material.GLASS_BOTTLE);
                }
            }.runTaskLater((Plugin)GameAPI.getInstance(), 3L);
            if (island == null) {
                return;
            }
            if (potionMeta.hasCustomEffect(PotionEffectType.INVISIBILITY)) {
                potionMeta.getCustomEffects().stream().filter(potion -> potion.getType().getId() == PotionEffectType.INVISIBILITY.getId()).findFirst().ifPresent(potionEffect -> {
                    player.removeMetadata("invencibility", (Plugin)GameAPI.getInstance());
                    int duration = potionEffect.getDuration();
                    if (this.registerInvisibleTeam(player, island)) {
                        GamerHelper.handleRemoveArmor(player);
                    }
                    this.playerInvisibleMap.put(player.getUniqueId(), System.currentTimeMillis() + (long)(duration / 20 * 1000));
                });
            }
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (event.getType() == UpdateEvent.UpdateType.SECOND) {
            for (Map.Entry entry : ImmutableList.copyOf(this.playerInvisibleMap.entrySet())) {
                Player player = Bukkit.getPlayer((UUID)((UUID)entry.getKey()));
                if (player == null) {
                    this.playerInvisibleMap.remove(entry.getKey());
                    continue;
                }
                if ((Long)entry.getValue() >= System.currentTimeMillis()) continue;
                if (player.isOnline() && this.unregisterInvisibleTeam(player, GameMain.getInstance().getIslandManager().getIsland(player.getUniqueId()))) {
                    GamerHelper.handleArmor(player);
                }
                this.playerInvisibleMap.remove(entry.getKey());
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        final Player player = (Player)event.getEntity();
        if (player.hasMetadata("invencibility")) {
            if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                player.getActivePotionEffects().stream().filter(potion -> potion.getType().getId() == PotionEffectType.INVISIBILITY.getId()).findFirst().ifPresent(potion -> {
                    int duration = potion.getDuration();
                    final Island island = GameMain.getInstance().getIslandManager().getIsland(player.getUniqueId());
                    if (this.unregisterInvisibleTeam(player, island)) {
                        GamerHelper.handleArmor(player);
                        if (duration - 100 > 0) {
                            new BukkitRunnable(){

                                public void run() {
                                    if (GameScheduler.this.registerInvisibleTeam(player, island)) {
                                        GamerHelper.handleRemoveArmor(player);
                                    }
                                }
                            }.runTaskLater((Plugin)GameAPI.getInstance(), 80L);
                        }
                    }
                });
            } else {
                player.removeMetadata("invencibility", (Plugin)GameAPI.getInstance());
            }
        }
    }

    private boolean unregisterInvisibleTeam(Player player, Island island) {
        if (player.hasMetadata("invencibility")) {
            String teamId = GameMain.getInstance().getId(island);
            String teamTag = GameMain.getInstance().getTag(island, Language.getLanguage(player.getUniqueId()));
            for (Player o : Bukkit.getOnlinePlayers()) {
                ScoreboardAPI.leaveTeam(o, teamId + "i", player);
                ScoreboardAPI.joinTeam(ScoreboardAPI.createTeamIfNotExistsToPlayer(o, teamId, teamTag, ""), player);
            }
            player.removeMetadata("invencibility", (Plugin)GameAPI.getInstance());
            return true;
        }
        return false;
    }

    private boolean registerInvisibleTeam(Player player, Island island) {
        if (!player.hasMetadata("invencibility")) {
            String teamId = GameMain.getInstance().getId(island);
            String teamTag = GameMain.getInstance().getTag(island, Language.getLanguage(player.getUniqueId()));
            for (Player o : Bukkit.getOnlinePlayers()) {
                Team createTeamIfNotExistsToPlayer = ScoreboardAPI.createTeamIfNotExistsToPlayer(o, teamId + "i", teamTag, "");
                ScoreboardAPI.joinTeam(createTeamIfNotExistsToPlayer, player);
                createTeamIfNotExistsToPlayer.setNameTagVisibility(NameTagVisibility.NEVER);
            }
            player.setMetadata("invencibility", GameAPI.getInstance().createMeta(true));
            return true;
        }
        return false;
    }

    @Override
    public void pulse() {
        int time = GameAPI.getInstance().getTime();
        if (GameMain.getInstance().getGeneratorUpgrade() != null && GameMain.getInstance().getGeneratorUpgrade().getTimer() - GameAPI.getInstance().getTime() - 1 == 0) {
            GameMain.getInstance().getGeneratorUpgrade().getConsumer().accept(null);
        }
        if (time >= 2100) {
            Bukkit.broadcastMessage((String)"\u00a7cNenhum time ganhou a partida.");
            Bukkit.getOnlinePlayers().forEach(player -> GameMain.getInstance().sendPlayerToServer((Player)player, CommonPlugin.getInstance().getServerId()));
            Bukkit.shutdown();
        }
    }
}

