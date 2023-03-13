/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.bukkit.Bukkit
 *  org.bukkit.Effect
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Chest
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerItemConsumeEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.scheduler.BukkitRunnable
 */
package br.com.dragonmc.game.bedwars.listener;

import br.com.dragonmc.game.bedwars.GameMain;
import br.com.dragonmc.game.bedwars.event.island.IslandUpgradeEvent;
import br.com.dragonmc.game.bedwars.gamer.Gamer;
import br.com.dragonmc.game.bedwars.generator.Generator;
import br.com.dragonmc.game.bedwars.generator.impl.NormalGenerator;
import br.com.dragonmc.game.bedwars.island.Island;
import br.com.dragonmc.game.bedwars.island.IslandUpgrade;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.bukkit.event.UpdateEvent;
import br.com.dragonmc.game.engine.GameAPI;
import br.com.dragonmc.core.bukkit.utils.Location;
import br.com.dragonmc.core.bukkit.utils.player.PlayerHelper;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class UpgradeListener
implements Listener {
    private static final double ISLAND_DISTANCE = 25.0;
    private Map<Island, Integer> regenMap = new HashMap<Island, Integer>();
    private Map<UUID, Long> trapBlockMap = new HashMap<UUID, Long>();
    private List<Island> trapList = new ArrayList<Island>();

    @EventHandler
    public void onUpdateEvent(UpdateEvent event) {
        if (event.getType() == UpdateEvent.UpdateType.SECOND) {
            this.regenMap.entrySet().forEach(entry -> {
                org.bukkit.Location location = ((Generator)((Island)entry.getKey()).getIslandGenerators().stream().findFirst().orElse(null)).getLocation();
                for (int i = 0; i <= 40; ++i) {
                    org.bukkit.Location particleLocation = new org.bukkit.Location(location.getWorld(), location.getX() + Math.random() * 16.666666666666668 * (double)(CommonConst.RANDOM.nextBoolean() ? -1 : 1), location.getY() + Math.random() * 8.333333333333334, location.getZ() + Math.random() * 16.666666666666668 * (double)(CommonConst.RANDOM.nextBoolean() ? -1 : 1));
                    location.getWorld().playEffect(particleLocation, Effect.HAPPY_VILLAGER, 1);
                }
            });
            ImmutableList.copyOf(this.regenMap.entrySet()).forEach(entry -> this.map((Island)entry.getKey()).filter(gamer -> gamer.getPlayer().getLocation().distance(((Island)entry.getKey()).getSpawnLocation().getAsLocation()) <= 25.0).forEach(gamer -> gamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, (Integer)entry.getValue() - 1))));
            ImmutableList.copyOf(this.trapList).forEach(island -> Bukkit.getOnlinePlayers().stream().filter(player -> !island.getTeam().isTeam(player.getUniqueId()) && GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class).isAlive() && player.getLocation().distance(island.getSpawnLocation().getAsLocation()) <= 25.0 && (!this.trapBlockMap.containsKey(player.getUniqueId()) || this.trapBlockMap.get(player.getUniqueId()) < System.currentTimeMillis())).forEach(player -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1), true);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0), true);
                this.forEach((Island)island, gamer -> PlayerHelper.actionbar(gamer.getPlayer(), "\u00a7c\u00a7lARMADILHA ACIONADA!"));
                this.trapList.remove(island);
                island.removeUpgrade(IslandUpgrade.TRAP);
            }));
        }
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.MILK_BUCKET) {
            event.setCancelled(true);
            final Player player = event.getPlayer();
            new BukkitRunnable(){

                public void run() {
                    player.getInventory().remove(Material.MILK_BUCKET);
                    player.removePotionEffect(PotionEffectType.BLINDNESS);
                    player.removePotionEffect(PotionEffectType.SLOW);
                    if (!UpgradeListener.this.trapBlockMap.containsKey(player.getUniqueId())) {
                        UpgradeListener.this.trapBlockMap.put(player.getUniqueId(), System.currentTimeMillis() + 30000L);
                    }
                }
            }.runTaskLater((Plugin)GameAPI.getInstance(), 3L);
        }
    }

    @EventHandler
    public void onIslandUpgrade(IslandUpgradeEvent event) {
        Island island = event.getIsland();
        IslandUpgrade upgrade = event.getUpgrade();
        int level = event.getLevel();
        switch (upgrade) {
            case SHARPNESS: {
                this.forEach(island, gamer -> {
                    if (gamer.getPlayer() == null) {
                        return;
                    }
                    for (ItemStack itemStack : gamer.getPlayer().getInventory().getContents()) {
                        if (itemStack == null || !itemStack.getType().name().contains("SWORD")) continue;
                        itemStack.addEnchantment(Enchantment.DAMAGE_ALL, level);
                    }
                    for (ItemStack itemStack : gamer.getPlayer().getEnderChest().getContents()) {
                        if (itemStack == null || !itemStack.getType().name().contains("SWORD")) continue;
                        itemStack.addEnchantment(Enchantment.DAMAGE_ALL, level);
                    }
                });
                for (org.bukkit.Location location : GameMain.getInstance().getNearestBlocksByMaterial(island.getSpawnLocation().getAsLocation(), Material.CHEST, 10, 5)) {
                    Chest chest = (Chest)location.getBlock().getState();
                    for (ItemStack itemStack : chest.getInventory().getContents()) {
                        if (itemStack == null || !itemStack.getType().name().contains("SWORD")) continue;
                        itemStack.addEnchantment(Enchantment.DAMAGE_ALL, level);
                    }
                }
                break;
            }
            case ARMOR_REINFORCEMENT: {
                this.forEach(island, gamer -> {
                    if (gamer.getPlayer() == null) {
                        return;
                    }
                    for (ItemStack itemStack : gamer.getPlayer().getInventory().getArmorContents()) {
                        if (itemStack == null) continue;
                        itemStack.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level);
                    }
                });
                break;
            }
            case HASTE: {
                this.forEach(island, gamer -> {
                    if (gamer.getPlayer() == null) {
                        return;
                    }
                    gamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 199980, level - 1), true);
                });
                break;
            }
            case TRAP: {
                this.trapList.add(island);
                break;
            }
            case REGENERATION: {
                this.regenMap.put(island, level);
                break;
            }
            case FORGE: {
                if (level == 1) {
                    island.getIslandGenerators().stream().limit(3L).filter(generator -> generator.getItemStack().getType() == Material.IRON_INGOT).forEach(generator -> {
                        generator.setGenerateTime(generator.getGenerateTime() - 1000L);
                        generator.setLevel(generator.getLevel() + 1);
                    });
                    island.getIslandGenerators().stream().filter(generator -> generator.getItemStack().getType() == Material.GOLD_INGOT).forEach(generator -> {
                        generator.setGenerateTime(generator.getGenerateTime() - 1000L);
                        generator.setLevel(generator.getLevel() + 1);
                    });
                    break;
                }
                if (level == 2) {
                    island.getIslandGenerators().stream().forEach(generator -> {
                        generator.setGenerateTime(generator.getGenerateTime() - 1000L);
                        generator.setLevel(generator.getLevel() + 1);
                    });
                    break;
                }
                if (level == 3) {
                    NormalGenerator generator2 = new NormalGenerator(((Location)island.getGeneratorMap().get(Material.GOLD_INGOT).stream().findFirst().orElse(null)).getAsLocation(), Material.EMERALD);
                    generator2.setGenerateTime(15000L);
                    island.getIslandGenerators().add(generator2);
                    break;
                }
                if (level != 4) break;
                island.getIslandGenerators().stream().forEach(generator -> {
                    generator.setGenerateTime(Math.max(generator.getGenerateTime() - 2L, 0L));
                    generator.setLevel(generator.getLevel() + 1);
                });
            }
        }
    }

    private Stream<Gamer> map(Island island) {
        return island.getTeam().getPlayerSet().stream().map((UUID id) -> GameAPI.getInstance().getGamerManager().getGamer((UUID)id, Gamer.class)).filter(gamer -> gamer.getPlayer() != null && gamer.isAlive());
    }

    private void forEach(Island island, Consumer<Gamer> consumer) {
        island.getTeam().getPlayerSet().stream().map((UUID id) -> GameAPI.getInstance().getGamerManager().getGamer((UUID)id, Gamer.class)).filter(gamer -> gamer.getPlayer() != null && gamer.isAlive()).forEach(consumer);
    }
}

