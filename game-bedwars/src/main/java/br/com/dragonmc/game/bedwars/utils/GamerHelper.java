/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ChatColor
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.block.Block
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scoreboard.Criterias
 *  org.bukkit.scoreboard.DisplaySlot
 *  org.bukkit.scoreboard.Objective
 *  org.bukkit.scoreboard.Scoreboard
 */
package br.com.dragonmc.game.bedwars.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.game.bedwars.island.Island;
import br.com.dragonmc.game.bedwars.island.IslandUpgrade;
import br.com.dragonmc.game.engine.GameAPI;
import br.com.dragonmc.game.bedwars.GameMain;
import br.com.dragonmc.game.bedwars.event.PlayerBoughtItemEvent;
import br.com.dragonmc.game.bedwars.event.PlayerSpectateEvent;
import br.com.dragonmc.game.bedwars.gamer.Gamer;
import br.com.dragonmc.game.bedwars.menu.SpectatorInventory;
import br.com.dragonmc.game.bedwars.store.ShopCategory;
import br.com.dragonmc.core.bukkit.utils.item.ActionItemStack;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.common.member.status.Status;
import br.com.dragonmc.core.common.member.status.StatusType;
import br.com.dragonmc.core.common.member.status.types.BedwarsCategory;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class GamerHelper {
    public static final ActionItemStack PLAYERS = new ActionItemStack(new ItemBuilder().name("\u00a7aTeleportador").type(Material.COMPASS).build(), new ActionItemStack.Interact(){

        @Override
        public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionItemStack.ActionType action) {
            new SpectatorInventory(player);
            return false;
        }
    });
    public static final ActionItemStack PLAY_AGAIN = new ActionItemStack(new ItemBuilder().name("\u00a7aJogar novamente").type(Material.PAPER).build(), new ActionItemStack.Interact(){

        @Override
        public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionItemStack.ActionType action) {
            GameAPI.getInstance().sendPlayerToServer(player, CommonPlugin.getInstance().getServerType());
            return false;
        }
    });
    public static final ActionItemStack LOBBY = new ActionItemStack(new ItemBuilder().name("\u00a7aVoltar ao lobby").type(Material.BED).build(), new ActionItemStack.Interact(){

        @Override
        public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionItemStack.ActionType action) {
            GameAPI.getInstance().sendPlayerToServer(player, CommonPlugin.getInstance().getServerType().getServerLobby());
            return false;
        }
    });

    public static void handlePlayerToSpawn(Player player) {
        GamerHelper.handlePlayer(player);
        player.getInventory().setItem(8, LOBBY.getItemStack());
    }

    public static boolean isPlayerProtection(Player player) {
        if (player.hasMetadata("bed-island")) {
            MetadataValue metadataValue = player.getMetadata("bed-island").stream().findFirst().orElse(null);
            if (metadataValue.asLong() > System.currentTimeMillis()) {
                return true;
            }
            metadataValue.invalidate();
        }
        return false;
    }

    public static void setPlayerProtection(Player player, int seconds) {
        player.setMetadata("bed-island", GameAPI.getInstance().createMeta(System.currentTimeMillis() + (long)(seconds * 1000)));
    }

    public static void removePlayerProtection(Player player) {
        player.removeMetadata("bed-island", (Plugin)GameAPI.getInstance());
    }

    public static void handlePlayerToGame(Player player) {
        Island island = GameMain.getInstance().getIslandManager().getIsland(player.getUniqueId());
        if (island == null) {
            return;
        }
        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);
        GamerHelper.handlePlayer(player);
        if (island.hasUpgrade(IslandUpgrade.HASTE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 199980, island.getUpgradeLevel(IslandUpgrade.HASTE) - 1));
        }
        player.getInventory().setItem(0, new ItemBuilder().type(Material.valueOf((String)(gamer.getSwordLevel().name() + "_SWORD"))).enchantment(Enchantment.DAMAGE_ALL, island.getUpgradeLevel(IslandUpgrade.SHARPNESS)).build());
        if (gamer.getAxeLevel() != Gamer.AxeLevel.NONE) {
            player.getInventory().addItem(new ItemStack[]{gamer.getAxeLevel().getItemStack()});
        }
        if (gamer.getPickaxeLevel() != Gamer.PickaxeLevel.NONE) {
            player.getInventory().addItem(new ItemStack[]{gamer.getPickaxeLevel().getItemStack()});
        }
        if (gamer.isShears()) {
            player.getInventory().addItem(new ItemStack[]{new ItemBuilder().type(Material.SHEARS).build()});
        }
        GamerHelper.handleArmor(player);
        GamerHelper.handleHeart(player);
        Status status = CommonPlugin.getInstance().getStatusManager().loadStatus(player.getUniqueId(), StatusType.BEDWARS);
        if (player != null) {
            GamerHelper.updatePoints(player, status);
        }
    }

    private static void handleHeart(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard.getObjective("showhealth") == null) {
            Objective objective = scoreboard.registerNewObjective("showhealth", Criterias.HEALTH);
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            objective.setDisplayName(ChatColor.DARK_RED + "\u2665");
        }
    }

    public static void updatePoints(Player player, Status status) {
        player.setExp(0.0f);
        player.setTotalExperience(0);
        player.setLevel(status.getInteger(BedwarsCategory.BEDWARS_LEVEL));
        player.setExp((float)(status.getInteger(BedwarsCategory.BEDWARS_POINTS) / GameMain.getInstance().getMaxPoints(player.getLevel())));
    }

    public static void handleSpectate(final Player player) {
        Bukkit.getPluginManager().callEvent((Event)new PlayerSpectateEvent(player));
        new BukkitRunnable(){

            public void run() {
                GamerHelper.handlePlayer(player);
                player.getInventory().setItem(0, PLAYERS.getItemStack());
                player.getInventory().setItem(7, PLAY_AGAIN.getItemStack());
                player.getInventory().setItem(8, LOBBY.getItemStack());
                player.setGameMode(GameMode.ADVENTURE);
                player.setAllowFlight(true);
                player.setFlying(true);
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 199980, 2));
                GamerHelper.hidePlayer(player);
            }
        }.runTaskLater((Plugin)GameAPI.getInstance(), 3L);
    }

    public static void handlePlayer(Player player) {
        player.setFallDistance(-1.0f);
        player.setHealth(20.0);
        player.setMaxHealth(20.0);
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setExp(0.0f);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getActivePotionEffects().forEach(potion -> player.removePotionEffect(potion.getType()));
    }

    private static String getYaw(Location location) {
        float yaw = location.getYaw();
        if (yaw < 0.0f) {
            yaw += 360.0f;
        }
        if (yaw >= 315.0f || yaw < 45.0f) {
            return "S";
        }
        if (yaw < 135.0f) {
            return "W";
        }
        if (yaw < 225.0f) {
            return "N";
        }
        if (yaw < 315.0f) {
            return "E";
        }
        return "N";
    }

    public static Location forwardLocationByPlayerRotation(Location locationToRotate, Location location, int blocks) {
        switch (GamerHelper.getYaw(locationToRotate)) {
            case "N": {
                return location.clone().add(0.0, 0.0, (double)(-blocks));
            }
            case "W": {
                return location.clone().add((double)(-blocks), 0.0, 0.0);
            }
            case "S": {
                return location.clone().add(0.0, 0.0, (double)blocks);
            }
            case "E": {
                return location.clone().add((double)blocks, 0.0, 0.0);
            }
        }
        return location.clone();
    }

    public static List<Player> getPlayersNear(Location location, double radius) {
        return Bukkit.getOnlinePlayers().stream().filter(player -> location.distance(player.getLocation()) <= radius).collect(Collectors.toList());
    }

    public static Location forwardLocationByPlayerRotation(Player player, Location location, int blocks) {
        return GamerHelper.forwardLocationByPlayerRotation(player.getLocation(), location, blocks);
    }

    public static List<Block> getNearbyBlocks(Location location, int radius) {
        ArrayList<Block> blocks = new ArrayList<Block>();
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; ++x) {
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; ++y) {
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; ++z) {
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    public static Player getMoreNearbyPlayers(Location location, double radius) {
        Player nearby = null;
        for (Player player : GamerHelper.getPlayersNear(location, radius)) {
            if (nearby == null) {
                nearby = player;
                continue;
            }
            if (!(nearby.getLocation().distance(location) > player.getLocation().distance(location))) continue;
            nearby = player;
        }
        return nearby;
    }

    public static void handleRemoveArmor(Player player) {
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }

    public static void handleArmor(Player player) {
        Island island = GameMain.getInstance().getIslandManager().getIsland(player.getUniqueId());
        Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);
        if (island == null) {
            return;
        }
        player.getInventory().setHelmet(new ItemBuilder().type(Material.LEATHER_HELMET).color(island.getIslandColor().getColorEquivalent()).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, island.getUpgradeLevel(IslandUpgrade.ARMOR_REINFORCEMENT)).build());
        player.getInventory().setChestplate(new ItemBuilder().type(Material.LEATHER_CHESTPLATE).color(island.getIslandColor().getColorEquivalent()).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, island.getUpgradeLevel(IslandUpgrade.ARMOR_REINFORCEMENT)).build());
        player.getInventory().setLeggings(new ItemBuilder().type(Material.valueOf((String)(gamer.getArmorLevel().name() + "_LEGGINGS"))).color(island.getIslandColor().getColorEquivalent()).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, island.getUpgradeLevel(IslandUpgrade.ARMOR_REINFORCEMENT)).build());
        player.getInventory().setBoots(new ItemBuilder().type(Material.valueOf((String)(gamer.getArmorLevel().name() + "_BOOTS"))).color(island.getIslandColor().getColorEquivalent()).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, island.getUpgradeLevel(IslandUpgrade.ARMOR_REINFORCEMENT)).build());
    }

    public static void hidePlayer(Player player) {
        GameMain.getInstance().getVanishManager().setPlayerVanishToGroup(player, CommonPlugin.getInstance().getPluginInfo().getHighGroup());
    }

    public static void buyItem(Player player, ShopCategory.ShopItem shopItem) {
        PlayerBoughtItemEvent playerBoughtItemEvent = new PlayerBoughtItemEvent(player, new ItemBuilder().type(shopItem.getStack().getType()).durability(shopItem.getStack().getDurability()).amount(shopItem.getStack().getAmount()).potion(ItemBuilder.fromStack(shopItem.getStack()).getPotions()).enchantment(shopItem.getStack().getEnchantments()).build());
        Bukkit.getPluginManager().callEvent((Event)playerBoughtItemEvent);
        if (!playerBoughtItemEvent.isCancelled()) {
            player.getInventory().removeItem(new ItemStack[]{new ItemBuilder().type(shopItem.getPrice().getMaterial()).amount(shopItem.getPrice().getAmount()).build()});
            if (playerBoughtItemEvent.getItemStack() != null) {
                player.getInventory().addItem(new ItemStack[]{playerBoughtItemEvent.getItemStack()});
            }
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
        }
    }
}

