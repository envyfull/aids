/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.ProtocolLibrary
 *  com.google.common.collect.ImmutableList
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Egg
 *  org.bukkit.entity.Fireball
 *  org.bukkit.entity.IronGolem
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockPhysicsEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDeathEvent
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.entity.ProjectileLaunchEvent
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.event.inventory.InventoryType$SlotType
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 *  org.bukkit.event.player.PlayerBedEnterEvent
 *  org.bukkit.event.player.PlayerBucketEmptyEvent
 *  org.bukkit.event.player.PlayerBucketFillEvent
 *  org.bukkit.event.player.PlayerDropItemEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerPickupItemEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.scheduler.BukkitRunnable
 */
package br.com.dragonmc.game.bedwars.listener;

import br.com.dragonmc.game.bedwars.GameMain;
import br.com.dragonmc.game.bedwars.event.PlayerKillPlayerEvent;
import br.com.dragonmc.game.bedwars.event.island.IslandLoseEvent;
import br.com.dragonmc.game.bedwars.gamer.Gamer;
import br.com.dragonmc.game.bedwars.generator.Generator;
import br.com.dragonmc.game.bedwars.island.Island;
import br.com.dragonmc.game.bedwars.island.IslandColor;
import br.com.dragonmc.game.bedwars.island.IslandUpgrade;
import br.com.dragonmc.game.bedwars.utils.GamerHelper;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.event.UpdateEvent;
import br.com.dragonmc.core.bukkit.event.player.PlayerAdminEvent;
import br.com.dragonmc.core.bukkit.event.player.PlayerMoveUpdateEvent;
import br.com.dragonmc.game.engine.GameAPI;
import br.com.dragonmc.game.engine.event.GamerLoadEvent;
import br.com.dragonmc.core.bukkit.utils.PacketBuilder;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuHolder;
import br.com.dragonmc.core.bukkit.utils.player.PlayerHelper;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.member.status.Status;
import br.com.dragonmc.core.common.member.status.StatusType;
import br.com.dragonmc.core.common.member.status.types.BedwarsCategory;
import br.com.dragonmc.core.common.packet.types.staff.Stafflog;
import br.com.dragonmc.core.common.server.loadbalancer.server.MinigameState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class GameListener
implements Listener {
    private List<Material> dropableItems = Arrays.asList(Material.DIAMOND, Material.EMERALD, Material.GOLD_INGOT, Material.POTION);
    private Map<UUID, Long> playerJoinMap = new HashMap<UUID, Long>();

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Egg) || !(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        final Egg egg = (Egg)event.getEntity();
        Player player = (Player)egg.getShooter();
        final Island island = GameMain.getInstance().getIslandManager().getIsland(player.getUniqueId());
        if (island == null) {
            return;
        }
        final int distance = 40;
        final Location startLocation = egg.getLocation().clone();
        List<Block> lineOfSight = player.getLineOfSight((Set)null, distance);
        for (Block block : lineOfSight) {
            Block highestBlockAt = block.getWorld().getHighestBlockAt(block.getLocation());
            if (this.isPlaceable(highestBlockAt.getLocation())) continue;
            player.getInventory().addItem(new ItemStack[]{ItemBuilder.fromStack(player.getItemInHand()).amount(1).build()});
            player.sendMessage("\u00a7cVoc\u00ea n\u00e3o pode jogar o ovo nessa dire\u00e7\u00e3o.");
            event.setCancelled(true);
            break;
        }
        new BukkitRunnable(){

            public void run() {
                Location eggLocation = egg.getLocation().subtract(0.0, 2.5, 0.0);
                if (eggLocation.getY() <= 70.0) {
                    egg.remove();
                }
                if (egg.isDead() || GameListener.this.distanceSquared(eggLocation, startLocation, distance)) {
                    this.cancel();
                    return;
                }
                if (eggLocation.getBlock().getType() == Material.AIR) {
                    final Location location = eggLocation.getBlock().getLocation().add(0.5, 0.0, 0.5);
                    new BukkitRunnable(){

                        public void run() {
                            for (int x = -1; x < 1; ++x) {
                                for (int z = -1; z < 1; ++z) {
                                    Location newLocation = location.clone().add((double)x, 0.0, (double)z);
                                    if (newLocation.getBlock().getType() != Material.AIR) continue;
                                    GameMain.getInstance().getBlockManager().setBlockFast(newLocation, Material.WOOL, (byte)island.getIslandColor().getWoolId(), place -> GameMain.getInstance().getPlayersBlock().add(place));
                                }
                            }
                        }
                    }.runTaskLater((Plugin)GameAPI.getInstance(), 5L);
                }
            }
        }.runTaskTimer((Plugin)GameAPI.getInstance(), 5L, 0L);
    }

    @EventHandler
    public void onGamerLoad(GamerLoadEvent event) {
        Player player = event.getPlayer();
        if (this.playerJoinMap.containsKey(player.getUniqueId())) {
            return;
        }
        Island island = this.getIsland(player.getUniqueId());
        if (island == null && !player.hasPermission("command.admin")) {
            event.setCancelled(true);
            event.setReason("\u00a7cO jogo j\u00e1 iniciou!");
            return;
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        String message;
        Player player = event.getPlayer();
        Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);
        if (gamer == null) {
            return;
        }
        Island island = this.getIsland(player.getUniqueId());
        if (island == null) {
            return;
        }
        event.setCancelled(true);
        if (island.getIslandStatus() != Island.IslandStatus.LOSER) {
            boolean solo;
            boolean global = solo = CommonPlugin.getInstance().getServerType().name().contains("SOLO");
            Status status = GameAPI.getInstance().getPlugin().getStatusManager().loadStatus(player.getUniqueId(), StatusType.BEDWARS);
            int level = status.getInteger(BedwarsCategory.BEDWARS_LEVEL);
            message = GameMain.getInstance().createMessage(player, event.getMessage(), island, global, global ? !solo : true, level);
            if (!global) {
                event.getRecipients().removeIf(p -> !island.getTeam().getPlayerSet().contains(p.getUniqueId()));
            }
        } else {
            event.getRecipients().removeIf(p -> !GameMain.getInstance().hasLose(p.getUniqueId()));
            message = "\u00a77[ESPECTADORES] " + player.getName() + ": " + event.getMessage();
        }
        event.getRecipients().forEach(ps -> ps.sendMessage(message));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof MenuHolder) {
            return;
        }
        Player player = (Player)event.getPlayer();
        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);
        if (gamer == null || !gamer.isAlive()) {
            return;
        }
        Inventory inventory = event.getInventory();
        PlayerInventory playerInventory = player.getInventory();
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null || itemStack.getType() != Material.WOOD_SWORD && itemStack.getType() != Material.SHEARS && itemStack.getType() != gamer.getAxeLevel().getItemStack().getType() && itemStack.getType() != gamer.getPickaxeLevel().getItemStack().getType()) continue;
            inventory.remove(itemStack);
            playerInventory.addItem(new ItemStack[]{itemStack});
        }
        int swordCount = this.getSwordCount((Inventory)playerInventory);
        int woodSwordCount = this.getItemCount(player, Material.WOOD_SWORD);
        if (this.getSwordCount((Inventory)playerInventory) == 0) {
            playerInventory.addItem(new ItemStack[]{new ItemBuilder().type(Material.WOOD_SWORD).enchantment(Enchantment.DAMAGE_ALL, GameMain.getInstance().getIslandManager().getIsland(player.getUniqueId()).getUpgradeLevel(IslandUpgrade.SHARPNESS)).build()});
        }
        if (swordCount != woodSwordCount) {
            for (ItemStack itemStack : playerInventory.getContents()) {
                if (itemStack == null || itemStack.getType() != Material.WOOD_SWORD) continue;
                playerInventory.removeItem(new ItemStack[]{itemStack});
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.CRAFTING || event.getSlotType() == InventoryType.SlotType.ARMOR) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (player.getAllowFlight()) {
            event.setCancelled(true);
            return;
        }
        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);
        ItemStack itemStack = event.getItemDrop().getItemStack();
        if (itemStack.getType().name().contains("SWORD")) {
            if (itemStack.getType() == Material.WOOD_SWORD) {
                event.setCancelled(true);
            } else if (this.getSwordCount(player) == 0) {
                player.getInventory().setItemInHand(new ItemBuilder().type(Material.WOOD_SWORD).enchantment(Enchantment.DAMAGE_ALL, GameMain.getInstance().getIslandManager().getIsland(player.getUniqueId()).getUpgradeLevel(IslandUpgrade.SHARPNESS)).build());
                gamer.setSwordLevel(Gamer.SwordLevel.WOOD);
            }
            return;
        }
        if (itemStack.getType().name().contains("HELMET") || itemStack.getType().name().contains("CHESTPLATE") || itemStack.getType().name().contains("LEGGINGS") || itemStack.getType().name().contains("BOOTS") || itemStack.getType().name().contains("AXE") || itemStack.getType().name().contains("PICKAXE") || itemStack.getType().name().contains("SHEARS")) {
            event.setCancelled(true);
            return;
        }
        if (gamer.isAlive() && (player.getFallDistance() > 2.0f || player.getLocation().getY() < 20.0)) {
            event.setCancelled(true);
            player.sendMessage(Language.getLanguage(player.getUniqueId()).t("bedwars.drop-item.void-drop", new String[0]));
            return;
        }
        if (this.dropableItems.contains(event.getItemDrop().getItemStack().getType())) {
            List<Block> lineOfSight = player.getLineOfSight((Set)null, 5);
            for (Block block : lineOfSight) {
                Block highestBlockAt = block.getWorld().getHighestBlockAt(block.getLocation());
                if (highestBlockAt != null && highestBlockAt.getLocation().getY() != 0.0) continue;
                event.setCancelled(true);
                player.sendMessage(Language.getLanguage(player.getUniqueId()).t("bedwars.drop-item.no-block-to-drop", new String[0]));
                return;
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);
        if (!gamer.isAlive()) {
            event.setCancelled(true);
            return;
        }
        if (event.getItem().getItemStack().getType().toString().contains("SWORD") && this.getItemCount(player, Material.WOOD_SWORD) == 1) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null || item.getType() != Material.WOOD_SWORD) continue;
                player.getInventory().removeItem(new ItemStack[]{item});
                break;
            }
            gamer.setSwordLevel(Gamer.SwordLevel.valueOf(event.getItem().getItemStack().getType().name().replace("_SWORD", "")));
            return;
        }
        Item item = event.getItem();
        ItemStack itemStack = item.getItemStack();
        Collection<Player> playerList = GameMain.getInstance().getAlivePlayers().stream().filter(g -> g.isAlive() && g.getPlayer() != null && g.getPlayer().getLocation().distance(item.getLocation()) <= 3.0).map(br.com.dragonmc.game.engine.gamer.Gamer::getPlayer).collect(Collectors.toList());
        if (playerList.size() > 1 && itemStack.getAmount() > playerList.size()) {
            int itemPerPlayer = itemStack.getAmount() / playerList.size();
            int remeaning = itemStack.getAmount() - itemPerPlayer * playerList.size();
            for (Player target : playerList) {
                target.getInventory().addItem(new ItemStack[]{ItemBuilder.fromStack(itemStack).amount(itemPerPlayer).build()});
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(target, new PacketBuilder(PacketType.Play.Server.COLLECT).writeInteger(0, item.getEntityId()).writeInteger(1, target.getEntityId()).build());
                }
                catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            item.remove();
            event.setCancelled(true);
            if (remeaning > 0) {
                player.getInventory().addItem(new ItemStack[]{ItemBuilder.fromStack(itemStack).amount(remeaning).build()});
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);
            if (!gamer.isAlive()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof IronGolem) {
            event.setDamage(6.0);
        }
        if (event.getEntity() instanceof Fireball) {
            event.setCancelled(true);
            return;
        }
        if (event.getEntity() instanceof Player) {
            Projectile projectile;
            Player player = (Player)event.getEntity();
            Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);
            if (!gamer.isAlive()) {
                event.setCancelled(true);
                return;
            }
            for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                itemStack.setDurability((short)0);
            }
            Player damager = null;
            if (event.getDamager() instanceof Player) {
                damager = (Player)event.getDamager();
            } else if (event.getDamager() instanceof Projectile && (projectile = (Projectile)event.getDamager()).getShooter() instanceof Player) {
                damager = (Player)projectile.getShooter();
            }
            if (!(damager instanceof Player)) {
                return;
            }
            Gamer damagerGamer = GameAPI.getInstance().getGamerManager().getGamer(damager.getUniqueId(), Gamer.class);
            if (!damagerGamer.isAlive()) {
                event.setCancelled(true);
                return;
            }
            Island island = this.getIsland(player.getUniqueId());
            Island islandDamager = this.getIsland(damager.getUniqueId());
            if (island.getIslandColor() == islandDamager.getIslandColor()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerMoveUpdate(PlayerMoveUpdateEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);
        Island island = this.getIsland(player.getUniqueId());
        if (gamer.isAlive() && event.getTo().getY() <= GameMain.getInstance().getMinimunY()) {
            boolean finalKill = island.getIslandStatus() != Island.IslandStatus.ALIVE;
            this.handleDeath(gamer, player);
            if (island.getIslandStatus() == Island.IslandStatus.LOSER) {
                this.spectate(player);
                return;
            }
            if (finalKill) {
                island.checkLose();
                this.spectate(player);
            } else {
                this.respawn(player);
            }
        }
        if (player.hasMetadata("player-target")) {
            if (gamer.isAlive()) {
                MetadataValue orElse = player.getMetadata("player-target").stream().findFirst().orElse(null);
                UUID uuid = UUID.fromString(orElse.asString());
                Player target = Bukkit.getPlayer((UUID)uuid);
                if (target == null) {
                    return;
                }
                PlayerHelper.actionbar(player, "\u00a7aRastreando: \u00a7f" + target.getName() + " \u00a77(" + CommonConst.DECIMAL_FORMAT.format(target.getLocation().distance(player.getLocation())) + " blocos)");
                player.setCompassTarget(target.getLocation());
            } else {
                player.removeMetadata("player-target", (Plugin)GameAPI.getInstance());
            }
        }
    }

    @EventHandler
    public void onIslandLose(IslandLoseEvent event) {
        GameMain.getInstance().checkWinner();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        Player player = event.getEntity();
        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);
        if (gamer.isSpectator()) {
            return;
        }
        if (gamer.isAlive()) {
            boolean finalKill;
            boolean bl = finalKill = this.getIsland(player.getUniqueId()).getIslandStatus() == Island.IslandStatus.BED_BROKEN;
            if (player.getKiller() instanceof Player) {
                Player killer = player.getKiller();
                for (ItemStack itemStack : event.getDrops()) {
                    if (!this.dropableItems.contains(itemStack.getType())) continue;
                    killer.getInventory().addItem(new ItemStack[]{itemStack});
                }
                Bukkit.getPluginManager().callEvent((Event)new PlayerKillPlayerEvent(player, killer, finalKill));
            }
            this.handleDeath(gamer, player);
            event.getDrops().clear();
            event.setDroppedExp(0);
            this.getIsland(player.getUniqueId()).checkLose();
            player.removeMetadata("player-armor", (Plugin)GameAPI.getInstance());
            if (finalKill) {
                this.spectate(player);
            } else {
                this.respawn(player);
            }
        }
    }

    private void handleDeath(Gamer gamer, Player player) {
        boolean finalKill = this.getIsland(player.getUniqueId()).getIslandStatus() == Island.IslandStatus.BED_BROKEN;
        player.setHealth(20.0);
        player.setMaxHealth(20.0);
        player.setLevel(0);
        player.setExp(0.0f);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        gamer.setAlive(false);
        gamer.setSwordLevel(Gamer.SwordLevel.WOOD);
        if (player.getKiller() instanceof Player) {
            Bukkit.getPluginManager().callEvent((Event)new PlayerKillPlayerEvent(player, player.getKiller(), finalKill));
        }
        if (gamer.getPickaxeLevel().ordinal() >= Gamer.PickaxeLevel.values()[2].ordinal()) {
            gamer.setPickaxeLevel(gamer.getPickaxeLevel().getPrevious());
        }
        if (gamer.getAxeLevel().ordinal() >= Gamer.AxeLevel.values()[2].ordinal()) {
            gamer.setAxeLevel(gamer.getAxeLevel().getPrevious());
        }
        this.broadcastDeath(player, player.getKiller(), finalKill);
    }

    public void broadcastDeath(Player player, Player killer, boolean finalKill) {
        Island island = this.getIsland(player.getUniqueId());
        StringBuilder stringBuilder = new StringBuilder();
        if (killer == null) {
            stringBuilder.append("\u00a77" + island.getIslandColor().getColor() + player.getName() + " \u00a77foi morto.");
        } else {
            Island killerIsland = this.getIsland(killer.getUniqueId());
            stringBuilder.append("\u00a77" + island.getIslandColor().getColor() + player.getName() + " \u00a77foi morto por " + killerIsland.getIslandColor().getColor() + killer.getName() + "\u00a77.");
        }
        if (finalKill) {
            stringBuilder.append(" ").append("\u00a7b\u00a7lFINAL KILL");
        }
        Bukkit.broadcastMessage((String)stringBuilder.toString().trim());
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (this.playerJoinMap.containsKey(player.getUniqueId())) {
            this.playerJoinMap.remove(player.getUniqueId());
        }
        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);
        Island island = this.getIsland(player.getUniqueId());
        if (island != null && island.getIslandStatus() == Island.IslandStatus.ALIVE && !gamer.isSpectator()) {
            this.respawn(player);
            return;
        }
        this.spectate(player);
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);
        Island island = this.getIsland(player.getUniqueId());
        if (island != null && GameAPI.getInstance().getState() == MinigameState.GAMETIME) {
            if (gamer.isAlive()) {
                if (island.getIslandStatus() == Island.IslandStatus.BED_BROKEN) {
                    this.broadcastDeath(player, null, true);
                    gamer.setSpectator(true);
                } else {
                    this.playerJoinMap.put(player.getUniqueId(), System.currentTimeMillis());
                    Bukkit.broadcastMessage((String)("\u00a77" + island.getIslandColor().getColor() + player.getName() + " \u00a77desconectou."));
                }
            }
            island.checkLose();
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (event.getType() == UpdateEvent.UpdateType.SECOND) {
            for (Map.Entry next : ImmutableList.copyOf(this.playerJoinMap.entrySet())) {
                if ((Long)next.getValue() + 45000L >= System.currentTimeMillis()) continue;
                this.playerJoinMap.remove(next.getKey());
                Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer((UUID)next.getKey(), Gamer.class);
                gamer.setAlive(false);
                gamer.setSpectator(true);
                Island island = this.getIsland(gamer.getUniqueId());
                if (island != null) {
                    if (island.getIslandStatus() == Island.IslandStatus.ALIVE && island.getTeam().getPlayerSet().stream().map(id -> GameAPI.getInstance().getGamerManager().getGamer((UUID)id, Gamer.class)).filter(g -> g.isAlive()).count() == 0L) {
                        island.handleBreakBed(null);
                    }
                    island.checkLose();
                }
                Bukkit.broadcastMessage((String)("\u00a77" + island.getIslandColor().getColor() + gamer.getPlayerName() + " \u00a77foi morto. \u00a7b\u00a7lFINAL KILL"));
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(event.getPlayer().getUniqueId(), Gamer.class);
        if (!gamer.isAlive()) {
            event.setCancelled(true);
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block.getType() == Material.FURNACE || block.getType() == Material.ANVIL || block.getType() == Material.ENCHANTMENT_TABLE || block.getType() == Material.WORKBENCH) {
            event.setCancelled(true);
            return;
        }
        if (block.getType() != Material.CHEST) {
            return;
        }
        Island playerIsland = this.getIsland(event.getPlayer().getUniqueId());
        if (playerIsland == null) {
            event.setCancelled(true);
            return;
        }
        if (block.hasMetadata("chest-island")) {
            MetadataValue metadataValue = block.getMetadata("chest-island").stream().findFirst().orElse(null);
            Island island = this.getIsland((IslandColor)((Object)metadataValue.value()));
            if (playerIsland.getIslandColor() != island.getIslandColor() && island.getIslandStatus() == Island.IslandStatus.ALIVE) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("\u00a7cVoc\u00ea n\u00e3o pode abrir o ba\u00fa do inimigo enquanto ele ainda estiver vivo.");
            }
        }
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getAllowFlight()) {
            event.setCancelled(true);
            return;
        }
        Island playerIsland = this.getIsland(player.getUniqueId());
        if (playerIsland == null) {
            event.setCancelled(true);
            return;
        }
        if (event.getBlock().getType() == Material.BED_BLOCK) {
            double distance = player.getLocation().distance(event.getBlock().getLocation());
            if (distance > 12.0) {
                CommonPlugin.getInstance().getServerData().sendPacket(new Stafflog("O jogador " + player.getName() + " quebrou uma cama a " + distance + " blocos de distancia"));
                event.setCancelled(true);
                return;
            }
            if (event.getBlock().hasMetadata("bed-island")) {
                MetadataValue metadataValue = event.getBlock().getMetadata("bed-island").stream().findFirst().orElse(null);
                Island island = GameMain.getInstance().getIslandManager().getIsland((IslandColor)((Object)metadataValue.value()));
                if (playerIsland.getIslandColor() != island.getIslandColor()) {
                    island.handleBreakBed(event.getPlayer());
                    for (Location location : GameMain.getInstance().getNearestBlocksByMaterial(event.getBlock().getLocation(), Material.BED_BLOCK, 2)) {
                        location.getBlock().setType(Material.AIR);
                    }
                    GameAPI.getInstance().getGamerManager().getGamer(event.getPlayer().getUniqueId(), Gamer.class).addBedBroken();
                } else {
                    event.setCancelled(true);
                }
            }
            return;
        }
        if (GameMain.getInstance().getPlayersBlock().contains(event.getBlock().getLocation())) {
            GameMain.getInstance().getPlayersBlock().remove(event.getBlock().getLocation());
        } else {
            event.setCancelled(true);
            player.sendMessage(Language.getLanguage(player.getUniqueId()).t("bedwars-cant-break-this-block", new String[0]));
        }
        if (player.getItemInHand() != null && player.getItemInHand().getType().name().contains("AXE")) {
            player.getItemInHand().setDurability((short)0);
            player.updateInventory();
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (!this.isPlaceable(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Language.getLanguage(event.getPlayer().getUniqueId()).t("bedwars.block-place.can-not-place-here", new String[0]));
            return;
        }
        if (event.getBlock().getType() == Material.SPONGE) {
            new BukkitRunnable(){

                public void run() {
                    event.getBlock().setType(Material.AIR);
                }
            }.runTaskLater((Plugin)GameAPI.getInstance(), 3L);
            return;
        }
        if (event.getBlock().getType() == Material.CHEST) {
            event.setCancelled(true);
            int amount = Math.max(event.getItemInHand().getAmount() - 1, 0);
            if (amount == 0) {
                event.getPlayer().setItemInHand(null);
            } else {
                event.getItemInHand().setAmount(amount);
            }
            if (GameMain.getInstance().getTowerSchematic() == null) {
                event.getPlayer().sendMessage("\u00a7cN\u00e3o foi poss\u00edvel spawnar a constru\u00e7\u00e3o.");
                return;
            }
            new BukkitRunnable(){

                public void run() {
                    BukkitCommon.getInstance().getBlockManager().spawn(event.getBlock().getLocation(), GameMain.getInstance().getTowerSchematic());
                }
            }.runTaskLater((Plugin)GameAPI.getInstance(), 3L);
            return;
        }
        GameMain.getInstance().getPlayersBlock().add(event.getBlock().getLocation());
    }

    public boolean distanceSquared(Location locatioTo, Location locationFrom, double radius) {
        double distZ;
        Location spawnLocation = locationFrom;
        double distX = locatioTo.getX() - spawnLocation.getX();
        double distance = distX * distX + (distZ = locatioTo.getZ() - spawnLocation.getZ()) * distZ;
        return distance > radius * radius;
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (event.getBlock().getType() == Material.BED_BLOCK) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onPlayerAdmin(PlayerAdminEvent event) {
        Island island = this.getIsland(event.getPlayer().getUniqueId());
        if (island != null) {
            island.checkLose();
        }
        GameMain.getInstance().checkWinner();
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            event.setDroppedExp(0);
            event.getDrops().clear();
        }
    }

    private void spectate(Player player) {
        if (player.getLocation().getY() <= 20.0) {
            player.teleport(BukkitCommon.getInstance().getLocationManager().getLocation("central"));
        }
        GamerHelper.handleSpectate(player);
        GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class).setSpectator(true);
        GameAPI.getInstance().getVanishManager().updateVanishToPlayer(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 254));
    }

    private void respawn(final Player player) {
        final Language language = Language.getLanguage(player.getUniqueId());
        final Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);
        final Island island = this.getIsland(player.getUniqueId());
        if (player.getLocation().getY() < 20.0) {
            player.teleport(GameAPI.getInstance().getLocationManager().getLocation("central"));
        } else {
            player.teleport(player.getLocation().add(0.0, 2.5, 0.0));
        }
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setMetadata("invencibility", GameAPI.getInstance().createMeta(System.currentTimeMillis() + 5000L));
        player.getActivePotionEffects().forEach(potion -> player.removePotionEffect(potion.getType()));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 140, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 254));
        GamerHelper.hidePlayer(player);
        new BukkitRunnable(){
            int time = 5;

            public void run() {
                if (player == null || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                if (this.time == 0) {
                    GameMain.getInstance().getVanishManager().showPlayer(player);
                    player.setFallDistance(-1.0f);
                    player.teleport(island.getSpawnLocation().getAsLocation());
                    gamer.setAlive(true);
                    GamerHelper.handlePlayerToGame(player);
                    PlayerHelper.title(player, language.t("bedwars-title-respawn", new String[0]), language.t("bedwars-subtitle-respawn", new String[0]), 10, 20, 10);
                    player.sendMessage(language.t("bedwars.message.respawn", new String[0]));
                    GamerHelper.setPlayerProtection(player, 5);
                    this.cancel();
                } else {
                    PlayerHelper.title(player, language.t("bedwars-title-you-are-dead", new String[0]), language.t("bedwars-subtitle-you-are-dead", "%time%", "" + this.time), 0, 20, 20);
                    player.sendMessage(language.t("bedwars.message.respawning", "%time%", "" + this.time));
                }
                --this.time;
            }
        }.runTaskTimer((Plugin)GameAPI.getInstance(), 20L, 20L);
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event) {
        new BukkitRunnable(){

            public void run() {
                event.getPlayer().getInventory().remove(Material.BUCKET);
            }
        }.runTaskLater((Plugin)GameAPI.getInstance(), 3L);
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerBucketEmpty(PlayerBucketFillEvent event) {
        event.setCancelled(true);
    }

    private int getSwordCount(Player player) {
        return this.getSwordCount((Inventory)player.getInventory());
    }

    private int getSwordCount(Inventory playerInventory) {
        int count = 0;
        for (int slot = 0; slot < playerInventory.getSize(); ++slot) {
            ItemStack itemStack = playerInventory.getContents()[slot];
            if (itemStack == null || !itemStack.getType().toString().contains("SWORD")) continue;
            ++count;
        }
        return count;
    }

    private int getItemCount(Player player, Material material) {
        int count = 0;
        for (int slot = 0; slot < player.getInventory().getSize(); ++slot) {
            ItemStack itemStack = player.getInventory().getContents()[slot];
            if (itemStack == null || itemStack.getType() != material) continue;
            ++count;
        }
        return count;
    }

    public boolean isPlaceable(Location location) {
        if (location.getY() >= GameMain.getInstance().getMaxHeight()) {
            return false;
        }
        if (GameMain.getInstance().getGeneratorManager().getGenerators().stream().filter(generator -> !this.distanceSquared(generator.getLocation(), location, 3.0) && location.getY() < generator.getLocation().getY() + 6.0).findFirst().isPresent()) {
            return false;
        }
        Optional<Generator> optionalGenerator = GameMain.getInstance().getIslandManager().getClosestGenerator(location);
        return !(location.distance(optionalGenerator.get().getLocation()) <= GameMain.getInstance().getMinimunDistanceToPlaceBlocks());
    }

    public Island getIsland(IslandColor islandColor) {
        return GameMain.getInstance().getIslandManager().getIsland(islandColor);
    }

    public Island getIsland(UUID playerId) {
        return GameMain.getInstance().getIslandManager().getIsland(playerId);
    }
}

