/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.inventory.ItemStack
 */
package br.com.dragonmc.game.bedwars.listener;

import br.com.dragonmc.game.bedwars.GameConst;
import br.com.dragonmc.game.bedwars.GameMain;
import br.com.dragonmc.game.bedwars.event.PlayerBoughtItemEvent;
import br.com.dragonmc.game.bedwars.gamer.Gamer;
import br.com.dragonmc.game.bedwars.island.Island;
import br.com.dragonmc.game.bedwars.island.IslandUpgrade;
import br.com.dragonmc.game.engine.GameAPI;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.common.language.Language;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class IslandListener
implements Listener {
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onPlayerBoughtItem(PlayerBoughtItemEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);
        Island island = GameMain.getInstance().getIslandManager().getIsland(player.getUniqueId());
        if (island != null) {
            switch (event.getItemStack().getType()) {
                case COMPASS: {
                    player.getInventory().remove(Material.COMPASS);
                    event.setItemStack(GameConst.FINDER.getItemStack());
                    break;
                }
                case WOOL: {
                    event.setItemStack(ItemBuilder.fromStack(event.getItemStack()).durability(island.getIslandColor().getWoolId()).build());
                    break;
                }
                case HARD_CLAY: {
                    event.setItemStack(ItemBuilder.fromStack(event.getItemStack()).type(Material.STAINED_CLAY).durability(island.getIslandColor().getWoolId()).build());
                    break;
                }
                case SHEARS: {
                    if (gamer.isShears()) {
                        event.setCancelled(true);
                        player.sendMessage(Language.getLanguage(player.getUniqueId()).t("bedwars.you-already-have-shears", new String[0]));
                        break;
                    }
                    gamer.setShears(true);
                    break;
                }
                case DIAMOND_AXE: 
                case GOLD_AXE: 
                case IRON_AXE: 
                case STONE_AXE: 
                case WOOD_AXE: {
                    if (gamer.getAxeLevel() == gamer.getAxeLevel().getNext()) {
                        event.setCancelled(true);
                        player.sendMessage("");
                        return;
                    }
                    gamer.setAxeLevel(gamer.getAxeLevel().getNext());
                    int slot = -1;
                    for (int i = 0; i < player.getInventory().getContents().length; ++i) {
                        ItemStack itemStack = player.getInventory().getContents()[i];
                        if (itemStack == null || itemStack.getType() != gamer.getAxeLevel().getPrevious().getItemStack().getType()) continue;
                        player.getInventory().removeItem(new ItemStack[]{itemStack});
                        slot = i;
                        break;
                    }
                    if (slot == -1) {
                        player.getInventory().addItem(new ItemStack[]{gamer.getAxeLevel().getItemStack()});
                    } else {
                        player.getInventory().setItem(slot, gamer.getAxeLevel().getItemStack());
                    }
                    event.setItemStack(null);
                    break;
                }
                case DIAMOND_PICKAXE: 
                case GOLD_PICKAXE: 
                case IRON_PICKAXE: 
                case STONE_PICKAXE: 
                case WOOD_PICKAXE: {
                    if (gamer.getPickaxeLevel() == gamer.getPickaxeLevel().getNext()) {
                        event.setCancelled(true);
                        return;
                    }
                    gamer.setPickaxeLevel(gamer.getPickaxeLevel().getNext());
                    if (gamer.getPickaxeLevel().ordinal() == 1) {
                        player.getInventory().addItem(new ItemStack[]{gamer.getPickaxeLevel().getItemStack()});
                    } else {
                        int slot = -1;
                        for (int i = 0; i < player.getInventory().getContents().length; ++i) {
                            ItemStack itemStack = player.getInventory().getContents()[i];
                            if (itemStack == null || itemStack.getType() != gamer.getPickaxeLevel().getPrevious().getItemStack().getType()) continue;
                            player.getInventory().removeItem(new ItemStack[]{itemStack});
                            slot = i;
                            break;
                        }
                        if (slot == -1) {
                            player.getInventory().addItem(new ItemStack[]{gamer.getPickaxeLevel().getItemStack()});
                        } else {
                            player.getInventory().setItem(slot, gamer.getPickaxeLevel().getItemStack());
                        }
                    }
                    event.setItemStack(null);
                    break;
                }
                case STONE_SWORD: 
                case IRON_SWORD: 
                case DIAMOND_SWORD: {
                    String swordType = event.getItemStack().getType().name().split("_")[0];
                    Gamer.SwordLevel swordLevel = Gamer.SwordLevel.valueOf(swordType);
                    if (gamer.getSwordLevel() == swordLevel || gamer.getSwordLevel().ordinal() > swordLevel.ordinal()) {
                        player.getInventory().addItem(new ItemStack[]{ItemBuilder.fromStack(event.getItemStack()).enchantment(Enchantment.DAMAGE_ALL, island.getUpgradeLevel(IslandUpgrade.SHARPNESS)).build()});
                    } else {
                        if (gamer.getSwordLevel() == Gamer.SwordLevel.WOOD) {
                            int slot = -1;
                            for (int i = 0; i < player.getInventory().getContents().length; ++i) {
                                ItemStack itemStack = player.getInventory().getContents()[i];
                                if (itemStack == null || itemStack.getType() != Material.valueOf((String)(gamer.getSwordLevel().name() + "_SWORD"))) continue;
                                player.getInventory().removeItem(new ItemStack[]{itemStack});
                                slot = i;
                                break;
                            }
                            if (slot != -1) {
                                player.getInventory().setItem(slot, ItemBuilder.fromStack(event.getItemStack()).enchantment(Enchantment.DAMAGE_ALL, island.getUpgradeLevel(IslandUpgrade.SHARPNESS)).build());
                            }
                        } else {
                            player.getInventory().addItem(new ItemStack[]{new ItemBuilder().type(Material.valueOf((String)(swordLevel.name() + "_SWORD"))).enchantment(Enchantment.DAMAGE_ALL, island.getUpgradeLevel(IslandUpgrade.SHARPNESS)).build()});
                        }
                        gamer.setSwordLevel(swordLevel);
                    }
                    event.setItemStack(null);
                    break;
                }
                case DIAMOND_BOOTS: 
                case IRON_BOOTS: 
                case CHAINMAIL_BOOTS: {
                    String start = event.getItemStack().getType().name().split("_")[0];
                    Gamer.ArmorLevel armorLevel = Gamer.ArmorLevel.valueOf(start);
                    if (gamer.getArmorLevel() == armorLevel) {
                        event.setCancelled(true);
                        player.sendMessage("\u00a7cVoc\u00ea j\u00e1 est\u00e1 usando essa armadura.");
                        return;
                    }
                    if (armorLevel.ordinal() < gamer.getArmorLevel().ordinal()) {
                        event.setCancelled(true);
                        player.sendMessage("\u00a7cVoc\u00ea n\u00e3o pode comprar essa armadura enquanto estiver usando uma de n\u00edvel superior.");
                        return;
                    }
                    player.getInventory().setLeggings(new ItemBuilder().type(Material.valueOf((String)(start + "_LEGGINGS"))).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, island.getUpgradeLevel(IslandUpgrade.ARMOR_REINFORCEMENT)).build());
                    player.getInventory().setBoots(new ItemBuilder().type(Material.valueOf((String)(start + "_BOOTS"))).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, island.getUpgradeLevel(IslandUpgrade.ARMOR_REINFORCEMENT)).build());
                    gamer.setArmorLevel(armorLevel);
                    event.setItemStack(null);
                    break;
                }
            }
        }
    }
}

