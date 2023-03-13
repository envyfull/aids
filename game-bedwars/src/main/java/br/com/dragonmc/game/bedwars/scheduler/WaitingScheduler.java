/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.player.PlayerBucketEmptyEvent
 *  org.bukkit.event.player.PlayerDropItemEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package br.com.dragonmc.game.bedwars.scheduler;

import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.event.player.PlayerAdminEvent;
import br.com.dragonmc.core.bukkit.event.player.PlayerMoveUpdateEvent;
import br.com.dragonmc.game.bedwars.GameMain;
import br.com.dragonmc.game.engine.GameAPI;
import br.com.dragonmc.game.bedwars.gamer.Gamer;
import br.com.dragonmc.game.engine.scheduler.Scheduler;
import br.com.dragonmc.core.bukkit.member.BukkitMember;
import br.com.dragonmc.core.bukkit.utils.item.ActionItemStack;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.player.PlayerHelper;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.server.ServerType;
import br.com.dragonmc.core.common.server.loadbalancer.server.MinigameState;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WaitingScheduler
implements Scheduler,
Listener {
    private static final ActionItemStack ACTION_ITEM_STACK = new ActionItemStack(new ItemBuilder().name("\u00a7aRetornar ao Lobby").type(Material.BED).build(), new ActionItemStack.Interact(){

        @Override
        public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionItemStack.ActionType action) {
            GameAPI.getInstance().sendPlayerToServer(player, CommonPlugin.getInstance().getServerType().getServerLobby(), ServerType.LOBBY);
            return false;
        }
    });
    private boolean timeWasReduced;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
        player.teleport(GameAPI.getInstance().getLocationManager().getLocation("spawn"));
        player.setHealth(20.0);
        player.setMaxHealth(20.0);
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setExp(0.0f);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getActivePotionEffects().forEach(potion -> player.removePotionEffect(potion.getType()));
        player.getInventory().setItem(8, ACTION_ITEM_STACK.getItemStack());
        gamer.setAlive(true);
        gamer.setSpectator(false);
        if (player.hasPermission("command.admin") && member.getMemberConfiguration().isAdminOnJoin()) {
            player.setMetadata("admin", (MetadataValue)new FixedMetadataValue((Plugin)GameAPI.getInstance(), (Object)true));
        } else {
            this.broadcast(member.getTag().getRealPrefix(), event.getPlayer().getName(), false);
        }
    }

    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Member member = CommonPlugin.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());
        if (!GameAPI.getInstance().getVanishManager().isPlayerInAdmin(event.getPlayer())) {
            new BukkitRunnable(){

                public void run() {
                    WaitingScheduler.this.broadcast(member.getTag().getRealPrefix(), event.getPlayer().getName(), true);
                }
            }.runTaskLater((Plugin)GameAPI.getInstance(), 7L);
        }
        if (event.getPlayer().hasMetadata("admin")) {
            event.getPlayer().removeMetadata("admin", (Plugin)GameAPI.getInstance());
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerAdmin(PlayerAdminEvent event) {
        Player player = event.getPlayer();
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
        if (player.hasMetadata("admin")) {
            player.removeMetadata("admin", (Plugin)GameAPI.getInstance());
        } else if (event.getAdminMode() == PlayerAdminEvent.AdminMode.ADMIN) {
            this.broadcast(member.getTag().getRealPrefix(), event.getPlayer().getName(), true);
        } else {
            this.broadcast(member.getTag().getRealPrefix(), event.getPlayer().getName(), false);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(!CommonPlugin.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId(), BukkitMember.class).isBuildEnabled());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(!CommonPlugin.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId(), BukkitMember.class).isBuildEnabled());
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        event.setCancelled(!CommonPlugin.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId(), BukkitMember.class).isBuildEnabled());
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        event.setCancelled(!CommonPlugin.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId(), BukkitMember.class).isBuildEnabled());
    }

    @EventHandler
    public void onPlayerMoveUpdate(PlayerMoveUpdateEvent event) {
        if (event.getTo().getY() < 10.0) {
            event.getPlayer().teleport(GameAPI.getInstance().getLocationManager().getLocation("spawn"));
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void pulse() {
        int time = GameAPI.getInstance().getTime();
        String s = (time <= 3) ? "§4" : ((time < 5) ? "§c" : ((time < 30) ? "§e" : "§a"));
        if (GameAPI.getInstance().isConsoleControl()) {
            int alivePlayers = GameMain.getInstance().getAlivePlayers().size();
            if (alivePlayers < Bukkit.getMaxPlayers() / 2) {
                GameAPI.getInstance().setTimer(false);
                GameAPI.getInstance().setTime(60);
                GameAPI.getInstance().setState(MinigameState.WAITING);
                return;
            }
            if (GameAPI.getInstance().isTimer()) {
                if (!this.timeWasReduced && alivePlayers == Bukkit.getMaxPlayers() && time > 15) {
                    GameAPI.getInstance().setTime(10);
                    Bukkit.broadcastMessage((String)"\u00a7eTempo alterado para \u00a7b10 segundos\u00a7e pois a sala est\u00e1 lotada.");
                    this.timeWasReduced = true;
                }
            } else {
                GameAPI.getInstance().setTimer(true);
                GameAPI.getInstance().setState(MinigameState.STARTING);
            }
        }
        String string = time <= 3 ? "\u00a74" : (time < 5 ? "\u00a7c" : (s = time < 30 ? "\u00a7e" : "\u00a7a"));
        if (time > 0 && (time <= 5 || time % 30 == 0 || time == 15)) {
            String finalS = s;
            Bukkit.getOnlinePlayers().forEach(player -> {
                PlayerHelper.title(player, finalS + StringFormat.formatTime(Language.getLanguage(player.getUniqueId()), time), " ");
                player.sendMessage(Language.getLanguage(player.getUniqueId()).t("bedwars-game-will-start", "%time%", finalS + time));
                player.playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
            });
        }
        if (time <= 0) {
            GameMain.getInstance().startGame();
            Bukkit.broadcastMessage((String)"\u00a7eO jogo iniciou.");
        }
    }

    private void broadcast(String tag, String playerName, boolean leave) {
        if (leave) {
            Bukkit.broadcastMessage((String)(tag + playerName + " \u00a7esaiu na sala (\u00a7b" + GameMain.getInstance().getAlivePlayers().size() + "\u00a7e/\u00a7b" + Bukkit.getMaxPlayers() + "\u00a7e)"));
        } else {
            Bukkit.broadcastMessage((String)(tag + playerName + " \u00a7eentrou na sala (\u00a7b" + GameMain.getInstance().getAlivePlayers().size() + "\u00a7e/\u00a7b" + Bukkit.getMaxPlayers() + "\u00a7e)"));
        }
    }
}

