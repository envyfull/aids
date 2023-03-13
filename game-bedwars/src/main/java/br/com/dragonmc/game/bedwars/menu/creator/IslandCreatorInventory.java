/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package br.com.dragonmc.game.bedwars.menu.creator;

import br.com.dragonmc.game.bedwars.island.Island;
import br.com.dragonmc.core.bukkit.utils.Location;
import br.com.dragonmc.core.bukkit.utils.item.ActionItemStack;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.bukkit.utils.menu.click.ClickType;
import br.com.dragonmc.core.bukkit.utils.menu.confirm.ConfirmInventory;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class IslandCreatorInventory {
    public IslandCreatorInventory(Player player, Island island) {
        Language language = Language.getLanguage(player.getUniqueId());
        MenuInventory menuInventory = new MenuInventory(language.t("bedwars.inventory.island-creator.name", "%island%", StringFormat.formatString(island.getIslandColor().name())), 3);
        menuInventory.setItem(10, new ItemBuilder().name("\u00a7aSpawn Location").type(Material.PAPER).lore("\u00a7fWorld: \u00a77" + island.getSpawnLocation().getWorldName(), "\u00a7fLocation: \u00a77" + StringFormat.formatString(", ", island.getSpawnLocation().getX(), island.getSpawnLocation().getY(), island.getSpawnLocation().getZ()), "\u00a7fYaw: \u00a77" + island.getSpawnLocation().getYaw(), "\u00a7fPitch: \u00a77" + island.getSpawnLocation().getPitch()).build(), (p, inv, type, stack, slot) -> {
            if (type == ClickType.RIGHT) {
                p.teleport(island.getSpawnLocation().getAsLocation());
            } else {
                this.handle(p, island, stack, menuInventory, () -> island.setSpawnLocation(Location.fromLocation(p.getLocation())));
            }
        });
        menuInventory.setItem(11, new ItemBuilder().name("\u00a7aBed Location").type(Material.BED).lore("\u00a7fWorld: \u00a77" + island.getBedLocation().getWorldName(), "\u00a7fLocation: \u00a77" + StringFormat.formatString(", ", island.getBedLocation().getX(), island.getBedLocation().getY(), island.getBedLocation().getZ()), "\u00a7fYaw: \u00a77" + island.getBedLocation().getYaw(), "\u00a7fPitch: \u00a77" + island.getBedLocation().getPitch()).build(), (p, inv, type, stack, slot) -> {
            if (type == ClickType.RIGHT) {
                p.teleport(island.getBedLocation().getAsLocation());
            } else {
                this.handle(p, island, stack, menuInventory, () -> island.setBedLocation(Location.fromLocation(p.getLocation())));
            }
        });
        menuInventory.setItem(12, new ItemBuilder().name("\u00a7aShop Location").type(Material.EMERALD).lore("\u00a7fWorld: \u00a77" + island.getShopLocation().getWorldName(), "\u00a7fLocation: \u00a77" + StringFormat.formatString(", ", island.getShopLocation().getX(), island.getShopLocation().getY(), island.getShopLocation().getZ()), "\u00a7fYaw: \u00a77" + island.getShopLocation().getYaw(), "\u00a7fPitch: \u00a77" + island.getShopLocation().getPitch()).build(), (p, inv, type, stack, slot) -> {
            if (type == ClickType.RIGHT) {
                p.teleport(island.getShopLocation().getAsLocation());
            } else {
                this.handle(p, island, stack, menuInventory, () -> island.setShopLocation(Location.fromLocation(p.getLocation())));
            }
        });
        menuInventory.setItem(13, new ItemBuilder().name("\u00a7aUpgrade Location").type(Material.DIAMOND).lore("\u00a7fWorld: \u00a77" + island.getUpgradeLocation().getWorldName(), "\u00a7fLocation: \u00a77" + StringFormat.formatString(", ", island.getUpgradeLocation().getX(), island.getUpgradeLocation().getY(), island.getUpgradeLocation().getZ()), "\u00a7fYaw: \u00a77" + island.getUpgradeLocation().getYaw(), "\u00a7fPitch: \u00a77" + island.getUpgradeLocation().getPitch()).build(), (p, inv, type, stack, slot) -> {
            if (type == ClickType.RIGHT) {
                p.teleport(island.getUpgradeLocation().getAsLocation());
            } else {
                this.handle(p, island, stack, menuInventory, () -> island.setUpgradeLocation(Location.fromLocation(p.getLocation())));
            }
        });
        menuInventory.setItem(14, new ItemBuilder().name("\u00a7aGenerators").type(Material.FURNACE).build(), (p, inv, type, stack, slot) -> new IslandCreatorGeneratorInventory(player, island));
        menuInventory.setItem(16, new ItemBuilder().name("\u00a7aSave").type(Material.ENCHANTED_BOOK).build(), (p, inv, type, stack, slot) -> {
            p.performCommand("config bedwars save");
            p.closeInventory();
            if (p.getInventory().getItemInHand().getType() == Material.BARRIER) {
                p.getInventory().removeItem(new ItemStack[]{p.getInventory().getItemInHand()});
            }
        });
        menuInventory.open(player);
    }

    private void handle(Player player, final Island island, final ItemStack itemStack, MenuInventory menuInventory, final ConfirmHandler confirmHandler) {
        player.getInventory().addItem(new ActionItemStack(new ItemBuilder().type(itemStack.getType()).name(itemStack.getItemMeta().getDisplayName()).build(), new ActionItemStack.Interact(){

            @Override
            public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionItemStack.ActionType action) {
                new ConfirmInventory(player, "\u00a77" + ChatColor.stripColor((String)itemStack.getItemMeta().getDisplayName()), b -> {
                    if (b) {
                        player.getInventory().remove(item);
                        ActionItemStack.unregisterHandler(this);
                        confirmHandler.confirm();
                        new IslandCreatorInventory(player, island);
                    }
                }, null);
                return false;
            }
        }).getItemStack());
        player.closeInventory();
    }

    public static interface ConfirmHandler {
        public void confirm();
    }
}

